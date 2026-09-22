package com.lazybones.godfalltrinkets.item.custom;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GodfallTrinketItem extends Item implements ICurioItem {

    private final String slotId;
    private final String[] descLines;
    private final Consumer<Multimap<Attribute, AttributeModifier>> attributeSupplier;

    public GodfallTrinketItem(Properties properties, String slotId, String[] descLines,
                              Consumer<Multimap<Attribute, AttributeModifier>> attributeSupplier) {
        super(properties);
        this.slotId = slotId;
        this.descLines = descLines;
        this.attributeSupplier = attributeSupplier;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().endsWith(slotId);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        if (attributeSupplier != null) {
            attributeSupplier.accept(map);
        }
        return map;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        // 提示文字：需要穿戴破厄之核才能使用
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.require_core")
                .withStyle(ChatFormatting.RED));
        // 循环每一行翻译，自动分行
        for (String lineKey : descLines) {
            tooltip.add(Component.translatable(lineKey).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        return ICurioItem.super.getSlotsTooltip(tooltips, stack);
    }
}
