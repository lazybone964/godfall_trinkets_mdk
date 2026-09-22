package com.lazybones.godfalltrinkets;

import com.lazybones.godfalltrinkets.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider; // 新增：导入TagLookup相关类
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModCuriosProvider(output, existingFileHelper, lookupProvider));
        generator.addProvider(event.includeClient(), new ModItemModelsProvider(output, existingFileHelper));

        // 核心修正：补充第三个参数（空的Block Tag Lookup）
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(
                        output,
                        lookupProvider,
                        CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), // 缺失的第三个参数
                        existingFileHelper
                ));

        generator.addProvider(event.includeClient(), new ModEnUsLangProvider(output));
        generator.addProvider(event.includeClient(), new ModZhCnLangProvider(output));
        generator.addProvider(event.includeClient(), new ModRecipeProvider(output));
    }
}