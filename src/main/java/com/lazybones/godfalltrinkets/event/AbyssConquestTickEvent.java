package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.item.ModItems;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent; // ← 关键：加这个导入
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbyssConquestTickEvent {
    private static final int CLEANSE_TICK = 20;
    private static final String CLEANSE_TAG = "abyss_chest_cleanse_tick";

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) { // ← 类名改成 PlayerTickEvent
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        Player p = event.player;
        ItemStack head = p.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = p.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = p.getItemBySlot(EquipmentSlot.LEGS);

        // 深渊头盔：幸运Ⅱ
        if (head.getItem() == ModItems.ABYSS_GOD_HELMET.get()) {
            p.addEffect(new MobEffectInstance(MobEffects.LUCK, 40, 1, false, false));
        }

        // 深渊护腿：移速20%
        if (legs.getItem() == ModItems.ABYSS_GOD_LEGGINGS.get()) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
        }

        // 深渊胸甲 计时清debuff
        if (chest.getItem() == ModItems.ABYSS_GOD_CHESTPLATE.get()) {
            int tick = p.getPersistentData().getInt(CLEANSE_TAG) + 1;
            p.getPersistentData().putInt(CLEANSE_TAG, tick);
            if (tick >= CLEANSE_TICK) {
                p.getPersistentData().putInt(CLEANSE_TAG, 0);
                List<MobEffect> toRemove = new ArrayList<>();
                for (MobEffectInstance inst : p.getActiveEffects()) {
                    MobEffect eff = inst.getEffect();
                    if (eff == MobEffects.WITHER
                            || eff == MobEffects.POISON
                            || eff == MobEffects.MOVEMENT_SLOWDOWN
                            || eff == MobEffects.DIG_SLOWDOWN
                            || eff == MobEffects.BLINDNESS
                            || eff == MobEffects.DARKNESS
                            || eff == MobEffects.WEAKNESS
                            || eff == MobEffects.HUNGER) {
                        toRemove.add(eff);
                    }
                }
                for (MobEffect eff : toRemove) {
                    p.removeEffect(eff);
                }
            }
        }
    }

    // 头盔免疫黑暗施加
    @SubscribeEvent
    public static void onApplyDarkness(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (event.getEffectInstance().getEffect() != MobEffects.DARKNESS) return;
        ItemStack head = p.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() == ModItems.ABYSS_GOD_HELMET.get()) {
            event.setResult(MobEffectEvent.Applicable.Result.DENY);
        }
    }

    // 胸甲减伤 + 靴子免坠落
    @SubscribeEvent
    public static void onTakeDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        ItemStack chest = p.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack boot = p.getItemBySlot(EquipmentSlot.FEET);
        float dmg = event.getAmount();

        if (chest.getItem() == ModItems.ABYSS_GOD_CHESTPLATE.get()) {
            dmg *= 0.88F;
        }
        if (boot.getItem() == ModItems.ABYSS_GOD_BOOTS.get() && event.getSource().is(DamageTypes.FALL)) {
            dmg = 0;
        }
        event.setAmount(Math.max(0, dmg));
    }
}