package com.lazybones.godfalltrinkets.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

public class CurseSlotUnlockItem extends Item {
    private static final String TAG_UNLOCKED_CURSE_SLOT = "godfall_unlock_curse_slot";
    private static final UUID CURSE_SLOT_UUID = UUID.fromString("c0000001-0000-4000-a000-000000000001");

    public CurseSlotUnlockItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.success(stack);

        boolean alreadyUnlock = player.getPersistentData().getBoolean(TAG_UNLOCKED_CURSE_SLOT);
        if (alreadyUnlock) {
            player.sendSystemMessage(Component.translatable("msg.godfall.slot.already_unlock"));
            return InteractionResultHolder.fail(stack);
        }

        player.getPersistentData().putBoolean(TAG_UNLOCKED_CURSE_SLOT, true);

        // 实际增长 Curios curse_transfer 槽位
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
            handler.addPermanentSlotModifier("curse_transfer", CURSE_SLOT_UUID,
                    "curse_slot_unlock", 1, AttributeModifier.Operation.ADDITION);
        });

        stack.shrink(1);
        player.sendSystemMessage(Component.translatable("msg.godfall.slot.unlock_success"));
        return InteractionResultHolder.consume(stack);
    }
}
