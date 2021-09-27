/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.render.Animations;
import net.ccbluex.liquidbounce.features.module.modules.world.ChestStealer;
import net.ccbluex.liquidbounce.utils.render.EaseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiContainer extends MixinGuiScreen {
    @Shadow
    protected int xSize;
    @Shadow
    protected int ySize;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;

    private GuiButton stealButton;

    private float progress = 0F;

    private long lastMS = 0L;

    @Inject(method = "initGui", at = @At("RETURN"), cancellable = true)
    public void injectInitGui(CallbackInfo callbackInfo){
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (guiScreen instanceof GuiChest) {
            if (this.guiTop < 50) { //prevent weird things
                buttonList.add(new GuiButton(1024576, 10, 35, 99, 20, "Disable KillAura"));
                buttonList.add(new GuiButton(727, 110, 35, 99, 20, "Disable Stealer"));
                buttonList.add(stealButton = new GuiButton(1234123, 10, 10, 200, 20, "Steal this chest"));
            } else {
                buttonList.add(new GuiButton(1024576, this.width / 2 - 100, this.guiTop - 30, 99, 20, "Disable KillAura"));
                buttonList.add(new GuiButton(727, this.width / 2 + 1, this.guiTop - 30, 99, 20, "Disable Stealer"));
                buttonList.add(stealButton = new GuiButton(1234123, this.width / 2 - 100, this.guiTop - 55, 200, 20, "Steal this chest"));
            }
        }
        lastMS = System.currentTimeMillis();
        progress = 0F;
    }

    @Override
    protected void injectedActionPerformed(GuiButton button) {
        ChestStealer chestStealer = (ChestStealer) LiquidBounce.moduleManager.getModule(ChestStealer.class);

        if (button.id == 1024576)
            LiquidBounce.moduleManager.getModule(KillAura.class).setState(false);
        if (button.id == 727)
            chestStealer.setState(false);
        if (button.id == 1234123 && !chestStealer.getState()) {
            chestStealer.setContentReceived(mc.thePlayer.openContainer.windowId);
            chestStealer.setOnce(true);
            chestStealer.setState(true);
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreenHead(CallbackInfo callbackInfo){
        if (progress >= 1F) progress = 1F;
        else progress = (float)(System.currentTimeMillis() - lastMS) / 750F;

        double trueAnim = EaseUtils.easeOutQuart(progress);

        final Animations animMod = (Animations) LiquidBounce.moduleManager.getModule(Animations.class);
        if (animMod != null && animMod.getState()) {
            GL11.glPushMatrix();
            switch (animMod.guiAnimations.get()) {
                case "Zoom":
                    GL11.glTranslated((1 - trueAnim) * (width / 2D), (1 - trueAnim) * (height / 2D), 0D);
                    GL11.glScaled(trueAnim, trueAnim, trueAnim);
                    break;
                case "HSlide":
                    GL11.glTranslated((1 - trueAnim) * -width, 0D, 0D);
                    break;
                case "VSlide":
                    GL11.glTranslated(0D, (1 - trueAnim) * -height, 0D);
                    break;
                case "HVSlide":
                    GL11.glTranslated((1 - trueAnim) * -width, (1 - trueAnim) * -height, 0D);
                    break;
            }
        }
        
        ChestStealer chestStealer = (ChestStealer) LiquidBounce.moduleManager.getModule(ChestStealer.class);
        try {
            Minecraft mc = Minecraft.getMinecraft();
            GuiScreen guiScreen = mc.currentScreen;

            if (stealButton != null) stealButton.enabled = !chestStealer.getState();

            if(chestStealer.getState() && chestStealer.getSilenceValue().get() && guiScreen instanceof GuiChest) {
                //mouse focus
                if (!mc.inGameHasFocus) {
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                    mc.leftClickCounter = 10000;
                }
                
                //hide GUI
                if (chestStealer.getShowStringValue().get() && !chestStealer.getStillDisplayValue().get()) {
                    String tipString = "Stealing... Press Esc to stop.";
                    
                    mc.fontRendererObj.drawString(tipString,
                        (width/2)-(mc.fontRendererObj.getStringWidth(tipString)/2)-1,
                        (height/2)+30,0,false);
                    mc.fontRendererObj.drawString(tipString,
                        (width/2)-(mc.fontRendererObj.getStringWidth(tipString)/2)+1,
                        (height/2)+30,0,false);
                    mc.fontRendererObj.drawString(tipString,
                        (width/2)-(mc.fontRendererObj.getStringWidth(tipString)/2),
                        (height/2)+30-1,0,false);
                    mc.fontRendererObj.drawString(tipString,
                        (width/2)-(mc.fontRendererObj.getStringWidth(tipString)/2),
                        (height/2)+30+1,0,false);
                    mc.fontRendererObj.drawString(tipString,
                        (width/2)-(mc.fontRendererObj.getStringWidth(tipString)/2),
                        (height/2)+30,0xffffffff,false);
                }
                
                if (!chestStealer.getStillDisplayValue().get()) callbackInfo.cancel();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"), cancellable = true) 
    public void drawScreenReturn(CallbackInfo callbackInfo) {
        final Animations animMod = (Animations) LiquidBounce.moduleManager.getModule(Animations.class);
        if (animMod != null && animMod.getState())
            GL11.glPopMatrix();
    }
}