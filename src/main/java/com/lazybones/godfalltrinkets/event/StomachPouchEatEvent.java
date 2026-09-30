package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.custom.StomachPouchItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StomachPouchEatEvent {

    @SubscribeEvent
    public static void onPlayerEatFinish(LivingEntityUseItemEvent.Finish event) {
        // 只处理玩家、只服务端
        if (!(event.getEntity() instanceof Player player)) return;
        if(player.level().isClientSide()) return;

        ItemStack foodStack = event.getItem();
        FoodProperties food = foodStack.getFoodProperties(player);
        if(food == null) return; //不是食物直接跳过

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var opt = handler.findFirstCurio(s -> s.getItem() instanceof StomachPouchItem);
            if(opt.isEmpty()) return;

            var res = opt.get();
            ItemStack pouch = res.stack();

            int addHunger = food.getNutrition();
            float addSat = food.getSaturationModifier() * food.getNutrition();

            int currH = StomachPouchItem.getStoredHunger(pouch);
            float currS = StomachPouchItem.getStoredSaturation(pouch);

            int maxH = GodfallConfig.STOMACH_MAX_HUNGER.get();
            float maxS = GodfallConfig.STOMACH_MAX_SATURATION.get().floatValue();

            //未达到上限，存入胃囊，扣除玩家本次进食获得的饥饿饱和度
            if(currH < maxH || currS < maxS){
                int newH = Math.min(maxH, currH + addHunger);
                float newS = Math.min(maxS, currS + addSat);

                StomachPouchItem.setStoredHunger(pouch, newH);
                StomachPouchItem.setStoredSaturation(pouch, newS);

                // 抵消本次食物给玩家增加的饥饿与饱和度
                player.getFoodData().eat(-addHunger, -addSat);
            }
        });
    }
}
