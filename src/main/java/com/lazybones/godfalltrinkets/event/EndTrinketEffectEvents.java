package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.EnderMan;          // ← 修复：EnderMan
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.TickEvent; // ← 改这里
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler; // ← 修复

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EndTrinketEffectEvents {

    private static final Random RAND = new Random();
    private static final String EYE_COOLDOWN_TAG = "eye_end_cooldown";
    private static final long HOUR_TICK = 1200;

    // 缓存玩家原始护甲/韧性
    private static final Map<Player, Double> ORIG_ARMOR = new HashMap<>();
    private static final Map<Player, Double> ORIG_TOUGH = new HashMap<>();

    // 工具：判断玩家是否佩戴指定饰品
    private static boolean hasTrinket(Player p, RegistryObject<?> item) {
        // ← 修复：Curios API 5.x 使用 ICuriosItemHandler + isEquipped
        return CuriosApi.getCuriosInventory(p)
                .map(handler -> handler.isEquipped((Item) item.get()))
                .orElse(false);
    }

    // 受伤事件：处理所有伤害相关的逻辑（免疫、反弹、增伤、保命）
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;

        Entity target = event.getEntity(); // 被攻击的目标（玩家）
        Entity sourceEntity = event.getSource().getEntity(); // 攻击源实体

        float damage = event.getAmount();
        boolean fatal = p.getHealth() - damage <= 0;

        // --- 增伤逻辑开始 ---
        float dmgMult = 1.0F;
        // 龙核之心增伤
        if (hasTrinket(p, ModItems.DRAGON_CORE_HEART)) {
            boolean isBoss = target instanceof EnderDragon || target instanceof WitherBoss || target instanceof Warden;
            boolean isEndMob = target instanceof EnderMan || target instanceof Shulker; // ← 修复：EnderMan
            if (isBoss || isEndMob) dmgMult *= 1.4F;
        }
        // 寂灭龙脊残血全局增伤
        if (hasTrinket(p, ModItems.DRAGON_SPINE_END)) {
            float hpPercent = p.getHealth() / p.getMaxHealth();
            float dmgBoost = 1 - hpPercent;
            dmgMult *= (1 + dmgBoost);
        }
        damage = damage * dmgMult;
        // --- 增伤逻辑结束 ---

        // 龙核之心免疫末影龙冲撞 & 龙息
        if (hasTrinket(p, ModItems.DRAGON_CORE_HEART)) {
            var src = event.getSource();
            boolean isDragonBreath = src.is(DamageTypes.DRAGON_BREATH);
            boolean isDragonAttack = src.getEntity() instanceof EnderDragon; // ← 替代 DRAGON_ATTACK
            if (isDragonBreath || isDragonAttack) {
                damage = 0;
            }
        }

        // 终末之眼：致命伤消耗末影珍珠回血传送
        if (hasTrinket(p, ModItems.EYE_OF_THE_END) && fatal) {
            long now = p.level().getGameTime();
            long cd = p.getPersistentData().getLong(EYE_COOLDOWN_TAG);
            if (now >= cd) {
                int pearlSlot = -1;
                for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                    ItemStack stack = p.getInventory().getItem(i);
                    if (stack.getItem() == Items.ENDER_PEARL && stack.getCount() > 0) {
                        pearlSlot = i;
                        break;
                    }
                }
                if (pearlSlot != -1) {
                    p.getInventory().removeItem(pearlSlot, 1);
                    p.heal(p.getMaxHealth() * 0.3F);
                    BlockPos safe = findSafePos(p.blockPosition(), p.level(), 5);
                    p.setPos(safe.getX(), safe.getY(), safe.getZ());
                    p.getPersistentData().putLong(EYE_COOLDOWN_TAG, now + HOUR_TICK);
                    damage = 0;
                }
            }
        }

        // 寂灭龙脊25%概率反弹30%龙伤
        if (hasTrinket(p, ModItems.DRAGON_SPINE_END) && sourceEntity instanceof LivingEntity attacker && RAND.nextFloat() <= 0.25F) {
            attacker.hurt(event.getSource(), damage * 0.3F);
        }

        event.setAmount(Math.max(0, damage));
    }

    // Tick：残晶冠锁当前血量1点、护甲韧性倍率；寂灭龙脊移速攻速加成
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) { // ← 改这里
        Player p = e.player;
        if (e.phase != TickEvent.Phase.END || p.level().isClientSide()) return; // ← 改这里

        boolean hasCrown = hasTrinket(p, ModItems.CRYSTAL_CROWN);
        boolean hasSpine = hasTrinket(p, ModItems.DRAGON_SPINE_END);

        AttributeInstance armorAttr = p.getAttribute(Attributes.ARMOR);
        AttributeInstance toughAttr = p.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (hasCrown) {
            // 强制当前血量=1
            if (p.getHealth() > 1.0F) p.setHealth(1.0F);
            // 缓存原始基础值
            ORIG_ARMOR.putIfAbsent(p, armorAttr.getBaseValue());
            ORIG_TOUGH.putIfAbsent(p, toughAttr.getBaseValue());
            double baseArmor = ORIG_ARMOR.get(p);
            double baseTough = ORIG_TOUGH.get(p);
            armorAttr.setBaseValue(baseArmor * 3.0D);
            toughAttr.setBaseValue(baseTough * 2.0D);
        } else {
            // 卸下饰品恢复原始属性
            if (ORIG_ARMOR.containsKey(p)) {
                armorAttr.setBaseValue(ORIG_ARMOR.get(p));
                toughAttr.setBaseValue(ORIG_TOUGH.get(p));
                ORIG_ARMOR.remove(p);
                ORIG_TOUGH.remove(p);
            }
        }

        // 寂灭龙脊：血量越低移速、攻速越高
        if (hasSpine) {
            float hpRatio = p.getHealth() / p.getMaxHealth();
            float speedBoost = (1 - hpRatio) * 0.3F;
            float atkBoost = (1 - hpRatio) * 0.25F;
            p.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1D + speedBoost);
            p.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4D + atkBoost);
        } else {
            p.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1D);
            p.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(4D);
        }
    }

    // 寻找5格内安全站立点（终末之眼传送）
    private static BlockPos findSafePos(BlockPos origin, Level level, int range) {
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -3; y <= 3; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockPos below = pos.below();
                    if (level.getBlockState(below).isSolid() && level.getBlockState(pos).isAir()) {
                        return pos;
                    }
                }
            }
        }
        return origin;
    }
}