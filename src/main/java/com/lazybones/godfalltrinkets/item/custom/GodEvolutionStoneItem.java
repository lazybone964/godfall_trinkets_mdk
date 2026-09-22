package com.lazybones.godfalltrinkets.item.custom;

import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.event.GodCoreEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

/** 神格进化石——集齐13碎片后自动发放，右键破厄之核将其进化为神格。 */
public class GodEvolutionStoneItem extends Item {

    public GodEvolutionStoneItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stone = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.success(stone);

        if (!GodfallConfig.ENABLE_GOD_CORE_EVOLUTION.get()) {
            player.sendSystemMessage(Component.literal("神格进化功能已被关闭").withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stone);
        }

        // 在 Curios 槽中查找破厄之核
        var result = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.is(ModItems.BROKEN_CORE.get())));
        if (result.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.godfall_trinkets.no_core_found")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stone);
        }

        ItemStack core = result.get().stack();
        // 防止重复进化
        if (GodCoreItem.isGodCore(core)) {
            player.sendSystemMessage(Component.translatable("message.godfall_trinkets.already_god")
                    .withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(stone);
        }

        // 复制全部NBT到神格
        ItemStack god = new ItemStack(ModItems.GOD_CORE.get());
        CompoundTag originalTag = core.getOrCreateTag().copy();
        god.setTag(originalTag);
        GodCoreItem.markAsGodCore(god);

        // 替换槽位中的物品
        int slotIndex = result.get().slotContext().index();
        String slotId = result.get().slotContext().identifier();
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            handler.getStacksHandler(slotId).ifPresent(stacks ->
                    stacks.getStacks().setStackInSlot(slotIndex, god));
        });

        stone.shrink(1);
        player.sendSystemMessage(Component.translatable("message.godfall_trinkets.evolve_success")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        return InteractionResultHolder.consume(stone);
    }
}
