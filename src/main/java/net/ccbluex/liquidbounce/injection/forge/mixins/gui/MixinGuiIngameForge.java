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

    @Inject(method = "renderHealth", at = @At("HEAD"), remap = false)
    private void renderHealthBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderHealth", at = @At("RETURN"), remap = false)
    private void renderHealthEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), remap = false)
    private void renderFoodBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderFood", at = @At("RETURN"), remap = false)
    private void renderFoodEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void renderExpBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void renderExpEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), remap = false)
    private void renderArmorBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderArmor", at = @At("RETURN"), remap = false)
    private void renderArmorEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderHealthMount", at = @At("HEAD"), remap = false)
    private void renderHealthMountBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderHealthMount", at = @At("RETURN"), remap = false)
    private void renderHealthMountEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderAir", at = @At("HEAD"), remap = false)
    private void renderAirBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderAir", at = @At("RETURN"), remap = false)
    private void renderAirEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderJumpBar", at = @At("HEAD"), remap = false)
    private void renderJumpBarBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderJumpBar", at = @At("RETURN"), remap = false)
    private void renderJumpBarEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }

}