package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EndBossDropHandler {
    // 标记战斗中玩家是否落地，全程鞘翅飞行才掉落寂灭龙脊
    private static final String TAG_FLY_ONLY = "dragon_fight_no_land";

    @SubscribeEvent
    public static void dragonTrackTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragon)) return;
        if (dragon.level().isClientSide()) return;

        CompoundTag nbt = dragon.getPersistentData();
        // 默认假设全程飞行，直到发现有人落地
        if (!nbt.contains(TAG_FLY_ONLY)) {
            nbt.putBoolean(TAG_FLY_ONLY, true);
        }

        // ← 修复：遍历末影龙附近玩家（战斗半径约 200 格）
        AABB searchBox = dragon.getBoundingBox().inflate(200.0D);
        List<Player> nearbyPlayers = dragon.level().getEntitiesOfClass(Player.class, searchBox);

        for (Player p : nearbyPlayers) {
            // 只要玩家有一次不是鞘翅飞行且在地面上，标记不满足条件
            if (!p.isFallFlying() && p.onGround()) {
                nbt.putBoolean(TAG_FLY_ONLY, false);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onDragonKilled(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Entity killerSrc = event.getSource().getEntity();
        if (!(dead instanceof EnderDragon dragon) || !(killerSrc instanceof Player player)) return;
        ServerLevel level = (ServerLevel) player.level();
        CompoundTag dragonNbt = dragon.getPersistentData(); // ← 修复变量名

        // 1. 龙核之心 无条件必掉
        spawnDrop(player, new ItemStack(ModItems.DRAGON_CORE_HEART.get())); // ← .get()

        // 2. 终末之眼：击杀瞬间血量＜20%
        float hpRatio = player.getHealth() / player.getMaxHealth();
        if (hpRatio < 0.20F) {
            spawnDrop(player, new ItemStack(ModItems.EYE_OF_THE_END.get())); // ← .get()
        }

        // 3. 残晶冠冕：末地平台10颗末影水晶全部完好未破坏
        // ← 修复：末影水晶是实体，用 getEntitiesOfClass 检测
        AABB crystalBox = new AABB(
                new BlockPos(-50, 50, -50),
                new BlockPos(50, 120, 50)
        );
        List<EndCrystal> crystals = level.getEntitiesOfClass(EndCrystal.class, crystalBox);
        int crystalCount = 0;
        for (EndCrystal crystal : crystals) {
            if (crystal.showsBottom()) { // 自然生成的有底座（showBottom=true）
                crystalCount++;
            }
        }
        if (crystalCount >= 10) {
            spawnDrop(player, new ItemStack(ModItems.CRYSTAL_CROWN.get())); // ← .get()
        }

        // 4. 寂灭龙脊：全程鞘翅不落地击杀末影龙
        boolean fullFly = dragonNbt.getBoolean(TAG_FLY_ONLY);
        if (fullFly) {
            spawnDrop(player, new ItemStack(ModItems.DRAGON_SPINE_END.get())); // ← .get()
        }
    }

    private static void spawnDrop(Player p, ItemStack stack) {
        p.spawnAtLocation(stack, 1.2F);
    }
}