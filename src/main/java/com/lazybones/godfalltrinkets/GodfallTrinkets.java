package com.lazybones.godfalltrinkets;

import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.ModCreativeTabs;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.item.custom.BrokenCoreItem;
import com.lazybones.godfalltrinkets.loot.ShardLootModifier;
import com.lazybones.godfalltrinkets.loot.TrinketLootModifier;
import com.lazybones.godfalltrinkets.network.ModNetwork;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(GodfallTrinkets.MOD_ID)
public class GodfallTrinkets {
    public static final String MOD_ID = "godfall_trinkets";

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TRINKET_LOOT =
            LOOT_MODIFIERS.register("trinket_chest_loot", () -> TrinketLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SHARD_LOOT =
            LOOT_MODIFIERS.register("shard_chest_loot", () -> ShardLootModifier.CODEC);

    public GodfallTrinkets(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        ModItems.register(bus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(bus);
        LOOT_MODIFIERS.register(bus);
        ModNetwork.register();
        MinecraftForge.EVENT_BUS.register(this);
        // 使用传入的context对象注册配置，消除弃用警告
        context.registerConfig(ModConfig.Type.COMMON, GodfallConfig.SPEC, MOD_ID + "-common.toml");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class CommandRegistry {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(
                Commands.literal("setcoreexp")
                    .requires(src -> src.hasPermission(2))
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0, 40000))
                        .executes(ctx -> setCoreExp(ctx, IntegerArgumentType.getInteger(ctx, "amount"))))
            );
        }

        private static int setCoreExp(CommandContext<CommandSourceStack> ctx, int amount) {
            CommandSourceStack src = ctx.getSource();
            if (!(src.getEntity() instanceof Player player)) {
                src.sendFailure(Component.literal("Only players can use this command"));
                return 0;
            }
            CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> {
                handler.findFirstCurio(item -> item.is(ModItems.BROKEN_CORE.get()))
                    .ifPresent(result -> {
                        ItemStack stack = result.stack();
                        CompoundTag tag = stack.getOrCreateTag();
                        tag.putInt("core_exp", amount);
                        src.sendSuccess(() -> Component.literal(
                            "破厄之核经验已设为 " + amount), true);
                    });
            });
            return 1;
        }
    }
}