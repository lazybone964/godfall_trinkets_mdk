package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurioRegenBuffEvent {

    // 目标饰品注册ID
    private static final String TARGET_ITEM_ID = "broken_core";
    // 玩家NBT标记：是否已发放本次佩戴的buff
    private static final String TAG_HAS_GIVEN_REGEN = "godfall_given_regen";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在服务端、tick末尾、存活玩家执行
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide() || !event.player.isAlive()) {
            return;
        }
        Player player = event.player;
        CompoundTag playerNbt = player.getPersistentData();

        int regenAmp = GodfallConfig.BROKEN_CORE_REGEN_AMPLIFIER.get();
        int durationTicks = GodfallConfig.BROKEN_CORE_REGEN_DURATION_TICKS.get();

        boolean wearTrinket = false;
        // 遍历Curios槽，检测是否佩戴目标饰品
        var optionalCurioInventory = CuriosApi.getCuriosInventory(player);
        if (optionalCurioInventory.isPresent()) {
            Map<String, ICurioStacksHandler> allSlots = optionalCurioInventory.resolve().get().getCurios();
            for (ICurioStacksHandler handler : allSlots.values()) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStacks().getStackInSlot(i);
                    if (!stack.isEmpty() && ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath().equals(TARGET_ITEM_ID)) {
                        wearTrinket = true;
                        break;
                    }
                }
                if (wearTrinket) break;
            }
        }

        if (wearTrinket) {
            //佩戴状态：无标记才发放buff，不重复刷新时长
            if (!playerNbt.getBoolean(TAG_HAS_GIVEN_REGEN)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        durationTicks,
                        regenAmp,
                        false,  //显示药水粒子
                        true,   //显示图标
                        true    //死亡不清除
                ));
                playerNbt.putBoolean(TAG_HAS_GIVEN_REGEN, true);
            }
        } else {
            //卸下饰品：删除标记、移除【当前配置等级】的再生效果
            playerNbt.remove(TAG_HAS_GIVEN_REGEN);
            MobEffectInstance currentRegen = player.getEffect(MobEffects.REGENERATION);
            if (currentRegen != null && currentRegen.getAmplifier() == regenAmp) {
                player.removeEffect(MobEffects.REGENERATION);
            }
        }
    }
}
