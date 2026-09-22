package com.lazybones.godfalltrinkets.item.custom;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BrokenCoreItem extends Item implements ICurioItem {
    private static final UUID DAMAGE_UUID    = UUID.fromString("ab12c3d4-5678-4e5f-8a9b-cdef01234567");
    private static final UUID HEALTH_UUID    = UUID.fromString("cd56ef78-1234-4e5f-8a9b-cdef01234567");
    private static final UUID ARMOR_UUID     = UUID.fromString("ef90abcd-1234-4e5f-8a9b-cdef01234567");
    private static final UUID SPEED_UUID     = UUID.fromString("5a12bc34-5678-4e5f-8a9b-cdef01234567");
    private static final UUID ATKSPEED_UUID  = UUID.fromString("6b23cd45-6789-5e6f-9a0b-def123456789");
    private static final UUID TOUGH_UUID     = UUID.fromString("7c34de56-7890-6f7a-0b1c-ef2345678901");
    private static final UUID KB_RESIST_UUID = UUID.fromString("8d45ef67-8901-7a8b-1c2d-f34567890123");
    private static final UUID LUCK_UUID      = UUID.fromString("9e56ab78-9012-8b9c-2d3e-456789012345");
    private static final UUID SHARD_SLOT_UUID = UUID.fromString("a0100001-0000-4000-a000-000000000001");
    private static final UUID AMULET_SLOT_UUID = UUID.fromString("a0100002-0000-4000-a000-000000000002");

    public static final int[] EXP_THRESHOLDS = {0, 300, 800, 1800, 3500, 6000, 10000, 16000, 25000, 40000};
    public static final String[] TIER_NAMES = {"凡骨","染咒","噬魂","堕渊","寂灭","咒骨","渊主","堕神","灾厄","神陨"};

    public static final float[] ALL_ATTRIBUTE_BONUS = {
            0.05f, 0.10f, 0.16f, 0.23f, 0.31f, 0.40f, 0.50f, 0.61f, 0.73f, 0.85f
    };

    public static final float[][] SOUL_DRAIN = {
            {1.5f, 0.5f,  0}, {2.5f, 0.8f, 0}, {4f, 1.2f, 0}, {5.5f, 1.6f, 0},
            {7f, 2f, 0}, {9f, 2.5f, 0}, {11f, 3f, 1}, {13f, 3.5f, 2},
            {16f, 4f, 3}, {20f, 5f, 5}
    };

    public static final float[] DODGE_CHANCE = {0.05f, 0.08f, 0.12f, 0.16f, 0.21f, 0.26f, 0.31f, 0.36f, 0.41f, 0.50f};
    public static final float[] DODGE_REFLECT = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.10f, 0.15f, 0.25f};

    public static final float[] SHIELD_MAX  = {3, 5, 8, 12, 16, 21, 27, 34, 42, 50};
    public static final int[]   SHIELD_CD   = {25, 23, 21, 19, 18, 17, 16, 15, 14, 12};

    public static final float[] CALAMITY_RATE  = {0.015f,0.02f,0.03f,0.04f,0.05f,0.06f,0.07f,0.08f,0.09f,0.10f};
    public static final float[] CALAMITY_CAP   = {0.08f,0.15f,0.25f,0.35f,0.48f,0.60f,0.75f,0.90f,1.10f,1.30f};

    private static final double CURSE_DAMAGE       = -0.3;
    private static final double CURSE_HEALTH       = -4.0;
    private static final double CURSE_DEEP_WOUND   = 0.2;
    private static final double CURSE_SCARRED      = 0.3;

    public BrokenCoreItem(Properties properties) { super(properties); }

    public static int[] getExpThresholds() {
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) arr[i] = GodfallConfig.TIER_EXP[i].get();
        return arr;
    }

    public int getTier(ItemStack stack) {
        int exp = stack.getOrCreateTag().getInt("core_exp");
        int[] thresholds = getExpThresholds();
        for (int i = thresholds.length - 1; i >= 0; i--) {
            if (exp >= thresholds[i]) return i;
        }
        return 0;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        int tier = getTier(stack);
        int reverseTier = GodfallConfig.REVERSE_CURSE_TIER.get();
        boolean rev = tier >= reverseTier;
        float b = ALL_ATTRIBUTE_BONUS[tier];

        double curseAtk = rev ? +0.30 : -0.30;
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_UUID, "破厄攻击",
                curseAtk, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.ARMOR, new AttributeModifier(ARMOR_UUID, "破厄护甲",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "破厄速度",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATKSPEED_UUID, "破厄攻速",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(TOUGH_UUID, "破厄韧性",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(KB_RESIST_UUID, "破厄抗击退",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        map.put(Attributes.LUCK, new AttributeModifier(LUCK_UUID, "破厄幸运",
                b, AttributeModifier.Operation.MULTIPLY_BASE));

        double curseHp = rev ? +4.0 : -4.0;
        double hp = curseHp + 0.5 * tier;
        map.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_UUID, "破厄生命",
                hp, AttributeModifier.Operation.ADDITION));

        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide) return;
        if (!(slotContext.entity() instanceof Player player)) return;

        int tier = getTier(stack);
        boolean rev = tier >= GodfallConfig.REVERSE_CURSE_TIER.get();

        if (GodfallConfig.ENABLE_CURSE_SYSTEM.get() && player.tickCount % 20 == 0) {
            if (rev) { player.heal(0.6f); }
            else if (!CurseTransferTrinketItem.isAbsorbingCurse(player, "agony")) {
                player.hurt(player.damageSources().magic(), 0.5f);
            }
        }

        if (GodfallConfig.ENABLE_ABYSS_VISION.get()) applyAbyssVision(player, tier);
        tickShield(stack, tier);
    }

    private void tickShield(ItemStack stack, int tier) {
        CompoundTag tag = stack.getOrCreateTag();
        int cd = tag.getInt("shield_cd");
        float max = GodfallConfig.SHIELD_MAX_BASE.get() + SHIELD_MAX[tier];
        int cooldownTicks = GodfallConfig.SHIELD_COOLDOWN_TICK.get();
        if (cd > 0) {
            tag.putInt("shield_cd", cd - 1);
        } else {
            tag.putFloat("shield_hp", max);
            tag.putInt("shield_cd", cooldownTicks * 20);
        }
    }

    public float absorbDamage(ItemStack stack, float incoming, int tier) {
        CompoundTag tag = stack.getOrCreateTag();
        float shield = tag.getFloat("shield_hp");
        if (shield <= 0) return incoming;
        float absorbed = Math.min(shield, incoming);
        tag.putFloat("shield_hp", shield - absorbed);
        return incoming - absorbed;
    }

    public boolean isShieldActive(ItemStack stack) { return stack.getOrCreateTag().getFloat("shield_hp") > 0; }

    private void applyAbyssVision(Player player, int tier) {
        if (tier < 1) return;
        int dur = 400;
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, dur, 0, false, false, true));
        if (tier >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, dur, 0, false, false, true));
            if (player.isUnderWater())
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, dur, 0, false, false, true));
        }
        if (tier >= 3 && player.tickCount % 40 == 0) {
            int range = tier >= 5 ? 15 : 10;
            AABB box = new AABB(player.blockPosition()).inflate(range);
            for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Mob && e.isAlive() && !e.isAlliedTo(player)))
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0, false, false));
        }
        if (tier >= 8 && player.tickCount % 30 == 0) {
            int amp = tier >= 9 ? 1 : 0;
            AABB box = new AABB(player.blockPosition()).inflate(20);
            for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e instanceof Mob && e.isAlive() && !e.isAlliedTo(player)))
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, amp, false, false));
        }
    }

    public static double getDeepWoundMultiplier(int tier)   { boolean rev = tier >= GodfallConfig.REVERSE_CURSE_TIER.get(); return rev ? 1.0 - CURSE_DEEP_WOUND   : 1.0 + CURSE_DEEP_WOUND; }
    public static double getScarredRatio(int tier)           { boolean rev = tier >= GodfallConfig.REVERSE_CURSE_TIER.get(); return rev ? 0 : CURSE_SCARRED; }
    public static boolean isReverse(int tier)                { return tier >= GodfallConfig.REVERSE_CURSE_TIER.get(); }
    public static boolean hasCalamityDR(int tier)            { return tier >= 5; }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack currentStack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            handler.addTransientSlotModifier("god_shard_slot", SHARD_SLOT_UUID,
                    "BrokenCore_shard_slots", 13, AttributeModifier.Operation.ADDITION);
            handler.addTransientSlotModifier("charm", AMULET_SLOT_UUID,
                    "BrokenCore_amulet_slots", 3, AttributeModifier.Operation.ADDITION);
        });
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack currentStack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            handler.removeSlotModifier("god_shard_slot", SHARD_SLOT_UUID);
            handler.removeSlotModifier("charm", AMULET_SLOT_UUID);
        });
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) { return tooltips; }

    public void addExp(ItemStack stack, int amount, Player player) {
        int oldTier = getTier(stack);
        CompoundTag tag = stack.getOrCreateTag();
        int scaledAmount = (int) (amount * GodfallConfig.CORE_EXP_MULTIPLIER.get());
        if (scaledAmount <= 0) return;
        int[] thresholds = getExpThresholds();
        int newExp = tag.getInt("core_exp") + scaledAmount;
        tag.putInt("core_exp", Math.min(newExp, thresholds[9]));
        int newTier = getTier(stack);
        if (newTier > oldTier) {
            if (GodfallConfig.ENABLE_BREAKTHROUGH_NOTICE.get())
                player.sendSystemMessage(Component.translatable("message.godfall_trinkets.breakthrough", TIER_NAMES[newTier])
                        .withStyle(ChatFormatting.DARK_PURPLE));
            if (GodfallConfig.ENABLE_BREAKTHROUGH_SOUND.get())
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        int tier = getTier(stack);
        int exp = stack.getOrCreateTag().getInt("core_exp");
        int[] thresholds = getExpThresholds();
        int nextExp = tier < 9 ? thresholds[tier + 1] : exp;
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.tier", TIER_NAMES[tier]).withStyle(ChatFormatting.GOLD));
        if (tier < 9)
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.exp", exp, nextExp).withStyle(ChatFormatting.GRAY));
        if (Screen.hasShiftDown()) {
            boolean rev = tier >= GodfallConfig.REVERSE_CURSE_TIER.get();
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.curses_header").withStyle(rev ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
            addCurseLines(tooltip, rev, tier);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.blessings_header").withStyle(ChatFormatting.DARK_PURPLE));
            addBlessingLines(tooltip, tier);
        } else if (!Screen.hasControlDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            if (stack.getItem() instanceof GodCoreItem) {
                tooltip.add(Component.translatable("tooltip.godfall_trinkets.hold_ctrl").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    private void addCurseLines(List<Component> tooltip, boolean rev, int tier) {
        String p="tooltip.godfall_trinkets.curse.", s=rev?"_reversed":"";
        for (int i=1;i<=8;i++) tooltip.add(Component.translatable(p+i+s).withStyle(ChatFormatting.RED));
    }
    private void addBlessingLines(List<Component> tooltip, int tier) {
        String p="tooltip.godfall_trinkets.blessing.";
        float b=ALL_ATTRIBUTE_BONUS[tier]*100;
        tooltip.add(Component.translatable(p+"1",String.format("%.0f",b)).withStyle(ChatFormatting.LIGHT_PURPLE));
        for (int i=2;i<=8;i++) tooltip.add(Component.translatable(p+i,tier).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override public List<Component> getSlotsTooltip(List<Component> tips, ItemStack stack) {
        tips.add(Component.translatable("curios.identifier.broken_core_slot").withStyle(ChatFormatting.DARK_GREEN));
        return tips;
    }

    @Override public boolean canEquip(SlotContext sc, ItemStack stack) {
        return sc.identifier().equals("broken_core_slot") || sc.identifier().endsWith(":broken_core_slot");
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            return player.isCreative();
        }
        return false;
    }
}
