package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** 系列剑武器事件：铁砧锻造、Tooltip、增伤、吸血 */
@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SwordForgeEvents {

    private record TierData(RegistryObject<Item> sword, Supplier<Item> mat) {}

    private static final TierData[] TIER_LIST = new TierData[]{
            new TierData(ModItems.WHITE_SWORD_BLANK,   () -> Items.IRON_INGOT),
            new TierData(ModItems.IRON_WHITE_SWORD,    () -> Items.DIAMOND),
            new TierData(ModItems.DIAMOND_WHITE_BLADE, ModItems.GODFRAGMENT),
            new TierData(ModItems.SHADOW_SWORD,        ModItems.GOD_INGOT),
            new TierData(ModItems.CORE_CONQUEST_SWORD, ModItems.CONQUEST_INGOT),
            new TierData(ModItems.WAR_BLADE,           ModItems.WITHER_SHARD),
            new TierData(ModItems.WITHER_BLADE,        ModItems.WARDEN_HEART),
            new TierData(ModItems.WARDEN_SWORD,        ModItems.ABYSS_GOD_INGOT),
            new TierData(ModItems.ABYSS_SWORD,         ModItems.DRAGON_SOUL)
    };

    private static TierData getTierByStack(ItemStack stack) {
        for (TierData td : TIER_LIST) {
            if (stack.is(td.sword().get())) return td;
        }
        return null;
    }

    // ==================== 铁砧锻造进度+1 ====================
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left  = event.getLeft();
        ItemStack right = event.getRight();
        TierData tier = getTierByStack(left);
        if (tier == null) return;
        if (!right.is(tier.mat().get())) return;
        CompoundTag tag = left.getOrCreateTag();
        int prog = tag.getInt("forge_progress");
        int maxProg = GodfallConfig.SWORD_FORGE_MAX_PROGRESS.get();

        // 跨阶NBT泄漏检测
        String currentId = ForgeRegistries.ITEMS.getKey(left.getItem()).toString();
        String lastType  = tag.getString("forge_last_type");
        if (prog >= maxProg && !currentId.equals(lastType) && !lastType.isEmpty()) {
            prog = 0;
            tag.putInt("forge_progress", 0);
        }
        if (prog >= maxProg) return;

        ItemStack out = left.copy();
        out.getOrCreateTag().putInt("forge_progress", prog + 1);
        out.getOrCreateTag().putString("forge_last_type", currentId);
        event.setOutput(out);
        event.setMaterialCost(1);
        event.setCost(1);
    }

    // ==================== 武器 Tooltip ====================
    @SubscribeEvent
    public static void renderItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        TierData tier = getTierByStack(stack);
        if (tier == null) return;
        CompoundTag tag = stack.getOrCreateTag();
        int prog = tag.getInt("forge_progress");
        int maxProg = GodfallConfig.SWORD_FORGE_MAX_PROGRESS.get();
        event.getToolTip().add(Component.translatable("tooltip.sword_forge_progress", prog));
        if (prog < maxProg) {
            event.getToolTip().add(Component.translatable("tooltip.sword_locked")
                    .withStyle(net.minecraft.ChatFormatting.GRAY));
        } else {
            event.getToolTip().add(Component.translatable("tooltip.sword_ready")
                    .withStyle(net.minecraft.ChatFormatting.GREEN));
        }
        String tipKey = switch (tier.sword().getId().getPath()) {
            case "white_sword_blank"   -> "tooltip.sword.t0";
            case "iron_white_sword"    -> "tooltip.sword.t1";
            case "diamond_white_blade" -> "tooltip.sword.t2";
            case "shadow_sword"        -> "tooltip.sword.t3";
            case "core_conquest_sword" -> "tooltip.sword.t4";
            case "war_blade"           -> "tooltip.sword.t5";
            case "wither_blade"        -> "tooltip.sword.t6";
            case "warden_sword"        -> "tooltip.sword.t7";
            case "abyss_sword"         -> "tooltip.sword.t8";
            case "end_dragon_sword"    -> "tooltip.sword.t9_1";
            default                    -> "";
        };
        if (!tipKey.isBlank())
            event.getToolTip().add(Component.translatable(tipKey).withStyle(net.minecraft.ChatFormatting.AQUA));
        if (stack.is(ModItems.END_DRAGON_SWORD.get())) {
            event.getToolTip().add(Component.translatable("tooltip.sword.t9_1"));
            event.getToolTip().add(Component.translatable("tooltip.sword.t9_2"));
        }
    }

    // ==================== 新玩家赠送胚子 ====================
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player p = event.getEntity();
        CompoundTag persist = p.getPersistentData();
        if (persist.getBoolean("given_white_sword")) return;
        ItemStack blank = new ItemStack(ModItems.WHITE_SWORD_BLANK.get());
        blank.getOrCreateTag().putInt("forge_progress", 0);
        p.getInventory().add(blank);
        persist.putBoolean("given_white_sword", true);
        p.sendSystemMessage(Component.literal("§b获得纯白剑胚，在铁砧使用对应材料积累锻造进度！"));
    }

    // ==================== 系列剑增伤 ====================
    @SubscribeEvent
    public static void onSwordHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player p)) return;
        ItemStack hand = p.getMainHandItem();
        if (getTierByStack(hand) == null) return;
        LivingEntity target = event.getEntity();
        float mult = 1.0F;

        boolean isT1 = hand.is(ModItems.IRON_WHITE_SWORD.get());
        boolean isT2 = hand.is(ModItems.DIAMOND_WHITE_BLADE.get());
        boolean isT3 = hand.is(ModItems.SHADOW_SWORD.get());
        boolean isT4 = hand.is(ModItems.CORE_CONQUEST_SWORD.get());
        boolean isT5 = hand.is(ModItems.WAR_BLADE.get());
        boolean isT6 = hand.is(ModItems.WITHER_BLADE.get());
        boolean isT7 = hand.is(ModItems.WARDEN_SWORD.get());
        boolean isT8 = hand.is(ModItems.ABYSS_SWORD.get());
        boolean isT9 = hand.is(ModItems.END_DRAGON_SWORD.get());

        // 亡灵增伤
        if (target instanceof Zombie || target instanceof Skeleton || target instanceof ZombifiedPiglin) {
            if (isT1) mult *= 1.25F;
            if (isT2 || isT3 || isT4 || isT5 || isT6 || isT7 || isT8 || isT9)
                mult *= GodfallConfig.SWORD_UNDEAD_DMG.get().floatValue();
        }
        // 下界增伤
        if (target instanceof Piglin || target instanceof Blaze || target instanceof Ghast) {
            if (isT3 || isT4 || isT5 || isT6 || isT7 || isT8 || isT9)
                mult *= GodfallConfig.SWORD_NETHER_DMG.get().floatValue();
        }
        // 小怪增伤
        if (!(target instanceof EnderDragon) && !(target instanceof WitherBoss) && !(target instanceof Warden)) {
            if (isT4) mult *= 1.20F;
            if (isT5 || isT6 || isT7 || isT8 || isT9)
                mult *= GodfallConfig.SWORD_MOB_DMG.get().floatValue();
        }
        // 凋零类增伤+效果
        if (target instanceof WitherBoss || target instanceof WitherSkeleton) {
            if (isT6 || isT7 || isT8 || isT9) {
                mult *= GodfallConfig.SWORD_WITHER_DMG.get().floatValue();
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
            }
        }
        // 黑暗增伤
        if (p.level().getMaxLocalRawBrightness(p.blockPosition()) < 4) {
            if (isT7 || isT8 || isT9)
                mult *= GodfallConfig.SWORD_DARK_DMG.get().floatValue();
        }
        // Boss增伤
        if (target instanceof EnderDragon || target instanceof WitherBoss || target instanceof Warden) {
            if (isT8)
                mult *= GodfallConfig.SWORD_BOSS_DMG.get().floatValue();
        }
        // T9 终末龙征
        if (isT9) {
            if (target instanceof EnderDragon || target instanceof WitherBoss
                    || target instanceof Warden || target instanceof EnderMan || target instanceof Shulker) {
                mult *= GodfallConfig.SWORD_T9_BOSS_DMG.get().floatValue();
            }
            if (GodfallConfig.ENABLE_ELYTRA_BONUS.get() && p.isFallFlying())
                mult *= GodfallConfig.SWORD_T9_ELYTRA_DMG.get().floatValue();
        }

        event.setAmount(event.getAmount() * mult * GodfallConfig.SWORD_GLOBAL_DAMAGE.get().floatValue());
    }

    // ==================== 吸血 ====================
    @SubscribeEvent
    public static void swordLifesteal(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player p)) return;
        ItemStack hand = p.getMainHandItem();
        float mod = GodfallConfig.SWORD_LIFESTEAL_MOD.get().floatValue();
        float heal = 0F;
        if (hand.is(ModItems.WAR_BLADE.get()) || hand.is(ModItems.WITHER_BLADE.get())
                || hand.is(ModItems.WARDEN_SWORD.get()) || hand.is(ModItems.ABYSS_SWORD.get()))
            heal = 1.2F * mod;
        if (hand.is(ModItems.END_DRAGON_SWORD.get())) heal = 2.0F * mod;
        if (heal > 0) p.heal(heal);
    }
}
