package com.lazybones.godfalltrinkets.item.custom;

import com.lazybones.godfalltrinkets.util.AdvancementHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AdvancementTallyTrinketItem extends Item implements ICurioItem {

    public static final String TAG_TOTAL_COMPLETED_ADV = "tally_adv_count";

    // 每个成就的增幅比例（无上限）
    public static final double HP_PER_ADV = 0.003;      // +0.3% 最大生命值
    public static final double DAMAGE_PER_ADV = 0.0025; // +0.25% 伤害
    public static final double RESIST_PER_ADV = 0.002;  // +0.2% 伤害抗性

    private static final UUID HP_UUID = UUID.fromString("a5f1c3d2-0000-4a00-b000-0000000000a1");
    private static final UUID DMG_UUID = UUID.fromString("a5f1c3d2-0000-4a00-b000-0000000000a2");

    public AdvancementTallyTrinketItem(Properties properties) {
        super(properties);
    }

    /**
     * 服务端调用：刷新本饰品自身NBT，写入玩家当前完成成就总数。
     * 只有饰品正在被佩戴才调用。
     */
    public static void updateTallyTag(ItemStack stack, Player player) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer sp) {
            int total = AdvancementHelper.countAllCompletedAdvancements(sp);
            stack.getOrCreateTag().putInt(TAG_TOTAL_COMPLETED_ADV, total);
        }
    }

    /**
     * 对外接口：读取该饰品存储的成就完成数量。
     */
    public static int getStoredAdvCount(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_TOTAL_COMPLETED_ADV)) {
            return 0;
        }
        return tag.getInt(TAG_TOTAL_COMPLETED_ADV);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack currentStack) {
        if (slotContext.entity() instanceof Player player) {
            // 刚戴上立刻刷新一次数值
            updateTallyTag(currentStack, player);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack currentStack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide()) {
            // 摘下不清除NBT，但立即移除属性加成
            applyTallyAttributes(player, 0);
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().endsWith("badge");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 普通状态：不按Shift，显示静态说明
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.desc.advancement_tally_trinket.line1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.desc.advancement_tally_trinket.line2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.godfall_trinkets.desc.advancement_tally_trinket.line3")
                .withStyle(ChatFormatting.GRAY));

        // ✅ 关键点：必须是客户端世界才允许访问Screen！服务端直接跳过Shift逻辑
        if(level != null && level.isClientSide()){
            if(Screen.hasShiftDown()){
                int count = getStoredAdvCount(stack);

                double hpBonus = count * HP_PER_ADV * 100D;
                double dmgBonus = count * DAMAGE_PER_ADV * 100D;
                double resistVal = getDamageResistance(count) * 100D;

                tooltip.add(Component.empty());
                tooltip.add(Component.literal("§6=== 当前生效加成 ==="));
                tooltip.add(Component.literal(String.format("§7已完成成就：%d 个", count)));
                tooltip.add(Component.literal(String.format("§7最大生命：+%.2f %%", hpBonus)));
                tooltip.add(Component.literal(String.format("§7伤害加成：+%.2f %%", dmgBonus)));
                tooltip.add(Component.literal(String.format("§7伤害减免：-%.2f %%", resistVal)));
            }else{
                // 提示玩家按Shift查看实时数值
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("tooltip.godfall_trinkets.hold_shift_view_stats")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
    }



    /**
     * 根据已完成成就数，向玩家施加/刷新最大生命值与攻击力加成。
     */
    public static void applyTallyAttributes(Player player, int count) {
        if (player.level().isClientSide()) return;
        applyModifier(player, Attributes.MAX_HEALTH, HP_UUID, "tally_max_health", count * HP_PER_ADV);

        // 删掉！！applyModifier(player, Attributes.ATTACK_DAMAGE, DMG_UUID, "tally_attack_damage", count * DAMAGE_PER_ADV);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }


    private static void applyModifier(Player player, Attribute attribute, UUID uuid, String name, double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(uuid);
        if (amount > 0) {
            instance.addTransientModifier(new AttributeModifier(uuid, name, amount,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    /**
     * 读取玩家佩戴中的功业徽记存储的成就完成数（未佩戴返回0）。
     */
    public static int getEquippedAdvCount(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.getItem() instanceof AdvancementTallyTrinketItem))
                .map(r -> getStoredAdvCount(r.stack()))
                .orElse(0);
    }

    /**
     * 计算伤害抗性比例（0.2% × 成就数，封顶99%）。
     */
    public static double getDamageResistance(int count) {
        return Math.min(count * RESIST_PER_ADV, 0.99);
    }
}
