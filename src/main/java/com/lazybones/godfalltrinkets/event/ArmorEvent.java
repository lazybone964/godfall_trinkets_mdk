package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.custom.ArmorItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorEvent {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 神陨套效果
        if (ArmorItems.isFullGodSet(player)) {
            ArmorItems.applyGodSetEffects(player);
        }
        // 征伐套效果
        if (ArmorItems.isFullConquestSet(player)) {
            ArmorItems.applyConquestSetEffects(player);
        }
        // 深渊征伐套（优先级高于普通征伐）
        if (ArmorItems.isFullAbyssConquestSet(player)) {
            ArmorItems.applyAbyssConquestSetEffects(player);
        }
    }
}
