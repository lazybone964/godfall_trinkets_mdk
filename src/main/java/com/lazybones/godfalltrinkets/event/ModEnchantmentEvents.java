package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import com.lazybones.godfalltrinkets.util.AdvancementHelper;
import com.lazybones.godfalltrinkets.ModEnchantments;
import com.lazybones.godfalltrinkets.ModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEnchantmentEvents {

    // ===================== 不朽誓约 玩家tick：递减冷却 =====================
    @SubscribeEvent
    public static void playerTickOathCooldown(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        if(event.player.level().isClientSide()) return;
        if(!(event.player instanceof ServerPlayer sp)) return;
        CompoundTag tag = sp.getPersistentData();
        int cd = tag.getInt("immortal_oath_cooldown");
        if(cd > 0) {
            tag.putInt("immortal_oath_cooldown", cd -1);
        }
    }

    // 不朽誓约 HIGHEST优先级，跑在原版不死图腾前面
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onImmortalOathRevive(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if(!(living instanceof ServerPlayer sp)) return;
        DamageSource ds = event.getSource();
        // 可选：开启下面一行屏蔽虚空死亡复活；注释掉则虚空也能复活
        // if(ds.isOutOfWorld()) return;

        CompoundTag tag = sp.getPersistentData();
        int cooldown = tag.getInt("immortal_oath_cooldown");
        if(cooldown > 0) return;

        ItemStack chestArmor = sp.getInventory().getArmor(2);
        int level = chestArmor.getEnchantmentLevel(ModEnchantments.IMMORTAL_OATH.get());
        if(level <1) return;

        // 触发复活，不消耗附魔，不消耗不死图腾
        event.setCanceled(true);
        sp.setHealth(10.0F);
        sp.removeAllEffects();
        sp.addEffect(new MobEffectInstance(MobEffects.REGENERATION,900,1));
        sp.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,800,0));
        sp.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,100,1));
        sp.level().broadcastEntityEvent(sp,(byte)35);
        tag.putInt("immortal_oath_cooldown",1200); //60秒冷却
    }

    // ========= 实体tick：神陨之怒，记录上次挨打tick，脱战3s清除debuff =========
    @SubscribeEvent
    public static void livingTickWrathClear(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if(target.level().isClientSide()) return;
        CompoundTag tag = target.getPersistentData();
        long lastHitTick = tag.getLong("fallen_wrath_last_hit_tick");
        long currentTick = target.level().getGameTime();
        //超过3s（60tick）没有挨打，移除全部神陨怒层；lastHitTick==0 表示从未被神陨之怒命中，不清理（否则指令添加的debuff会立刻被清掉）
        if(lastHitTick > 0 && currentTick - lastHitTick > 60) {
            if(target.hasEffect(ModMobEffects.FALLEN_WRATH_STACK.get())) {
                target.removeEffect(ModMobEffects.FALLEN_WRATH_STACK.get());
            }
        }
    }

    // ========== 主伤害事件：全部附魔与debuff逻辑集中在这里 =========
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();
        if(!(source.getEntity() instanceof LivingEntity attacker)) return;
        ItemStack weapon = attacker.getMainHandItem();
        int weaponEnchLevel;

        //--------【神陨庇护】盔甲附魔：只有佩戴破厄之核才生效，每级减伤1%，最高5% --------
        if(attacker instanceof Player player && victim instanceof Player hurtPlayer){
            LazyOptional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(hurtPlayer);
            boolean hasBrokenCore = opt.map(h-> h.findFirstCurio(s->s.getItem() == ModItems.BROKEN_CORE.get()).isPresent()).orElse(false);
            if(hasBrokenCore){
                int shelterLv = 0;
                shelterLv += hurtPlayer.getInventory().getArmor(0).getEnchantmentLevel(ModEnchantments.GODFALL_SHELTER.get());
                shelterLv += hurtPlayer.getInventory().getArmor(1).getEnchantmentLevel(ModEnchantments.GODFALL_SHELTER.get());
                shelterLv += hurtPlayer.getInventory().getArmor(2).getEnchantmentLevel(ModEnchantments.GODFALL_SHELTER.get());
                shelterLv += hurtPlayer.getInventory().getArmor(3).getEnchantmentLevel(ModEnchantments.GODFALL_SHELTER.get());
                shelterLv = Math.min(shelterLv,5);
                float reduce = 1.0F - (shelterLv * 0.01F);
                event.setAmount(event.getAmount() * reduce);
            }
        }

        //------【碎核共鸣】武器增伤：level ×5% ×佩戴碎片数量；0碎片无加成 --------
        weaponEnchLevel = weapon.getEnchantmentLevel(ModEnchantments.SHARD_RESONANCE.get());
        if(weaponEnchLevel > 0 && attacker instanceof ServerPlayer spAttacker){
            int shardCount = AdvancementHelper.countEquippedShards(spAttacker);
            float bonus = 1.0F + weaponEnchLevel * 0.05F * shardCount;
            event.setAmount(event.getAmount() * bonus);
        }

        //------【深渊凝视】：目标血量低于30%，施加深渊标记debuff，时长100tick ------
        weaponEnchLevel = weapon.getEnchantmentLevel(ModEnchantments.ABYSS_GAZE.get());
        if(weaponEnchLevel > 0 && victim.getHealth() / victim.getMaxHealth() <= 0.30F){
            victim.addEffect(new MobEffectInstance(ModMobEffects.ABYSS_MARK.get(),100, weaponEnchLevel-1, false,true));
        }

        //------【神陨之怒】连续攻击，叠加神陨怒层debuff，更新上次被攻击tick ------
        weaponEnchLevel = weapon.getEnchantmentLevel(ModEnchantments.FURY_OF_GODFALL.get());
        if(weaponEnchLevel > 0){
            CompoundTag vTag = victim.getPersistentData();
            vTag.putLong("fallen_wrath_last_hit_tick", victim.level().getGameTime());
            int amp = 0;
            var eff = victim.getEffect(ModMobEffects.FALLEN_WRATH_STACK.get());
            if(eff != null) amp = eff.getAmplifier()+1;
            amp = Math.min(amp,4); //最多5层，amplifier0~4
            victim.addEffect(new MobEffectInstance(ModMobEffects.FALLEN_WRATH_STACK.get(),100,amp,false,true));
        }

        //=====【Debuff易伤计算】深渊标记 + 神陨怒层，修改最终伤害 =====
        float dmg = event.getAmount();
        var abyssMark = victim.getEffect(ModMobEffects.ABYSS_MARK.get());
        if(abyssMark != null){
            float mul = 1.0F + (abyssMark.getAmplifier()+1)*0.15F;
            dmg *= mul;
        }
        var wrathStack = victim.getEffect(ModMobEffects.FALLEN_WRATH_STACK.get());
        if(wrathStack != null){
            float perLayer = 0.10F;
            dmg *= 1.0F + (wrathStack.getAmplifier()+1)*perLayer;
        }
        event.setAmount(dmg);

        //------【虚空汲取】按攻击伤害百分比吸血，每级8%，不超过目标剩余血量 --------
        weaponEnchLevel = weapon.getEnchantmentLevel(ModEnchantments.VOID_DRAIN.get());
        if(weaponEnchLevel > 0 && !victim.level().isClientSide()){
            float healVal = event.getAmount() * (weaponEnchLevel * 0.08F);
            healVal = Math.min(healVal, victim.getHealth()); //不能超过目标剩余血量
            attacker.heal(healVal);
        }
    }
}
