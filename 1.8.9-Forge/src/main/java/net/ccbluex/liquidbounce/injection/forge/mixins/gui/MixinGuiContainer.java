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

    @Inject(method = "initGui", at = @At("RETURN"), cancellable = true)
    public void injectInitGui(CallbackInfo callbackInfo){
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (guiScreen instanceof GuiChest) {
            if (this.guiTop < 50) { //prevent weird things
                buttonList.add(new GuiButton(1024576, 10, 25, 99, 20, "Disable KillAura"));
                buttonList.add(new GuiButton(727, 110, 25, 99, 20, "Disable Stealer"));
                buttonList.add(stealButton = new GuiButton(1234123, 10, this.guiTop - 55, 200, 10, "Steal this chest"));
            } else {
                buttonList.add(new GuiButton(1024576, this.width / 2 - 100, this.guiTop - 30, 99, 20, "Disable KillAura"));
                buttonList.add(new GuiButton(727, this.width / 2 + 1, this.guiTop - 30, 99, 20, "Disable Stealer"));
                buttonList.add(stealButton = new GuiButton(1234123, this.width / 2 - 100, this.guiTop - 55, 200, 20, "Steal this chest"));
            }
        }
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
        ChestStealer chestStealer = (ChestStealer) LiquidBounce.moduleManager.getModule(ChestStealer.class);
        try {
            Minecraft mc = Minecraft.getMinecraft();
            GuiScreen guiScreen = mc.currentScreen;

            stealButton.enabled = !chestStealer.getState();

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
            e.printStackTrace();
        }
    }
}