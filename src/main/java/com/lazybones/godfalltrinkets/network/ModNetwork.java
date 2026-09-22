package com.lazybones.godfalltrinkets.network;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

/** 模组网络通道：客户端 → 服务端按键请求 */
public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(GodfallTrinkets.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, OpenEnderChestPacket.class,
                OpenEnderChestPacket::encode,
                OpenEnderChestPacket::decode,
                OpenEnderChestPacket::handle);
    }

    /** 打开末影箱请求包（无数据） */
    public static class OpenEnderChestPacket {
        public static void encode(OpenEnderChestPacket msg, FriendlyByteBuf buf) {}

        public static OpenEnderChestPacket decode(FriendlyByteBuf buf) {
            return new OpenEnderChestPacket();
        }

        public static void handle(OpenEnderChestPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null && canOpenEnderChest(player)) {
                    player.openMenu(new SimpleMenuProvider(
                            (id, inv, p) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()),
                            Component.translatable("container.enderchest")));
                }
            });
            ctx.get().setPacketHandled(true);
        }

        /** 服务端校验：佩戴破厄之核/神格 + 虚空碎片才可打开 */
        private static boolean canOpenEnderChest(ServerPlayer player) {
            return CuriosApi.getCuriosInventory(player).resolve().map(handler -> {
                boolean hasCore = handler.findFirstCurio(s ->
                        s.is(ModItems.BROKEN_CORE.get()) || s.is(ModItems.GOD_CORE.get())).isPresent();
                if (!hasCore) return false;
                return handler.findFirstCurio(s -> s.is(ModItems.SHARD_VOID.get())).isPresent();
            }).orElse(false);
        }
    }
}
