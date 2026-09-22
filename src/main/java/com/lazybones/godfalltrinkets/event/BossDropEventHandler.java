package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BossDropEventHandler {
    // NBT标记键：记录凋零整场战斗是否触碰地面
    private static final String TAG_WITHER_TOUCH_GROUND = "wither_touch_ground_flag";

    // 每Tick检测凋零是否落地，打上持久NBT标记
    @SubscribeEvent
    public static void trackWitherGroundState(LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof WitherBoss wither)) return;
        CompoundTag nbt = wither.getPersistentData();
        // 一旦落地永久标记为true，整场战斗不再重置
        if (wither.onGround()) {
            nbt.putBoolean(TAG_WITHER_TOUCH_GROUND, true);
        }
    }

    // Boss死亡掉落主逻辑
    @SubscribeEvent
    public static void onBossDeath(LivingDeathEvent event) {
        LivingEntity deadMob = event.getEntity();
        Entity killerSource = event.getSource().getEntity();
        // 仅服务端 + 玩家亲手击杀才执行饰品掉落
        if (!(deadMob.level() instanceof ServerLevel serverLevel)) return;
        if (!(killerSource instanceof Player player)) return;

        if (deadMob instanceof WitherBoss wither) {
            handleAllWitherDrops(serverLevel, player, wither);
        } else if (deadMob instanceof Warden warden) {
            handleAllWardenDrops(serverLevel, player, warden);
        }
    }

    /**
     * 凋零全套4件饰品掉落
     * 枯骨之心：必掉；其余三件满足条件额外掉落
     */
    private static void handleAllWitherDrops(ServerLevel level, Player killer, WitherBoss wither) {
        BlockPos playerPos = killer.blockPosition();
        CompoundTag witherNbt = wither.getPersistentData();
        // 1. 枯骨之心 无条件必掉
        spawnDropItem(killer, new ItemStack(ModItems.WITHER_HEART.get()));

        // 2. 王者之握：整场从未落地（标记为false）
        boolean hasTouchedGround = witherNbt.getBoolean(TAG_WITHER_TOUCH_GROUND);
        if (!hasTouchedGround) {
            spawnDropItem(killer, new ItemStack(ModItems.KINGS_GRIP.get()));
        }

        // 3. 凋零王冠：击杀时血量≤150（半血护盾阶段）
        if (wither.getHealth() <= 150F) {
            spawnDropItem(killer, new ItemStack(ModItems.WITHER_CROWN.get()));
        }

        // 4. 凋零脊骨坠：击杀瞬间凋零自身携带凋零debuff
        if (wither.hasEffect(MobEffects.WITHER)) {
            spawnDropItem(killer, new ItemStack(ModItems.WITHER_SPINE.get()));
        }
    }

    /**
     * 监守者4件幽系饰品，条件独立，满足多条可一次性全部掉落
     */
    private static void handleAllWardenDrops(ServerLevel level, Player killer, Warden warden) {
        BlockPos playerPos = killer.blockPosition();
        int blockLight = level.getBrightness(LightLayer.BLOCK, playerPos);
        float playerMaxHp = killer.getMaxHealth();
        float currentHp = killer.getHealth();

        // 1. 幽匿之眼：击杀时玩家所处亮度≥12
        if (blockLight >= 12) {
            spawnDropItem(killer, new ItemStack(ModItems.WARDEN_EYE.get()));
        }

        // 2. 幽匿脉动：自身血量低于20%（满血20点 → 低于4.0）
        if (currentHp < playerMaxHp * 0.20F) {
            spawnDropItem(killer, new ItemStack(ModItems.WARDEN_PULSE.get()));
        }

        // 3. 幽匿重压：玩家黑暗效果放大器≥2（对应3层黑暗debuff）
        if (killer.hasEffect(MobEffects.DARKNESS)) {
            int darkAmp = killer.getEffect(MobEffects.DARKNESS).getAmplifier();
            if (darkAmp >= 2) {
                spawnDropItem(killer, new ItemStack(ModItems.WARDEN_WEIGHT.get()));
            }
        }

        // 4. 幽匿破甲：玩家所处方块亮度严格=0
        if (blockLight == 0) {
            spawnDropItem(killer, new ItemStack(ModItems.WARDEN_ARMORBREAK.get()));
        }
    }

    /**
     * 统一生成掉落物工具方法，在玩家身边生成物品
     */
    private static void spawnDropItem(Player player, ItemStack stack) {
        player.spawnAtLocation(stack, 1.0F);
    }
}