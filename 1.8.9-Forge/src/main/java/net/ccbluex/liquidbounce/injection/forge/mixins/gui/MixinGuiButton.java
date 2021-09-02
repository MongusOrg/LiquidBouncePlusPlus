/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import org.lwjgl.opengl.GL11;


@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

   @Shadow
   public boolean visible;

   @Shadow
   public int xPosition;

   @Shadow
   public int yPosition;

   @Shadow
   public int width;

   @Shadow
   public int height;

   @Shadow
   protected boolean hovered;

   @Shadow
   public boolean enabled;

   @Shadow
   protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

   @Shadow
   public String displayString;

   @Shadow
   @Final
   protected static ResourceLocation buttonTextures;
   private float bright;

   private float moveX = 0F;

   /**
    * @author CCBlueX
    */
   @Overwrite
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (visible) {
         final FontRenderer fontRenderer =
            mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.font40;
         hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

         final int delta = RenderUtils.deltaTime;

         if (enabled && hovered) {
            bright += 0.3F * delta;

            if (bright >= 80) bright = 80;

            moveX = AnimationUtils.animate(5F, moveX, 0.2F * (1.25F - mc.timer.renderPartialTicks));
         } else {
            bright -= 0.3F * delta;

            if (bright <= 0) bright = 0;
            moveX = AnimationUtils.animate(0F, moveX, 0.2F * (1.25F - mc.timer.renderPartialTicks));
         }

         /*GL11.glPushMatrix();
         RenderUtils.drawGradientSideways(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, new Color((int)bright, (int)bright, (int)bright, 160).getRGB(), new Color(0, 0, 0, 160).getRGB());
         GL11.glPopMatrix();*/
         RenderUtils.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 2.4F, /*new Color((int)bright, (int)bright, (int)bright, 150).getRGB()*/ new Color(0, 0, 0, 180).getRGB());
         //RenderUtils.customRounded(this.xPosition, this.yPosition + this.height - 2F, this.xPosition + this.width, this.yPosition + this.height, 0F, 0F, 2F, 2F, (this.enabled ? new Color(0, 111, 255) : new Color(71, 71, 71)).getRGB());
         /*for (float i = 5; i >= 0; i -= 0.5F)
            RenderUtils.customRounded(
               this.xPosition - i, 
               this.yPosition + this.height - 2F - i, 
               this.xPosition + this.width + i, 
               this.yPosition + this.height + i, 
               0F, 0F, 2F, 2F, 
               (this.enabled ? new Color(0F, 111F / 255F, 1F, ((5F - i) / 5F) * (40F / 255F)) 
                              : new Color(71F / 255F, 71F / 255F, 71F / 255F, ((5F - i) / 5F) * (40F / 255F))).getRGB()
            );*/

         RenderUtils.customRounded(this.xPosition, this.yPosition, this.xPosition + 2.4F, this.yPosition + this.height, 2.4F, 0F, 0F, 2.4F, (this.enabled ? new Color(0, 111, 255) : new Color(71, 71, 71)).getRGB());
         for (float i = 5; i >= 0; i -= 1F)
            RenderUtils.customRounded(
               this.xPosition - i, 
               this.yPosition - i, 
               this.xPosition + 2.4F + i, 
               this.yPosition + this.height + i, 
               2.4F, 0F, 0F, 2.4F, 
               (this.enabled ? new Color(0F, 111F / 255F, 1F, ((5F - i) / 5F) * (50F / 255F)) 
                              : new Color(71F / 255F, 71F / 255F, 71F / 255F, ((5F - i) / 5F) * (50F / 255F))).getRGB()
            );

         mc.getTextureManager().bindTexture(buttonTextures);
         mouseDragged(mc, mouseX, mouseY);

         AWTFontRenderer.Companion.setAssumeNonVolatile(true);

         /*fontRenderer.drawStringWithShadow(displayString,
                 (float) ((this.xPosition + this.width / 2) -
                         fontRenderer.getStringWidth(displayString) / 2),
                 this.yPosition + (this.height - 5) / 2F - 2, 14737632);*/

         fontRenderer.drawStringWithShadow(displayString,
                 this.xPosition + 5F + moveX,
                 this.yPosition + (this.height - 5) / 2F - 2, 14737632);

         AWTFontRenderer.Companion.setAssumeNonVolatile(false);

         GlStateManager.resetColor();
      }
   }
}