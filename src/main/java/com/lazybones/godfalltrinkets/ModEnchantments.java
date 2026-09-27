package com.lazybones.godfalltrinkets;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GodfallTrinkets.MOD_ID);

    //1.神陨庇护：全部盔甲，最大5级
    public static final RegistryObject<Enchantment> GODFALL_SHELTER = ENCHANTMENTS.register("godfall_protection",
            () -> new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
                @Override public int getMaxLevel() { return 5; }
            });

    //2.碎核共鸣：武器，最大4级
    public static final RegistryObject<Enchantment> SHARD_RESONANCE = ENCHANTMENTS.register("shard_resonance",
            () -> new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND}){
                @Override public int getMaxLevel() { return 4; }
            });

    //3.虚空汲取：武器，最大3级
    public static final RegistryObject<Enchantment> VOID_DRAIN = ENCHANTMENTS.register("void_drain",
            () -> new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND}){
                @Override public int getMaxLevel() { return 3; }
            });

    //4.不朽誓约：仅胸甲，最大1级，复活，优先级高于图腾，附魔永不消耗
    public static final RegistryObject<Enchantment> IMMORTAL_OATH = ENCHANTMENTS.register("immortal_oath",
            () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST,
                    new EquipmentSlot[]{EquipmentSlot.CHEST}){
                @Override public int getMaxLevel() { return 1; }
            });

    //5.深渊凝视：武器，最大1级，给低血量敌人挂深渊标记debuff
    public static final RegistryObject<Enchantment> ABYSS_GAZE = ENCHANTMENTS.register("abyss_gaze",
            () -> new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND}){
                @Override public int getMaxLevel() { return 1; }
            });

    //6.神陨之怒：武器，最大5级，连续攻击叠神陨怒层debuff
    public static final RegistryObject<Enchantment> FURY_OF_GODFALL = ENCHANTMENTS.register("fury_of_godfall",
            () -> new Enchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND}){
                @Override public int getMaxLevel() { return 5; }
            });
}
