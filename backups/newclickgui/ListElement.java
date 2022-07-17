package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl;

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.ValueElement;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.Stencil;
import net.ccbluex.liquidbounce.value.impl.ListValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ListElement extends ValueElement<String> {

    private float expandHeight = 0F;
    private boolean expansion = false;

    private float maxSubWidth = 0F;

    private final ListValue saveValue;

    private static final ResourceLocation expanding = new ResourceLocation("twilight/expand.png");

    public ListElement(ListValue value) {
        super(value);
        this.saveValue = value;
        this.maxSubWidth = -value.getValues().stream().map(s -> -mc.fontRenderer.getStringWidth(s)).sorted().findFirst().orElse(0) + 20F;
    }

    @Override
    public float drawElement(int mouseX, int mouseY, float x, float y, float width, int backgroundColor) {
        expandHeight = AnimationUtils.animate(expansion ? 16F * (saveValue.getValues().size() - 1) : 0F, expandHeight, 0.25F * RenderUtils.deltaTime * 0.0075F);
        float percent = expandHeight / (16F * (saveValue.getValues().size() - 1));
        mc.fontRenderer.drawString(value.getName(), x + 10, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        RenderUtils.originalRoundedRect(x + width - 18 - maxSubWidth, y + 2, x + width - 10, y + 18 + expandHeight, 4F, ColorManager.button.getRGB());
        GlStateManager.resetColor();
        GL11.glPushMatrix();
        GL11.glTranslated(x + width - 20, y + 10F, 0F);
        GL11.glPushMatrix();
        GL11.glRotatef(180F * percent, 0F, 0F, 1F);
        GL11.glColor4f(1, 1, 1, 1);
        RenderUtils.drawImg(expanding, -4F, -4F, 8F, 8F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        mc.fontRenderer.drawString(value.get(), x + width - 14 - maxSubWidth, y + 7, -1);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + width - 14 - maxSubWidth, y + 7, 0F);
        GlStateManager.scale(percent, percent, percent);
        float vH = 0F;
        if (percent > 0F) for (String subV : getUnusedValues()) {
            mc.fontRenderer.drawString(subV, 0F, (16 + vH) * percent, new Color(0.5F, 0.5F, 0.5F, MathHelper.clamp_float(percent, 0F, 1F)).getRGB());
            vH += 16;
        }
        GlStateManager.popMatrix();
        this.valueHeight = 20F + expandHeight;
        return this.valueHeight;
    }

    @Override
    public void onClick(int mouseX, int mouseY, float x, float y, float width) {
        if (this.isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y + 2F, x + width, y + 18F))
            expansion = !expansion;
        if (expansion) {
            float vH = 0F;
            for (String subV : getUnusedValues()) {
                if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 14 - maxSubWidth, y + 18 + vH, x + width - 10, y + 18 + 16 + vH)) {
                    value.set(subV);
                    expansion = false;
                    break;
                }
                vH += 16;
            }
        }
    }

    private List<String> getUnusedValues() {
        return saveValue.getValues().stream().filter(s -> s != value.get()).collect(Collectors.toList());
    }
}
