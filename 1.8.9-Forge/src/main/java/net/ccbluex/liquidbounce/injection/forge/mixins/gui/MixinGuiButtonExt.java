/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;
import org.lwjgl.opengl.GL11;

@Mixin(GuiButtonExt.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButtonExt extends GuiButton {

   private float bright;
   private float moveX = 0F;

   public MixinGuiButtonExt(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
      super(p_i1020_1_, p_i1020_2_, p_i1020_3_, p_i1020_4_);
   }

   public MixinGuiButtonExt(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_,
                            int p_i46323_5_, String p_i46323_6_) {
      super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
   }

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
         final float speedDelta = 0.01F * delta;

         if (enabled && hovered) {
            /*bright += 0.3F * delta;

            if (bright >= 80) bright = 80;*/

            moveX = AnimationUtils.animate(this.width - 2.4F, moveX, speedDelta);
         } else {
            /*bright -= 0.3F * delta;

            if (bright <= 0) bright = 0;*/
            moveX = AnimationUtils.animate(0F, moveX, speedDelta);
         }

         float roundCorner = (float) Math.max(0F, 2.4F + moveX - (this.width - 2.4F));

         RenderUtils.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 2.4F, new Color((int)bright, (int)bright, (int)bright, 150).getRGB());
         RenderUtils.customRounded(this.xPosition, this.yPosition, this.xPosition + 2.4F + moveX, this.yPosition + this.height, 2.4F, roundCorner, roundCorner, 2.4F, (this.enabled ? new Color(0, 111, 255) : new Color(71, 71, 71)).getRGB());

         mc.getTextureManager().bindTexture(buttonTextures);
         mouseDragged(mc, mouseX, mouseY);

         fontRenderer.drawStringWithShadow(displayString,
                 (float) ((this.xPosition + this.width / 2) -
                         fontRenderer.getStringWidth(displayString) / 2),
                 this.yPosition + (this.height - 5) / 2F - 2, 14737632);

         GlStateManager.resetColor();
      }
   }
}
