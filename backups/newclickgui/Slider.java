package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components;

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import inf.twilight.utils.ColorUtils;
import inf.twilight.utils.render.AnimationUtils;
import inf.twilight.utils.render.RenderUtils;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class Slider {

    private float smooth = 0F;
    private float value = 0F;
    private Color accentColor;

    public Slider() {
        this.accentColor = new Color(0, 140, 255);
    }

    public Slider(final Color accentColor) {
        this.accentColor = accentColor;
    }

    public void onDraw(float x, float y, float width) {
        smooth = AnimationUtils.animate(value, smooth, 0.5F);
        RenderUtils.originalRoundedRect(x - 1, y - 1, x + width + 1, y + 1, 1, ColorManager.unusedSlider.getRGB());
        RenderUtils.originalRoundedRect(x - 1, y - 1, x + width * (smooth / 100F) + 1, y + 1, 1, this.accentColor.getRGB());
        RenderUtils.drawFilledCircle(x + width * (smooth / 100F), y, 5F, Color.white);
        RenderUtils.drawFilledCircle(x + width * (smooth / 100F), y, 3F, ColorManager.background);
    }

    public void setValue(Number desired, Number minimum, Number maximum) {
        value = (desired.floatValue() - minimum.floatValue()) / (maximum.floatValue() - minimum.floatValue()) * 100F;
    }

}
