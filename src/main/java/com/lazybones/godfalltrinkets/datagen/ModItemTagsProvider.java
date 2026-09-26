package com.lazybones.godfalltrinkets.datagen;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                               CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, GodfallTrinkets.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagKey<Item> brokenCoreTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "broken_core_slot"));
        this.tag(brokenCoreTag).add(ModItems.BROKEN_CORE.get(), ModItems.GOD_CORE.get());

        TagKey<Item> amuletTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "charm"));
        this.tag(amuletTag)
                .add(ModItems.TOUGH_BONE_AMULET.get(),
                     ModItems.VITAL_AMULET.get(),
                     ModItems.BLUNTING_AMULET.get(),
                     ModItems.SHOCK_AMULET.get(),
                     ModItems.FORGE_AMULET.get(),
                     ModItems.TREASURE_AMULET.get(),
                        ModItems.EYE_OF_THE_END.get()
                );

        TagKey<Item> ringTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "ring"));
        this.tag(ringTag)
                .add(ModItems.SHARP_EDGE_RING.get(),
                     ModItems.SOUL_DRAIN_RING.get(),
                     ModItems.TOUGH_HEART_RING.get(),
                     ModItems.DEATH_DEFY_RING.get(),
                     ModItems.HUNTER_RING.get(),
                     ModItems.ABYSS_RUNE_RING.get(),
                     ModItems.DEEP_ROCK_RING.get(),
                     ModItems.WARDEN_EYE.get(),
                     ModItems.WARDEN_PULSE.get()   );

        TagKey<Item> handsTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "bracelet"));
        this.tag(handsTag)
                .add(ModItems.PURIFYING_BRACELET.get(),
                     ModItems.BARRIER_BRACELET.get(),
                     ModItems.EMBER_BRACELET.get(),
                     ModItems.FERTILE_BRACELET.get(),
                     ModItems.PROSPERITY_BRACELET.get(),
                     ModItems.WARDEN_ARMORBREAK.get(),
                     ModItems.KINGS_GRIP.get());

        TagKey<Item> curseTransferTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "curse_transfer"));
        this.tag(curseTransferTag).add(ModItems.CURSE_TRANSFER_TRINKET.get());
        // 新增自定义槽标签（放在tag生成末尾）
        TagKey<Item> headTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "head"));
        this.tag(headTag).add(ModItems.WITHER_CROWN.get(),ModItems.CRYSTAL_CROWN.get());

        TagKey<Item> necklaceTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "necklace"));
        this.tag(necklaceTag).add(ModItems.WITHER_SPINE.get());

        TagKey<Item> bodyTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "body"));
        this.tag(bodyTag)
                .add(ModItems.WARDEN_WEIGHT.get(),ModItems.WITHER_HEART.get(), ModItems.DRAGON_CORE_HEART.get());
        TagKey<Item> backTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "back"));
        this.tag(backTag).add(ModItems.DRAGON_SPINE_END.get());
        // ========== 神位碎片槽标签 ==========
        TagKey<Item> godShardTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "god_shard_slot"));
        this.tag(godShardTag)
                .add(ModItems.SHARD_SKY.get(),
                        ModItems.SHARD_SUN.get(),
                        ModItems.SHARD_LIFE.get(),
                        ModItems.SHARD_STRENGTH.get(),
                        ModItems.SHARD_FATE.get(),
                        ModItems.SHARD_WIND.get(),
                        ModItems.SHARD_SPIRIT.get(),
                        ModItems.SHARD_BARRIER.get(),
                        ModItems.SHARD_VOID.get(),
                        ModItems.SHARD_SHADOW.get(),
                        ModItems.SHARD_THUNDER.get(),
                        ModItems.SHARD_STAR.get(),
                        ModItems.SHARD_RUNE.get());

        // ========== 徽章槽标签 ==========
        TagKey<Item> badgeTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "badge"));
        this.tag(badgeTag).add(ModItems.ADVANCEMENT_TALLY_TRINKET.get());
    }
}
