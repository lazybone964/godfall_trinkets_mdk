package com.lazybones.godfalltrinkets.loot;

import com.lazybones.godfalltrinkets.item.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Random;

/**
 * 携带指定 Curios 饰品后，在宝箱战利品中额外注入物品。
 * JSON 配置示例见 data/godfall_trinkets/loot_modifiers/
 */
public class TrinketLootModifier extends LootModifier {

    public static final Codec<TrinketLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance)
                    .and(ForgeRegistries.ITEMS.getCodec().fieldOf("trinket")
                            .forGetter(m -> m.trinketItem))
                    .and(WeightedLootEntry.CODEC.listOf().fieldOf("extra_items")
                            .forGetter(m -> m.extraItems))
                    .apply(instance, TrinketLootModifier::new));

    private final Item trinketItem;
    private final List<WeightedLootEntry> extraItems;
    private static final Random RAND = new Random();

    /**
     * @param conditions 原版战利品条件（如限定地牢箱）
     * @param trinket    需要佩戴的饰品物品
     * @param extraItems 额外注入的物品列表（含权重和数量范围）
     */
    public TrinketLootModifier(LootItemCondition[] conditions, Item trinket,
                               List<WeightedLootEntry> extraItems) {
        super(conditions);
        this.trinketItem = trinket;
        this.extraItems = extraItems;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 检查是否有玩家
        Entity entity = context.getParamOrNull(net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Player player)) return generatedLoot;

        // 检查玩家是否佩戴了指定饰品
        boolean hasTrinket = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.is(trinketItem)))
                .isPresent();
        if (!hasTrinket) return generatedLoot;

        // 按权重随机生成额外物品
        int totalWeight = extraItems.stream().mapToInt(e -> e.weight).sum();
        if (totalWeight <= 0) return generatedLoot;

        for (WeightedLootEntry entry : extraItems) {
            if (RAND.nextInt(totalWeight) < entry.weight) {
                int count = entry.minCount == entry.maxCount
                        ? entry.minCount
                        : entry.minCount + RAND.nextInt(entry.maxCount - entry.minCount + 1);
                generatedLoot.add(new ItemStack(entry.item, count));
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    /**
     * 额外物品条目：物品 + 权重 + 数量范围
     */
    public record WeightedLootEntry(Item item, int weight, int minCount, int maxCount) {
        public static final Codec<WeightedLootEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(e -> e.item),
                        Codec.INT.optionalFieldOf("weight", 1).forGetter(e -> e.weight),
                        Codec.INT.optionalFieldOf("min", 1).forGetter(e -> e.minCount),
                        Codec.INT.optionalFieldOf("max", 1).forGetter(e -> e.maxCount)
                ).apply(instance, WeightedLootEntry::new));
    }
}
