package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StomachPouchAdvancementReward {

    //原版均衡饮食进度ID：尝遍全部食物
    private static final ResourceLocation BALANCED_DIET =ResourceLocation.fromNamespaceAndPath("minecraft","husbandry/balanced_diet");
    //玩家NBT标记，已经领取过大胃袋
    private static final String TAG_GOT_STOMACH_POUCH = "godfall_got_stomach_pouch";

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        //不是均衡饮食直接返回
        if(!BALANCED_DIET.equals(event.getAdvancement().getId())){
            return;
        }
        CompoundTag persistent = player.getPersistentData();
        //已经领过，不再重复给
        if(persistent.getBoolean(TAG_GOT_STOMACH_POUCH)){
            return;
        }

        //给大胃袋
        ItemStack pouch = new ItemStack(ModItems.STOMACH_POUCH.get());
        player.addItem(pouch);
        //标记已领取，永久保存在玩家persistentData，跨存档重启有效
        persistent.putBoolean(TAG_GOT_STOMACH_POUCH, true);
    }
}
