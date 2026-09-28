package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShockAmuletSlowEvent {
    // 30秒 = 30 * 20 tick
    private static final int SLOW_DURATION = 30 * 20;
    // 缓慢I 放大器0
    private static final int SLOW_AMP = 0;

    @SubscribeEvent
    public static void onPlayerMeleeHit(LivingAttackEvent event) {
        // 1. 攻击者不是玩家，直接跳过
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        // 客户端不处理逻辑
        if (player.level().isClientSide()) return;
        // 3. 判断玩家是否佩戴震斥护符
        if (!hasShockAmulet(player)) return;

        // 4. 给被击目标施加30秒缓慢
        event.getEntity().addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SLOW_DURATION,
                SLOW_AMP,
                false, // 显示粒子
                true,  // 显示buff图标
                true
        ));
    }

    /** 检测是否佩戴震斥护符（用 findFirstCurio 直接匹配物品，不再字符串比较） */
    private static boolean hasShockAmulet(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() == ModItems.SHOCK_AMULET.get()))
                .isPresent();
    }
}
