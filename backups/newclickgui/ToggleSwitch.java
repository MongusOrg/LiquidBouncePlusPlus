package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components;

import inf.twilight.utils.ColorUtils;
import inf.twilight.utils.render.RenderUtils;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ToggleSwitch {

    private float smooth = 0F;
    private boolean state = false;
    private Color accentColor;

    public ToggleSwitch() {
        this.accentColor = new Color(0, 140, 255);
    }

    public ToggleSwitch(final Color accentColor) {
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
        final Color borderColor = ColorUtils.blendColors(new float[]{0F, 1F}, new Color[] {new Color(160, 160, 160), accentColor}, smooth);
        final Color mainColor = ColorUtils.blendColors(new float[]{0F, 1F}, new Color[] {new Color(backgroundColor), accentColor}, smooth);
        final Color switchColor = ColorUtils.blendColors(new float[]{0F, 1F}, new Color[] {new Color(160, 160, 160), new Color(backgroundColor)}, smooth);

        RenderUtils.originalRoundedRect(x - 0.5F, y - 0.5F, x + width + 0.5F, y + height + 0.5F, (height + 1F) / 2F, borderColor.getRGB());
        RenderUtils.originalRoundedRect(x, y, x + width, y + height, height / 2F, mainColor.getRGB());
        RenderUtils.drawFilledCircle(x + (1F - smooth) * (2F + (height - 4F) / 2F) + smooth * (width - 2F - (height - 4F) / 2F), y + 2F + (height - 4F) / 2F, (height - 4F) / 2F, switchColor);
    }

}
