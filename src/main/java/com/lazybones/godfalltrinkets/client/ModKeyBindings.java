package com.lazybones.godfalltrinkets.client;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.network.ModNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/** 客户端按键绑定 */
public class ModKeyBindings {

    public static final String CATEGORY = "key.category.godfall_trinkets";

    /** 打开末影箱按键（默认 K 键，可在控制设置中修改） */
    public static final KeyMapping OPEN_ENDER_CHEST = new KeyMapping(
            "key.godfall_trinkets.open_ender_chest",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            CATEGORY
    );

    /** MOD 总线：注册按键 */
    @Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_ENDER_CHEST);
        }
    }

    /** FORGE 总线：监听按键按下 */
    @Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, value = Dist.CLIENT)
    public static class Input {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (OPEN_ENDER_CHEST.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new ModNetwork.OpenEnderChestPacket());
            }
        }
    }
}
