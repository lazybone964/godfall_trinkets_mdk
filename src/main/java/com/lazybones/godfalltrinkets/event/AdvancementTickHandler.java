package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.item.custom.AdvancementTallyTrinketItem;
import com.lazybones.godfalltrinkets.item.custom.BrokenCoreItem;
import com.lazybones.godfalltrinkets.util.AdvancementHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.*;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID)
public class AdvancementTickHandler {
    //=====tick节流计数器，每20tick(1秒)刷新一次功业徽记=====
    private static int tickCounter = 0;
    //【幽冥猎狩】内存记录本局生命击杀Boss；死亡/登出清空
    private static final Map<ServerPlayer, Set<ResourceLocation>> BOSS_KILL_TRACK = new WeakHashMap<>();
    public static final ResourceLocation BOSS_WITHER = ResourceLocation.parse("minecraft:wither");
    public static final ResourceLocation BOSS_DRAGON = ResourceLocation.parse("minecraft:ender_dragon");
    public static final ResourceLocation BOSS_WARDEN = ResourceLocation.parse("minecraft:warden");
    //【厄难余生】全部8诅咒同时激活计时 tick
    private static final Map<ServerPlayer, Integer> ALL_CURSE_TIMER = new WeakHashMap<>();
    private static final int REQUIRED_CURSE_TICKS = 288000; //40游戏分钟
    //【裸身征伐】内存记录本局生命击杀Boss
    private static final Map<ServerPlayer,Set<ResourceLocation>> NO_ARMOR_BOSS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onServerPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;
        LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(sp);

        // --------① 检测Curios槽内的破厄之核 --------
        curiosOpt.ifPresent(curioItemHandler -> {
            Map<String, ICurioStacksHandler> slotMap = curioItemHandler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()) {
                IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() == ModItems.BROKEN_CORE.get()) {
                        AdvancementHelper.checkBrokenCoreTierAdvancements(stack, sp);
                    }
                }
            }
        });

        // --------② 检测碎片集齐成就（背包+Curios）--------
        AdvancementHelper.checkShardAdvancements(sp);

        // --------③ 检测长剑锻刃前行成就 T3及以上 --------
        for (ItemStack stack : sp.getInventory().items) {
            checkMidSword(stack, sp);
        }
        curiosOpt.ifPresent(curioItemHandler -> {
            Map<String, ICurioStacksHandler> slotMap = curioItemHandler.getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()) {
                IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    checkMidSword(stack, sp);
                }
            }
        });

        //检测神格觉醒成就
        AdvancementHelper.checkGodCoreAdvancement(sp);

        // ----------------厄难余生计时 ----------------
        int timer = ALL_CURSE_TIMER.getOrDefault(sp,0);
        if(BrokenCoreItem.hasAllEightCursesActive(sp)){
            timer++;
            if(timer >= REQUIRED_CURSE_TICKS){
                AdvancementHelper.grant(sp, AdvancementHelper.ADV_SECRET_CURSE_SURVIVOR);
            }
            ALL_CURSE_TIMER.put(sp, timer);
        }else{
            ALL_CURSE_TIMER.put(sp,0);
        }

        //====================功业徽记每秒刷新逻辑 ====================
        tickCounter++;
        if(tickCounter >= 20){
            tickCounter = 0;
            curiosOpt.ifPresent(curioItemHandler -> {
                Map<String, ICurioStacksHandler> slotMap = curioItemHandler.getCurios();
                for (Map.Entry<String, ICurioStacksHandler> entry : slotMap.entrySet()) {
                    IDynamicStackHandler stackHandler = entry.getValue().getStacks();
                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack stack = stackHandler.getStackInSlot(i);
                        if (!stack.isEmpty() && stack.getItem() == ModItems.ADVANCEMENT_TALLY_TRINKET.get()) {
                            AdvancementTallyTrinketItem.updateTallyTag(stack, sp);
                        }
                    }
                }
            });
            AdvancementTallyTrinketItem.applyTallyAttributes(sp,
                    AdvancementTallyTrinketItem.getEquippedAdvCount(sp));
        }
    }

    /** Boss死亡事件，处理多个隐藏成就判定 */
    @SubscribeEvent
    public static void onBossKilled(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (!(event.getSource().getEntity() instanceof ServerPlayer sp)) {
            return;
        }
        // 修复：EntityType.getRegistryName()移除，改用BuiltInRegistries
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

        //=====幽冥猎狩：同生命击杀三大Boss =====
        if (BOSS_WITHER.equals(entityId) || BOSS_DRAGON.equals(entityId) || BOSS_WARDEN.equals(entityId)) {
            Set<ResourceLocation> killedSet = BOSS_KILL_TRACK.computeIfAbsent(sp, k -> new HashSet<>());
            killedSet.add(entityId);
            if(killedSet.contains(BOSS_WITHER)
                    && killedSet.contains(BOSS_DRAGON)
                    && killedSet.contains(BOSS_WARDEN)){
                AdvancementHelper.grant(sp, AdvancementHelper.ADV_SECRET_BOSS_HUNTER);
                killedSet.clear();
            }
        }

        //=====凡躯弑神：Tier1破厄击杀监守者 =====
        if(BOSS_WARDEN.equals(entityId)){
            // ✅移除弃用findEquippedCurio，手动遍历curios槽查找BrokenCore
            LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(sp);
            boolean foundTier1Core = false;
            if(curiosOpt.isPresent()){
                ICuriosItemHandler handler = curiosOpt.orElseThrow(() -> new IllegalStateException("Curios missing"));
                outerSearch:
                for(Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()){
                    IDynamicStackHandler stacks = entry.getValue().getStacks();
                    for(int i=0;i<stacks.getSlots();i++){
                        ItemStack st = stacks.getStackInSlot(i);
                        if(!st.isEmpty() && st.getItem() instanceof BrokenCoreItem bc){
                            if(bc.getTier(st) == 1){
                                foundTier1Core = true;
                                break outerSearch;
                            }
                        }
                    }
                }
            }
            if(foundTier1Core){
                AdvancementHelper.grant(sp, AdvancementHelper.ADV_SECRET_TIER1_VANQUISH);
            }

            //=====超限迸发：13碎片全部佩戴击杀监守者 =====
            if(AdvancementHelper.checkAll13ShardsEquipped(sp)){
                AdvancementHelper.grant(sp, AdvancementHelper.ADV_SECRET_OVERLOAD_POWER);
            }
        }

        //=====裸身征伐：无盔甲击杀凋零/末影龙 =====
        if(BOSS_WITHER.equals(entityId) || BOSS_DRAGON.equals(entityId)){
            boolean noArmor = sp.getInventory().getArmor(0).isEmpty()
                    && sp.getInventory().getArmor(1).isEmpty()
                    && sp.getInventory().getArmor(2).isEmpty()
                    && sp.getInventory().getArmor(3).isEmpty();
            if(noArmor){
                Set<ResourceLocation> set = NO_ARMOR_BOSS.computeIfAbsent(sp, k->new HashSet<>());
                set.add(entityId);
                if(set.contains(BOSS_WITHER) && set.contains(BOSS_DRAGON)){
                    AdvancementHelper.grant(sp, AdvancementHelper.ADV_SECRET_NO_ARMOR_CHAMPION);
                    set.clear();
                }
            }
        }
    }

    /** 玩家死亡：清空全部内存临时记录 */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event){
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        BOSS_KILL_TRACK.remove(sp);
        ALL_CURSE_TIMER.remove(sp);
        NO_ARMOR_BOSS.remove(sp);
    }

    /** 玩家登出：清理内存，防止内存泄漏 */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer sp)) return;
        BOSS_KILL_TRACK.remove(sp);
        ALL_CURSE_TIMER.remove(sp);
        NO_ARMOR_BOSS.remove(sp);
    }

    /** 任意一个隐藏成就完成，校验7个全部是否集齐，发放功业徽记 */
    @SubscribeEvent
    public static void onHiddenAdvComplete(AdvancementEvent.AdvancementEarnEvent event){
        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer sp)) return;
        ResourceLocation earnedId = event.getAdvancement().getId();
        if(!AdvancementHelper.HIDDEN_TALLY_ADVS.contains(earnedId)) return;
        //背包已有功业徽记不再重复发放
        if(sp.getInventory().countItem(ModItems.ADVANCEMENT_TALLY_TRINKET.get()) > 0){
            return;
        }
        if(AdvancementHelper.hasCompletedAllHiddenTallyAdvancements(sp)){
            ItemStack sigil = new ItemStack(ModItems.ADVANCEMENT_TALLY_TRINKET.get());
            sp.getInventory().add(sigil);
            sp.sendSystemMessage(Component.literal("§6【秘力觉醒】你完成了全部隐秘挑战，获得了功业徽记！"));
        }
    }

    @SubscribeEvent
    public static void onPlayerDealDamage(LivingHurtEvent event) {
        if(event.getSource().getEntity() instanceof ServerPlayer sp) {
            int cnt = AdvancementTallyTrinketItem.getEquippedAdvCount(sp);
            if(cnt <= 0) return;
            double dmgMult = 1.0D + cnt * AdvancementTallyTrinketItem.DAMAGE_PER_ADV;
            event.setAmount((float)(event.getAmount() * dmgMult));
        }
    }

    /** 功业徽记：按成就数减免受到的伤害 */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            int count = AdvancementTallyTrinketItem.getEquippedAdvCount(player);
            if (count > 0) {
                float reduction = (float) AdvancementTallyTrinketItem.getDamageResistance(count);
                event.setAmount(event.getAmount() * (1.0f - reduction));
            }
        }
    }

    /** 辅助：判断长剑是否达到T3 */
    private static void checkMidSword(ItemStack stack, ServerPlayer sp) {
        if (stack.isEmpty()) return;
        var tag = stack.getTag();
        if (tag == null || !tag.contains("sword_tier")) return;
        int tier = tag.getInt("sword_tier");
        if (tier >= 3) {
            AdvancementHelper.grant(sp, AdvancementHelper.ADV_SWORD_MID_FORGE);
        }
    }
}
