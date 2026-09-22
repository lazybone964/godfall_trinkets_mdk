package com.lazybones.godfalltrinkets.config;

import net.minecraftforge.common.ForgeConfigSpec;

/** 登神残饰 - 七大模块全量配置文件 */
public class GodfallConfig {

    // ==================== 模块一：破厄之核 / 神格核心养成 ====================

    public static final ForgeConfigSpec.BooleanValue AUTO_GIVE_BROKEN_CORE;
    public static final ForgeConfigSpec.DoubleValue CORE_EXP_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue[] TIER_EXP = new ForgeConfigSpec.IntValue[10];
    public static final ForgeConfigSpec.IntValue SHIELD_MAX_BASE;
    public static final ForgeConfigSpec.IntValue SHIELD_COOLDOWN_TICK;
    public static final ForgeConfigSpec.IntValue REVERSE_CURSE_TIER;

    public static final ForgeConfigSpec.BooleanValue AUTO_GOD_UPGRADE_STONE;
    public static final ForgeConfigSpec.DoubleValue GOD_CORE_SHARD_POWER_MOD;
    public static final ForgeConfigSpec.DoubleValue GOD_CORE_ALL_ATTR_ADD;
    public static final ForgeConfigSpec.DoubleValue GOD_CORE_GLOBAL_DAMAGE_UP;
    public static final ForgeConfigSpec.DoubleValue GOD_CORE_DAMAGE_REDUCE;
    public static final ForgeConfigSpec.BooleanValue GOD_IMMUNE_ALL_ENV_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue MAX_SINGLE_DAMAGE_RATIO;
    public static final ForgeConfigSpec.IntValue INVULNERABLE_TICK;
    public static final ForgeConfigSpec.DoubleValue CREATIVE_FLY_SPEED_MOD;
    public static final ForgeConfigSpec.DoubleValue CREATIVE_FLY_DAMAGE_MOD;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ARMOR_PIERCE;

    // ==================== 模块二：13枚神位碎片 ====================

    public static final ForgeConfigSpec.DoubleValue SHARD_GLOBAL_POWER;

    // 每片碎片：开关 + 倍率
    public static final ForgeConfigSpec.BooleanValue[] SHARD_ENABLE = new ForgeConfigSpec.BooleanValue[13];
    public static final ForgeConfigSpec.DoubleValue[] SHARD_MOD = new ForgeConfigSpec.DoubleValue[13];

    // 碎片专属独立数值
    public static final ForgeConfigSpec.IntValue SHARD_LIFE_REVIVE_CD_SECONDS;
    public static final ForgeConfigSpec.DoubleValue SHARD_THUNDER_LIGHTNING_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SHARD_SHADOW_BACKSTAB_MULT;
    public static final ForgeConfigSpec.IntValue SHARD_RUNE_ENCHANT_LEVEL_BONUS;
    public static final ForgeConfigSpec.DoubleValue SHARD_SUN_DAY_REGEN_SPEED;

    // ==================== 模块三：九阶成长长剑 ====================

    public static final ForgeConfigSpec.DoubleValue SWORD_GLOBAL_DAMAGE;
    public static final ForgeConfigSpec.IntValue SWORD_FORGE_MAX_PROGRESS;
    public static final ForgeConfigSpec.DoubleValue SWORD_EXP_BONUS;
    public static final ForgeConfigSpec.BooleanValue SWORD_SPAWN_ENABLE;

    public static final ForgeConfigSpec.DoubleValue SWORD_UNDEAD_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_NETHER_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_MOB_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_WITHER_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_DARK_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_BOSS_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_T9_BOSS_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_T9_ELYTRA_DMG;
    public static final ForgeConfigSpec.DoubleValue SWORD_LIFESTEAL_MOD;

    // ==================== 模块四：怪物 / 宝箱掉落 ====================

    public static final ForgeConfigSpec.DoubleValue SHARD_DROP_RATE_MOD;
    public static final ForgeConfigSpec.DoubleValue MOB_LOOT_GLOBAL;
    public static final ForgeConfigSpec.DoubleValue CHEST_LOOT_GLOBAL;
    public static final ForgeConfigSpec.BooleanValue ENDER_DRAGON_SKY_SHARD_GUARANTEED;
    public static final ForgeConfigSpec.BooleanValue BOSS_EXTRA_DROP;

    // ==================== 模块五：盔甲 & 饰品套装 ====================

    public static final ForgeConfigSpec.DoubleValue ARMOR_SET_DAMAGE_MOD;
    public static final ForgeConfigSpec.DoubleValue ARMOR_DAMAGE_TAKEN_MOD;
    public static final ForgeConfigSpec.IntValue CURSE_TRINKET_MAX_DURABILITY;
    public static final ForgeConfigSpec.DoubleValue TRINKET_LUCK_GLOBAL;

    // ==================== 模块六：全局功能总开关 ====================

    public static final ForgeConfigSpec.BooleanValue ENABLE_CURSE_SYSTEM;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ELYTRA_BONUS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ABYSS_VISION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BREAKTHROUGH_SOUND;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BREAKTHROUGH_NOTICE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_GOD_CORE_EVOLUTION;

    // ==================== 模块七：UI 与客户端可视化 ====================

    public static final ForgeConfigSpec.BooleanValue TOOLTIP_COLOR_CUSTOM;
    public static final ForgeConfigSpec.BooleanValue SLOT_ICON_RENDER;
    public static final ForgeConfigSpec.BooleanValue SHOW_TIER_NUMBER;
    public static final ForgeConfigSpec.BooleanValue SHOW_SHARD_COLLECTION_TIP;

    // ==================== Builder ====================

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        // ── 模块一：破厄之核 / 神格 ──
        b.push("1. 破厄之核_神格核心养成");
        AUTO_GIVE_BROKEN_CORE = b.comment("新玩家上线是否自动发放破厄之核").define("auto_give_broken_core", true);
        CORE_EXP_MULTIPLIER = b.comment("击杀怪物获取破厄经验倍率，0=关闭").defineInRange("core_exp_multiplier", 1.0, 0.0, 100.0);
        for (int i = 0; i < 10; i++) {
            TIER_EXP[i] = b.comment("境界 " + i + " 累计所需经验")
                    .defineInRange("tier_exp_" + i, new int[]{0, 300, 800, 1800, 3500, 6000, 10000, 16000, 25000, 40000}[i], 0, Integer.MAX_VALUE);
        }
        SHIELD_MAX_BASE = b.comment("破厄基础护盾容量").defineInRange("shield_max_base", 3, 0, 100000);
        SHIELD_COOLDOWN_TICK = b.comment("护盾刷新冷却刻数（20刻=1秒）").defineInRange("shield_cooldown_tick", 25, 1, 12000);
        REVERSE_CURSE_TIER = b.comment("多少阶反转诅咒（原版9阶，可改为7/10阶）").defineInRange("reverse_curse_tier", 9, 0, 10);

        AUTO_GOD_UPGRADE_STONE = b.comment("集齐13碎片是否自动发放进化石").define("auto_god_upgrade_stone", true);
        GOD_CORE_SHARD_POWER_MOD = b.comment("神核加持下所有神位碎片效果倍率").defineInRange("god_core_shard_power_mod", 1.5, 0.0, 100.0);
        GOD_CORE_ALL_ATTR_ADD = b.comment("神格额外全属性加成（0.35=35%）").defineInRange("god_core_all_attr_add", 0.35, 0.0, 100.0);
        GOD_CORE_GLOBAL_DAMAGE_UP = b.comment("全局伤害增加比例（0.20=20%）").defineInRange("god_core_global_damage_up", 0.20, 0.0, 100.0);
        GOD_CORE_DAMAGE_REDUCE = b.comment("受到伤害减免比例（0.25=25%）").defineInRange("god_core_damage_reduce", 0.25, 0.0, 1.0);
        GOD_IMMUNE_ALL_ENV_DAMAGE = b.comment("是否开启全套环境伤害免疫（火/摔/岩浆/雷击等）").define("god_immune_all_env_damage", true);
        MAX_SINGLE_DAMAGE_RATIO = b.comment("单次受伤上限占自身最大生命值百分比（0.30=30%）").defineInRange("max_single_damage_ratio", 0.30, 0.0, 1.0);
        INVULNERABLE_TICK = b.comment("挨打无敌持续刻数").defineInRange("invulnerable_tick", 30, 0, 200);
        CREATIVE_FLY_SPEED_MOD = b.comment("创造飞行移速增幅").defineInRange("creative_fly_speed_mod", 0.15, 0.0, 1.0);
        CREATIVE_FLY_DAMAGE_MOD = b.comment("创造飞行状态增伤比例（0.30=30%）").defineInRange("creative_fly_damage_mod", 0.30, 0.0, 100.0);
        ENABLE_ARMOR_PIERCE = b.comment("攻击是否无视护甲、韧性、药水抗性、Boss固定限伤").define("enable_armor_pierce", true);
        b.pop();

        // ── 模块二：13枚神位碎片 ──
        b.push("2. 13枚神位碎片");
        SHARD_GLOBAL_POWER = b.comment("所有神位碎片基础效果统一倍率").defineInRange("shard_global_power", 1.0, 0.0, 100.0);

        String[] shardNames = {"sky", "sun", "life", "strength", "fate", "wind", "spirit", "barrier", "void", "shadow", "thunder", "star", "rune"};
        double[] shardDefaults = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        for (int i = 0; i < 13; i++) {
            String n = shardNames[i];
            SHARD_ENABLE[i] = b.comment("是否启用「" + n + "」碎片功能").define("shard_" + n + "_enable", true);
            SHARD_MOD[i] = b.comment("「" + n + "」碎片效果倍率").defineInRange("shard_" + n + "_mod", shardDefaults[i], 0.0, 100.0);
        }

        SHARD_LIFE_REVIVE_CD_SECONDS = b.comment("生命碎片复活冷却秒数").defineInRange("shard_life_revive_cd_seconds", 60, 1, 86400);
        SHARD_THUNDER_LIGHTNING_CHANCE = b.comment("御雷碎片落雷概率（0.20=20%）").defineInRange("shard_thunder_lightning_chance", 0.20, 0.0, 1.0);
        SHARD_SHADOW_BACKSTAB_MULT = b.comment("影杀碎片背击伤害倍率（2.0=200%）").defineInRange("shard_shadow_backstab_mult", 2.0, 0.0, 100.0);
        SHARD_RUNE_ENCHANT_LEVEL_BONUS = b.comment("铭纹碎片附魔等级加成数值").defineInRange("shard_rune_enchant_level_bonus", 5, 0, 100);
        SHARD_SUN_DAY_REGEN_SPEED = b.comment("烈阳碎片白天回血速度（每tick治疗量）").defineInRange("shard_sun_day_regen_speed", 0.0, 0.0, 100.0);
        b.pop();

        // ── 模块三：九阶成长长剑 ──
        b.push("3. 九阶成长长剑");
        SWORD_GLOBAL_DAMAGE = b.comment("全部长剑统一伤害缩放倍率").defineInRange("sword_global_damage", 1.0, 0.0, 100.0);
        SWORD_FORGE_MAX_PROGRESS = b.comment("铁砧锻造单次满进度需求（原版20）").defineInRange("sword_forge_max_progress", 20, 1, 1000);
        SWORD_EXP_BONUS = b.comment("长剑击杀经验加成倍率").defineInRange("sword_exp_bonus", 1.0, 0.0, 100.0);
        SWORD_SPAWN_ENABLE = b.comment("是否允许世界自然生成长剑（关闭只能锻造）").define("sword_spawn_enable", false);

        SWORD_UNDEAD_DMG = b.comment("亡灵生物增伤倍率（1.30=30%）").defineInRange("sword_undead_dmg", 1.30, 1.0, 100.0);
        SWORD_NETHER_DMG = b.comment("下界生物增伤倍率（1.35=35%）").defineInRange("sword_nether_dmg", 1.35, 1.0, 100.0);
        SWORD_MOB_DMG = b.comment("普通怪物增伤倍率（1.25=25%）").defineInRange("sword_mob_dmg", 1.25, 1.0, 100.0);
        SWORD_WITHER_DMG = b.comment("凋零类增伤倍率（1.45=45%）").defineInRange("sword_wither_dmg", 1.45, 1.0, 100.0);
        SWORD_DARK_DMG = b.comment("黑暗环境增伤倍率（1.40=40%）").defineInRange("sword_dark_dmg", 1.40, 1.0, 100.0);
        SWORD_BOSS_DMG = b.comment("Boss增伤倍率（1.40=40%）").defineInRange("sword_boss_dmg", 1.40, 1.0, 100.0);
        SWORD_T9_BOSS_DMG = b.comment("终末龙征剑 Boss/末地生物增伤倍率（1.60=60%）").defineInRange("sword_t9_boss_dmg", 1.60, 1.0, 100.0);
        SWORD_T9_ELYTRA_DMG = b.comment("鞘翅飞行增伤倍率（1.30=30%）").defineInRange("sword_t9_elytra_dmg", 1.30, 1.0, 100.0);
        SWORD_LIFESTEAL_MOD = b.comment("长剑吸血倍率（1.0=原版）").defineInRange("sword_lifesteal_mod", 1.0, 0.0, 100.0);
        b.pop();

        // ── 模块四：怪物 / 宝箱掉落 ──
        b.push("4. 怪物_宝箱掉落");
        SHARD_DROP_RATE_MOD = b.comment("全部神位碎片掉落总倍率").defineInRange("shard_drop_rate_mod", 1.0, 0.0, 100.0);
        MOB_LOOT_GLOBAL = b.comment("模组怪物材料掉落倍率").defineInRange("mob_loot_global", 1.0, 0.0, 100.0);
        CHEST_LOOT_GLOBAL = b.comment("宝箱模组道具掉落倍率").defineInRange("chest_loot_global", 1.0, 0.0, 100.0);
        ENDER_DRAGON_SKY_SHARD_GUARANTEED = b.comment("末影龙是否必掉天穹碎片").define("ender_dragon_sky_shard_guaranteed", true);
        BOSS_EXTRA_DROP = b.comment("Boss是否额外多掉落模组专属材料").define("boss_extra_drop", false);
        b.pop();

        // ── 模块五：盔甲 & 饰品套装 ──
        b.push("5. 盔甲_饰品套装");
        ARMOR_SET_DAMAGE_MOD = b.comment("全套盔甲伤害加成总倍率").defineInRange("armor_set_damage_mod", 1.0, 0.0, 100.0);
        ARMOR_DAMAGE_TAKEN_MOD = b.comment("全套承伤减免倍率（1.0=原版）").defineInRange("armor_damage_taken_mod", 1.0, 0.0, 100.0);
        CURSE_TRINKET_MAX_DURABILITY = b.comment("咒厄转移坠最大耐久").defineInRange("curse_trinket_max_durability", 1000, 1, 100000);
        TRINKET_LUCK_GLOBAL = b.comment("所有饰品幸运加成统一缩放").defineInRange("trinket_luck_global", 1.0, 0.0, 100.0);
        b.pop();

        // ── 模块六：全局功能总开关 ──
        b.push("6. 全局功能总开关");
        ENABLE_CURSE_SYSTEM = b.comment("完全关闭诅咒/祝福体系").define("enable_curse_system", true);
        ENABLE_ELYTRA_BONUS = b.comment("关闭所有鞘翅相关增伤移速").define("enable_elytra_bonus", true);
        ENABLE_ABYSS_VISION = b.comment("是否开启深渊夜视感知").define("enable_abyss_vision", true);
        ENABLE_BREAKTHROUGH_SOUND = b.comment("境界突破音效开关").define("enable_breakthrough_sound", true);
        ENABLE_BREAKTHROUGH_NOTICE = b.comment("境界突破弹窗提示开关").define("enable_breakthrough_notice", true);
        ENABLE_GOD_CORE_EVOLUTION = b.comment("彻底关闭破厄进化神核功能").define("enable_god_core_evolution", true);
        b.pop();

        // ── 模块七：UI 与客户端可视化 ──
        b.push("7. UI与客户端可视化");
        TOOLTIP_COLOR_CUSTOM = b.comment("是否自定义词条颜色").define("tooltip_color_custom", true);
        SLOT_ICON_RENDER = b.comment("Curios槽图标显示开关").define("slot_icon_render", true);
        SHOW_TIER_NUMBER = b.comment("物品是否显示境界数字").define("show_tier_number", true);
        SHOW_SHARD_COLLECTION_TIP = b.comment("集齐碎片提示弹窗开关").define("show_shard_collection_tip", true);
        b.pop();

        SPEC = b.build();
    }
}
