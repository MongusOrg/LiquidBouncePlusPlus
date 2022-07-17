package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components;

import net.ccbluex.liquidbounce.utils.render.BlendUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Checkbox {

    private float smooth = 0F;
    private boolean state = false;
    private Color accentColor;

    public Checkbox() {
        this.accentColor = new Color(0, 140, 255);
    }

    public Checkbox(final Color accentColor) {
        this.accentColor = accentColor;
    }

    public void enable() {
        this.state = true;
    }

    public void disable() {
        this.state = false;
    }

    public void onDraw(float x, float y, float width, float height, int backgroundColor) {
        smooth += state ? 0.2F : -0.2F;
        smooth = MathHelper.clamp_float(smooth, 0F, 1F);
        final Color bgColor = new Color(backgroundColor);
        final Color borderColor = ColorUtils.blendColors(new float[]{0F, 1F}, new Color[] {new Color(160, 160, 160), accentColor}, smooth);
        final Color mainColor = ColorUtils.blendColors(new float[]{0F, 1F}, new Color[] {bgColor, accentColor}, smooth);

        RenderUtils.originalRoundedRect(x - 0.5F, y - 0.5F, x + width + 0.5F, y + width + 0.5F, 3F, borderColor.getRGB());
        RenderUtils.originalRoundedRect(x, y, x + width, y + width, 3F, mainColor.getRGB());
        GL11.glColor4f(bgColor.getRed() / 255F, bgColor.getGreen() / 255F, bgColor.getBlue() / 255F, 1F);
        RenderUtils.drawLine(x + width / 4F, y + width / 2F, x + width / 2.15F, y + width / 4F * 3F, 2F);
        RenderUtils.drawLine(x + width / 2.15F, y + width / 4F * 3F, x + width / 3.95F * 3F, y + width / 3F, 2F);
        GlStateManager.resetColor();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

}
