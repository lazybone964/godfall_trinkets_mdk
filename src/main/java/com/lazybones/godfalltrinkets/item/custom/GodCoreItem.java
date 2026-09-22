package com.lazybones.godfalltrinkets.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/** 神格——破厄之核的终局进化形态。继承全部境界/诅咒/祝福，额外解锁 8 条神格权能。 */
public class GodCoreItem extends BrokenCoreItem {

    private static final String TAG_IS_GOD = "is_god_core";

    public GodCoreItem(Properties properties) {
        super(properties);
    }

    /** 标记此物品为神格 */
    public static void markAsGodCore(ItemStack stack) {
        stack.getOrCreateTag().putBoolean(TAG_IS_GOD, true);
    }

    /** 判定是否为神格 */
    public static boolean isGodCore(ItemStack stack) {
        return stack.getItem() instanceof GodCoreItem && stack.getOrCreateTag().getBoolean(TAG_IS_GOD);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (Screen.hasControlDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.godcore_header")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            for (int i = 1; i <= 8; i++) {
                tooltip.add(Component.translatable("tooltip.godfall_trinkets.godcore." + i)
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
