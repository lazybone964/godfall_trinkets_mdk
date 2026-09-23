package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import top.theillusivec4.curios.api.CuriosApi;
import java.util.List;
import java.util.Random;

/** 神位碎片击杀掉落：从配置文件读取怪物与概率 */
@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GodShardDropHandler {
    private static final Random RAND = new Random();

    // 与 GodfallConfig.SHARD_DROP_MOBS/CHANCES 索引一一对应
    private static final List<RegistryObject<Item>> SHARD_ITEMS = List.of(
            ModItems.SHARD_SKY, ModItems.SHARD_SUN, ModItems.SHARD_LIFE, ModItems.SHARD_STRENGTH,
            ModItems.SHARD_FATE, ModItems.SHARD_WIND, ModItems.SHARD_SPIRIT, ModItems.SHARD_BARRIER,
            ModItems.SHARD_VOID, ModItems.SHARD_SHADOW, ModItems.SHARD_THUNDER, ModItems.SHARD_STAR,
            ModItems.SHARD_RUNE
    );

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (!(killer instanceof Player player)) return;
        if (dead.level().isClientSide()) return;

        for (int i = 0; i < 13; i++) {
            List<? extends String> mobs = GodfallConfig.SHARD_DROP_MOBS.get(i).get();
            List<? extends Double> chances = GodfallConfig.SHARD_DROP_CHANCES.get(i).get();
            double rateMod = GodfallConfig.SHARD_DROP_RATE_MOD.get();
            int size = Math.min(mobs.size(), chances.size());
            for (int j = 0; j < size; j++) {
                if (matchesMob(dead, mobs.get(j)) && RAND.nextFloat() < chances.get(j) * rateMod) {
                    dropShard(player, SHARD_ITEMS.get(i));
                    break; // 同一碎片每次击杀只判定一次
                }
            }
        }
    }

    /** 怪物匹配：支持 :powered=带电苦力怕, :captain=掠夺者队长，否则按注册名匹配 */
    private static boolean matchesMob(LivingEntity dead, String mobId) {
        if (mobId.endsWith(":powered")) {
            return dead instanceof Creeper && ((Creeper) dead).isPowered();
        }
        if (mobId.endsWith(":captain")) {
            return dead instanceof Pillager && isRaidCaptain((Pillager) dead);
        }
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(dead.getType());
        return key != null && key.toString().equals(mobId);
    }

    /** 掠夺者队长判定：头上戴有灾厄旗帜 */
    private static boolean isRaidCaptain(Pillager pillager) {
        return pillager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)
                .getItem() instanceof BannerItem;
    }

    /** 检查玩家是否已拥有该碎片（Curios槽位 或 背包/物品栏/盔甲栏/副手） */
    private static boolean hasShard(Player p, RegistryObject<Item> shard) {
        Item item = shard.get();
        boolean inCurios = CuriosApi.getCuriosInventory(p).resolve()
                .map(h -> h.findFirstCurio(s -> s.is(item)).isPresent()).orElse(false);
        if (inCurios) return true;
        return p.getInventory().contains(new ItemStack(item));
    }

    /** 掉落碎片，已拥有则不掉 */
    private static void dropShard(Player player, RegistryObject<Item> shard) {
        if (hasShard(player, shard)) return;
        ItemStack stack = new ItemStack(shard.get());
        player.spawnAtLocation(stack, 1.0f);
    }
}
