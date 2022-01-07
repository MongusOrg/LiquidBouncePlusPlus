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
import org.objectweb.asm.Opcodes;
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

    @Inject(
        method = "renderGameOverlay", 
        at = @At(
            /*value = "FIELD", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHotbar:Z", opcode = Opcodes.GETSTATIC*/
            value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderTooltip(Lnet/minecraft/client/gui/ScaledResolution;F)V", shift = At.Shift.BEFORE, remap = false), 
        remap = false
        )
    public void injectHotbarPre(float partialTicks, CallbackInfo callbackInfo) {
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);
    }

    @Inject(
        method = "renderGameOverlay", 
        at = @At(
            /*value = "FIELD", target = "Lnet/minecraftforge/client/GuiIngameForge;renderHotbar:Z", opcode = Opcodes.GETSTATIC*/
            value = "INVOKE", target = "Lnet/minecraftforge/client/GuiIngameForge;renderTooltip(Lnet/minecraft/client/gui/ScaledResolution;F)V", shift = At.Shift.AFTER, remap = false), 
        remap = false
        )
    public void injectHotbarPost(float partialTicks, CallbackInfo callbackInfo) {
        GlStateManager.translate(0F, RenderUtils.yPosOffset, 0F);
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