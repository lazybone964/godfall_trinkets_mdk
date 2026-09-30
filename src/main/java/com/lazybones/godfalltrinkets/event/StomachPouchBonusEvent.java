package com.lazybones.godfalltrinkets.event;

import com.lazybones.godfalltrinkets.GodfallTrinkets;
import com.lazybones.godfalltrinkets.config.GodfallConfig;
import com.lazybones.godfalltrinkets.item.custom.StomachPouchItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GodfallTrinkets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StomachPouchBonusEvent {
    // ========== 大胃袋Modifier固定UUID，【全部使用英文半角短横线 - 】 ==========
    private static final UUID UUID_DAMAGE           = UUID.fromString("11111111-1111-4111-a111-111111111101");
    private static final UUID UUID_MAX_HEALTH      = UUID.fromString("11111111-1111-4111-a111-111111111102");
    private static final UUID UUID_ARMOR           = UUID.fromString("11111111-1111-4111-a111-111111111103");
    private static final UUID UUID_ARMOR_TOUGHNESS = UUID.fromString("11111111-1111-4111-a111-111111111104");
    private static final UUID UUID_KNOCKBACK_RESIST= UUID.fromString("11111111-1111-4111-a111-111111111105");
    private static final UUID UUID_LUCK            = UUID.fromString("11111111-1111-4111-a111-111111111106");
    private static final UUID UUID_ATTACK_SPEED    = UUID.fromString("11111111-1111-4111-a111-111111111107");

    /** 获取佩戴的大胃袋，无返回EMPTY */
    public static ItemStack getEquippedPouch(Player player) {
        Optional<ItemStack> opt = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(h -> h.findFirstCurio(s -> s.getItem() instanceof StomachPouchItem))
                .map(r -> r.stack());
        return opt.orElse(ItemStack.EMPTY);
    }

    /** 移除全部大胃袋添加的Modifier */
    private static void removeAllPouchModifiers(Player player) {
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, UUID_DAMAGE);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, UUID_MAX_HEALTH);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, UUID_ARMOR);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, UUID_ARMOR_TOUGHNESS);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, UUID_KNOCKBACK_RESIST);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.LUCK, UUID_LUCK);
        removeModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, UUID_ATTACK_SPEED);
    }

    private static void removeModifier(Player player, Attribute attr, UUID uuid) {
        AttributeInstance inst = player.getAttribute(attr);
        if(inst != null) {
            inst.removeModifier(uuid);
        }
    }

    private static void addModifier(Player player, Attribute attr, UUID uuid, double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if(inst == null) return;
        inst.removeModifier(uuid);
        AttributeModifier mod = new AttributeModifier(uuid,"stomach_pouch_bonus", amount, op);
        inst.addTransientModifier(mod);
    }

    /**
     * ✅全伤害加成：近战、弓、弩、三叉戟弹射物全部生效
     * 攻击者是玩家就放大伤害
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if(!(sourceEntity instanceof Player player)) return;

        if(player.level().isClientSide()) return;
        ItemStack pouch = getEquippedPouch(player);
        if(pouch.isEmpty()) return;

        float coeffH = StomachPouchItem.getHungerCoeff(pouch);
        double mul = 1.0D + coeffH * GodfallConfig.STOMACH_FULL_DMG_BONUS.get();
        event.setAmount((float)(event.getAmount() * mul));
    }

    /**
     * 服务端PlayerTick，维护全部百分比属性Modifier
     * 每tick先清一遍；佩戴则重新计算添加；卸下则保持清空
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if(player.level().isClientSide()) return;

        ItemStack pouch = getEquippedPouch(player);
        removeAllPouchModifiers(player);
        if(pouch.isEmpty()) {
            return;
        }

        float coeffH = StomachPouchItem.getHungerCoeff(pouch);
        float coeffS = StomachPouchItem.getSatCoeff(pouch);

        //注意：MULTIPLY_BASE = 基于【基础值】百分比叠加，不叠加其他modifier
        //1.生命上限 百分比
        double hpBonus = coeffH * GodfallConfig.STOMACH_FULL_MAXHP_BONUS.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, UUID_MAX_HEALTH, hpBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        //2.护甲 百分比
        double armBonus = coeffS * GodfallConfig.STOMACH_FULL_ARMOR_BONUS.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, UUID_ARMOR, armBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        //3.护甲韧性 百分比
        double toughBonus = coeffS * GodfallConfig.STOMACH_FULL_TOUGH_BONUS.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, UUID_ARMOR_TOUGHNESS, toughBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        //4.击退抗性（绝对值，上限配置）
        double krVal = coeffS * GodfallConfig.STOMACH_FULL_KNOCKBACK_RESIST.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, UUID_KNOCKBACK_RESIST, krVal, AttributeModifier.Operation.ADDITION);
        //5.幸运 百分比
        double luckBonus = coeffS * GodfallConfig.STOMACH_FULL_LUCK_BONUS.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.LUCK, UUID_LUCK, luckBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        //6.攻击速度 百分比
        double atkSpdBonus = coeffS * GodfallConfig.STOMACH_FULL_ATKSPEED_BONUS.get();
        addModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, UUID_ATTACK_SPEED, atkSpdBonus, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}
