package com.lazybones.godfalltrinkets.datagen;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class ModCuriosProvider extends CuriosDataProvider {

    public ModCuriosProvider(PackOutput output, ExistingFileHelper fileHelper,
                             CompletableFuture<HolderLookup.Provider> registries) {
        super(GodfallTrinkets.MOD_ID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        // 破厄之核专属槽位（原有）
        createSlot("broken_core_slot")
                .size(1)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/broken_core"))
                .order(0)
                .renderToggle(true);

        // 新增：神位碎片槽位（默认0个，靠破厄之核解锁）
        createSlot("god_shard_slot")
                .size(0)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/god_shard"))
                .order(1)
                .renderToggle(true);

        // ==========【新增自定义护符槽 charm】==========
        createSlot("charm")
                .size(2) // 初始0，佩戴饰品+3
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/charm"))
                .order(4) // 排序放在碎片槽后面，按需调整数字
                .renderToggle(true);
        // ===================================================
        createSlot("ring")
                .size(2)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/ring"))
                .order(2)
                .renderToggle(true);
        createSlot("bracelet")
                .size(2)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/bracelet"))
                .order(3)
                .renderToggle(true);
        createSlot("curse_transfer")
                .size(0)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/curse_transfer"))
                .order(5)
                .renderToggle(true);
        createSlot("head")
                .size(1)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/head"))
                .order(6)
                .renderToggle(true);
        createSlot("back")
                .size(1)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/back"))
                .order(7)
                .renderToggle(true);
        createSlot("body")
                .size(2)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/body"))
                .order(8)
                .renderToggle(true);
        createSlot("necklace")
                .size(2)
                .icon(ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "slot/necklace"))
                .order(9)
                .renderToggle(true);

        // 关联实体-槽位
        createEntities("broken_core_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("broken_core_slot");

        createEntities("god_shard_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("god_shard_slot");
        createEntities("broken_amulet_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("charm");
        createEntities("broken_ring_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("ring");
        createEntities("broken_bracelet_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("bracelet");
        createEntities("curse_transfer_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("curse_transfer");
        createEntities("broken_head_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("head");
        createEntities("broken_back_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("back");
        createEntities("broken_body_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("body");
        createEntities("broken_necklace_slot_entities")
                .addEntities(EntityType.PLAYER)
                .addSlots("necklace");
    }
}
