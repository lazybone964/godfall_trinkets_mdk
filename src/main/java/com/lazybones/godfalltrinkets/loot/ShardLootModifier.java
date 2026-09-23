package com.lazybones.godfalltrinkets.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * 神位碎片宝箱掉落：按概率在指定结构宝箱中注入碎片（无需佩戴饰品）。
 * JSON 示例见 data/godfall_trinkets/loot_modifiers/
 */
public class ShardLootModifier extends LootModifier {

    public static final Codec<ShardLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance)
                    .and(ShardEntry.CODEC.listOf().fieldOf("shards")
                            .forGetter(m -> m.shards))
                    .apply(instance, ShardLootModifier::new));

    private final List<ShardEntry> shards;
    private static final Random RAND = new Random();

    public ShardLootModifier(LootItemCondition[] conditions, List<ShardEntry> shards) {
        super(conditions);
        this.shards = shards;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (ShardEntry entry : shards) {
            if (RAND.nextFloat() < entry.chance()) {
                generatedLoot.add(new ItemStack(entry.item()));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    /** 碎片条目：物品 + 概率（0~1） */
    public record ShardEntry(Item item, float chance) {
        public static final Codec<ShardEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(e -> e.item),
                        Codec.FLOAT.fieldOf("chance").forGetter(e -> e.chance)
                ).apply(instance, ShardEntry::new));
    }
}
