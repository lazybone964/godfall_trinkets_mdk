package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.Random;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrinketBossEffectEvents {

    private static final Random RAND = new Random();

    // 工具：判断玩家是否佩戴指定饰品
    // 修复点：明确泛型为 Item，并使用 Optional 链式调用
    private static boolean hasTrinket(Player player, RegistryObject<Item> item) {
        Optional<?> curios = CuriosApi.getCuriosInventory(player).resolve();
        if (curios.isEmpty()) return false;

        // 注意：Curios API 在不同版本返回类型可能略有不同，这里采用通用写法
        return CuriosApi.getCuriosInventory(player).map(inv -> inv.findFirstCurio(stack -> stack.is(item.get())).isPresent()).orElse(false);
    }

    // 获取环境亮度（方块光照）
    // 修复点：p.level 改为 p.level()
    private static int getBlockLight(Player p) {
        return p.level().getBrightness(LightLayer.BLOCK, p.blockPosition());
    }

    // 1. 枯骨之心：免疫凋零、攻击挂凋零
    @SubscribeEvent
    public static void onApplyWither(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player p)) return;
        // 修复点：event.getEffect() 结构变更，需先获取 EffectInstance
        if (event.getEffectInstance() == null || event.getEffectInstance().getEffect() != MobEffects.WITHER) return;

        if (hasTrinket(p, ModItems.WITHER_HEART)) {
            event.setResult(MobEffectEvent.Applicable.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        // 修复点：AttackEntityEvent 获取玩家的方法变更
        Player p = event.getEntity();

        if (hasTrinket(p, ModItems.WITHER_HEART) && RAND.nextFloat() <= 0.2f) {
            // 修复点：getTarget 返回的是 Entity，需要强转为 LivingEntity 才能加药水效果
            if (event.getTarget() instanceof LivingEntity target) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
            }
        }
    }

    // 2. 王者之握 & 凋零王冠 & 幽匿破甲：攻击增伤与破甲
    @SubscribeEvent
    public static void onAttackDamage(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player p)) return;
        float dmg = event.getAmount();

        // 王者之握：飞行增伤
        if (hasTrinket(p, ModItems.KINGS_GRIP) && p.isFallFlying()) {
            dmg *= 1.25f;
        }

        // 凋零王冠：两层增伤
        if (hasTrinket(p, ModItems.WITHER_CROWN)) {
            LivingEntity target = event.getEntity();
            if (target.getMaxHealth() > p.getMaxHealth()) {
                dmg *= 1.30f;
            }
            if (target.hasEffect(MobEffects.WITHER)) {
                dmg *= 1.30f;
            }
        }

        // 幽匿破甲：黑暗概率无视护甲
        // 逻辑：亮度越低，概率越高。亮度0时 15*5% = 75% 概率破甲
        if (hasTrinket(p, ModItems.WARDEN_ARMORBREAK)) {
            int light = getBlockLight(p);
            if (light < 15) {
                int stack = 15 - light;
                float chance = stack * 0.05f;
                if (RAND.nextFloat() <= chance) {
                    // 简单粗暴的破甲：将伤害设置为极大值，让护甲计算忽略不计
                    // 或者你可以使用 event.setAmount(dmg * 2.0f) 做倍率提升
                    dmg = 9999f;
                }
            }
        }

        event.setAmount(dmg);
    }

    // 3. 凋零脊骨坠：受伤转化治疗
    @SubscribeEvent
    public static void onPlayerTakeDmg(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        // 注意：这里要防止自我回环（比如摔落伤害），但根据你的设计是直接转化
        if (hasTrinket(p, ModItems.WITHER_SPINE)) {
            float heal = event.getAmount() * 0.30f;
            p.heal(heal);
        }
    }

    // 4. 监守者三件黑暗被动（tick持续buff）
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;

        // 客户端不执行逻辑
        if (p.level().isClientSide) return;

        int light = getBlockLight(p);
        int stack = Math.max(0, 15 - light);
        if (stack <= 0) return;

        // 幽匿之眼：幸运
        if (hasTrinket(p, ModItems.WARDEN_EYE)) {
            // 等级是 stack-1 因为等级0代表1级效果
            p.addEffect(new MobEffectInstance(MobEffects.LUCK, 40, stack - 1, false, false));
        }

        // 幽匿脉动：生命恢复
        if (hasTrinket(p, ModItems.WARDEN_PULSE)) {
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, stack - 1, false, false));
        }
    }

    // 5. 幽匿重压：受伤减伤
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!hasTrinket(p, ModItems.WARDEN_WEIGHT)) return;

        int light = getBlockLight(p);
        int stack = Math.max(0, 15 - light);
        // 每层减少1%，最高15%
        float reduce = 1f - (stack * 0.01f);
        event.setAmount(event.getAmount() * reduce);
    }
}