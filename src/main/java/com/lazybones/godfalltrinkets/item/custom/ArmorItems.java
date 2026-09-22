package com.lazybones.godfalltrinkets.item.custom;


import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.item.armor.ArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.google.common.collect.HashMultimap; // 添加导入
import javax.annotation.Nullable;
import java.util.List;

public class ArmorItems extends ArmorItem {
    public ArmorItems(ArmorMaterial pMaterial, Type pSlot, Properties pProperties) {
        super(pMaterial, pSlot, pProperties);
    }
    // ==================== 检测全套盔甲 ====================
    // 判断是否全套神陨诅咒套
    public static boolean isFullGodSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        return head.getItem() == ModItems.GOD_HELMET.get()
                && chest.getItem() == ModItems.GOD_CHESTPLATE.get()
                && legs.getItem() == ModItems.GOD_LEGGINGS.get()
                && feet.getItem() == ModItems.GOD_BOOTS.get();
    }

    // 判断是否全套征伐战斗套
    public static boolean isFullConquestSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        return head.getItem() == ModItems.CONQUEST_HELMET.get()
                && chest.getItem() == ModItems.CONQUEST_CHESTPLATE.get()
                && legs.getItem() == ModItems.CONQUEST_LEGGINGS.get()
                && feet.getItem() == ModItems.CONQUEST_BOOTS.get();
    }
    // ==================== 添加套装效果 ====================

    // 神陨全套：生命恢复+抗性
    public static void applyGodSetEffects(Player player) {
        int EFFECT_DURATION = 400;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 3,false,true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 1,false,true));
    }

    // 征伐全套：生命吸取（吸血）+额外减伤
    public static void applyConquestSetEffects(Player player) {
        int EFFECT_DURATION = 400;
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 1,false,true));
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (this.getType().getSlot() != slot) return map;
        ArmorMaterial mat = this.getMaterial();

        String uuidBase = switch (this.getType()) {
            case HELMET -> "c001";
            case CHESTPLATE -> "c002";
            case LEGGINGS -> "c003";
            case BOOTS -> "c004";
        };
        UUID atkUuid = UUID.fromString("00000000-0000-4000-a000-" + uuidBase + "1");

        // ========== 普通征伐套 独立攻击倍率 ==========
        if (mat == ArmorMaterials.CONQUEST_INGOT) {
            float atkMultiplier = switch (this.getType()) {
                case HELMET -> 0.20F;      // 头盔 +20%
                case CHESTPLATE -> 0.35F;  // 胸甲 +35%
                case LEGGINGS -> 0.25F;    // 护腿 +25%
                case BOOTS -> 0.15F;       // 靴子 +15%
            };
            map.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(atkUuid, "conquest_atk_pct", atkMultiplier,
                            AttributeModifier.Operation.MULTIPLY_BASE));
        }
        // ========== 深渊征伐套 独立更高攻击倍率（不共用普通征伐数值） ==========
        else if (mat == ArmorMaterials.ABYSS_GOD_INGOT) {
            float atkMultiplier = switch (this.getType()) {
                case HELMET -> 0.30F;      // 头盔 +30%
                case CHESTPLATE -> 0.50F;  // 胸甲 +50%
                case LEGGINGS -> 0.35F;    // 护腿 +35%
                case BOOTS -> 0.22F;       // 靴子 +22%
            };
            map.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(atkUuid, "abyss_conquest_atk_pct", atkMultiplier,
                            AttributeModifier.Operation.MULTIPLY_BASE));
        }
        return map;
    }
    // 统计玩家当前穿了几件征伐套
    public static int getConquestPieceCount(Player player) {
        int count = 0;
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.CONQUEST_HELMET.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.CONQUEST_CHESTPLATE.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.CONQUEST_LEGGINGS.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.CONQUEST_BOOTS.get()) count++;
        return count;
    }
    // 判断是否全套深渊征伐套
    public static boolean isFullAbyssConquestSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        return head.getItem() == ModItems.ABYSS_GOD_HELMET.get()
                && chest.getItem() == ModItems.ABYSS_GOD_CHESTPLATE.get()
                && legs.getItem() == ModItems.ABYSS_GOD_LEGGINGS.get()
                && feet.getItem() == ModItems.ABYSS_GOD_BOOTS.get();
    }

    // 统计深渊征伐穿戴件数
    public static int getAbyssConquestPieceCount(Player player) {
        int count = 0;
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.ABYSS_GOD_HELMET.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.ABYSS_GOD_CHESTPLATE.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.ABYSS_GOD_LEGGINGS.get()) count++;
        if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.ABYSS_GOD_BOOTS.get()) count++;
        return count;
    }
    // 深渊征伐全套终极效果
    public static void applyAbyssConquestSetEffects(Player player) {
        int EFFECT_DURATION = 400;
        // 保留原版征伐全套抗性
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 2,false,true));
        // 全套专属增益
        // 近战伤害+36
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, 11,false,true));
        // 血量低于30%攻速+25%
        if(player.getHealth() / player.getMaxHealth() <= 0.3F){
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, EFFECT_DURATION, 1,false,true));
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        ArmorMaterial mat = this.getMaterial();
        // 神陨套提示
        if(mat == ArmorMaterials.GOD_INGOT){
            tooltip.add(Component.translatable("armor.godfall_trinkets.set_god")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("armor.godfall_trinkets.god_1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("armor.godfall_trinkets.god_2")
                    .withStyle(ChatFormatting.GRAY));
        }
        // 征伐套提示
        else if(mat == ArmorMaterials.CONQUEST_INGOT){
            tooltip.add(Component.translatable("armor.godfall_trinkets.set_conquest")
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("armor.godfall_trinkets.conquest_e1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("armor.godfall_trinkets.conquest_e2")
                    .withStyle(ChatFormatting.GRAY));
        }
        // 深渊征伐套（分单件描述 + 全套效果）
        else if(mat == ArmorMaterials.ABYSS_GOD_INGOT){
            tooltip.add(Component.translatable("armor.godfall_trinkets.set_abyss_god")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            // 根据槽位添加单件专属词条
            switch(this.getType()){
                case HELMET:
                    tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_helmet")
                            .withStyle(ChatFormatting.AQUA));
                    break;
                case CHESTPLATE:
                    tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_chest")
                            .withStyle(ChatFormatting.AQUA));
                    break;
                case LEGGINGS:
                    tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_legs")
                            .withStyle(ChatFormatting.AQUA));
                    break;
                case BOOTS:
                    tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_boots")
                            .withStyle(ChatFormatting.AQUA));
                    break;
            }
            // 全套套装效果统一显示
            tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_full1")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_full2")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_full3")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("armor.godfall_trinkets.abyss_full4")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
