package com.lazybones.godfalltrinkets.util;

import com.lazybones.godfalltrinkets.item.custom.BrokenCoreItem;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdvancementHelper {
    // -------------------- 进度ID常量（改为ResourceLocation） --------------------
    public static final ResourceLocation ADV_TWELVE_CONSTELLATION = ResourceLocation.parse("godfall_trinkets:twelve_constellation");
    public static final ResourceLocation ADV_THIRTEEN_RETURN = ResourceLocation.parse("godfall_trinkets:thirteen_return");
    public static final ResourceLocation ADV_TIER9_REVERSE = ResourceLocation.parse("godfall_trinkets:tier9_reverse");
    public static final ResourceLocation ADV_SWORD_MID_FORGE = ResourceLocation.parse("godfall_trinkets:sword_mid_forge");
    public static final ResourceLocation ADV_HARDSHIP_TEMPER = ResourceLocation.parse("godfall_trinkets:hardship_temper");
    public static final ResourceLocation ADV_CHEST_FIND_SHARD = ResourceLocation.parse("godfall_trinkets:chest_find_shard");
    public static final ResourceLocation ADV_GODHOOD_AWAKEN = ResourceLocation.parse("godfall_trinkets:godhood_awaken");

    /** 根据advancement ResourceLocation获取Advancement对象 */
    public static Advancement getAdvancement(ServerPlayer player, ResourceLocation advId) {
        return player.getServer().getAdvancements().getAdvancement(advId);
    }

    /** 授予一个进度（安全空判断，避免空指针） */
    public static void grant(ServerPlayer player, ResourceLocation advId) {
        Advancement adv = getAdvancement(player, advId);
        if (adv != null && !player.getAdvancements().getOrStartProgress(adv).isDone()) {
            player.getAdvancements().award(adv, "dummy");
        }
    }

    /** 撤销进度（一般不用，调试用） */
    public static void revoke(ServerPlayer player, ResourceLocation advId) {
        Advancement adv = getAdvancement(player, advId);
        if (adv != null) {
            player.getAdvancements().revoke(adv, "dummy");
        }
    }

    // ============ 判断1：玩家背包+Curios饰品槽拥有的神位碎片集合 ============
    public static Set<Item> getOwnedShards(ServerPlayer player) {
        Set<Item> set = new HashSet<>();
        // 1.普通背包
        for (ItemStack stack : player.getInventory().items) {
            addShardItem(set, stack);
        }
        // 2.Curios全部饰品槽，替换弃用getCuriosHelper
        LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(player);
        curiosOpt.ifPresent(curioItemHandler -> {
            Map<String, ICurioStacksHandler> allSlots = curioItemHandler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : allSlots.entrySet()) {
                IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    addShardItem(set, stack);
                }
            }
        });
        return set;
    }

    /**
     * 检测神格觉醒：背包 OR 任意Curios槽（佩戴/存放）存在god_core即解锁
     */
    public static void checkGodCoreAdvancement(ServerPlayer player) {
        boolean found = false;
        //扫描普通背包
        for(ItemStack stack : player.getInventory().items){
            if(stack.getItem() == ModItems.GOD_CORE.get()){
                found = true;
                break;
            }
        }
        //没找到，遍历全部Curios槽
        if(!found){
            LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(player);
            // ✅不使用orElseThrow，改用if‑present，不存在直接跳过，规避空参orElseThrow编译错误
            if(curiosOpt.isPresent()){
                ICuriosItemHandler handler = curiosOpt.orElseThrow(() -> new IllegalStateException("Curios capability missing"));
                Map<String, ICurioStacksHandler> slotMap = handler.getCurios();
                outerLoop:
                for(Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()){
                    IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                    for(int i=0;i<stackHandler.getSlots();i++){
                        ItemStack st = stackHandler.getStackInSlot(i);
                        if(st.getItem() == ModItems.GOD_CORE.get()){
                            found = true;
                            break outerLoop;
                        }
                    }
                }
            }
        }
        if(found){
            grant(player, ADV_GODHOOD_AWAKEN);
        }
    }

    // 抽取小辅助函数，判断stack是不是神位碎片，加入集合
    private static void addShardItem(Set<Item> set, ItemStack stack) {
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        if (item == ModItems.SHARD_SKY.get()
                || item == ModItems.SHARD_SUN.get()
                || item == ModItems.SHARD_LIFE.get()
                || item == ModItems.SHARD_STRENGTH.get()
                || item == ModItems.SHARD_FATE.get()
                || item == ModItems.SHARD_WIND.get()
                || item == ModItems.SHARD_SPIRIT.get()
                || item == ModItems.SHARD_BARRIER.get()
                || item == ModItems.SHARD_VOID.get()
                || item == ModItems.SHARD_SHADOW.get()
                || item == ModItems.SHARD_THUNDER.get()
                || item == ModItems.SHARD_STAR.get()
                || item == ModItems.SHARD_RUNE.get()) {
            set.add(item);
        }
    }

    /**
     * 检查碎片相关两个成就：十二星罗 / 十三归位
     * 在玩家物品变化事件调用（InventoryChangeEvent / ItemPickupEvent）
     */
    public static void checkShardAdvancements(ServerPlayer player) {
        Set<Item> owned = getOwnedShards(player);
        // 12枚：排除天穹碎片，其余12个全部拥有
        boolean hasTwelve = owned.contains(ModItems.SHARD_SUN.get())
                && owned.contains(ModItems.SHARD_LIFE.get())
                && owned.contains(ModItems.SHARD_STRENGTH.get())
                && owned.contains(ModItems.SHARD_FATE.get())
                && owned.contains(ModItems.SHARD_WIND.get())
                && owned.contains(ModItems.SHARD_SPIRIT.get())
                && owned.contains(ModItems.SHARD_BARRIER.get())
                && owned.contains(ModItems.SHARD_VOID.get())
                && owned.contains(ModItems.SHARD_SHADOW.get())
                && owned.contains(ModItems.SHARD_THUNDER.get())
                && owned.contains(ModItems.SHARD_STAR.get())
                && owned.contains(ModItems.SHARD_RUNE.get());
        if (hasTwelve) {
            grant(player, ADV_TWELVE_CONSTELLATION);
        }
        // 全部13枚，包含天穹碎片
        boolean hasAll13 = hasTwelve && owned.contains(ModItems.SHARD_SKY.get());
        if (hasAll13) {
            grant(player, ADV_THIRTEEN_RETURN);
        }
    }

    /**
     * 检查破厄之核境界成就：
     * 苦厄磨砺（境界5）、境界逆转（境界9）
     * 在破厄升级事件、玩家tick事件调用
     * @param stack 破厄之核 ItemStack
     * @param player 服务端玩家
     */
    public static void checkBrokenCoreTierAdvancements(ItemStack stack, ServerPlayer player) {
        if (!(stack.getItem() instanceof BrokenCoreItem coreItem)) return;
        int tier = coreItem.getTier(stack);
        // 苦厄磨砺：达到境界5
        if (tier >= 5) {
            grant(player, ADV_HARDSHIP_TEMPER);
        }
        // 境界逆转：达到境界9
        if (tier >= 9) {
            grant(player, ADV_TIER9_REVERSE);
        }
    }

    /**
     * 检查长剑锻刃前行成就：长剑>=T3（碎神影剑及以上）
     * 在锻造完成事件、玩家物品变更调用
     * @param swordStack 长剑物品栈
     * @param player 服务端玩家
     */
    public static void checkSwordMidAdvancement(ItemStack swordStack, ServerPlayer player) {
        CompoundTag tag = swordStack.getOrCreateTag();
        int swordTier = tag.getInt("sword_tier");
        // T3 = 3，达到3及以上解锁【锻刃前行】
        if (swordTier >= 3) {
            grant(player, ADV_SWORD_MID_FORGE);
        }
    }

    /**
     * 宝匣寻神：从宝箱获取碎片的时候调用这个！
     * 👉在你的战利品/宝箱生成拿碎片的代码处执行这一行
     */
    public static void triggerChestFindShard(ServerPlayer player) {
        grant(player, ADV_CHEST_FIND_SHARD);
    }
}
