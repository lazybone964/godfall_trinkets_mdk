package com.lazybones.godfalltrinkets.item.custom;

import com.lazybones.godfalltrinkets.config.GodfallConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class StomachPouchItem extends GodfallTrinketItem implements ICurioItem {

    public StomachPouchItem(Properties properties, String slotId, String[] descLines, java.util.function.Consumer<com.google.common.collect.Multimap<net.minecraft.world.entity.ai.attributes.Attribute, net.minecraft.world.entity.ai.attributes.AttributeModifier>> attributeSupplier) {
        super(properties, slotId, descLines, attributeSupplier);
    }

    public static int getStoredHunger(ItemStack stack) {
        return stack.getOrCreateTag().getInt("stored_hunger");
    }
    public static void setStoredHunger(ItemStack stack, int val) {
        stack.getOrCreateTag().putInt("stored_hunger", val);
    }
    public static float getStoredSaturation(ItemStack stack) {
        return stack.getOrCreateTag().getFloat("stored_saturation");
    }
    public static void setStoredSaturation(ItemStack stack, float val) {
        stack.getOrCreateTag().putFloat("stored_saturation", val);
    }

    /** 获取饥饿系数 0~1 */
    public static float getHungerCoeff(ItemStack stack) {
        int max = GodfallConfig.STOMACH_MAX_HUNGER.get();
        if(max <= 0) return 0F;
        return Math.min(1F, (float)getStoredHunger(stack) / max);
    }
    /** 获取饱和度系数 0~1 */
    public static float getSatCoeff(ItemStack stack) {
        float max = GodfallConfig.STOMACH_MAX_SATURATION.get().floatValue();
        if(max <= 0) return 0F;
        return Math.min(1F, getStoredSaturation(stack) / max);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.empty());
        int h = getStoredHunger(stack);
        float s = getStoredSaturation(stack);
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.store", h, String.format("%.1f", s))
                .withStyle(ChatFormatting.GRAY));

        if(net.minecraft.client.gui.screens.Screen.hasShiftDown()){
            float cH = getHungerCoeff(stack);
            float cS = getSatCoeff(stack);

            float dmg = (float)(cH * GodfallConfig.STOMACH_FULL_DMG_BONUS.get() *100F);
            float hp = (float)(cH * GodfallConfig.STOMACH_FULL_MAXHP_BONUS.get() *100F);
            float arm = (float)(cS * GodfallConfig.STOMACH_FULL_ARMOR_BONUS.get() *100F);
            float tough = (float)(cS * GodfallConfig.STOMACH_FULL_TOUGH_BONUS.get() *100F);
            float kr = (float)(cS * GodfallConfig.STOMACH_FULL_KNOCKBACK_RESIST.get() *100F);
            float luck = (float)(cS * GodfallConfig.STOMACH_FULL_LUCK_BONUS.get() *100F);
            float atkSpd = (float)(cS * GodfallConfig.STOMACH_FULL_ATKSPEED_BONUS.get() *100F);

            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_dmg", String.format("%.1f",dmg)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_hp", String.format("%.1f",hp)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_armor", String.format("%.1f",arm)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_tough", String.format("%.1f",tough)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_kr", String.format("%.1f",kr)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_luck", String.format("%.1f",luck)).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.stomach_pouch.bonus_atk_spd", String.format("%.1f",atkSpd)).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().endsWith("body");
    }
    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        //死亡永远保留饰品，不掉落
        return ICurio.DropRule.ALWAYS_KEEP;
    }
}
