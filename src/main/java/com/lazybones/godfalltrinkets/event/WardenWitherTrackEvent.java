package com.lazybones.godfalltrinkets.event;

import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.lazybones.godfalltrinkets.GodfallTrinkets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WardenWitherTrackEvent {
    private static final String TAG_WITHER_TOUCH_GROUND = "wither_touch_ground";
    private static final String TAG_WITHER_HALF_SHIELD_TRIGGER = "wither_half_shield";
    private static final String TAG_WITHER_HAS_WITHER_EFFECT = "target_has_wither_debuff";

    // 每tick记录凋零是否落地
    @SubscribeEvent
    public static void onWitherTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof WitherBoss wither)) return;
        CompoundTag nbt = wither.getPersistentData();
        if (wither.onGround()) {
            nbt.putBoolean(TAG_WITHER_TOUCH_GROUND, true);
        }
    }

    // 凋零血量低于50%标记护盾阶段
    @SubscribeEvent
    public static void onWitherHealthCheck(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof WitherBoss wither)) return;
        CompoundTag nbt = wither.getPersistentData();
        float hpPercent = wither.getHealth() / wither.getMaxHealth();
        if (hpPercent <= 0.5F) {
            nbt.putBoolean(TAG_WITHER_HALF_SHIELD_TRIGGER, true);
        }
    }

    // 怪物死亡时统一把击杀者存入NBT
    @SubscribeEvent
    public static void onBossDeathRecordKiller(LivingDeathEvent event) {
        var source = event.getSource();
        if (!(source.getEntity() instanceof Player killer)) return;
        var boss = event.getEntity();
        CompoundTag tag = boss.getPersistentData();
        tag.putUUID("last_killer_uuid", killer.getUUID());
        if (boss instanceof WitherBoss w) {
            tag.putBoolean(TAG_WITHER_HAS_WITHER_EFFECT, w.hasEffect(MobEffects.WITHER));
        }
    }

    // 通过Boss获取击杀玩家（修复level私有报错）
    public static Player getKillerFromBoss(net.minecraft.world.entity.LivingEntity boss) {
        CompoundTag tag = boss.getPersistentData();
        if (!tag.hasUUID("last_killer_uuid")) return null;
        var uuid = tag.getUUID("last_killer_uuid");
        Level level = boss.level(); // 改用公共方法level()
        return level.getPlayerByUUID(uuid);
    }

    // 获取玩家所在方块亮度（修复level私有报错）
    public static int getBlock(Player p) {
        Level level = p.level();
        return level.getBrightness(LightLayer.BLOCK, p.blockPosition());
    }

    // 获取玩家黑暗层数
    public static int getDarknessStack(Player p) {
        var inst = p.getEffect(MobEffects.DARKNESS);
        return inst == null ? 0 : inst.getAmplifier() + 1;
    }
}