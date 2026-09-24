package com.lazybones.godfalltrinkets.datagen;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, GodfallTrinkets.MOD_ID, "en_us");
    }
    @Override
    protected void addTranslations() {
        add("itemGroup.godfall_trinkets.godfall_trinkets_tab", "Godfall Trinkets");
        add("item.godfall_trinkets.broken_core", "Broken Core");
        add("item.godfall_trinkets.godfragment", "God Fragment");
        add("curios.identifier.broken_core_slot", "Broken Core Slot");
        add("curios.identifier.charm", "Amulet");
        add("curios.identifier.ring", "Ring");
        add("curios.identifier.bracelet", "Handcuffs");
        add("curios.modifiers.charm", " ");
        add("curios.modifiers.god_shard_slot", " ");
        add("curios.modifiers.ring", " ");
        add("curios.modifiers.bracelet", " ");
        add("curios.identifier.god_shard_slot", "God Shard");
        add("curios.modifiers.broken_core_slot", "When wearing this trinket\n  +13 God Shard Slots\n  +3 Amulet Slots");
        add("message.godfall_trinkets.breakthrough", "Broken Core breakthrough to '%s'!");
        add("message.godfall_trinkets.cant_sleep", "The Sleepless Curse prevents you from sleeping...");
        add("tooltip.godfall_trinkets.hold_shift", "Hold Shift to view curses and blessings");
        add("tooltip.godfall_trinkets.curses_header", "▸ Eight Curses ◂");
        add("tooltip.godfall_trinkets.blessings_header", "▸ Eight Blessings ◂");
        add("tooltip.godfall_trinkets.tier", "Tier: %s");
        add("tooltip.godfall_trinkets.exp", "Exp: %d / %d");
        add("tooltip.godfall_trinkets.curse.1", "Life Drain: No natural regen, lose 0.5 HP/s");
        add("tooltip.godfall_trinkets.curse.1_reversed", "Life Surge: Natural regen + 0.6 HP/s recovery");
        add("tooltip.godfall_trinkets.curse.2", "Deep Wound: +20% damage taken from all sources");
        add("tooltip.godfall_trinkets.curse.2_reversed", "Damage Ward: -20% damage taken from all sources");
        add("tooltip.godfall_trinkets.curse.3", "Blunt Edge: -30% melee attack damage");
        add("tooltip.godfall_trinkets.curse.3_reversed", "Sharp Edge: +30% melee attack damage");
        add("tooltip.godfall_trinkets.curse.4", "Eternal Hunger: Double hunger drain, XP drains at 0 hunger");
        add("tooltip.godfall_trinkets.curse.4_reversed", "Eternal Feast: Half hunger drain");
        add("tooltip.godfall_trinkets.curse.5", "Sleepless Curse: Cannot sleep to skip nights");
        add("tooltip.godfall_trinkets.curse.5_reversed", "Dreamer's Gift: Can sleep, +200% night speed");
        add("tooltip.godfall_trinkets.curse.6", "Scarred: Extra 30% max HP damage on each hit");
        add("tooltip.godfall_trinkets.curse.6_reversed", "Resilient: Damage capped at 30% of max HP per hit");
        add("tooltip.godfall_trinkets.curse.7", "Frail Body: -4 max HP permanently");
        add("tooltip.godfall_trinkets.curse.7_reversed", "Iron Body: +4 max HP permanently");
        add("tooltip.godfall_trinkets.curse.8", "Tainted: Poison/wither/weakness never expire");
        add("tooltip.godfall_trinkets.curse.8_reversed", "Purified: Half poison/wither/weakness duration");
        add("tooltip.godfall_trinkets.blessing.1", "Ascending: All attributes +%s%%");
        add("tooltip.godfall_trinkets.blessing.2", "Abyss Authority: Unlock abyss equipment (T9 +30%% power)");
        add("tooltip.godfall_trinkets.blessing.3", "Soul Drain: Kill heal&hunger (Tier:%d)");
        add("tooltip.godfall_trinkets.blessing.4", "Shadow Dodge: Dodge attacks (Tier:%d)");
        add("tooltip.godfall_trinkets.blessing.5", "Abyss Vision: Perceptual enhancement (Tier:%d)");
        add("tooltip.godfall_trinkets.blessing.6", "Soul Harvest: EXP bonus + God Fragments (Tier:%d)");
        add("tooltip.godfall_trinkets.blessing.7", "Calamity: Enemy proximity ATK bonus (Tier:%d)");
        add("tooltip.godfall_trinkets.blessing.8", "Abyss Shield: Damage absorb shield (Tier:%d)");
        // 18 item names
        add("item.godfall_trinkets.tough_bone_amulet", "Tough Bone Amulet");
        add("item.godfall_trinkets.sharp_edge_ring", "Sharp Edge Ring");
        add("item.godfall_trinkets.purifying_bracelet", "Purifying Bracelet");
        add("item.godfall_trinkets.soul_drain_ring", "Soul Drain Ring");
        add("item.godfall_trinkets.vital_amulet", "Vital Amulet");
        add("item.godfall_trinkets.blunting_amulet", "Blunting Amulet");
        add("item.godfall_trinkets.tough_heart_ring", "Tough Heart Ring");
        add("item.godfall_trinkets.barrier_bracelet", "Barrier Bracelet");
        add("item.godfall_trinkets.death_defy_ring", "Death-Defying Ring");
        add("item.godfall_trinkets.shock_amulet", "Shock Amulet");
        add("item.godfall_trinkets.ember_bracelet", "Ember Bracelet");
        add("item.godfall_trinkets.hunter_ring", "Hunter Ring");
        add("item.godfall_trinkets.abyss_rune_ring", "Abyss Rune Ring");
        add("item.godfall_trinkets.fertile_bracelet", "Fertile Bracelet");
        add("item.godfall_trinkets.forge_amulet", "Forge Amulet");
        add("item.godfall_trinkets.deep_rock_ring", "Deep Rock Ring");
        add("item.godfall_trinkets.treasure_amulet", "Treasure Amulet");
        add("item.godfall_trinkets.prosperity_bracelet", "Prosperity Bracelet");
        // 18 description tooltips
        add("tooltip.godfall_trinkets.desc.tough_bone_amulet", "+3 Max HP\n-12% damage taken\nCounters: Frail Body, Scarred");
        add("tooltip.godfall_trinkets.desc.sharp_edge_ring", "+12% all damage\nCounters: Blunt Edge");
        add("tooltip.godfall_trinkets.desc.purifying_bracelet", "Clears one debuff every 20s\nCounters: Tainted");
        add("tooltip.godfall_trinkets.desc.soul_drain_ring", "15% lifesteal on hit\n+3 HP on kill\nCounters: Life Drain, Scarred");
        add("tooltip.godfall_trinkets.desc.vital_amulet", "3 HP/s regen\n+2 HP when hit");
        add("tooltip.godfall_trinkets.desc.blunting_amulet", "-15% damage taken\n+8% all damage\nCounters: Deep Wound, Blunt Edge");
        add("tooltip.godfall_trinkets.desc.tough_heart_ring", "+4 Max HP\n20% DR below 30% HP");
        add("tooltip.godfall_trinkets.desc.barrier_bracelet", "Blocking deflects projectiles (30% reflect)\nExplosion damage -22%\n0.8 HP/2s while blocking");
        add("tooltip.godfall_trinkets.desc.death_defy_ring", "Below 35% HP: 28% DR + 22% lifesteal\n3s invincibility on near-death (45s CD)\n+4 HP on low-HP kill");
        add("tooltip.godfall_trinkets.desc.shock_amulet", "Slowness on melee hit (30s)\n+18% knockback resist\n+1 HP/s regen per nearby enemy (max 6)");
        add("tooltip.godfall_trinkets.desc.ember_bracelet", "Immune to fire damage\nPoison/wither DOT -50%");
        add("tooltip.godfall_trinkets.desc.hunter_ring", "+16% damage vs >50% HP targets\n+3.5 HP on kill");
        add("tooltip.godfall_trinkets.desc.abyss_rune_ring", "-20% enchantment XP cost\nSlightly better enchant rolls\n+12% XP gain\n+20% enchantment effect");
        add("tooltip.godfall_trinkets.desc.fertile_bracelet", "+25% crop growth (8 blocks)\n30% double crop drop");
        add("tooltip.godfall_trinkets.desc.forge_amulet", "+30% smelting speed, -15% fuel\n-25% anvil repair XP\n-20% tool durability loss");
        add("tooltip.godfall_trinkets.desc.deep_rock_ring", "+15% mining speed\n25% double ore chance\nImmune to mining fatigue");
        add("tooltip.godfall_trinkets.desc.treasure_amulet", "+20% base drops, +10% rare drops\n35% extra chest loot item");
        add("tooltip.godfall_trinkets.desc.prosperity_bracelet", "+30% baby animal growth (10 blocks)\n-25% breeding cooldown\n20% double meat/leather on slaughter");
        add("tooltip.godfall_trinkets.require_core", "§cRequires Broken Core equipped");
        // Curse Transfer
        add("item.godfall_trinkets.curse_transfer_blank", "Curse Transfer Blank");
        add("item.godfall_trinkets.curse_transfer_trinket", "Curse Transfer Trinket");
        add("item.godfall_trinkets.curse_slot_unlock_talisman", "Curse Slot Talisman");
        add("curios.identifier.curse_transfer", "Curse Transfer");
        add("tooltip.godfall_trinkets.absorbed_curses", "▸ Absorbed Curses ◂");
        add("tooltip.godfall_trinkets.curse_durability", "Durability: %d / %d");
        add("message.godfall_trinkets.curse_return", "§cCurse Transfer Trinket shattered! Curses returned...§r");
        add("msg.godfall.slot.already_unlock", "You have already unlocked the curse slot.");
        add("msg.godfall.slot.unlock_success", "Curse slot unlocked! You can now equip the Curse Transfer Trinket.");
        add("curse.godfall_trinkets.agony", "Life Drain");
        add("curse.godfall_trinkets.deep_wound", "Deep Wound");
        add("curse.godfall_trinkets.blunt_edge", "Blunt Edge");
        add("curse.godfall_trinkets.hunger_curse", "Eternal Hunger");
        add("curse.godfall_trinkets.sleepless", "Sleepless");
        add("curse.godfall_trinkets.scarred", "Scarred");
        add("curse.godfall_trinkets.weak_body", "Frail Body");
        add("curse.godfall_trinkets.foul_body", "Foul Body");
        // God Shards
        add("item.godfall_trinkets.shard_sky", "God Shard - Aether");
        add("item.godfall_trinkets.shard_sun", "God Shard - Sol");
        add("item.godfall_trinkets.shard_life", "God Shard - Vitalis");
        add("item.godfall_trinkets.shard_strength", "God Shard - Might");
        add("item.godfall_trinkets.shard_fate", "God Shard - Destiny");
        add("item.godfall_trinkets.shard_wind", "God Shard - Tempest");
        add("item.godfall_trinkets.shard_spirit", "God Shard - Spirit");
        add("item.godfall_trinkets.shard_barrier", "God Shard - Aegis");
        add("item.godfall_trinkets.shard_void", "God Shard - Null");
        add("item.godfall_trinkets.shard_shadow", "God Shard - Umbra");
        add("item.godfall_trinkets.shard_thunder", "God Shard - Fulmen");
        add("item.godfall_trinkets.shard_star", "God Shard - Meteor");
        add("item.godfall_trinkets.shard_rune", "God Shard - Glyph");
        // God Core
        add("item.godfall_trinkets.god_core", "God Core");
        add("item.godfall_trinkets.god_evolution_stone", "God Evolution Stone");
        add("tooltip.godfall_trinkets.hold_ctrl", "Hold Ctrl to view God Core powers");
        add("message.godfall_trinkets.evolve_success", "§6Broken Core has evolved into God Core!§r");
        add("message.godfall_trinkets.no_core_found", "§cNo Broken Core found in curios slots§r");
        add("message.godfall_trinkets.already_god", "§cBroken Core is already a God Core§r");
        add("tooltip.godfall_trinkets.godcore_header", "▸ God Core Powers ◂");
        add("tooltip.godfall_trinkets.godcore.1", "Omni-Attribute: All attributes +35%");
        add("tooltip.godfall_trinkets.godcore.2", "Shard Resonance: All shard effects +50%");
        add("tooltip.godfall_trinkets.godcore.3", "Eternal War: +20% damage dealt, -25% damage taken");
        add("tooltip.godfall_trinkets.godcore.4", "Elemental Immunity: Fall/Fire/Lava/Cactus/Drown/Lightning/Explosion/Magic immune");
        add("tooltip.godfall_trinkets.godcore.5", "Divine Ward: Damage capped at 30% max HP per hit");
        add("tooltip.godfall_trinkets.godcore.6", "Swift Recovery: Invulnerability frames extended to 30 ticks");
        add("tooltip.godfall_trinkets.godcore.7", "Sky Sovereign: Creative flight speed boosted, +30% flight damage");
        add("tooltip.godfall_trinkets.godcore.8", "Absolute Pierce: Ignore armor/toughness/resistance/boss cap");
        // Keybindings
        add("key.category.godfall_trinkets", "Godfall Trinkets");
        add("key.godfall_trinkets.open_ender_chest", "Open Ender Chest");
        // ==================== Advancement / Achievements English ====================
// Challenge purple
        add("advancement.godfall_trinkets.catastrophe_begin.title", "Beginning of Calamity");
        add("advancement.godfall_trinkets.catastrophe_begin.desc", "Equip the Broken‑Destiny Core and start your ascension journey.");

        add("advancement.godfall_trinkets.godhood_awaken.title", "Divine Awakening");
        add("advancement.godfall_trinkets.godhood_awaken.desc", "Evolve Broken‑Destiny Core into God Core, unlock full end‑game power.");

        add("advancement.godfall_trinkets.dragon_forge_blade.title", "Dragon‑Forged Edge");
        add("advancement.godfall_trinkets.dragon_forge_blade.desc", "Forge the ultimate weapon: Dragon‑Conquering Sword.");

// Goal
        add("advancement.godfall_trinkets.first_shard.title", "First Divine Shard");
        add("advancement.godfall_trinkets.first_shard.desc", "Obtain your first Divine Shard and glimpse divine power.");

        add("advancement.godfall_trinkets.twelve_constellation.title", "Twelve Constellations");
        add("advancement.godfall_trinkets.twelve_constellation.desc", "Collect 12 Divine Shards excluding the Aether Shard.");

        add("advancement.godfall_trinkets.thirteen_return.title", "Thirteen Divine Parts");
        add("advancement.godfall_trinkets.thirteen_return.desc", "Gather all thirteen Divine Shards.");

        add("advancement.godfall_trinkets.tier9_reverse.title", "Curse Reversal");
        add("advancement.godfall_trinkets.tier9_reverse.desc", "Reach tier 9 on Broken‑Destiny Core and reverse all curses.");

// Task 13 shards
        add("advancement.godfall_trinkets.shard_sky.title", "Aether Echo");
        add("advancement.godfall_trinkets.shard_sky.desc", "Obtain Divine Shard — Aether.");

        add("advancement.godfall_trinkets.shard_sun.title", "Scorching Sun");
        add("advancement.godfall_trinkets.shard_sun.desc", "Obtain Divine Shard — Sol.");

        add("advancement.godfall_trinkets.shard_life.title", "Spring of Life");
        add("advancement.godfall_trinkets.shard_life.desc", "Obtain Divine Shard — Vitalis.");

        add("advancement.godfall_trinkets.shard_strength.title", "Might of Titan");
        add("advancement.godfall_trinkets.shard_strength.desc", "Obtain Divine Shard — Might.");

        add("advancement.godfall_trinkets.shard_fate.title", "Weaver of Destiny");
        add("advancement.godfall_trinkets.shard_fate.desc", "Obtain Divine Shard — Destiny.");

        add("advancement.godfall_trinkets.shard_wind.title", "Swift Gale");
        add("advancement.godfall_trinkets.shard_wind.desc", "Obtain Divine Shard — Tempest.");

        add("advancement.godfall_trinkets.shard_spirit.title", "Blessed Spirit");
        add("advancement.godfall_trinkets.shard_spirit.desc", "Obtain Divine Shard — Spirit.");

        add("advancement.godfall_trinkets.shard_barrier.title", "Unbreakable Bulwark");
        add("advancement.godfall_trinkets.shard_barrier.desc", "Obtain Divine Shard — Aegis.");

        add("advancement.godfall_trinkets.shard_void.title", "Secret of Void");
        add("advancement.godfall_trinkets.shard_void.desc", "Obtain Divine Shard — Null.");

        add("advancement.godfall_trinkets.shard_shadow.title", "Shadow Assassin");
        add("advancement.godfall_trinkets.shard_shadow.desc", "Obtain Divine Shard — Umbra.");

        add("advancement.godfall_trinkets.shard_thunder.title", "Thunder Wrath");
        add("advancement.godfall_trinkets.shard_thunder.desc", "Obtain Divine Shard — Fulmen.");

        add("advancement.godfall_trinkets.shard_star.title", "Meteor Piercer");
        add("advancement.godfall_trinkets.shard_star.desc", "Obtain Divine Shard — Meteor.");

        add("advancement.godfall_trinkets.shard_rune.title", "Enchanted Glyph");
        add("advancement.godfall_trinkets.shard_rune.desc", "Obtain Divine Shard — Glyph.");

// Sword task
        add("advancement.godfall_trinkets.sword_blank_start.title", "Beginning of Forging");
        add("advancement.godfall_trinkets.sword_blank_start.desc", "Acquire blank sword template to start your forging journey.");

        add("advancement.godfall_trinkets.sword_mid_forge.title", "Blade Onward");
        add("advancement.godfall_trinkets.sword_mid_forge.desc", "Forge your longsword to intermediate tier.");

// Hidden
        add("advancement.godfall_trinkets.hardship_temper.title", "Enduring Tribulation");
        add("advancement.godfall_trinkets.hardship_temper.desc", "Reach tier 5: Oblivion on Broken‑Destiny Core.");

        add("advancement.godfall_trinkets.chest_find_shard.title", "Treasure‑Hunt for Divinity");
        add("advancement.godfall_trinkets.chest_find_shard.desc", "Locate a Divine Shard inside a structure chest.");
    }
}
