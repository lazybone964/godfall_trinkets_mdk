package com.lazybones.godfalltrinkets.item;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GodfallTrinkets.MOD_ID);
    public static final RegistryObject<CreativeModeTab> GODFALL_TRINKETS_TAB = CREATIVE_MODE_TABS.register("godfall_trinkets_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.BROKEN_CORE.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.godfall_trinkets.godfall_trinkets_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BROKEN_CORE.get());
                        output.accept(ModItems.GODFRAGMENT.get());
                        output.accept(ModItems.TOUGH_BONE_AMULET.get());
                        output.accept(ModItems.SHARP_EDGE_RING.get());
                        output.accept(ModItems.PURIFYING_BRACELET.get());
                        output.accept(ModItems.SOUL_DRAIN_RING.get());
                        output.accept(ModItems.VITAL_AMULET.get());
                        output.accept(ModItems.BLUNTING_AMULET.get());
                        output.accept(ModItems.TOUGH_HEART_RING.get());
                        output.accept(ModItems.BARRIER_BRACELET.get());
                        output.accept(ModItems.DEATH_DEFY_RING.get());
                        output.accept(ModItems.SHOCK_AMULET.get());
                        output.accept(ModItems.EMBER_BRACELET.get());
                        output.accept(ModItems.HUNTER_RING.get());
                        output.accept(ModItems.ABYSS_RUNE_RING.get());
                        output.accept(ModItems.FERTILE_BRACELET.get());
                        output.accept(ModItems.FORGE_AMULET.get());
                        output.accept(ModItems.DEEP_ROCK_RING.get());
                        output.accept(ModItems.TREASURE_AMULET.get());
                        output.accept(ModItems.PROSPERITY_BRACELET.get());
                        output.accept(ModItems.SHARP_EDGE_STONE.get());
                        output.accept(ModItems.GOD_INGOT.get());
                        output.accept(ModItems.GOD_HELMET.get());
                        output.accept(ModItems.GOD_CHESTPLATE.get());
                        output.accept(ModItems.GOD_LEGGINGS.get());
                        output.accept(ModItems.GOD_BOOTS.get());
                        output.accept(ModItems.GOD_TEMPLATE.get());
                        output.accept(ModItems.CURSE_SLOT_UNLOCK_TALISMAN.get());
                        output.accept(ModItems.CURSE_TRANSFER_BLANK.get());
                        output.accept(ModItems.CURSE_TRANSFER_TRINKET.get());
                        output.accept(ModItems.GOD_CORE.get());
                        output.accept(ModItems.GOD_EVOLUTION_STONE.get());
                        output.accept(ModItems.CONQUEST_INGOT.get());
                        output.accept(ModItems.CONQUEST_HELMET.get());
                        output.accept(ModItems.CONQUEST_CHESTPLATE.get());
                        output.accept(ModItems.CONQUEST_LEGGINGS.get());
                        output.accept(ModItems.CONQUEST_BOOTS.get());
                        output.accept(ModItems.WARDEN_HEART.get());
                        output.accept(ModItems.WITHER_SHARD.get());
                        output.accept(ModItems.ABYSS_GOD_INGOT.get());
                        output.accept(ModItems.ABYSS_GOD_HELMET.get());
                        output.accept(ModItems.ABYSS_GOD_CHESTPLATE.get());
                        output.accept(ModItems.ABYSS_GOD_LEGGINGS.get());
                        output.accept(ModItems.ABYSS_GOD_BOOTS.get());
                        output.accept(ModItems.WITHER_HEART.get());
                        output.accept(ModItems.KINGS_GRIP.get());
                        output.accept(ModItems.WITHER_CROWN.get());
                        output.accept(ModItems.WITHER_SPINE.get());
                        output.accept(ModItems.WARDEN_EYE.get());
                        output.accept(ModItems.WARDEN_PULSE.get());
                        output.accept(ModItems.WARDEN_WEIGHT.get());
                        output.accept(ModItems.WARDEN_ARMORBREAK.get());
                        output.accept(ModItems.DRAGON_SOUL.get());
                        output.accept(ModItems.DRAGON_CORE_HEART.get());
                        output.accept(ModItems.EYE_OF_THE_END.get());
                        output.accept(ModItems.CRYSTAL_CROWN.get());
                        output.accept(ModItems.DRAGON_SPINE_END.get());
                        output.accept(ModItems.WHITE_SWORD_BLANK.get());
                        output.accept(ModItems.IRON_WHITE_SWORD.get());
                        output.accept(ModItems.DIAMOND_WHITE_BLADE.get());
                        output.accept(ModItems.SHADOW_SWORD.get());
                        output.accept(ModItems.CORE_CONQUEST_SWORD.get());
                        output.accept(ModItems.WAR_BLADE.get());
                        output.accept(ModItems.WITHER_BLADE.get());
                        output.accept(ModItems.WARDEN_SWORD.get());
                        output.accept(ModItems.ABYSS_SWORD.get());
                        output.accept(ModItems.END_DRAGON_SWORD.get());
                        output.accept(ModItems.SHARD_SKY.get());
                        output.accept(ModItems.SHARD_SUN.get());
                        output.accept(ModItems.SHARD_LIFE.get());
                        output.accept(ModItems.SHARD_STRENGTH.get());
                        output.accept(ModItems.SHARD_FATE.get());
                        output.accept(ModItems.SHARD_WIND.get());
                        output.accept(ModItems.SHARD_SPIRIT.get());
                        output.accept(ModItems.SHARD_BARRIER.get());
                        output.accept(ModItems.SHARD_VOID.get());
                        output.accept(ModItems.SHARD_SHADOW.get());
                        output.accept(ModItems.SHARD_THUNDER.get());
                        output.accept(ModItems.SHARD_STAR.get());
                        output.accept(ModItems.SHARD_RUNE.get());
                    })
                    .build());
}
