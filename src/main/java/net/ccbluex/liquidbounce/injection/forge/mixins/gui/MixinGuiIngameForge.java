/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import net.minecraft.client.Minecraft;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.EaseUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.lwjgl.opengl.GL11;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends MixinGuiInGame {

    @Shadow(remap = false)
    abstract boolean pre(ElementType type);

    @Shadow(remap = false)
    abstract void post(ElementType type);

    public float xScale = 0F;

    @Inject(method = "renderHealth", at = @At("HEAD"), remap = false)
    private void renderHealthBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderHealth", at = @At("RETURN"), remap = false)
    private void renderHealthEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), remap = false)
    private void renderFoodBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderFood", at = @At("RETURN"), remap = false)
    private void renderFoodEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void renderExpBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void renderExpEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), remap = false)
    private void renderArmorBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderArmor", at = @At("RETURN"), remap = false)
    private void renderArmorEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderHealthMount", at = @At("HEAD"), remap = false)
    private void renderHealthMountBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderHealthMount", at = @At("RETURN"), remap = false)
    private void renderHealthMountEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderAir", at = @At("HEAD"), remap = false)
    private void renderAirBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderAir", at = @At("RETURN"), remap = false)
    private void renderAirEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderJumpBar", at = @At("HEAD"), remap = false)
    private void renderJumpBarBegin(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderJumpBar", at = @At("RETURN"), remap = false)
    private void renderJumpBarEnd(int width, int height, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }
/*
    @Inject(method = "renderChat", at = @At("HEAD"), remap = false)
    private void renderChatBegin(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderChat", at = @At("RETURN"), remap = false)
    private void renderChatEnd(int width, int height, CallbackInfo callbackInfo) {
        GlStateManager.popMatrix();
    }
*/
    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), remap = false)
    private void renderRecordOverlayBegin(int width, int height, float partialTicks, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderRecordOverlay", at = @At("RETURN"), remap = false)
    private void renderRecordOverlayEnd(int width, int height, float partialTicks, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderToolHightlight", at = @At("HEAD"), remap = false)
    private void renderToolHightlightBegin(ScaledResolution sc, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(method = "renderToolHightlight", at = @At("RETURN"), remap = false)
    private void renderToolHightlightEnd(ScaledResolution sc, CallbackInfo callbackInfo) {
        final boolean state = LiquidBounce.moduleManager.getModule(HUD.class).getState();
        if (!state) return;
        GlStateManager.popMatrix();
    }

    @Overwrite(remap = false)
    protected void renderPlayerList(int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        ScoreObjective scoreobjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = mc.thePlayer.sendQueue;

        if (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)
        {
            xScale = AnimationUtils.animate((mc.gameSettings.keyBindPlayerList.isKeyDown() ? 100F : 0F), xScale, 0.0125F * RenderUtils.deltaTime);
            float rescaled = xScale / 100F;
            boolean displayable = rescaled > 0F;
            this.overlayPlayerList.updatePlayerList(displayable);
            if (!displayable || pre(PLAYER_LIST)) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(width / 2F * (1F - rescaled), 0F, 0F);
            GlStateManager.scale(rescaled, rescaled, rescaled);
            this.overlayPlayerList.renderPlayerlist(width, mc.theWorld.getScoreboard(), scoreobjective);
            GlStateManager.popMatrix();
            post(PLAYER_LIST);
        }
        else
        {
            this.overlayPlayerList.updatePlayerList(false);
        }
    }

}