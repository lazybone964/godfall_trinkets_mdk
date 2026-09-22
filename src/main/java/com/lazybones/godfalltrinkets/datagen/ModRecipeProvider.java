package com.lazybones.godfalltrinkets.datagen;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.Consumer;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        Item fragment = ModItems.GODFRAGMENT.get();

        // ====================== 1.韧骨护符 tough_bone_amulet
        // 配方：2神陨碎片 + 4骨头 + 2铁粒 + 皮革
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TOUGH_BONE_AMULET.get())
                .pattern("BFB")
                .pattern("N#N")
                .pattern("BFB")
                .define('F', Ingredient.of(fragment))    // 神陨碎片 x2
                .define('B', Ingredient.of(Items.BONE))  // 骨头 x4
                .define('N', Ingredient.of(Items.IRON_NUGGET)) //铁粒x2
                .define('#', Ingredient.of(Items.LEATHER))    //皮革x1
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 2.锐锋残戒 sharp_edge_ring
        // 1神陨碎片 + 6铁锭 + 燧石
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARP_EDGE_RING.get())
                .pattern("IFI")
                .pattern("I I")
                .pattern("ISI")
                .define('F', Ingredient.of(fragment))
                .define('I', Ingredient.of(Items.IRON_INGOT))
                .define('S', Ingredient.of(Items.FLINT))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 3.清秽腕饰 purifying_bracelet
        // 2神陨碎片 + 4蜘蛛眼 + 糖 + 线
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PURIFYING_BRACELET.get())
                .pattern("TST")
                .pattern("F F")
                .pattern("T#T")
                .define('F', Ingredient.of(fragment))
                .define('T', Ingredient.of(Items.SPIDER_EYE))
                .define('S', Ingredient.of(Items.SUGAR))
                .define('#', Ingredient.of(Items.STRING))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 4.噬魂血戒 soul_drain_ring
        // 2神陨碎片 + 4腐肉 + 红石 + 铁粒
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SOUL_DRAIN_RING.get())
                .pattern("R#R")
                .pattern("F F")
                .pattern("RNR")
                .define('F', Ingredient.of(fragment))
                .define('R', Ingredient.of(Items.ROTTEN_FLESH))
                .define('#', Ingredient.of(Items.REDSTONE))
                .define('N', Ingredient.of(Items.IRON_NUGGET))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 5.生纹护符 vital_amulet
        // 3神陨碎片 + 3生牛肉 + 线
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.VITAL_AMULET.get())
                .pattern("F#F")
                .pattern("MMM")
                .pattern(" F ")
                .define('F', Ingredient.of(fragment))
                .define('M', Ingredient.of(Items.BEEF))
                .define('#', Ingredient.of(Items.STRING))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 6.挫刃护符 blunting_amulet
        // 3神陨碎片 + 5黑曜石 + 燧石
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLUNTING_AMULET.get())
                .pattern("OFO")
                .pattern("O#O")
                .pattern("FOF")
                .define('F', Ingredient.of(fragment))
                .define('O', Ingredient.of(Items.OBSIDIAN))
                .define('#', Ingredient.of(Items.FLINT))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 7.韧心指环 tough_heart_ring
        // 2神陨碎片 + 3铁粒 + 岩浆膏
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TOUGH_HEART_RING.get())
                .pattern("N#N")
                .pattern("F F")
                .pattern(" N ")
                .define('F', Ingredient.of(fragment))
                .define('N', Ingredient.of(Items.IRON_NUGGET))
                .define('#', Ingredient.of(Items.MAGMA_CREAM))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 8.壁垒腕 barrier_bracelet
        // 2神陨碎片 + 2黑曜石 + 盾牌 + 铁粒
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BARRIER_BRACELET.get())
                .pattern("O#O")
                .pattern("N N")
                .pattern("FNF")
                .define('F', Ingredient.of(fragment))
                .define('O', Ingredient.of(Items.OBSIDIAN))
                .define('#', Ingredient.of(Items.SHIELD))
                .define('N', Ingredient.of(Items.IRON_NUGGET))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 9.濒生戒 death_defy_ring
        // 2神陨碎片 + 1岩浆膏 + 2金粒+ 腐肉
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DEATH_DEFY_RING.get())
                .pattern("G#G")
                .pattern("F F")
                .pattern(" R ")
                .define('F', Ingredient.of(fragment))
                .define('G', Ingredient.of(Items.GOLD_NUGGET))
                .define('#', Ingredient.of(Items.MAGMA_CREAM))
                .define('R', Ingredient.of(Items.ROTTEN_FLESH))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 10.震斥护符 shock_amulet
        // 3神陨碎片 + 4铁锭 + 火药
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHOCK_AMULET.get())
                .pattern("IFI")
                .pattern("IPI")
                .pattern(" F ")
                .define('F', Ingredient.of(fragment))
                .define('I', Ingredient.of(Items.IRON_INGOT))
                .define('P', Ingredient.of(Items.GUNPOWDER))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 11.烬肤镯 ember_bracelet
        // 2神陨碎片 + 岩浆桶 + 3煤炭
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EMBER_BRACELET.get())
                .pattern("CLC")
                .pattern("F F")
                .pattern(" C ")
                .define('F', Ingredient.of(fragment))
                .define('C', Ingredient.of(Items.COAL))
                .define('L', Ingredient.of(Items.LAVA_BUCKET))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 12.追猎戒 hunter_ring
        // 2神陨碎片 + 燧石 + 红石 + 4骨头
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HUNTER_RING.get())
                .pattern("B#B")
                .pattern("F F")
                .pattern("BSB")
                .define('F', Ingredient.of(fragment))
                .define('B', Ingredient.of(Items.BONE))
                .define('S', Ingredient.of(Items.FLINT))
                .define('#', Ingredient.of(Items.REDSTONE))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 13.渊纹铭刻戒 abyss_rune_ring
        // 2神陨碎片 + 2青金石 + 书 + 金粒
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ABYSS_RUNE_RING.get())
                .pattern("L#L")
                .pattern("F F")
                .pattern(" G ")
                .define('F', Ingredient.of(fragment))
                .define('L', Ingredient.of(Items.LAPIS_LAZULI))
                .define('#', Ingredient.of(Items.BOOK))
                .define('G', Ingredient.of(Items.GOLD_NUGGET))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 14.沃壤芽镯 fertile_bracelet
        // 2神陨碎片 + 4小麦种子 + 骨粉 + 藤蔓
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FERTILE_BRACELET.get())
                .pattern("SBS")
                .pattern("F F")
                .pattern("S#S")
                .define('F', Ingredient.of(fragment))
                .define('S', Ingredient.of(Items.WHEAT_SEEDS))
                .define('B', Ingredient.of(Items.BONE_MEAL))
                .define('#', Ingredient.of(Items.VINE))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 15.熔渊锻符 forge_amulet
        // 2神陨碎片 + 4铁锭 + 岩浆桶 + 黑曜石
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FORGE_AMULET.get())
                .pattern("IFI")
                .pattern("#L#")
                .pattern("IFI")
                .define('F', Ingredient.of(fragment))
                .define('I', Ingredient.of(Items.IRON_INGOT))
                .define('L', Ingredient.of(Items.LAVA_BUCKET))
                .define('#', Ingredient.of(Items.OBSIDIAN))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 16.深岩寻矿戒 deep_rock_ring
        // 2神陨碎片 + 3铁矿石 + 红石火把 + 粗金
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DEEP_ROCK_RING.get())
                .pattern("O#O")
                .pattern("F F")
                .pattern(" R ")
                .define('F', Ingredient.of(fragment))
                .define('O', Ingredient.of(Items.RAW_IRON))
                .define('#', Ingredient.of(Items.REDSTONE_TORCH))
                .define('R', Ingredient.of(Items.RAW_GOLD))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 17.拾珍护符 treasure_amulet
        // 2神陨碎片 + 线 + 皮革 + 3金粒
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TREASURE_AMULET.get())
                .pattern("G#G")
                .pattern("FLF")
                .pattern(" G ")
                .define('F', Ingredient.of(fragment))
                .define('G', Ingredient.of(Items.GOLD_NUGGET))
                .define('#', Ingredient.of(Items.STRING))
                .define('L', Ingredient.of(Items.LEATHER))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);

        // ====================== 18.繁生腕镯 prosperity_bracelet
        // 2神陨碎片 + 4小麦 + 鸡蛋 + 皮革
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PROSPERITY_BRACELET.get())
                .pattern("WEW")
                .pattern("F F")
                .pattern("W#W")
                .define('F', Ingredient.of(fragment))
                .define('W', Ingredient.of(Items.WHEAT))
                .define('E', Ingredient.of(Items.EGG))
                .define('#', Ingredient.of(Items.LEATHER))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);
        // ====================== 19.神陨合金锭 god_ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GOD_INGOT.get())
                .pattern("FFF")
                .pattern("F##")
                .pattern("## ")
                .define('F', Ingredient.of(fragment))
                .define('#', Ingredient.of(Items.GOLD_INGOT))
                .unlockedBy("has_fragment", has(fragment))
                .save(consumer);
        // ====================== 20.神陨合金块 god_block
        // ====================== 21.诅咒耐受头盔 god_helmet
        // 头盔
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_HELMET),
                        Ingredient.of(ModItems.GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.GOD_HELMET.get()
                ).unlocks("has_god_ingot", has(ModItems.GOD_INGOT.get()))
                .save(consumer, "god_helmet_smith");

// 胸甲
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        Ingredient.of(ModItems.GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.GOD_CHESTPLATE.get()
                ).unlocks("has_god_ingot", has(ModItems.GOD_INGOT.get()))
                .save(consumer, "god_chestplate_smith");

// 护腿
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_LEGGINGS),
                        Ingredient.of(ModItems.GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.GOD_LEGGINGS.get()
                ).unlocks("has_god_ingot", has(ModItems.GOD_INGOT.get()))
                .save(consumer, "god_leggings_smith");

// 靴子
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_BOOTS),
                        Ingredient.of(ModItems.GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.GOD_BOOTS.get()
                ).unlocks("has_god_ingot", has(ModItems.GOD_INGOT.get()))
                .save(consumer, "god_boots_smith");
        // 模板合成：神陨碎片包裹模板
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOD_TEMPLATE.get())
                .pattern("FFF")
                .pattern("F#F")
                .pattern("FFF")
                .define('F', Ingredient.of(ModItems.GODFRAGMENT.get()))
                .define('#', Ingredient.of(Items.GOLD_INGOT))
                .unlockedBy("has_fragment", has(ModItems.GODFRAGMENT.get()))
                .save(consumer);
        // ====================== 咒厄转移坠 无序合成：粗胚+磨刀石+3神陨碎片
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.CURSE_TRANSFER_TRINKET.get())
                .requires(ModItems.CURSE_TRANSFER_BLANK.get())    // 粗胚
                .requires(ModItems.SHARP_EDGE_STONE.get())       // 磨刀石
                .requires(fragment, 3)         // 3个神陨碎片
                .unlockedBy("has_godfragment", has(ModItems.GODFRAGMENT.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARP_EDGE_STONE.get())
                .pattern(" GG")
                .pattern("G# ")
                .pattern("G  ")
                .define('G', Ingredient.of(Items.COBBLESTONE))
                .define('#', Ingredient.of(fragment))
                .unlockedBy("has_sharp_edge_stone", has(ModItems.SHARP_EDGE_STONE.get()))
                .save(consumer);
        // 征伐头盔锻造
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.GOD_HELMET.get()),
                        Ingredient.of(ModItems.CONQUEST_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.CONQUEST_HELMET.get()
                ).unlocks("has_conquest_ingot", has(ModItems.CONQUEST_INGOT.get()))
                .save(consumer, "conquest_helmet_smith");

// 征伐胸甲锻造
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.GOD_CHESTPLATE.get()),
                        Ingredient.of(ModItems.CONQUEST_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.CONQUEST_CHESTPLATE.get()
                ).unlocks("has_conquest_ingot", has(ModItems.CONQUEST_INGOT.get()))
                .save(consumer, "conquest_chestplate_smith");

// 征伐护腿锻造
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.GOD_LEGGINGS.get()),
                        Ingredient.of(ModItems.CONQUEST_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.CONQUEST_LEGGINGS.get()
                ).unlocks("has_conquest_ingot", has(ModItems.CONQUEST_INGOT.get()))
                .save(consumer, "conquest_leggings_smith");

// 征伐靴子锻造
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.GOD_BOOTS.get()),
                        Ingredient.of(ModItems.CONQUEST_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.CONQUEST_BOOTS.get()
                ).unlocks("has_conquest_ingot", has(ModItems.CONQUEST_INGOT.get()))
                .save(consumer, "conquest_boots_smith");
        // 征伐合金锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CONQUEST_INGOT.get())
                .requires(Items.BLAZE_ROD)
                .requires(Items.NETHERITE_SCRAP)
                .requires(ModItems.GOD_INGOT.get(), 2)
                .unlockedBy("has_god_ingot", has(ModItems.GOD_INGOT.get()))
                .save(consumer);
        // 深渊神格神陨锭：凋零碎片+坚守之心+2征伐合金
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ABYSS_GOD_INGOT.get())
                .requires(ModItems.WITHER_SHARD.get())
                .requires(ModItems.WARDEN_HEART.get())
                .requires(ModItems.CONQUEST_INGOT.get(), 2)
                .unlockedBy("has_wither_shard", has(ModItems.WITHER_SHARD.get()))
                .save(consumer, "abyss_god_ingot_craft");
        // 深渊征伐头盔
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.CONQUEST_HELMET.get()),
                        Ingredient.of(ModItems.ABYSS_GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.ABYSS_GOD_HELMET.get()
                ).unlocks("has_abyss_ingot", has(ModItems.ABYSS_GOD_INGOT.get()))
                .save(consumer, "abyss_conquest_helmet_smith");

// 深渊征伐胸甲
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.CONQUEST_CHESTPLATE.get()),
                        Ingredient.of(ModItems.ABYSS_GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.ABYSS_GOD_CHESTPLATE.get()
                ).unlocks("has_abyss_ingot", has(ModItems.ABYSS_GOD_INGOT.get()))
                .save(consumer, "abyss_conquest_chestplate_smith");

// 深渊征伐护腿
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.CONQUEST_LEGGINGS.get()),
                        Ingredient.of(ModItems.ABYSS_GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.ABYSS_GOD_LEGGINGS.get()
                ).unlocks("has_abyss_ingot", has(ModItems.ABYSS_GOD_INGOT.get()))
                .save(consumer, "abyss_conquest_leggings_smith");

// 深渊征伐战靴
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.CONQUEST_BOOTS.get()),
                        Ingredient.of(ModItems.ABYSS_GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.ABYSS_GOD_BOOTS.get()
                ).unlocks("has_abyss_ingot", has(ModItems.ABYSS_GOD_INGOT.get()))
                .save(consumer, "abyss_conquest_boots_smith");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CURSE_TRANSFER_TRINKET.get())
                .requires(ModItems.CURSE_TRANSFER_TRINKET.get())
                .requires(ModItems.GODFRAGMENT.get())
                .unlockedBy("has_godfragment", has(ModItems.GODFRAGMENT.get()))
                .save(consumer, "curse_transfer_trinket_craft");
        // 纯下界材料从零合成凋零头颅（无需任何头颅）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WITHER_SKELETON_SKULL)
                .pattern("BCB")
                .pattern("CSC")
                .pattern("BXB")
                .define('B', Ingredient.of(Items.BLACKSTONE))
                .define('C', Ingredient.of(Items.COAL_BLOCK))
                .define('S', Ingredient.of(Items.SOUL_SAND))
                .define('X', Ingredient.of(Items.BONE))
                .unlockedBy("has_blackstone", has(Items.BLACKSTONE))
                .save(consumer, "wither_skull_craft_vanilla_nether");
        // 2. 灵魂沙复制凋零头（前期无模组材料）
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.WITHER_SKELETON_SKULL, 2)
                .pattern("NNN")
                .pattern("NWN")
                .pattern("NNN")
                .define('N', Items.SOUL_SAND)
                .define('W', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("has_wither_skull", has(Items.WITHER_SKELETON_SKULL))
                .save(consumer, "wither_skull_duplicate_soulsand");
        // ====================== 九阶长剑 铁砧进度配方（无序，仅加NBT，不生成新物品）
// T0 纯白剑胚 + 铁锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.WHITE_SWORD_BLANK.get())
                .requires(ModItems.WHITE_SWORD_BLANK.get())
                .requires(Items.IRON_INGOT)
                .unlockedBy("has_white_sword", has(ModItems.WHITE_SWORD_BLANK.get()))
                .save(consumer, "anvil_sword_t0_iron");
// T1 锻铁白剑 + 钻石
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.IRON_WHITE_SWORD.get())
                .requires(ModItems.IRON_WHITE_SWORD.get())
                .requires(Items.DIAMOND)
                .unlockedBy("has_iron_sword", has(ModItems.IRON_WHITE_SWORD.get()))
                .save(consumer, "anvil_sword_t1_diamond");
// T2 碎钻白刃 + 神陨碎片
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.DIAMOND_WHITE_BLADE.get())
                .requires(ModItems.DIAMOND_WHITE_BLADE.get())
                .requires(ModItems.GODFRAGMENT.get())
                .unlockedBy("has_diamond_blade", has(ModItems.DIAMOND_WHITE_BLADE.get()))
                .save(consumer, "anvil_sword_t2_shadow");
// T3 碎神影剑 + 神陨合金锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.SHADOW_SWORD.get())
                .requires(ModItems.SHADOW_SWORD.get())
                .requires(ModItems.GOD_INGOT.get())
                .unlockedBy("has_shadow_sword", has(ModItems.SHADOW_SWORD.get()))
                .save(consumer, "anvil_sword_t3_core");
// T4 神核征伐剑 + 征伐合金锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.CORE_CONQUEST_SWORD.get())
                .requires(ModItems.CORE_CONQUEST_SWORD.get())
                .requires(ModItems.CONQUEST_INGOT.get())
                .unlockedBy("has_core_sword", has(ModItems.CORE_CONQUEST_SWORD.get()))
                .save(consumer, "anvil_sword_t4_war");
// T5 战伐神锋 + 凋零碎片
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.WAR_BLADE.get())
                .requires(ModItems.WAR_BLADE.get())
                .requires(ModItems.WITHER_SHARD.get())
                .unlockedBy("has_war_blade", has(ModItems.WAR_BLADE.get()))
                .save(consumer, "anvil_sword_t5_wither");
// T6 凋寂断刃 + 监守之心
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.WITHER_BLADE.get())
                .requires(ModItems.WITHER_BLADE.get())
                .requires(ModItems.WARDEN_HEART.get())
                .unlockedBy("has_wither_blade", has(ModItems.WITHER_BLADE.get()))
                .save(consumer, "anvil_sword_t6_warden");
// T7 幽守战剑 + 深渊征伐锭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.WARDEN_SWORD.get())
                .requires(ModItems.WARDEN_SWORD.get())
                .requires(ModItems.ABYSS_GOD_INGOT.get())
                .unlockedBy("has_warden_sword", has(ModItems.WARDEN_SWORD.get()))
                .save(consumer, "anvil_sword_t7_abyss");
// T8 深渊征伐圣剑 + 末影龙魂
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT,ModItems.ABYSS_SWORD.get())
                .requires(ModItems.ABYSS_SWORD.get())
                .requires(ModItems.DRAGON_SOUL.get())
                .unlockedBy("has_abyss_sword", has(ModItems.ABYSS_SWORD.get()))
                .save(consumer, "anvil_sword_t8_dragon");

// ====================== 九阶长剑 锻造台进阶配方（统一神陨模板，满进度才可合成）
// T0→T1 纯白剑胚升锻铁白剑
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.WHITE_SWORD_BLANK.get()),
                        Ingredient.of(Items.IRON_INGOT),
                        RecipeCategory.TOOLS,
                        ModItems.IRON_WHITE_SWORD.get()
                ).unlocks("smith_t0_up", has(ModItems.WHITE_SWORD_BLANK.get()))
                .save(consumer, "smith_sword_t0_t1");
// T1→T2
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.IRON_WHITE_SWORD.get()),
                        Ingredient.of(Items.DIAMOND),
                        RecipeCategory.TOOLS,
                        ModItems.DIAMOND_WHITE_BLADE.get()
                ).unlocks("smith_t1_up", has(ModItems.IRON_WHITE_SWORD.get()))
                .save(consumer, "smith_sword_t1_t2");
// T2→T3
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.DIAMOND_WHITE_BLADE.get()),
                        Ingredient.of(ModItems.GODFRAGMENT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.SHADOW_SWORD.get()
                ).unlocks("smith_t2_up", has(ModItems.DIAMOND_WHITE_BLADE.get()))
                .save(consumer, "smith_sword_t2_t3");
// T3→T4
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.SHADOW_SWORD.get()),
                        Ingredient.of(ModItems.GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.CORE_CONQUEST_SWORD.get()
                ).unlocks("smith_t3_up", has(ModItems.SHADOW_SWORD.get()))
                .save(consumer, "smith_sword_t3_t4");
// T4→T5
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.CORE_CONQUEST_SWORD.get()),
                        Ingredient.of(ModItems.CONQUEST_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.WAR_BLADE.get()
                ).unlocks("smith_t4_up", has(ModItems.CORE_CONQUEST_SWORD.get()))
                .save(consumer, "smith_sword_t4_t5");
// T5→T6
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.WAR_BLADE.get()),
                        Ingredient.of(ModItems.WITHER_SHARD.get()),
                        RecipeCategory.TOOLS,
                        ModItems.WITHER_BLADE.get()
                ).unlocks("smith_t5_up", has(ModItems.WAR_BLADE.get()))
                .save(consumer, "smith_sword_t5_t6");
// T6→T7
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.WITHER_BLADE.get()),
                        Ingredient.of(ModItems.WARDEN_HEART.get()),
                        RecipeCategory.TOOLS,
                        ModItems.WARDEN_SWORD.get()
                ).unlocks("smith_t6_up", has(ModItems.WITHER_BLADE.get()))
                .save(consumer, "smith_sword_t6_t7");
// T7→T8
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.WARDEN_SWORD.get()),
                        Ingredient.of(ModItems.ABYSS_GOD_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.ABYSS_SWORD.get()
                ).unlocks("smith_t7_up", has(ModItems.WARDEN_SWORD.get()))
                .save(consumer, "smith_sword_t7_t8");
// T8→T9 毕业龙剑
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.GOD_TEMPLATE.get()),
                        Ingredient.of(ModItems.ABYSS_SWORD.get()),
                        Ingredient.of(ModItems.DRAGON_SOUL.get()),
                        RecipeCategory.TOOLS,
                        ModItems.END_DRAGON_SWORD.get()
                ).unlocks("smith_t8_up", has(ModItems.ABYSS_SWORD.get()))
                .save(consumer, "smith_sword_t8_t9");
    }
}