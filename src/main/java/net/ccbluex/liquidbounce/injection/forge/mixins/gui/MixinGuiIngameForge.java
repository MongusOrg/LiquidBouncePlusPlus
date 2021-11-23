/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.lwjgl.opengl.GL11;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderBossHealth()V", shift = At.Shift.AFTER, remap = false))
    private void renderOverlayBegin(float partialTicks, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderTitle(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Float;)V", shift = At.Shift.AFTER))
    private void renderOverlayEnd(float partialTicks, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

}