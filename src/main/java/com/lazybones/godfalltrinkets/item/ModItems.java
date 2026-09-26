package com.lazybones.godfalltrinkets.item;

import com.google.common.collect.Multimap;
import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.armor.ArmorMaterials;
import com.lazybones.godfalltrinkets.item.custom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import java.util.UUID;
import java.util.function.Consumer;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GodfallTrinkets.MOD_ID);

    public static final RegistryObject<Item> BROKEN_CORE = ITEMS.register("broken_core",
            () -> new BrokenCoreItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final RegistryObject<Item> GODFRAGMENT = ITEMS.register("godfragment",
            () -> new Item(new Item.Properties().stacksTo(64)));

    // 生成多行描述key数组
    private static String[] descLines(String baseName, int lineCount) {
        String[] arr = new String[lineCount];
        for (int i = 0; i < lineCount; i++) {
            arr[i] = "tooltip.godfall_trinkets.desc." + baseName + ".line" + (i + 1);
        }
        return arr;
    }

    // 唯一合法4参数注册方法：name, slotId, 多行key数组, 属性Consumer
    private static RegistryObject<Item> registerTrinket(
            String name,
            String slotId,
            String[] descKeys,
            Consumer<Multimap<Attribute, AttributeModifier>> attrs
    ) {
        return ITEMS.register(name, () -> new GodfallTrinketItem(
                new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
                slotId,
                descKeys,
                attrs
        ));
    }

    private static UUID uuid(String hex) {
        String padded = "00000000-0000-4000-a000-" + hex;
        return UUID.fromString(padded);
    }

    // 1 韧骨护符（2行描述）
    public static final RegistryObject<Item> TOUGH_BONE_AMULET = registerTrinket("tough_bone_amulet",
            "charm", descLines("tough_bone_amulet", 2), map -> {
                map.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid("b0100001"),
                        "tough_bone_hp", 3, AttributeModifier.Operation.ADDITION));
                map.put(Attributes.ARMOR, new AttributeModifier(uuid("b0100002"),
                        "tough_bone_armor", 2, AttributeModifier.Operation.ADDITION));
            });

    // 2 锐锋残戒（1行描述）
    public static final RegistryObject<Item> SHARP_EDGE_RING = registerTrinket("sharp_edge_ring",
            "ring", descLines("sharp_edge_ring", 1), map -> map.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(uuid("b0200001"), "sharp_edge_atk", 0.12,
                            AttributeModifier.Operation.MULTIPLY_BASE)));

    // 3 清秽腕饰（1行描述）
    public static final RegistryObject<Item> PURIFYING_BRACELET = registerTrinket("purifying_bracelet",
            "bracelet", descLines("purifying_bracelet", 1), null);

    // 4 噬魂血戒（2行描述）
    public static final RegistryObject<Item> SOUL_DRAIN_RING = registerTrinket("soul_drain_ring",
            "ring", descLines("soul_drain_ring", 2), null);

    // 5 生纹护符（2行描述）
    public static final RegistryObject<Item> VITAL_AMULET = registerTrinket("vital_amulet",
            "charm", descLines("vital_amulet", 2), null);

    // 6 挫刃护符（2行描述）
    public static final RegistryObject<Item> BLUNTING_AMULET = registerTrinket("blunting_amulet",
            "charm", descLines("blunting_amulet", 2), map -> {
                map.put(Attributes.ARMOR, new AttributeModifier(uuid("b0600001"),
                        "blunting_armor", 3, AttributeModifier.Operation.ADDITION));
                map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid("b0600002"),
                        "blunting_atk", 0.08, AttributeModifier.Operation.MULTIPLY_BASE));
            });

    // 7 韧心指环（2行描述）
    // 韧心指环：血量低于30%额外20%减伤
    public static final RegistryObject<Item> TOUGH_HEART_RING = registerTrinket("tough_heart_ring",
            "ring", descLines("tough_heart_ring", 2), map -> {
                map.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid("b0700001"), "tough_heart_hp", 4, AttributeModifier.Operation.ADDITION));
    });

    // 8 壁垒腕（3行描述）
    public static final RegistryObject<Item> BARRIER_BRACELET = registerTrinket("barrier_bracelet",
            "bracelet", descLines("barrier_bracelet", 3), null);

    // 9 濒生戒（3行描述）
    public static final RegistryObject<Item> DEATH_DEFY_RING = registerTrinket("death_defy_ring",
            "ring", descLines("death_defy_ring", 3), null);

    // 10 震斥护符（3行描述）
    public static final RegistryObject<Item> SHOCK_AMULET = registerTrinket("shock_amulet",
            "charm", descLines("shock_amulet", 3), map -> map.put(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(uuid("b1000001"), "shock_kb", 0.18,
                            AttributeModifier.Operation.MULTIPLY_BASE)));

    // 11 烬肤镯（2行描述）
    public static final RegistryObject<Item> EMBER_BRACELET = registerTrinket("ember_bracelet",
            "bracelet", descLines("ember_bracelet", 2), null);

    // 12 追猎戒（2行描述）
    public static final RegistryObject<Item> HUNTER_RING = registerTrinket("hunter_ring",
            "ring", descLines("hunter_ring", 2), null);

    // 13 渊纹铭刻戒（4行描述）
    public static final RegistryObject<Item> ABYSS_RUNE_RING = registerTrinket("abyss_rune_ring",
            "ring", descLines("abyss_rune_ring", 3), map -> map.put(Attributes.LUCK, new AttributeModifier(uuid("b1300001"),
                    "abyss_rune_luck", 0.2, AttributeModifier.Operation.MULTIPLY_BASE)));

    // 14 沃壤芽镯（2行描述）
    public static final RegistryObject<Item> FERTILE_BRACELET = registerTrinket("fertile_bracelet",
            "bracelet", descLines("fertile_bracelet", 2), null);

    // 15 熔渊锻符（3行描述）
    public static final RegistryObject<Item> FORGE_AMULET = registerTrinket("forge_amulet",
            "charm", descLines("forge_amulet", 3), null);

    // 16 深岩寻矿戒（3行描述）
    public static final RegistryObject<Item> DEEP_ROCK_RING = registerTrinket("deep_rock_ring",
            "ring", descLines("deep_rock_ring", 3), map -> map.put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid("b1600001"),
                    "deep_rock_dig", 0.15, AttributeModifier.Operation.MULTIPLY_BASE)));

    // 17 拾珍护符（2行描述）
    public static final RegistryObject<Item> TREASURE_AMULET = registerTrinket("treasure_amulet",
            "charm", descLines("treasure_amulet", 2), map -> map.put(Attributes.LUCK, new AttributeModifier(uuid("b1700001"),
                    "treasure_luck", 0.2, AttributeModifier.Operation.MULTIPLY_BASE)));

    // 18 繁生腕镯（3行描述）
    public static final RegistryObject<Item> PROSPERITY_BRACELET = registerTrinket("prosperity_bracelet",
            "bracelet", descLines("prosperity_bracelet", 3), null);

    //磨刀石
    public static final RegistryObject<Item> SHARP_EDGE_STONE = ITEMS.register("sharp_edge_stone", () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
    //神陨合金锭
    public static final RegistryObject<Item> GOD_INGOT = ITEMS.register("god_ingot", () -> new Item(new Item.Properties().stacksTo(64)));
    // 诅咒耐受全套盔甲
    public static final RegistryObject<ArmorItem> GOD_HELMET = ITEMS.register("god_helmet",
            ()-> new ArmorItems(ArmorMaterials.GOD_INGOT, ArmorItem.Type.HELMET,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                // 【核心修正】重写盔甲纹理路径
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    // 逻辑：如果是护腿（LEGS），就用layer_2.png，否则都用layer_1.png
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    public static final RegistryObject<ArmorItem> GOD_CHESTPLATE = ITEMS.register("god_chestplate",
            () -> new ArmorItems(ArmorMaterials.GOD_INGOT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                // 【核心修正】重写盔甲纹理路径
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    // 逻辑：如果是护腿（LEGS），就用layer_2.png，否则都用layer_1.png
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    public static final RegistryObject<ArmorItem> GOD_LEGGINGS = ITEMS.register("god_leggings",
            ()-> new ArmorItems(ArmorMaterials.GOD_INGOT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                // 【核心修正】重写盔甲纹理路径
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    // 逻辑：如果是护腿（LEGS），就用layer_2.png，否则都用layer_1.png
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    public static final RegistryObject<ArmorItem> GOD_BOOTS = ITEMS.register("god_boots",
            ()-> new ArmorItems(ArmorMaterials.GOD_INGOT, ArmorItem.Type.BOOTS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                // 【核心修正】重写盔甲纹理路径
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    // 逻辑：如果是护腿（LEGS），就用layer_2.png，否则都用layer_1.png
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    // 神陨合金升级模板
    public static final RegistryObject<Item> GOD_TEMPLATE = ITEMS.register("god_template", () -> new SmithingTemplateItem(
            // 1. appliesTo 蓝色小字：适用装备
            Component.translatable("tooltip.god_template.apply").withStyle(ChatFormatting.BLUE),
            // 2. ingredients 蓝色小字：所需材料
            Component.translatable("tooltip.god_template.material").withStyle(ChatFormatting.BLUE),
            // 3. title 锻造台顶部标题文本（Component，不是贴图！）
            Component.translatable("upgrade.god_alloy"),
            // 4. description 底部灰色描述
            Component.translatable("tooltip.god_template.desc").withStyle(ChatFormatting.GRAY),
            // 5. 【关键修正】模板物品悬浮图标的贴图路径Component（原版写法）
            Component.translatable("item.god_template"),
            // 6. 基底槽UI贴图列表（盔甲槽空白图）
            List.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/smithing_template_armor_slot")),
            // 7. 材料槽UI贴图列表（材料槽空白图）
            List.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/smithing_template_material_slot"))
    ));
    // 1. 咒厄坠粗胚（半成品，不可佩戴curio，普通物品）
    public static final RegistryObject<Item> CURSE_TRANSFER_BLANK = ITEMS.register("curse_transfer_blank",
            () -> new Item(new Item.Properties().stacksTo(16)));

    // 2. 咒厄转移坠（成品Curio饰品，专属curse_transfer槽）
    public static final RegistryObject<CurseTransferTrinketItem> CURSE_TRANSFER_TRINKET = ITEMS.register("curse_transfer_trinket",
            () -> new CurseTransferTrinketItem(new Item.Properties().durability(1000)));

    // 3. 咒槽解锁信物（右键解锁专属饰品栏）
    public static final RegistryObject<Item> CURSE_SLOT_UNLOCK_TALISMAN = ITEMS.register("curse_slot_unlock_talisman",
            () -> new CurseSlotUnlockItem(new Item.Properties().stacksTo(1)));
    // 征伐合金锭（征伐套修复/锻造材料）
    public static final RegistryObject<Item> CONQUEST_INGOT = ITEMS.register("conquest_ingot", () -> new Item(new Item.Properties().stacksTo(64)));
    // 征伐战斗套 - 头盔
    public static final RegistryObject<ArmorItem> CONQUEST_HELMET = ITEMS.register("conquest_helmet",
            ()-> new ArmorItems(ArmorMaterials.CONQUEST_INGOT, ArmorItem.Type.HELMET,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/conquest_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    // 征伐战斗套 - 胸甲
    public static final RegistryObject<ArmorItem> CONQUEST_CHESTPLATE = ITEMS.register("conquest_chestplate",
            () -> new ArmorItems(ArmorMaterials.CONQUEST_INGOT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/conquest_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    // 征伐战斗套 - 护腿
    public static final RegistryObject<ArmorItem> CONQUEST_LEGGINGS = ITEMS.register("conquest_leggings",
            ()-> new ArmorItems(ArmorMaterials.CONQUEST_INGOT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/conquest_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    // 征伐战斗套 - 靴子
    public static final RegistryObject<ArmorItem> CONQUEST_BOOTS = ITEMS.register("conquest_boots",
            ()-> new ArmorItems(ArmorMaterials.CONQUEST_INGOT, ArmorItem.Type.BOOTS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
                @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/conquest_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
    );
    // 深渊征伐锭
    public static final RegistryObject<Item> ABYSS_GOD_INGOT = ITEMS.register("abyss_god_ingot", () -> new Item(new Item.Properties().stacksTo(64)));
    // 深渊征伐战套 - 头盔
    public static final RegistryObject<ArmorItem> ABYSS_GOD_HELMET = ITEMS.register("abyss_god_helmet",
            () -> new ArmorItems(ArmorMaterials.ABYSS_GOD_INGOT, ArmorItem.Type.HELMET,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
        @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/abyss_god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
            );
    // 深渊征伐战套 - 胸甲
    public static final RegistryObject<ArmorItem> ABYSS_GOD_CHESTPLATE = ITEMS.register("abyss_god_chestplate",
            () -> new ArmorItems(ArmorMaterials.ABYSS_GOD_INGOT, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
        @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/abyss_god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
        }
                    }
    );
    // 深渊征伐战套 - 护腿
    public static final RegistryObject<ArmorItem> ABYSS_GOD_LEGGINGS = ITEMS.register("abyss_god_leggings",
            () -> new ArmorItems(ArmorMaterials.ABYSS_GOD_INGOT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
        @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                    String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/abyss_god_layer_";
                    texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
                    texturePath += ".png";
                    return texturePath;
                }
            }
            );
    // 深渊征伐战套 - 靴子
    public static final RegistryObject<ArmorItem> ABYSS_GOD_BOOTS = ITEMS.register("abyss_god_boots",
            () -> new ArmorItems(ArmorMaterials.ABYSS_GOD_INGOT, ArmorItem.Type.BOOTS,
                    new Item.Properties().rarity(Rarity.UNCOMMON)){
        @Override
                public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            String texturePath = GodfallTrinkets.MOD_ID + ":textures/models/armor/abyss_god_layer_";
            texturePath += (slot == EquipmentSlot.LEGS) ? "2" : "1";
            texturePath += ".png";
            return texturePath;
        }
                    }
    );
    // Boss掉落材料
    public static final RegistryObject<Item> WITHER_SHARD = ITEMS.register("wither_shard",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WARDEN_HEART = ITEMS.register("warden_heart",
            () -> new Item(new Item.Properties().stacksTo(64)));
    // ================== 凋零4件极限考验饰品 ==================
// 枯骨之心 - charm护符槽
    public static final RegistryObject<Item> WITHER_HEART = registerTrinket("wither_heart",
            "body", descLines("wither_heart", 2), null);

    // 王者之握 - bracelet手镯槽
    public static final RegistryObject<Item> KINGS_GRIP = registerTrinket("kings_grip",
            "bracelet", descLines("kings_grip", 1), null);

    // 凋零王冠 - head头饰槽（新增自定义槽）
    public static final RegistryObject<Item> WITHER_CROWN = registerTrinket("wither_crown",
            "head", descLines("wither_crown", 2), null);

    // 凋零脊骨坠 - necklace项链槽（新增自定义槽）
    public static final RegistryObject<Item> WITHER_SPINE = registerTrinket("wither_spine",
            "necklace", descLines("wither_spine", 1), null);

    // ================== 监守者4件极限反差饰品 ==================
// 幽匿之眼 - ring戒指槽
    public static final RegistryObject<Item> WARDEN_EYE = registerTrinket("warden_eye",
            "ring", descLines("warden_eye", 1), null);

    // 幽匿脉动 - ring戒指槽
    public static final RegistryObject<Item> WARDEN_PULSE = registerTrinket("warden_pulse",
            "ring", descLines("warden_pulse", 1), null);

    // 幽匿重压 - body胸饰槽（新增自定义槽）
    public static final RegistryObject<Item> WARDEN_WEIGHT = registerTrinket("warden_weight",
            "body", descLines("warden_weight", 1), null);

    // 幽匿破甲 - bracelet手镯槽
    public static final RegistryObject<Item> WARDEN_ARMORBREAK = registerTrinket("warden_armorbreak",
            "bracelet", descLines("warden_armorbreak", 1), null);
    // 末影龙终焉龙饰
// 必掉护符：龙核之心
    public static final RegistryObject<Item> DRAGON_CORE_HEART = registerTrinket("dragon_core_heart",
            "body", descLines("dragon_core_heart", 2), null);
    // 条件手镯：终末之眼（替换虚空龙翼）
    public static final RegistryObject<Item> EYE_OF_THE_END = registerTrinket("eye_of_the_end",
            "charm", descLines("eye_of_the_end", 3), null);
    // 条件头饰：残晶冠冕
    public static final RegistryObject<Item> CRYSTAL_CROWN = registerTrinket("crystal_crown",
            "head", descLines("crystal_crown", 2), null);
    // 条件项链：寂灭龙脊
    public static final RegistryObject<Item> DRAGON_SPINE_END = registerTrinket("dragon_spine_end",
            "back", descLines("dragon_spine_end", 4), null);
    //独特材料：末影龙魂
    public static final RegistryObject<Item> DRAGON_SOUL = ITEMS.register("dragon_soul",
            ()-> new Item(new Item.Properties().stacksTo(64)));
    // ========== 九阶进阶长剑 独立武器 ==========
// 0阶 纯白剑胚（小幅拉高至3点，锻造载体）
    public static final RegistryObject<Item> WHITE_SWORD_BLANK = ITEMS.register("white_sword_blank",
            () -> new SwordItem(Tiers.WOOD, 3, -1.6F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 1阶 锻铁白剑
    public static final RegistryObject<Item> IRON_WHITE_SWORD = ITEMS.register("iron_white_sword",
            () -> new SwordItem(Tiers.IRON, 7, -2.3F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 2阶 碎钻白刃
    public static final RegistryObject<Item> DIAMOND_WHITE_BLADE = ITEMS.register("diamond_white_blade",
            () -> new SwordItem(Tiers.DIAMOND, 10, -2.1F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 3阶 碎神影剑
    public static final RegistryObject<Item> SHADOW_SWORD = ITEMS.register("shadow_sword",
            () -> new SwordItem(Tiers.NETHERITE, 14, -2.0F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 4阶 神核征伐剑
    public static final RegistryObject<Item> CORE_CONQUEST_SWORD = ITEMS.register("core_conquest_sword",
            () -> new SwordItem(Tiers.NETHERITE, 18, -1.9F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 5阶 战伐神锋
    public static final RegistryObject<Item> WAR_BLADE = ITEMS.register("war_blade",
            () -> new SwordItem(Tiers.NETHERITE, 23, -1.7F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 6阶 凋寂断刃
    public static final RegistryObject<Item> WITHER_BLADE = ITEMS.register("wither_blade",
            () -> new SwordItem(Tiers.NETHERITE, 28, -1.6F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 7阶 幽守战剑
    public static final RegistryObject<Item> WARDEN_SWORD = ITEMS.register("warden_sword",
            () -> new SwordItem(Tiers.NETHERITE, 34, -1.5F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 8阶 深渊征伐圣剑
    public static final RegistryObject<Item> ABYSS_SWORD = ITEMS.register("abyss_sword",
            () -> new SwordItem(Tiers.NETHERITE, 42, -1.4F, new Item.Properties().stacksTo(1).fireResistant()){});
    // 9阶 终末龙征（毕业）
    public static final RegistryObject<Item> END_DRAGON_SWORD = ITEMS.register("end_dragon_sword",
            () -> new SwordItem(Tiers.NETHERITE, 55, -1.2F, new Item.Properties().stacksTo(1).fireResistant()){});
    // ===================== 13件神位碎片｜专属god_shard_slot槽 =====================
// registerTrinket参数：物品ID / 槽位 / 描述行数key数组 / 属性（无填null）
    public static final RegistryObject<Item> SHARD_SKY = registerTrinket("shard_sky", "god_shard_slot", descLines("shard_sky", 1), null);
    public static final RegistryObject<Item> SHARD_SUN = registerTrinket("shard_sun", "god_shard_slot", descLines("shard_sun", 1), null);
    public static final RegistryObject<Item> SHARD_LIFE = registerTrinket("shard_life", "god_shard_slot", descLines("shard_life", 1), null);
    public static final RegistryObject<Item> SHARD_STRENGTH = registerTrinket("shard_strength", "god_shard_slot", descLines("shard_strength", 1), null);
    public static final RegistryObject<Item> SHARD_FATE = registerTrinket("shard_fate", "god_shard_slot", descLines("shard_fate", 1), null);
    public static final RegistryObject<Item> SHARD_WIND = registerTrinket("shard_wind", "god_shard_slot", descLines("shard_wind", 1), null);
    public static final RegistryObject<Item> SHARD_SPIRIT = registerTrinket("shard_spirit", "god_shard_slot", descLines("shard_spirit", 1), null);
    public static final RegistryObject<Item> SHARD_BARRIER = registerTrinket("shard_barrier", "god_shard_slot", descLines("shard_barrier", 1), null);
    public static final RegistryObject<Item> SHARD_VOID = registerTrinket("shard_void", "god_shard_slot", descLines("shard_void", 1), null);
    public static final RegistryObject<Item> SHARD_SHADOW = registerTrinket("shard_shadow", "god_shard_slot", descLines("shard_shadow", 1), null);
    public static final RegistryObject<Item> SHARD_THUNDER = registerTrinket("shard_thunder", "god_shard_slot", descLines("shard_thunder", 1), null);
    public static final RegistryObject<Item> SHARD_STAR = registerTrinket("shard_star", "god_shard_slot", descLines("shard_star", 1), null);
    public static final RegistryObject<Item> SHARD_RUNE = registerTrinket("shard_rune", "god_shard_slot", descLines("shard_rune", 1), null);
    // ===================== 神核进化道具 =====================
    public static final RegistryObject<Item> GOD_EVOLUTION_STONE = ITEMS.register("god_evolution_stone",
            GodEvolutionStoneItem::new);

    public static final RegistryObject<GodCoreItem> GOD_CORE = ITEMS.register("god_core",
            () -> new GodCoreItem(new Item.Properties().stacksTo(1).fireResistant()));
    // 功业徽记：必须注册为 AdvancementTallyTrinketItem，才能触发其自定义的按Shift悬浮文本与成就加成逻辑
    public static final RegistryObject<AdvancementTallyTrinketItem> ADVANCEMENT_TALLY_TRINKET = ITEMS.register("advancement_tally_trinket",
            () -> new AdvancementTallyTrinketItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
