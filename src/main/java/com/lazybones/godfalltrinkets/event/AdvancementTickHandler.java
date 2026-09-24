package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.util.AdvancementHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID)
public class AdvancementTickHandler {

    @SubscribeEvent
    public static void onServerPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;

        LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(sp);

        // --------① 检测Curios槽内的破厄之核（替换弃用findEquippedCurio）--------
        curiosOpt.ifPresent(curioItemHandler -> {
            Map<String, ICurioStacksHandler> slotMap = curioItemHandler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()) {
                IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() == ModItems.BROKEN_CORE.get()) {
                        AdvancementHelper.checkBrokenCoreTierAdvancements(stack, sp);
                    }
                }
            }
        });

        // --------② 检测碎片集齐成就（背包+Curios）--------
        AdvancementHelper.checkShardAdvancements(sp);

        // --------③ 检测长剑锻刃前行成就 T3及以上 --------
        // 普通背包
        for (ItemStack stack : sp.getInventory().items) {
            checkMidSword(stack, sp);
        }
        // Curios全部槽遍历
        curiosOpt.ifPresent(curioItemHandler -> {
            Map<String, ICurioStacksHandler> slotMap = curioItemHandler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()) {
                IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    checkMidSword(stack, sp);
                }
            }
        });
        //检测神格觉醒成就（背包 / Curios任意槽位存在神格就解锁）
        AdvancementHelper.checkGodCoreAdvancement(sp);
    }

    /** 辅助：判断长剑是否达到T3，满足解锁【锻刃前行】 */
    private static void checkMidSword(ItemStack stack, ServerPlayer sp) {
        if (stack.isEmpty()) return;
        var tag = stack.getTag();
        if (tag == null || !tag.contains("sword_tier")) return;
        int tier = tag.getInt("sword_tier");
        if (tier >= 3) {
            AdvancementHelper.grant(sp, AdvancementHelper.ADV_SWORD_MID_FORGE);
        }
    }
}
