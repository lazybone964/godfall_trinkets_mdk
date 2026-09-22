package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.item.custom.BrokenCoreItem;
import com.lazybones.godfalltrinkets.item.custom.CurseTransferTrinketItem;
import com.lazybones.godfalltrinkets.item.custom.GodfallTrinketItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = "godfall_trinkets")
public class TrinketGrowthEvents {

    private static final Random RANDOM = new Random();

    private record CoreInfo(ItemStack stack, BrokenCoreItem item, int tier) {}

    private static Optional<CoreInfo> findCore(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.is(ModItems.BROKEN_CORE.get())))
                .map(r -> {
                    ItemStack s = r.stack();
                    return new CoreInfo(s, (BrokenCoreItem) s.getItem(), ((BrokenCoreItem) s.getItem()).getTier(s));
                });
    }

    private static boolean isHarmfulEffect(MobEffectInstance effect) {
        return effect.getEffect() == MobEffects.POISON
                || effect.getEffect() == MobEffects.WITHER
                || effect.getEffect() == MobEffects.WEAKNESS
                || effect.getEffect() == MobEffects.DARKNESS
                || effect.getEffect() == MobEffects.HUNGER
                || effect.getEffect() == MobEffects.LEVITATION
                || effect.getEffect() == MobEffects.BLINDNESS
                || effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN
                || effect.getEffect() == MobEffects.DIG_SLOWDOWN
                || effect.getEffect() == MobEffects.CONFUSION;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        findCore(player).ifPresent(core -> {
            applyHungerCurse(player, core);
            applyDebuffCurse(player, core);
        });
    }

    private static void applyHungerCurse(Player player, CoreInfo core) {
        if (core.tier >= 9) return;
        if (CurseTransferTrinketItem.isAbsorbingCurse(player, "hunger_curse")) return;
        if (player.tickCount % 20 == 0) {
            player.causeFoodExhaustion(0.2f);
        }
        if (player.getFoodData().getFoodLevel() <= 0 && player.tickCount % 20 == 0) {
            player.giveExperiencePoints(-1);
        }
    }

    // 结构：玩家UUID -> 已经处理过的负面效果集合
    private static final Map<UUID, Set<MobEffect>> PLAYER_PROCESSED_EFFECTS = new HashMap<>();

    private static void applyDebuffCurse(Player player, CoreInfo core) {
        if (CurseTransferTrinketItem.isAbsorbingCurse(player, "foul_body")) return;
        if (player.tickCount % 20 != 0) return;
        boolean rev = core.tier >= 9;
        UUID pid = player.getUUID();
        // 获取该玩家已处理过的效果集合，不存在则新建
        Set<MobEffect> processed = PLAYER_PROCESSED_EFFECTS.computeIfAbsent(pid, k -> new HashSet<>());

        List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance inst : effects) {
            MobEffect eff = inst.getEffect();
            if (!isHarmfulEffect(inst)) continue;
            // ✅核心判定：已经处理过 → 跳过，不再修改
            if (processed.contains(eff)) continue;

            int orig = inst.getDuration();
            if (orig <= 20) continue;

            int newDur;
            if (rev) {
                newDur = Math.max(20, orig / 2);
            } else {
                newDur = Math.min(orig * 2, 24000);
            }
            if (newDur == orig) continue;

            // 保存全部属性
            int amp = inst.getAmplifier();
            boolean amb = inst.isAmbient();
            boolean vis = inst.isVisible();
            boolean ico = inst.showIcon();

            player.removeEffect(eff);
            player.forceAddEffect(new MobEffectInstance(eff, newDur, amp, amb, vis, ico), player);
            // ✅标记：这个效果已经修改过一次，后续不再处理
            processed.add(eff);
        }

        // 清理：玩家身上消失的效果，从已处理列表移除
        processed.removeIf(e -> !player.hasEffect(e));
        // 清理离线玩家，防止内存泄漏
        PLAYER_PROCESSED_EFFECTS.keySet().removeIf(u -> player.level().getPlayerByUUID(u) == null);
    }



    /**
     * 处理生物受伤事件，应用核心的防御和攻击效果。
     *
     * <p>当玩家作为受害者时，依次应用以下效果：</p>
     * <ul>
     *   <li>护盾效果：吸收部分伤害</li>
     *   <li>闪避效果：有概率完全躲避并反弹伤害</li>
     *   <li>重伤效果：增加受到的伤害</li>
     *   <li>伤痕效果：基于最大生命值增加额外伤害</li>
     * </ul>
     *
     * <p>当玩家作为攻击者时，应用灾难效果（根据周围敌人数量增加伤害）。</p>
     *
     * @param event 生物受伤事件，包含受害者、伤害来源和伤害值等信息
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();

        if (victim instanceof Player player) {
            findCore(player).ifPresent(core -> {
                float dmg = event.getAmount();
                dmg = applyShield(core, dmg);
                dmg = applyDodge(core, player, dmg, source);
                dmg = applyDeepWoundAbsorbed(core, player, dmg);
                dmg = applyScarred(core, player, dmg);
                if (dmg <= 0) event.setCanceled(true);
                event.setAmount(Math.max(0, dmg));
            });
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof Player atkPlayer) {
            findCore(atkPlayer).ifPresent(core -> {
                float dmg = event.getAmount();
                dmg = applyCalamity(core, atkPlayer, dmg, victim);
                event.setAmount(Math.max(0, dmg));
            });
        }
    }

    private static float applyShield(CoreInfo core, float dmg) {
        if (!core.item.isShieldActive(core.stack)) return dmg;
        float remaining = core.item.absorbDamage(core.stack, dmg, core.tier);
        return remaining;
    }

    private static float applyDodge(CoreInfo core, Player player, float dmg, DamageSource source) {
        float chance = BrokenCoreItem.DODGE_CHANCE[core.tier];
        if (RANDOM.nextFloat() < chance) {
            float reflect = BrokenCoreItem.DODGE_REFLECT[core.tier];
            if (reflect > 0 && source.getEntity() instanceof LivingEntity attacker) {
                attacker.hurt(player.damageSources().thorns(player), dmg * reflect);
            }
            return 0;
        }
        return dmg;
    }

    private static float applyDeepWound(CoreInfo core, float dmg) {
        return (float)(dmg * BrokenCoreItem.getDeepWoundMultiplier(core.tier));
    }

    private static float applyDeepWoundAbsorbed(CoreInfo core, Player player, float dmg) {
        if (CurseTransferTrinketItem.isAbsorbingCurse(player, "deep_wound")) return dmg;
        return applyDeepWound(core, dmg);
    }

    private static float applyScarred(CoreInfo core, Player player, float dmg) {
        if (CurseTransferTrinketItem.isAbsorbingCurse(player, "scarred")) return dmg;
        if (core.tier >= 9) {
            float cap = player.getMaxHealth() * 0.3f;
            return Math.min(dmg, cap);
        }
        return dmg + (float)(player.getMaxHealth() * BrokenCoreItem.getScarredRatio(core.tier));
    }

    private static float applyCalamity(CoreInfo core, Player player, float dmg, LivingEntity victim) {
        int count = countNearbyEnemies(player);
        float bonus = BrokenCoreItem.CALAMITY_RATE[core.tier];
        float cap   = BrokenCoreItem.CALAMITY_CAP[core.tier];
        float mult  = 1.0f + Math.min(count * bonus, cap);
        return dmg * mult;
    }

    private static int countNearbyEnemies(Player player) {
        AABB box = new AABB(player.blockPosition()).inflate(10);
        return player.level().getEntitiesOfClass(Mob.class, box,
                e -> e.isAlive() && e.getTarget() != null).size();
    }

    /**
     * 处理生物死亡事件，当玩家击杀生物时触发核心效果。
     *
     * <p>该方法会在生物死亡时被调用，主要功能包括：</p>
     * <ul>
     *   <li>计算并添加经验值给玩家的装备</li>
     *   <li>应用灵魂吸取效果</li>
     *   <li>应用粉碎效果（对周围敌人造成伤害）</li>
     *   <li>生成神之碎片掉落</li>
     * </ul>
     *
     * <p>经验值计算公式：基础值 = max(5, 生物最大生命值)，然后根据核心等级进行加成。</p>
     *
     * @param event 生物死亡事件，包含受害者信息和伤害来源
     */
    @SubscribeEvent
    public static void onMobKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player player)) return;

        LivingEntity victim = event.getEntity();

        findCore(player).ifPresent(core -> {
            int exp = Math.max(5, (int) victim.getMaxHealth());
            exp = (int)(exp * (1.0f + core.tier * 0.1f + core.tier * core.tier * 0.01f));
            if (core.tier >= 6) exp = (int)(exp * (1.0f + 0.1f * core.tier));
            core.item.addExp(core.stack, exp, player);

            applySoulDrain(player, core, victim);
            applyShatter(player, core, victim, event);
            dropGodFragments(player, core, victim);
        });
    }

    /**
     * 应用灵魂吸取效果
     * <p>该方法会在玩家击杀生物时触发，主要功能包括：</p>
     * <ul>
     *     <li>对玩家进行恢复</li>
     *     <li>对玩家进行饱食度恢复</li>
     *     <li>对玩家进行物品损坏恢复</li>
     *     <li>对玩家进行经验恢复</li>
     * </ul>
     *
     * @param player
     * @param core
     * @param victim
     */

    private static void applySoulDrain(Player player, CoreInfo core, LivingEntity victim) {
        float[] data = BrokenCoreItem.SOUL_DRAIN[core.tier];
        player.heal(data[0] * 0.5f);
        player.getFoodData().eat(1, data[1] * 0.5f);
        int repair = (int) data[2];
        if (repair > 0) {
            ItemStack held = player.getMainHandItem();
            if (held.isDamageableItem()) {
                held.setDamageValue(Math.max(0, held.getDamageValue() - repair));
            }
        }
    }

    /**
      * 应用粉碎效果（对周围敌人造成伤害）
      * <p>该方法会在玩家击杀生物时触发，主要功能包括：</p>
      * <ul>
      *   <li>计算并应用粉碎效果（对周围敌人造成伤害）</li>
      *   <li>对玩家造成伤害（如果玩家是攻击者）</li>
      * </ul>
      * @param player 击杀生物的玩家
      *   <li>对玩家造成伤害（如果玩家是攻击者）</li>
      * </ul>
      * @param victim 被击杀的生物
      * @param event 击杀事件
     */
    private static void applyShatter(Player player, CoreInfo core, LivingEntity victim, LivingDeathEvent event) {
        if (core.tier < 4) return;
        float baseDmg = victim.getMaxHealth();
        float ratio;
        int range;
        boolean ignite = false;
        if (core.tier >= 9)      { ratio = 0.70f; range = 6; ignite = true; }
        else if (core.tier >= 6) { ratio = 0.45f; range = 4; }
        else                     { ratio = 0.30f; range = 3; }

        AABB box = new AABB(victim.blockPosition()).inflate(range);
        for (LivingEntity e : victim.level().getEntitiesOfClass(LivingEntity.class, box,
                e -> e != victim && e instanceof Mob && e.isAlive())) {
            e.hurt(player.damageSources().playerAttack(player), baseDmg * ratio);
            if (ignite) e.setSecondsOnFire(3);
            if (core.tier >= 6 && RANDOM.nextFloat() < 0.25f) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));
            }
        }
        if (core.tier >= 7) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0, false, false, true));
        }
    }
    /**
     * 在击杀生物时掉落神之碎片。
     *
     * <p>根据核心等级决定基础掉落数量，并有一定概率获得额外碎片。</p>
     *
     * <ul>
     *   <li>核心等级 ≥ 9: 基础4个碎片</li>
     *   <li>核心等级 ≥ 7: 基础3个碎片</li>
     *   <li>核心等级 ≥ 4: 基础2个碎片</li>
     *   <li>核心等级 < 4: 基础1个碎片</li>
     * </ul>
     *
     * <p>额外碎片概率 = 10% + 核心等级 × 5%</p>
     *
     * @param player 持有核心的玩家
     * @param core 核心的信息（包含物品栈、物品实例和等级）
     * @param victim 被击杀的生物
     */

    private static void dropGodFragments(Player player, CoreInfo core, LivingEntity victim) {
        int tier = core.tier;
        int count;
        if      (tier >= 9) count = 4;
        else if (tier >= 7) count = 3;
        else if (tier >= 4) count = 2;
        else                count = 1;

        int bonusChance = 10 + tier * 5;
        if (RANDOM.nextInt(100) < bonusChance) count++;

        ItemStack fragment = new ItemStack(ModItems.GODFRAGMENT.get(), count);
        victim.spawnAtLocation(fragment);
    }

    @SubscribeEvent
    public static void onSleepAttempt(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        findCore(player).ifPresent(core -> {
            if (core.tier < 9 && !CurseTransferTrinketItem.isAbsorbingCurse(player, "sleepless")) {
                player.displayClientMessage(
                        Component.translatable("message.godfall_trinkets.cant_sleep"), true);
                event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
            }
        });
    }

    /** 破厄之核被取下时，自动弹出所有依赖它的子饰品 */
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!event.getFrom().is(ModItems.BROKEN_CORE.get())) return;
        if (!event.getTo().isEmpty()) return;
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            for (var entry : handler.getCurios().entrySet()) {
                var stacks = entry.getValue().getStacks();
                for (int i = entry.getValue().getSlots() - 1; i >= 0; i--) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof GodfallTrinketItem) {
                        player.getInventory().placeItemBackInInventory(stack);
                        stacks.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        });
    }
}
