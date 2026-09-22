package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.item.custom.BrokenCoreItem;
import com.lazybones.godfalltrinkets.item.custom.GodCoreItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

/** 神格 8 大终局权能事件处理 */
@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GodCoreEvents {

    private static final UUID GOD_ALL_ATTR_UUID = UUID.fromString("f0100001-0000-4000-a000-000000000001");

    /** 检查玩家是否佩戴神格 */
    public static boolean hasGodCore(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> GodCoreItem.isGodCore(s)))
                .isPresent();
    }

    // ==================== 权能1：全属性+35% ====================
    @SubscribeEvent
    public static void applyGodAttributes(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;
        if (p.level().isClientSide()) return;
        applyAllAttr(p, hasGodCore(p));
    }

    private static void applyAllAttr(Player p, boolean active) {
        float bonus = active ? (float) GodfallConfig.GOD_CORE_ALL_ATTR_ADD.get().doubleValue() : 0f;
        applyAttr(p, Attributes.ATTACK_DAMAGE, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.ARMOR, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.MOVEMENT_SPEED, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.ATTACK_SPEED, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.ARMOR_TOUGHNESS, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.KNOCKBACK_RESISTANCE, GOD_ALL_ATTR_UUID, bonus);
        applyAttr(p, Attributes.LUCK, GOD_ALL_ATTR_UUID, bonus);
    }

    private static void applyAttr(Player p, net.minecraft.world.entity.ai.attributes.Attribute attr,
                                   UUID uuid, float bonus) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(uuid);
        if (bonus > 0) {
            inst.addPermanentModifier(new AttributeModifier(uuid, "god_all", bonus,
                    AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    // ==================== 权能3：+20%伤害、-25%受伤 ====================
    @SubscribeEvent
    public static void onGodHurt(LivingHurtEvent event) {
        // 玩家攻击 → 增伤
        if (event.getSource().getEntity() instanceof Player p && hasGodCore(p)) {
            event.setAmount(event.getAmount() * (1.0f + GodfallConfig.GOD_CORE_GLOBAL_DAMAGE_UP.get().floatValue()));
        }
        // 玩家受伤 → 减伤
        if (event.getEntity() instanceof Player p && hasGodCore(p)) {
            event.setAmount(event.getAmount() * (1.0f - GodfallConfig.GOD_CORE_DAMAGE_REDUCE.get().floatValue()));
        }
    }

    // ==================== 权能4：全类型伤害免疫 ====================
    @SubscribeEvent
    public static void onGodImmune(LivingAttackEvent event) {
        if (!GodfallConfig.GOD_IMMUNE_ALL_ENV_DAMAGE.get()) return;
        if (!(event.getEntity() instanceof Player p) || !hasGodCore(p)) return;
        DamageSource src = event.getSource();
        if (src.is(DamageTypes.FALL) || src.is(DamageTypes.IN_FIRE)
                || src.is(DamageTypes.ON_FIRE) || src.is(DamageTypes.LAVA)
                || src.is(DamageTypes.CACTUS) || src.is(DamageTypes.DROWN)
                || src.is(DamageTypes.LIGHTNING_BOLT) || src.is(DamageTypes.EXPLOSION)
                || src.is(DamageTypes.PLAYER_EXPLOSION) || src.is(DamageTypes.FALLING_ANVIL)
                || src.is(DamageTypes.FLY_INTO_WALL) || src.is(DamageTypes.FREEZE)
                || src.is(DamageTypes.IN_WALL) || src.is(DamageTypes.MAGIC)
                || src.is(DamageTypes.INDIRECT_MAGIC) || src.is(DamageTypes.WITHER)
                || src.is(DamageTypes.HOT_FLOOR) || src.is(DamageTypes.SWEET_BERRY_BUSH)
                || src.is(DamageTypes.STALAGMITE) || src.is(DamageTypes.STARVE)) {
            event.setCanceled(true);
        }
    }

    // ==================== 权能5：单次伤害上限 ====================
    @SubscribeEvent
    public static void onGodDmgCap(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player p) || !hasGodCore(p)) return;
        float cap = p.getMaxHealth() * GodfallConfig.MAX_SINGLE_DAMAGE_RATIO.get().floatValue();
        if (event.getAmount() > cap) event.setAmount(cap);
    }

    // ==================== 权能6：无敌帧延长 ====================
    @SubscribeEvent
    public static void onGodInvuln(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player p) || !hasGodCore(p)) return;
        int invulnTick = GodfallConfig.INVULNERABLE_TICK.get();
        if (p.invulnerableTime > 0 && p.invulnerableTime < invulnTick) {
            p.invulnerableTime = invulnTick;
        }
    }

    // ==================== 权能7：创造飞行移速、飞行增伤 ====================
    @SubscribeEvent
    public static void onGodFlight(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;
        if (!hasGodCore(p)) return;
        float flySpeed = GodfallConfig.CREATIVE_FLY_SPEED_MOD.get().floatValue();
        if (p.getAbilities().flying) {
            if (!p.level().isClientSide() && p.getAbilities().getFlyingSpeed() < flySpeed) {
                p.getAbilities().setFlyingSpeed(flySpeed);
                if (p instanceof ServerPlayer sp) sp.onUpdateAbilities();
            }
        }
    }

    // ==================== 权能8：无视护甲/韧性/抗性/Boss限伤 ====================
    @SubscribeEvent
    public static void onGodPiercing(LivingHurtEvent event) {
        if (!GodfallConfig.ENABLE_ARMOR_PIERCE.get()) return;
        if (!(event.getSource().getEntity() instanceof Player p) || !hasGodCore(p)) return;
        LivingEntity target = event.getEntity();

        // 清空护甲/韧性
        AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
        if (armor != null) armor.setBaseValue(0);
        AttributeInstance tough = target.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (tough != null) tough.setBaseValue(0);

        // 清除正面药水效果（抗性、防火等）
        for (MobEffectInstance eff : target.getActiveEffects()) {
            if (eff.getEffect().isBeneficial() && eff.getEffect() != MobEffects.REGENERATION
                    && eff.getEffect() != MobEffects.HEAL) {
                target.removeEffect(eff.getEffect());
            }
        }
    }

    // ==================== 破厄之核 & 进化石自动发放 ====================
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player p = event.getEntity();
        if (p.level().isClientSide()) return;
        tryGiveBrokenCore(p);
        tryGrantEvolutionStone(p);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;
        if (p.level().isClientSide()) return;
        // 每5秒兜底检查一次（覆盖某些登录事件未触发的情况）
        if (p.tickCount % 100 == 0) {
            tryGiveBrokenCore(p);
            tryGrantEvolutionStone(p);
        }
    }

    /** 新玩家自动发放破厄之核 */
    private static void tryGiveBrokenCore(Player p) {
        if (!GodfallConfig.AUTO_GIVE_BROKEN_CORE.get()) return;
        if (p.getPersistentData().getBoolean("godfall_got_broken_core")) return;

        // 已有破厄之核/神格则不再发放
        boolean alreadyHas = CuriosApi.getCuriosInventory(p).resolve()
                .map(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get()) || s.is(ModItems.GOD_CORE.get())))
                .map(java.util.Optional::isPresent)
                .orElse(false);
        if (alreadyHas) {
            p.getPersistentData().putBoolean("godfall_got_broken_core", true);
            return;
        }

        p.getPersistentData().putBoolean("godfall_got_broken_core", true);
        p.getInventory().add(new ItemStack(ModItems.BROKEN_CORE.get()));
    }

    private static void tryGrantEvolutionStone(Player p) {
        if (!GodfallConfig.AUTO_GOD_UPGRADE_STONE.get()) return;
        if (GodfallConfig.ENABLE_GOD_CORE_EVOLUTION.get() && GodCoreEvents.hasGodCore(p)) return;
        if (p.getPersistentData().getBoolean("godfall_got_evolution_stone")) return;

        // 检查是否佩戴破厄之核且境界>=9
        boolean hasCore = CuriosApi.getCuriosInventory(p).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get())
                        && !GodCoreItem.isGodCore(s)))
                .isPresent();
        if (!hasCore) return;

        // 确认境界 >= 9
        var coreResult = CuriosApi.getCuriosInventory(p).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get())));
        if (coreResult.isEmpty()) return;
        ItemStack core = coreResult.get().stack();
        if (core.getItem() instanceof BrokenCoreItem bci && bci.getTier(core) < 9) return;

        // 检查13个碎片槽是否各有1枚不同碎片
        var handler = CuriosApi.getCuriosInventory(p).resolve();
        if (handler.isEmpty()) return;
        var shardSlot = handler.get().getStacksHandler("god_shard_slot");
        if (shardSlot.isEmpty()) return;
        var stacks = shardSlot.get().getStacks();
        int count = 0;
        for (int i = 0; i < stacks.getSlots(); i++) {
            ItemStack s = stacks.getStackInSlot(i);
            if (!s.isEmpty()) {
                // 去重检查：收集到的碎片ID
                count++;
            }
        }
        if (count < 13) return;

        // 发放进化石
        p.getPersistentData().putBoolean("godfall_got_evolution_stone", true);
        ItemStack stone = new ItemStack(ModItems.GOD_EVOLUTION_STONE.get());
        p.getInventory().add(stone);
    }
}
