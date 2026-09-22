package com.lazybones.godfalltrinkets.event;
import com.lazybones.godfalltrinkets.item.custom.ArmorItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = "godfall_trinkets", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConquestArmorEventHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 攻击者是玩家才计算暴击、吸血
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        // 1. 分别统计两套穿戴件数，完全独立
        int normalConquestCount = ArmorItems.getConquestPieceCount(player);
        int abyssConquestCount = ArmorItems.getAbyssConquestPieceCount(player);
        float finalDamage = event.getAmount();

        // ========== 普通征伐 会心判定 ==========
        if (normalConquestCount > 0) {
            float critChance = normalConquestCount * 0.06f; // 单件6%，全套24%
            if (player.getRandom().nextFloat() < critChance) {
                finalDamage *= 1.75f;
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        // ========== 深渊征伐 独立更高会心概率 ==========
        if (abyssConquestCount > 0) {
            float critChance = abyssConquestCount * 0.1f; // 单件9%，全套36%
            if (player.getRandom().nextFloat() < critChance) {
                finalDamage *= 2.0f; // 深渊暴击倍率更高
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.2f);
            }
        }

        // ========== 全套吸血效果 两套分开触发，倍率不同 ==========
        if (ArmorItems.isFullConquestSet(player)) {
            float heal = finalDamage * 0.25f;
            player.heal(heal);
        }
        if (ArmorItems.isFullAbyssConquestSet(player)) {
            float heal = finalDamage * 0.35f; // 深渊吸血比例更高
            player.heal(heal);
        }

        event.setAmount(finalDamage);
    }
}