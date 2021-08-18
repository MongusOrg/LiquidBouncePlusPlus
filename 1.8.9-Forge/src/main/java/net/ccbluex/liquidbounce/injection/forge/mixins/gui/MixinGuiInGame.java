/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.Crosshair;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.utils.ClassUtils;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.lwjgl.opengl.GL11;

@Mixin(GuiIngame.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInGame {

    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true) 
    private void injectCrosshair(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final Crosshair crossHair = (Crosshair) LiquidBounce.moduleManager.getModule(Crosshair.class);
        if (crossHair.getState() && crossHair.noVanillaCH.get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);
        if ((antiBlind.getState() && antiBlind.getScoreBoard().get()) || LiquidBounce.moduleManager.getModule(HUD.class).getState())
            callbackInfo.cancel();
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);

        if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && hud.getState() && hud.getBlackHotbarValue().get()) {
            EntityPlayer entityPlayer = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

            int middleScreen = sr.getScaledWidth() / 2;

            //GlStateManager.pushMatrix();
            //GuiIngame.drawRect(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 90, sr.getScaledHeight(), Integer.MIN_VALUE);
            //GuiIngame.drawRect(0, sr.getScaledHeight() - 24, sr.getScaledWidth(), sr.getScaledHeight(), Integer.MIN_VALUE);
            //GuiIngame.drawRect(middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 1, sr.getScaledHeight() - 2, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight(), Integer.MAX_VALUE);
            //RenderUtils.roundBlur(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 90, sr.getScaledHeight() - 22, 3);
            
            if (hud.getBlackHotbarBlurValue().get())
                BlurUtils.blurArea(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 90, sr.getScaledHeight() - 22, 15);

            GuiIngame.drawRect(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 90, sr.getScaledHeight() - 22, Integer.MIN_VALUE);
            GuiIngame.drawRect(middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 1, sr.getScaledHeight() - 2, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight() - 22, Integer.MAX_VALUE);
            //GlStateManager.popMatrix();

            GlStateManager.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for(int j = 0; j < 9; ++j) {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(j, k, l - 1, partialTicks, entityPlayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if(antiBlind.getState() && antiBlind.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}