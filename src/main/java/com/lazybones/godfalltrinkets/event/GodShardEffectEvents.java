package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.*;
import java.lang.reflect.Field;

/** 神位碎片效果事件：天穹飞行、烈阳回血、生命恢复、疾风、影杀、命运幸运、力量、星芒、屏障、虚空、风免摔、火免、雷电、击退、复活、灵韵、铭纹 */
@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GodShardEffectEvents {

    private static final Random RAND = new Random();
    private static final UUID FATE_LUCK_UUID = UUID.fromString("a1b2c3d4-e5f6-7000-8000-000000000001");
    private static final UUID BARRIER_SPEED_UUID = UUID.fromString("a1b2c3d4-e5f6-7000-8000-000000000002");
    private static final String RUNE_TAG = "godfall_rune_boosted";
    private static final String RUNE_ORIGINAL = "godfall_rune_original";
    /** 防止影杀背刺 target.hurt() 触发递归伤害 */
    private static final ThreadLocal<Boolean> HURT_PROCESSING = ThreadLocal.withInitial(() -> false);

    private static boolean hasShard(Player p, RegistryObject<Item> shard) {
        return CuriosApi.getCuriosInventory(p).resolve()
                .map(h -> h.findFirstCurio(s -> s.is(shard.get())).isPresent()).orElse(false);
    }

    private static boolean hasBrokenCore(Player p) {
        return CuriosApi.getCuriosInventory(p).resolve()
                .map(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get()) || s.is(ModItems.GOD_CORE.get())).isPresent()).orElse(false);
    }

    /** 神格增幅倍率：佩戴神格时碎片效果 * 配置倍率，否则为 1.0 */
    private static double getGodCoreBonus(Player p) {
        return GodCoreEvents.hasGodCore(p) ? GodfallConfig.GOD_CORE_SHARD_POWER_MOD.get() : 1.0;
    }

    // ==================== Tick ====================
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) {
        Player p = e.player;
        if (e.phase != TickEvent.Phase.END) return;

        boolean isClient = p.level().isClientSide();
        boolean hasCore = hasBrokenCore(p);

        // 天穹飞行（仅服务端权威处理，客户端靠 onUpdateAbilities 同步）
        if (!isClient) {
            handleSkyFlight(p, hasCore);
        }

        if (isClient) return;

        // 铭纹附魔同步
        syncRuneEnchantments(p, hasCore && hasShard(p, ModItems.SHARD_RUNE));

        // 壁垒举盾不减速（在 hasCore 判断前执行，确保卸下时移除修饰符）
        handleBarrierBlocking(p, hasCore);

        if (!hasCore) {
            AttributeInstance luckAttr = p.getAttribute(Attributes.LUCK);
            if (luckAttr != null && luckAttr.getModifier(FATE_LUCK_UUID) != null)
                luckAttr.removeModifier(FATE_LUCK_UUID);
            return;
        }

        // 烈阳白天回血
        if (hasShard(p, ModItems.SHARD_SUN) && p.level().getBrightness(LightLayer.SKY, p.blockPosition()) > 12)
            p.heal(0.1F);

        // 生命常驻恢复X
        if (hasShard(p, ModItems.SHARD_LIFE))
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 9, true, false));

        // 疾风游泳/攀爬加速
        if (hasShard(p, ModItems.SHARD_WIND) && (p.onClimbable() || p.isInWater()))
            p.setDeltaMovement(p.getDeltaMovement().scale(1.2D));

        // 影杀潜行隐身
        if (hasShard(p, ModItems.SHARD_SHADOW) && p.isCrouching())
            p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, true, false));

        // 命运幸运+200%（神格加持*1.5→300%）
        AttributeInstance luckAttr = p.getAttribute(Attributes.LUCK);
        if (hasShard(p, ModItems.SHARD_FATE)) {
            double expectedVal = 2.0 * getGodCoreBonus(p);
            if (luckAttr != null) {
                AttributeModifier existing = luckAttr.getModifier(FATE_LUCK_UUID);
                if (existing == null || existing.getAmount() != expectedVal) {
                    luckAttr.removeModifier(FATE_LUCK_UUID);
                    luckAttr.addPermanentModifier(new AttributeModifier(FATE_LUCK_UUID,
                            "shard_fate_luck", expectedVal, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        } else {
            if (luckAttr != null && luckAttr.getModifier(FATE_LUCK_UUID) != null)
                luckAttr.removeModifier(FATE_LUCK_UUID);
        }
    }

    /** 天穹碎片：生存模式解锁创造飞行 */
    private static void handleSkyFlight(Player p, boolean hasCore) {
        boolean shouldFly = hasCore && hasShard(p, ModItems.SHARD_SKY)
                && GodfallConfig.SHARD_ENABLE[0].get();
        if (shouldFly) {
            if (!p.getAbilities().mayfly) {
                p.getAbilities().mayfly = true;
                if (p instanceof ServerPlayer sp) sp.onUpdateAbilities();
            }
        } else if (!p.isCreative() && !p.isSpectator()) {
            if (p.getAbilities().mayfly) {
                p.getAbilities().mayfly = false;
                p.getAbilities().flying = false;
                if (p instanceof ServerPlayer sp) sp.onUpdateAbilities();
            }
        }
    }

    /** 壁垒碎片：举盾不减速（抵消原版 0.2 倍移动减速） */
    private static void handleBarrierBlocking(Player p, boolean hasCore) {
        AttributeInstance speed = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        boolean shouldApply = hasCore && hasShard(p, ModItems.SHARD_BARRIER)
                && GodfallConfig.SHARD_ENABLE[7].get() && p.isBlocking();
        if (shouldApply) {
            if (speed.getModifier(BARRIER_SPEED_UUID) == null) {
                // 原版举盾移动速度 ×0.2，补 ×4（即 +400%）抵消
                speed.addTransientModifier(new AttributeModifier(BARRIER_SPEED_UUID,
                        "shard_barrier_speed", 4.0, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        } else {
            if (speed.getModifier(BARRIER_SPEED_UUID) != null)
                speed.removeModifier(BARRIER_SPEED_UUID);
        }
    }

    // ==================== 攻击 ====================
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p)) return;
        if (HURT_PROCESSING.get()) return; // 防递归：影杀背刺 target.hurt() 再次触发本事件
        HURT_PROCESSING.set(true);
        try {
            ItemStack hand = p.getMainHandItem();
            LivingEntity target = event.getEntity();
            float dmg = event.getAmount();

            if (hasShard(p, ModItems.SHARD_STRENGTH)) dmg *= 1.5F * (float)getGodCoreBonus(p);
            if ((hand.getItem() instanceof BowItem || hand.getItem() instanceof TridentItem)
                    && hasShard(p, ModItems.SHARD_STAR)) dmg *= 1.4F * (float)getGodCoreBonus(p);
            if (hasShard(p, ModItems.SHARD_SUN)) target.setSecondsOnFire(5);

            // 闪电只在服务端召唤，避免客户端 addFreshEntity 引发问题
            if (!p.level().isClientSide()
                    && hasShard(p, ModItems.SHARD_THUNDER)
                    && RAND.nextFloat() <= GodfallConfig.SHARD_THUNDER_LIGHTNING_CHANCE.get().floatValue()) {
                LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, p.level());
                bolt.setPos(target.getX(), target.getY(), target.getZ());
                bolt.setVisualOnly(true);
                p.level().addFreshEntity(bolt);
            }

            if (hasShard(p, ModItems.SHARD_SHADOW)) {
                Vec3 lookDir = target.getViewVector(1F);
                Vec3 toPlayer = p.position().subtract(target.position()).normalize();
                if (lookDir.dot(toPlayer) > 0)
                    target.hurt(event.getSource(), dmg * GodfallConfig.SHARD_SHADOW_BACKSTAB_MULT.get().floatValue() * (float)getGodCoreBonus(p));
            }
            event.setAmount(dmg * GodfallConfig.SHARD_GLOBAL_POWER.get().floatValue());
        } finally {
            HURT_PROCESSING.set(false);
        }
    }

    // ==================== 防御 ====================
    @SubscribeEvent
    public static void shieldReflect(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Player p) || !p.isBlocking()) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_BARRIER)) return;
        float dmg = event.getAmount();
        if (event.getSource().getEntity() instanceof LivingEntity le) le.hurt(event.getSource(), dmg);
        p.heal(1F);
    }

    @SubscribeEvent
    public static void fallImmune(LivingDamageEvent e) {
        if (e.getEntity() instanceof Player p && e.getSource().is(DamageTypes.FALL)
                && hasBrokenCore(p) && hasShard(p, ModItems.SHARD_WIND))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public static void fireImmune(LivingDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        var src = e.getSource();
        if ((src.is(DamageTypes.IN_FIRE) || src.is(DamageTypes.ON_FIRE) || src.is(DamageTypes.LAVA))
                && hasBrokenCore(p) && hasShard(p, ModItems.SHARD_SUN))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public static void thunderProtect(LivingDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p)) return;
        if (e.getSource().is(DamageTypes.LIGHTNING_BOLT) && hasShard(p, ModItems.SHARD_THUNDER))
            e.setCanceled(true);
        if (p.level().isThundering() && hasShard(p, ModItems.SHARD_THUNDER))
            e.setAmount(e.getAmount() * 1.4F * (float)getGodCoreBonus(p));
    }

    @SubscribeEvent
    public static void noKnockback(LivingKnockBackEvent e) {
        if (e.getEntity() instanceof Player p && hasBrokenCore(p) && hasShard(p, ModItems.SHARD_STRENGTH))
            e.setCanceled(true);
    }

    // ==================== 星陨 SHARD_STAR：弓弩三叉戟蓄力加速 ====================
    @SubscribeEvent
    public static void onRangedCharge(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_STAR)) return;
        if (!GodfallConfig.SHARD_ENABLE[11].get()) return;
        ItemStack stack = event.getItem();
        boolean isRanged = stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof TridentItem;
        if (!isRanged) return;
        // 蓄力加速：每 tick 额外减少剩余使用时间（约 2 倍蓄力速度）
        int remaining = event.getDuration();
        if (remaining > 2) {
            event.setDuration(remaining - 1);
        }
    }

    @SubscribeEvent
    public static void reviveOnDeath(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_LIFE)) return;
        CompoundTag tag = p.getPersistentData();
        long gameTime = p.level().getGameTime();
        if (gameTime < tag.getLong("shard_life_cd")) return;
        e.setCanceled(true);
        p.setHealth(p.getMaxHealth());
        p.removeAllEffects();
        tag.putLong("shard_life_cd", gameTime + (GodfallConfig.SHARD_LIFE_REVIVE_CD_SECONDS.get() * 20L));
    }

    // ==================== 灵韵 SHARD_SPIRIT ====================
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_SPIRIT)) return;
        MobEffectInstance effect = event.getEffectInstance();
        if (effect.getEffect().getCategory() != MobEffectCategory.BENEFICIAL) return;
        try {
            java.lang.reflect.Field durF = MobEffectInstance.class.getDeclaredField("duration");
            durF.setAccessible(true);
            durF.setInt(effect, (int) (durF.getInt(effect) * 2 * getGodCoreBonus(p)));

            java.lang.reflect.Field ampF = MobEffectInstance.class.getDeclaredField("amplifier");
            ampF.setAccessible(true);
            ampF.setInt(effect, (int) ampF.getInt(effect) + 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onPotionDrink(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_SPIRIT)) return;
        if (!(event.getItem().getItem() instanceof PotionItem)) return;
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
    }

    // ==================== 铭纹 SHARD_RUNE ====================
    @SubscribeEvent
    public static void runeArmorReduce(LivingDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!hasBrokenCore(p) || !hasShard(p, ModItems.SHARD_RUNE)) return;
        for (ItemStack armor : p.getInventory().armor) {
            if (!armor.isEmpty()) {
                e.setAmount(e.getAmount() * 0.75F);
                return;
            }
        }
    }

    private static void syncRuneEnchantments(Player p, boolean active) {
        List<ItemStack> allStacks = new ArrayList<>();
        allStacks.addAll(p.getInventory().items);
        allStacks.addAll(p.getInventory().armor);
        allStacks.addAll(p.getInventory().offhand);
        for (ItemStack stack : allStacks) {
            if (stack == null || stack.isEmpty()) continue;
            if (active) boostRuneEnchants(stack);
            else restoreRuneEnchants(stack);
        }
    }

    private static void boostRuneEnchants(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().getBoolean(RUNE_TAG)) return;
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.isEmpty()) return;
        CompoundTag originalTag = new CompoundTag();
        Map<Enchantment, Integer> boosted = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey());
            if (id == null) continue;
            originalTag.putInt(id.toString(), entry.getValue());
            boosted.put(entry.getKey(), Math.min(entry.getValue() + 5, 255));
        }
        EnchantmentHelper.setEnchantments(boosted, stack);
        stack.getOrCreateTag().putBoolean(RUNE_TAG, true);
        stack.getOrCreateTag().put(RUNE_ORIGINAL, originalTag);
    }

    private static void restoreRuneEnchants(ItemStack stack) {
        if (stack.getTag() == null || !stack.getTag().getBoolean(RUNE_TAG)) return;
        CompoundTag originalTag = stack.getTag().getCompound(RUNE_ORIGINAL);
        if (originalTag.isEmpty()) {
            stack.getTag().remove(RUNE_TAG);
            stack.getTag().remove(RUNE_ORIGINAL);
            return;
        }
        Map<Enchantment, Integer> restored = new HashMap<>();
        for (String key : originalTag.getAllKeys()) {
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.parse(key));
            if (enchantment != null) restored.put(enchantment, originalTag.getInt(key));
        }
        EnchantmentHelper.setEnchantments(restored, stack);
        stack.getTag().remove(RUNE_TAG);
        stack.getTag().remove(RUNE_ORIGINAL);
    }
}
