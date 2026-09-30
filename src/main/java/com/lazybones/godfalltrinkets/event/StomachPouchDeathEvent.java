package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.custom.StomachPouchItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StomachPouchDeathEvent {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        if(player.level().isClientSide()) return;
        double drainRatio = GodfallConfig.STOMACH_DEATH_DRAIN_RATIO.get();
        if(drainRatio <= 0D) return;

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var opt = handler.findFirstCurio(s -> s.getItem() instanceof StomachPouchItem);
            if(opt.isEmpty()) return;
            ItemStack pouch = opt.get().stack();

            int currH = StomachPouchItem.getStoredHunger(pouch);
            float currS = StomachPouchItem.getStoredSaturation(pouch);

            int remainH = (int) Math.floor(currH * (1D - drainRatio));
            float remainS = (float) (currS * (1D - drainRatio));

            StomachPouchItem.setStoredHunger(pouch, Math.max(0, remainH));
            StomachPouchItem.setStoredSaturation(pouch, Math.max(0F, remainS));
        });
    }
}
