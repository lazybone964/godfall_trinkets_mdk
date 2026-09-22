package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BannerItem;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import top.theillusivec4.curios.api.CuriosApi;
import java.util.Random;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GodShardDropHandler {
    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (!(killer instanceof Player player)) return;

        // ===== 天穹：末影龙击杀必掉 =====
        if (dead instanceof EnderDragon) {
            dropShard(player, ModItems.SHARD_SKY);
            return;
        }

        // ===== 烈阳：烈焰人 12% / 岩浆怪 8% =====
        if (dead instanceof Blaze && roll(0.12f)) {
            dropShard(player, ModItems.SHARD_SUN);
        } else if (dead instanceof MagmaCube && roll(0.08f)) {
            dropShard(player, ModItems.SHARD_SUN);
        }

        // ===== 生命：监守者 4% =====
        if (dead instanceof Warden && roll(0.04f)) {
            dropShard(player, ModItems.SHARD_LIFE);
        }

        // ===== 巨力：猪灵蛮兵 10% =====
        if (dead instanceof PiglinBrute && roll(0.10f)) {
            dropShard(player, ModItems.SHARD_STRENGTH);
        }

        // ===== 疾风：山羊 7% =====
        if (dead instanceof Goat && roll(0.07f)) {
            dropShard(player, ModItems.SHARD_WIND);
        }

        // ===== 灵韵：女巫 9% =====
        if (dead instanceof Witch && roll(0.09f)) {
            dropShard(player, ModItems.SHARD_SPIRIT);
        }

        // ===== 壁垒：铁傀儡 11% =====
        if (dead instanceof IronGolem && roll(0.11f)) {
            dropShard(player, ModItems.SHARD_BARRIER);
        }

        // ===== 虚空：潜影贝 9% =====
        if (dead instanceof Shulker && roll(0.09f)) {
            dropShard(player, ModItems.SHARD_VOID);
        }

        // ===== 影杀：循声守卫 5% =====
        if (dead instanceof Warden && roll(0.05f)) {
            dropShard(player, ModItems.SHARD_SHADOW);
        }

        // ===== 御雷：闪电苦力怕 16% / 掠夺者队长 10% =====
        if (dead instanceof Creeper creeper && creeper.isPowered() && roll(0.16f)) {
            dropShard(player, ModItems.SHARD_THUNDER);
        }
        if (dead instanceof Pillager pillager && isRaidCaptain(pillager) && roll(0.10f)) {
            dropShard(player, ModItems.SHARD_THUNDER);
        }

        // ===== 星陨：骷髅 8% / 潜影贝 13% =====
        if (dead instanceof Skeleton && roll(0.08f)) {
            dropShard(player, ModItems.SHARD_STAR);
        } else if (dead instanceof Shulker && roll(0.13f)) {
            dropShard(player, ModItems.SHARD_STAR);
        }
    }

    /** 掠夺者队长判定：头上戴有灾厄旗帜 */
    private static boolean isRaidCaptain(Pillager pillager) {
        return pillager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)
                .getItem() instanceof BannerItem;
    }

    private static boolean roll(float chance) {
        return RAND.nextFloat() < chance;
    }

    /** 检查玩家是否已拥有该碎片（Curios槽位 或 背包/物品栏/盔甲栏/副手） */
    private static boolean hasShard(Player p, RegistryObject<Item> shard) {
        Item item = shard.get();
        // 1. 检查 Curios 槽位
        boolean inCurios = CuriosApi.getCuriosInventory(p).resolve()
                .map(h -> h.findFirstCurio(s -> s.is(item)).isPresent()).orElse(false);
        if (inCurios) return true;
        // 2. 检查背包、盔甲栏、副手
        return p.getInventory().contains(new ItemStack(item));
    }

    /** 掉落碎片，已拥有则不掉 */
    private static void dropShard(Player player, RegistryObject<Item> shard) {
        if (hasShard(player, shard)) return; // ← 核心：已拥有则跳过
        ItemStack stack = new ItemStack(shard.get());
        player.spawnAtLocation(stack, 1.0f);
    }
}