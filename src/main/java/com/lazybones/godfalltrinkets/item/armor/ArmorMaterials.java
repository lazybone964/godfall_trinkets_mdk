package com.lazybones.godfalltrinkets.item.armor;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum ArmorMaterials implements ArmorMaterial {
    // 神陨合金诅咒耐受套
    GOD_INGOT(
            "god_ingot",// 材质名称，用于拼接纹理路径
            42,// 耐久值
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 4);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.BOOTS, 4);
            }),// 防御值
            18,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.2F,
            () -> Ingredient.of(ModItems.GOD_INGOT.get())
    ),
    ABYSS_GOD_INGOT(
            "abyss_god_ingot",
            50, // 更高耐久
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 12);
                map.put(ArmorItem.Type.LEGGINGS, 9);
                map.put(ArmorItem.Type.BOOTS, 6);
            }),
            25, // 更高附魔值
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5.0F, // 更高韧性
            0.3F, // 更高击退抗性
            () -> Ingredient.of(ModItems.ABYSS_GOD_INGOT.get())
    ),
    // 征伐战斗套材质
    CONQUEST_INGOT(
            "conquest_ingot",
                    45, // 耐久倍率（比神陨套更高）
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 5);
                map.put(ArmorItem.Type.CHESTPLATE, 10);
                map.put(ArmorItem.Type.LEGGINGS, 8);
                map.put(ArmorItem.Type.BOOTS, 5);
            }),
            20, // 附魔值
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.5F, // 韧性
            0.25F, // 击退抗性
            () -> Ingredient.of(ModItems.CONQUEST_INGOT.get()) // 修复锭，后面在ModItems注册
    );

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE;
    static {
        HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.HELMET, 13);
            map.put(ArmorItem.Type.CHESTPLATE, 15);
            map.put(ArmorItem.Type.LEGGINGS, 16);
            map.put(ArmorItem.Type.BOOTS, 11);
        });
    }

    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> defense;
    private final int enchantValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResist;
    private final Supplier<Ingredient> repairIngredient;

    ArmorMaterials(String name, int durMult, EnumMap<ArmorItem.Type, Integer> def, int ench, SoundEvent sound, float tough, float kb, Supplier<Ingredient> rep) {
        this.name = name;
        this.durabilityMultiplier = durMult;
        this.defense = def;
        this.enchantValue = ench;
        this.equipSound = sound;
        this.toughness = tough;
        this.knockbackResist = kb;
        this.repairIngredient = rep;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier;
    }
    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.defense.get(type);
    }
    @Override
    public int getEnchantmentValue() { return this.enchantValue; }
    @Override
    public SoundEvent getEquipSound() { return this.equipSound; }
    @Override
    public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
    @Override
    public String getName() { return GodfallTrinkets.MOD_ID + ":" + this.name; }
    @Override
    public float getToughness() { return this.toughness; }
    @Override
    public float getKnockbackResistance() { return this.knockbackResist; }
}
