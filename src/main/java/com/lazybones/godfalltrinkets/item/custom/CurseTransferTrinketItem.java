package com.lazybones.godfalltrinkets.item.custom;

import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 咒厄转移坠 — 替玩家承受破厄之核的诅咒。
 * <ul>
 * <li>创建时随机绑定 2 条不重复诅咒（从 8 种中抽取）</li>
 * <li>佩戴后吸收匹配的诅咒效果，消耗耐久（1 点/秒/条）</li>
 * <li>耐久归零时销毁，诅咒归还玩家并播放碎裂音效 + 粒子</li>
 * </ul>
 */
public class CurseTransferTrinketItem extends Item implements ICurioItem {

    /** 全部 8 条诅咒标识,与 BrokeCoreItem 的诅咒一一对应 */
    public static final List<String> ALL_CURSE_TAGS = List.of(
            "agony",          // 诅咒1：生命枯竭（每秒扣血）
            "deep_wound",     // 诅咒2：伤痛加深（受伤×1.2）
            "blunt_edge",     // 诅咒3：锋芒钝化（-30%攻击）
            "hunger_curse",   // 诅咒4：永饥之噬（快速饥饿）
            "sleepless",      // 诅咒5：无眠诅咒（不能睡觉）
            "scarred",        // 诅咒6：重创烙印（额外扣最大HP%）
            "weak_body",      // 诅咒7：身躯羸弱（-4最大HP）
            "foul_body"       // 诅咒8：秽体易伤（负面时间翻倍）
    );

    private static final Random RAND = new Random();
    private static final String TAG_LIST = "transfer_curse_list";
    private static final String TAG_LOCKED = "curse_locked";
    /** 每tick耐久损耗量 = curseCount * DRAIN_PER_CURSE */
    private static final float DRAIN_PER_CURSE = 0.05f;

    /** NBT黑名单：这些标签会在 tick 时被自动剥离 */
    private static final Set<String> NBT_BLACKLIST = Set.of(
            "Unbreakable", "Enchantments", "StoredEnchantments",
            "RepairCost", "AttributeModifiers", "CustomCreativeLock"
    );

    public CurseTransferTrinketItem(Properties properties) {
        super(properties);
    }

    // ==================== 初始化 & 诅咒绑定 ====================

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        initRandomCursePair(stack);
        return stack;
    }

    /** 为新物品随机绑定 2 条不重复诅咒 */
    public static void initRandomCursePair(ItemStack stack) {
        if (!(stack.getItem() instanceof CurseTransferTrinketItem)) return;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TAG_LIST)) return;

        List<String> pool = new ArrayList<>(ALL_CURSE_TAGS);
        ListTag list = new ListTag();
        list.add(StringTag.valueOf(pool.remove(RAND.nextInt(pool.size()))));
        list.add(StringTag.valueOf(pool.remove(RAND.nextInt(pool.size()))));

        tag.put(TAG_LIST, list);
        tag.putBoolean(TAG_LOCKED, true);
    }

    /** 获取此饰品锁定的诅咒标识列表（首次调用时懒初始化 NBT） */
    public static List<String> getLockedCurseTags(ItemStack stack) {
        if (stack.getItem() instanceof CurseTransferTrinketItem) {
            initRandomCursePair(stack); // 懒初始化：合成品首次调用时自动绑定
        }
        List<String> result = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_LIST)) return result;
        ListTag list = tag.getList(TAG_LIST, 8);
        for (int i = 0; i < list.size(); i++) result.add(list.getString(i));
        return result;
    }

    /** 查询玩家身上是否存在咒厄转移坠并返回其吸取的诅咒集合 */
    public static Set<String> getAbsorbedCurses(Player player) {
        var opt = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.getItem() instanceof CurseTransferTrinketItem));
        if (opt.isPresent()) {
            return new HashSet<>(getLockedCurseTags(opt.get().stack()));
        }
        return Collections.emptySet();
    }

    // ==================== Curios 槽位 ====================

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!"curse_transfer".equals(slotContext.identifier())) return false;
        if (slotContext.entity() instanceof Player player) {
            return player.getPersistentData().getBoolean("godfall_unlock_curse_slot");
        }
        return false;
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.add(Component.literal("curse_transfer"));
        return tooltips;
    }

    // ==================== Tick — 耐久持续损耗 ====================

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide) return;
        if (!(slotContext.entity() instanceof Player player)) return;

        // NBT黑名单剥离
        stripBlacklistedNbt(stack);

        // 仅在同时佩戴破厄之核时才损耗（诅咒实际生效时）
        boolean hasCore = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get())))
                .isPresent();
        if (!hasCore) return;

        if (player.tickCount % 20 != 0) return;

        int curseCount = getLockedCurseTags(stack).size();
        if (curseCount == 0) return;

        int newDmg = stack.getDamageValue() + curseCount;
        stack.setDamageValue(newDmg);

        if (newDmg >= stack.getMaxDamage() - 1) {
            destroyAndReturnCurses(slotContext, stack, player);
        }
    }

    /** 销毁饰品，归还诅咒，播放粒子+音效 */
    private void destroyAndReturnCurses(SlotContext slotContext, ItemStack stack, Player player) {
        Level level = player.level();
        // 粒子效果
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 1.2, player.getZ(),
                    15, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    8, 0.2, 0.2, 0.2, 0.01);
        }
        // 音效
        level.playSound(null, player.blockPosition(),
                SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.2f);
        level.playSound(null, player.blockPosition(),
                SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 0.6f, 0.8f);
        // 提示消息
        player.sendSystemMessage(Component.translatable(
                "message.godfall_trinkets.curse_return").withStyle(ChatFormatting.DARK_RED));

        // 将饰品从 Curios 栏位移除
        stack.setCount(0);
    }

    // ==================== Tooltip ====================

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        List<String> curses = getLockedCurseTags(stack);
        if (!curses.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.godfall_trinkets.absorbed_curses")
                    .withStyle(ChatFormatting.DARK_RED));
            for (String tag : curses) {
                tooltip.add(Component.literal("  - ")
                        .append(Component.translatable("curse.godfall_trinkets." + tag))
                        .withStyle(ChatFormatting.RED));
            }
        }
        int dur = stack.getMaxDamage() - stack.getDamageValue();
        int max = stack.getMaxDamage();
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.curse_durability", dur, max)
                .withStyle(ChatFormatting.GRAY));
    }

    /** 获取已吸取的诅咒标识集合（用于外部查询） */
    public static boolean isAbsorbingCurse(Player player, String curseTag) {
        return getAbsorbedCurses(player).contains(curseTag);
    }

    // ==================== 附魔 / 修复 / NBT防御 ====================

    /** 禁止附魔 */
    @Override
    public boolean isEnchantable(ItemStack stack) { return false; }

    /** 禁止附魔光效 */
    @Override
    public boolean isFoil(ItemStack stack) { return false; }

    /** 铁砧修复材料：咒厄坠粗胚 */
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return repairMaterial.is(ModItems.GODFRAGMENT.get());
    }

    /** 每次 tick 剥离 NBT 黑名单中的标签 */
    private static void stripBlacklistedNbt(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        boolean changed = false;
        for (String banned : NBT_BLACKLIST) {
            if (tag.contains(banned)) {
                tag.remove(banned);
                changed = true;
            }
        }
        if (changed && tag.isEmpty()) {
            stack.setTag(null);
        }
    }
}
