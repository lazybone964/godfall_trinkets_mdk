package com.lazybones.godfalltrinkets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Consumer;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GodfallTrinkets.MOD_ID);
    // 贴图路径常量
    private static final ResourceLocation ABYSS_MARK_TEX = ResourceLocation.parse("godfall_trinkets:textures/mob_effect/abyss_mark.png");
    private static final ResourceLocation FALLEN_WRATH_TEX = ResourceLocation.parse("godfall_trinkets:textures/mob_effect/fallen_wrath_stack.png");

    /** 深渊标记：受到伤害增加 */
    public static final RegistryObject<MobEffect> ABYSS_MARK = MOB_EFFECTS.register("abyss_mark",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x602090) {
                @Override
                public boolean isDurationEffectTick(int duration, int amplifier) {
                    return false;
                }
                // =====客户端渲染注册（只在客户端执行）=====
                @Override
                public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
                    consumer.accept(new IClientMobEffectExtensions() {
                        // 背包界面内的图标
                        @Override
                        public boolean renderInventoryIcon(MobEffectInstance instance, net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                            guiGraphics.blit(ABYSS_MARK_TEX, x, y+7, blitOffset, 0,0,18,18,18,18);
                            return true;
                        }
                        // HUD右上角屏幕图标
                        @Override
                        public boolean renderGuiIcon(MobEffectInstance instance, net.minecraft.client.gui.Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                            guiGraphics.blit(ABYSS_MARK_TEX, x+3, y+3, 0, 0, 0, 18, 18, 18, 18);
                            return true;
                        }
                    });
                }
            });

    /** 神陨怒层：可堆叠易伤debuff */
    public static final RegistryObject<MobEffect> FALLEN_WRATH_STACK = MOB_EFFECTS.register("fallen_wrath_stack",
            () -> new MobEffect(MobEffectCategory.HARMFUL,0x903020){
                @Override
                public boolean isDurationEffectTick(int duration, int amplifier) {
                    return false;
                }
                @Override
                public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
                    consumer.accept(new IClientMobEffectExtensions() {
                        @Override
                        public boolean renderInventoryIcon(MobEffectInstance instance, net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                            guiGraphics.blit(FALLEN_WRATH_TEX, x, y+7, blitOffset,0,0,18,18,18,18);
                            return true;
                        }
                        @Override
                        public boolean renderGuiIcon(MobEffectInstance instance, net.minecraft.client.gui.Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                            guiGraphics.blit(FALLEN_WRATH_TEX, x+3, y+3, 0, 0, 0, 18, 18, 18, 18);
                            return true;
                        }
                    });
                }
            });
}
