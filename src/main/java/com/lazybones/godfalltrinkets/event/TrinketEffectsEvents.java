package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "godfall_trinkets")
public class TrinketEffectsEvents {

    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        if (player.tickCount % 20 == 0) {
            tickCleanseBracelet(player);
            tickVitalAmulet(player);
            tickShockAmulet(player);
            tickDeepRockRingFatigue(player);
            tickProsperityBraceletGrowth(player);
        }
        tickBarrierHeal(player);
    }

    private static void tickCleanseBracelet(Player player) {
        if (!hasTrinket(player, ModItems.PURIFYING_BRACELET)) return;
        for (MobEffectInstance eff : player.getActiveEffects()) {
            if (!eff.getEffect().isBeneficial()) {
                player.removeEffect(eff.getEffect());
                break;
            }
        }
    }

    private static void tickVitalAmulet(Player player) {
        if (!hasTrinket(player, ModItems.VITAL_AMULET)) return;
        player.heal(3f);
    }

    private static void tickShockAmulet(Player player) {
        if (!hasTrinket(player, ModItems.SHOCK_AMULET)) return;
        AABB box = new AABB(player.blockPosition()).inflate(10);
        int count = (int) player.level().getEntitiesOfClass(Mob.class, box,
                e -> e.isAlive() && e.getTarget() != null).size();
        float heal = Math.min(count, 6f);
        if (heal > 0) player.heal(heal);
    }

    private static void tickBarrierHeal(Player player) {
        if (!hasTrinket(player, ModItems.BARRIER_BRACELET)) return;
        if (player.isBlocking() && player.tickCount % 40 == 0) {
            player.heal(0.8f);
        }
    }

    // 深岩寻矿戒：免疫挖掘疲劳
    private static void tickDeepRockRingFatigue(Player player) {
        if (!hasTrinket(player, ModItems.DEEP_ROCK_RING)) return;
        MobEffectInstance fatigue = player.getEffect(MobEffects.DIG_SLOWDOWN);
        if (fatigue != null) player.removeEffect(MobEffects.DIG_SLOWDOWN);
    }

    // 繁生腕镯：10格内幼崽生长加速30%
    private static void tickProsperityBraceletGrowth(Player player) {
        if (!hasTrinket(player, ModItems.PROSPERITY_BRACELET)) return;
        AABB box = new AABB(player.blockPosition()).inflate(10);
        for (AgeableMob mob : player.level().getEntitiesOfClass(AgeableMob.class, box,
                e -> e.isBaby() && e.isAlive())) {
            mob.setAge(mob.getAge() + 1);
        }
    }

    // ======================== LIVING HURT ========================
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity victim = event.getEntity();

        if (victim instanceof Player player) {
            float dmg = event.getAmount();
            dmg = applyDeathDefyRing(player, dmg, event);
            dmg = applyBarrierBracelet(player, dmg, event);
            dmg = applyEmberBraceletDR(player, dmg, event);
            event.setAmount(Math.max(0, dmg));
            if (hasTrinket(player, ModItems.VITAL_AMULET) && event.getAmount() > 0) {
                player.heal(2f);
            }
        }

        if (event.getSource().getEntity() instanceof Player attacker) {
            float dmg = event.getAmount();
            dmg = applySoulDrainRing(attacker, dmg);
            dmg = applyHunterRing(attacker, victim, dmg);
            dmg = applyAbyssRuneRing(attacker, dmg);
            event.setAmount(Math.max(0, dmg));
        }
    }

    private static float applyAbyssRuneRing(Player attacker, float dmg) {
        if (!hasTrinket(attacker, ModItems.ABYSS_RUNE_RING)) return dmg;
        return dmg * 1.08f;
    }

    private static float applyDeathDefyRing(Player player, float dmg, LivingHurtEvent event) {
        if (!hasTrinket(player, ModItems.DEATH_DEFY_RING)) return dmg;
        if (player.getHealth() / player.getMaxHealth() > 0.35f) return dmg;
        long last = player.getPersistentData().getLong("death_defy_cd");
        long now = player.level().getGameTime();
        if (now - last > 45 * 20) {
            player.getPersistentData().putLong("death_defy_cd", now);
            player.invulnerableTime = 60;
            return 0;
        }
        return dmg * 0.72f;
    }

    private static float applyBarrierBracelet(Player player, float dmg, LivingHurtEvent event) {
        if (!hasTrinket(player, ModItems.BARRIER_BRACELET)) return dmg;
        DamageSource src = event.getSource();
        if (src.is(DamageTypes.EXPLOSION) || src.is(DamageTypes.PLAYER_EXPLOSION))
            return dmg * 0.78f;
        if (player.isBlocking() && src.getDirectEntity() instanceof Projectile) return 0;
        return dmg;
    }

    private static float applyEmberBraceletDR(Player player, float dmg, LivingHurtEvent event) {
        if (!hasTrinket(player, ModItems.EMBER_BRACELET)) return dmg;
        DamageSource src = event.getSource();
        if (src.is(DamageTypes.IN_FIRE) || src.is(DamageTypes.ON_FIRE)
                || src.is(DamageTypes.LAVA) || src.is(DamageTypes.HOT_FLOOR)) return 0;
        if (src.is(DamageTypes.MAGIC) || src.is(DamageTypes.WITHER)
                || src.is(DamageTypes.INDIRECT_MAGIC)) return dmg * 0.5f;
        return dmg;
    }

    private static float applySoulDrainRing(Player attacker, float dmg) {
        if (!hasTrinket(attacker, ModItems.SOUL_DRAIN_RING)) return dmg;
        attacker.heal(dmg * 0.15f);
        return dmg;
    }

    private static float applyHunterRing(Player attacker, LivingEntity victim, float dmg) {
        if (!hasTrinket(attacker, ModItems.HUNTER_RING)) return dmg;
        if (victim.getHealth() / victim.getMaxHealth() > 0.5f) return dmg * 1.16f;
        return dmg;
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player && hasTrinket(player, ModItems.EMBER_BRACELET)) {
            DamageSource src = event.getSource();
            if (src.is(DamageTypes.IN_FIRE) || src.is(DamageTypes.ON_FIRE)
                    || src.is(DamageTypes.LAVA) || src.is(DamageTypes.HOT_FLOOR))
                event.setCanceled(true);
        }
    }

    // ======================== XP EVENTS: 渊纹铭刻戒 + 熔渊锻符 ========================
    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        int amt = event.getAmount();

        if (hasTrinket(player, ModItems.ABYSS_RUNE_RING) && amt > 0) {
            event.setAmount((int)(amt * 1.12));
        }
        if (hasTrinket(player, ModItems.FORGE_AMULET) && amt < 0) {
            event.setAmount((int)(amt * 0.75));
        }
    }

    // ======================== LIVING DEATH ========================
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity victim = event.getEntity();

        if (hasTrinket(player, ModItems.SOUL_DRAIN_RING)) player.heal(3f);
        if (hasTrinket(player, ModItems.DEATH_DEFY_RING)
                && player.getHealth() / player.getMaxHealth() < 0.35f) player.heal(4f);
        if (hasTrinket(player, ModItems.HUNTER_RING)) player.heal(3.5f);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult hit
                && hit.getEntity() instanceof Player player
                && hasTrinket(player, ModItems.BARRIER_BRACELET)
                && player.isBlocking()) {
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            if (event.getProjectile().getOwner() instanceof LivingEntity owner) {
                owner.hurt(player.damageSources().thorns(player),
                        0.3f * (float) event.getProjectile().getDeltaMovement().length());
            }
            event.getProjectile().discard();
        }
    }

    @SubscribeEvent
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            AABB box = new AABB(event.getPos()).inflate(8);
            for (Player player : level.players()) {
                if (player.distanceToSqr(event.getPos().getCenter()) < 64
                        && hasTrinket(player, ModItems.FERTILE_BRACELET)) {
                    event.getLevel().getBlockState(event.getPos())
                            .randomTick(level, event.getPos(), level.random);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLooting(LootingLevelEvent event) {
        if (event.getDamageSource() != null
                && event.getDamageSource().getEntity() instanceof Player player
                && hasTrinket(player, ModItems.TREASURE_AMULET)) {
            event.setLootingLevel(event.getLootingLevel() + 1);
        }
    }

    // 深岩寻矿戒：挖矿 25% 双倍原矿
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;
        if (!hasTrinket(player, ModItems.DEEP_ROCK_RING)) return;

        BlockState state = event.getState();
        if (state.is(BlockTags.COAL_ORES) || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.IRON_ORES) || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.DIAMOND_ORES) || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.REDSTONE_ORES) || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.COPPER_ORES)) {
            if (RAND.nextFloat() < 0.25f) {
                List<ItemStack> drops = Block.getDrops(state, (ServerLevel) player.level(),
                        event.getPos(), player.level().getBlockEntity(event.getPos()), player,
                        player.getMainHandItem());
                for (ItemStack drop : drops) {
                    Block.popResource(player.level(), event.getPos(), drop);
                }
            }
        }
    }

    // 繁生腕镯：宰杀 20% 双倍肉/皮革
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!hasTrinket(player, ModItems.PROSPERITY_BRACELET)) return;
        if (!(event.getEntity() instanceof Animal)) return;
        if (RAND.nextFloat() < 0.2f) {
            for (var drop : event.getDrops()) {
                event.getDrops().add(drop.copy());
            }
        }
    }

    // 繁生腕镯：繁殖冷却 -25%
    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        Player player = event.getCausedByPlayer();
        if (player != null && hasTrinket(player, ModItems.PROSPERITY_BRACELET)) {
            if (event.getParentA() instanceof Animal a) a.setAge(Math.max(0, a.getAge() + 3000));
            if (event.getParentB() instanceof Animal b) b.setAge(Math.max(0, b.getAge() + 3000));
        }
    }

    // 深岩寻矿戒：阻止挖掘疲劳被施加
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.DIG_SLOWDOWN
                && event.getEntity() instanceof Player player
                && hasTrinket(player, ModItems.DEEP_ROCK_RING)) {
            event.setResult(MobEffectEvent.Applicable.Result.DENY);
        }
    }

    private static boolean hasTrinket(Player player, RegistryObject<? extends Item> item) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(stack -> stack.is(item.get())))
                .isPresent();
    }
}
