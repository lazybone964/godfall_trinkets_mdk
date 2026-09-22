package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShockAmuletSlowEvent {
    // 震斥护符物品ID
    private static final String SHOCK_AMULET_ID = "shock_amulet";
    // 30秒 = 30 * 20 tick
    private static final int SLOW_DURATION = 30 * 20;
    // 缓慢I 放大器0
    private static final int SLOW_AMP = 0;

    @SubscribeEvent
    public void onPlayerMeleeHit(LivingAttackEvent event) {
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

    /** 遍历Curios槽检测是否佩戴震斥护符 */
    private boolean hasShockAmulet(Player player) {
        var opt = CuriosApi.getCuriosInventory(player);
        if (!opt.isPresent()) return false;
        Map<String, ICurioStacksHandler> allSlots = opt.resolve().get().getCurios();
        for (ICurioStacksHandler handler : allSlots.values()) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStacks().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();
                    if (SHOCK_AMULET_ID.equals(itemId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}