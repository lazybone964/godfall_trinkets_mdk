package com.lazybones.godfalltrinkets.datagen;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GodfallTrinkets.MOD_ID, existingFileHelper);
    }
    @Override
    protected void registerModels() {
        basicItem(ModItems.BROKEN_CORE.get());
        basicItem(ModItems.GODFRAGMENT.get());
        // 18 trinkets
        basicItem(ModItems.TOUGH_BONE_AMULET.get());
        basicItem(ModItems.SHARP_EDGE_RING.get());
        basicItem(ModItems.PURIFYING_BRACELET.get());
        basicItem(ModItems.SOUL_DRAIN_RING.get());
        basicItem(ModItems.VITAL_AMULET.get());
        basicItem(ModItems.BLUNTING_AMULET.get());
        basicItem(ModItems.TOUGH_HEART_RING.get());
        basicItem(ModItems.BARRIER_BRACELET.get());
        basicItem(ModItems.DEATH_DEFY_RING.get());
        basicItem(ModItems.SHOCK_AMULET.get());
        basicItem(ModItems.EMBER_BRACELET.get());
        basicItem(ModItems.HUNTER_RING.get());
        basicItem(ModItems.ABYSS_RUNE_RING.get());
        basicItem(ModItems.FERTILE_BRACELET.get());
        basicItem(ModItems.FORGE_AMULET.get());
        basicItem(ModItems.DEEP_ROCK_RING.get());
        basicItem(ModItems.TREASURE_AMULET.get());
        basicItem(ModItems.PROSPERITY_BRACELET.get());
        basicItem(ModItems.SHARP_EDGE_STONE.get());
        basicItem(ModItems.GOD_INGOT.get());
        basicItem(ModItems.GOD_HELMET.get());
        basicItem(ModItems.GOD_CHESTPLATE.get());
        basicItem(ModItems.GOD_LEGGINGS.get());
        basicItem(ModItems.GOD_BOOTS.get());
        basicItem(ModItems.GOD_TEMPLATE.get());
        basicItem(ModItems.CURSE_TRANSFER_BLANK.get());
        basicItem(ModItems.CURSE_TRANSFER_TRINKET.get());
        basicItem(ModItems.CURSE_SLOT_UNLOCK_TALISMAN.get());
        basicItem(ModItems.GOD_CORE.get());
        basicItem(ModItems.GOD_EVOLUTION_STONE.get());
        basicItem(ModItems.CONQUEST_INGOT.get());
        basicItem(ModItems.CONQUEST_HELMET.get());
        basicItem(ModItems.CONQUEST_CHESTPLATE.get());
        basicItem(ModItems.CONQUEST_LEGGINGS.get());
        basicItem(ModItems.CONQUEST_BOOTS.get());
        // 深渊征伐全套盔甲
        basicItem(ModItems.ABYSS_GOD_HELMET.get());
        basicItem(ModItems.ABYSS_GOD_CHESTPLATE.get());
        basicItem(ModItems.ABYSS_GOD_LEGGINGS.get());
        basicItem(ModItems.ABYSS_GOD_BOOTS.get());
        basicItem(ModItems.ABYSS_GOD_INGOT.get());
        basicItem(ModItems.WITHER_SHARD.get());
        basicItem(ModItems.WARDEN_HEART.get());
        // 凋零四件饰品
        basicItem(ModItems.WITHER_HEART.get());
        basicItem(ModItems.KINGS_GRIP.get());
        basicItem(ModItems.WITHER_CROWN.get());
        basicItem(ModItems.WITHER_SPINE.get());
// 监守者四件饰品
        basicItem(ModItems.WARDEN_EYE.get());
        basicItem(ModItems.WARDEN_PULSE.get());
        basicItem(ModItems.WARDEN_WEIGHT.get());
        basicItem(ModItems.WARDEN_ARMORBREAK.get());
        // 终焉龙饰模型
        basicItem(ModItems.DRAGON_CORE_HEART.get());
        basicItem(ModItems.EYE_OF_THE_END.get());
        basicItem(ModItems.CRYSTAL_CROWN.get());
        basicItem(ModItems.DRAGON_SPINE_END.get());
        basicItem(ModItems.DRAGON_SOUL.get());
        // 白色剑模
        basicItem(ModItems.WHITE_SWORD_BLANK.get());
        basicItem(ModItems.IRON_WHITE_SWORD.get());
        basicItem(ModItems.DIAMOND_WHITE_BLADE.get());
        basicItem(ModItems.SHADOW_SWORD.get());
        basicItem(ModItems.CORE_CONQUEST_SWORD.get());
        basicItem(ModItems.WAR_BLADE.get());
        basicItem(ModItems.WITHER_BLADE.get());
        basicItem(ModItems.WARDEN_SWORD.get());
        basicItem(ModItems.ABYSS_SWORD.get());
        basicItem(ModItems.END_DRAGON_SWORD.get());
        // ========== 13神位碎片模型 ==========
        basicItem(ModItems.SHARD_SKY.get());
        basicItem(ModItems.SHARD_SUN.get());
        basicItem(ModItems.SHARD_LIFE.get());
        basicItem(ModItems.SHARD_STRENGTH.get());
        basicItem(ModItems.SHARD_FATE.get());
        basicItem(ModItems.SHARD_WIND.get());
        basicItem(ModItems.SHARD_SPIRIT.get());
        basicItem(ModItems.SHARD_BARRIER.get());
        basicItem(ModItems.SHARD_VOID.get());
        basicItem(ModItems.SHARD_SHADOW.get());
        basicItem(ModItems.SHARD_THUNDER.get());
        basicItem(ModItems.SHARD_STAR.get());
        basicItem(ModItems.SHARD_RUNE.get());
    }
}
