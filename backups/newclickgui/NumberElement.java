package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl;

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components.Slider;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.ValueElement;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;

public class NumberElement extends ValueElement<Double> {

    public NumberElement(NumberValue value) {
        super(value);
        this.savedValue = value;
    }

    private final NumberValue savedValue;
    private final Slider slider = new Slider();

    private boolean dragged = false;

    @Override
    public float drawElement(int mouseX, int mouseY, float x, float y, float width, int backgroundColor) {
        float idk = 30 + mc.fontRenderer.getStringWidth((int) savedValue.getMax() + savedValue.getInc() * (savedValue.getInc() <= 0.1F ? 99F : 1F) + "");
        float sliderWidth = width - 50 - mc.fontRenderer.getStringWidth(value.getName()) - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - mc.fontRenderer.getStringWidth(savedValue.getMin() + "") - idk;
        float startPoint = x + width - 20 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk;
        if (dragged)
            savedValue.set((Number) (savedValue.getMin() + (mouseX - startPoint) / sliderWidth * (savedValue.getMax() - savedValue.getMin())));
        mc.fontRenderer.drawString(value.getName(), x + 10, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        mc.fontRenderer.drawString(savedValue.getMax() + "", x + width - 10 - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        mc.fontRenderer.drawString(savedValue.getMin() + "", x + width - 30 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - mc.fontRenderer.getStringWidth(savedValue.getMin() + "") - idk, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        slider.setValue(savedValue.get(), savedValue.getMin(), savedValue.getMax());
        if (dragged && savedValue.getInc() >= 0.5F && sliderWidth / savedValue.getMax() >= 0.5F)
            for (double i = 0; i <= savedValue.getMax() - savedValue.getMin(); i += savedValue.getInc()) {
                double plusAmount = sliderWidth * (i / (savedValue.getMax() - savedValue.getMin()));
                Gui.drawRect(
                        x + width - 20 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk + plusAmount - 0.5F,
                        y + 5,
                        x + width - 20 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk + plusAmount + 0.5F,
                        y + 15, ColorManager.buttonOutline.getRGB());
            }
        slider.onDraw(x + width - 20 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk, y + 10, sliderWidth);
        RenderUtils.originalRoundedRect(x + width - 5 - idk, y + 2, x + width - 10, y + 18, 4, ColorManager.button.getRGB());
        RenderUtils.customRounded(x + width - 18, y + 2, x + width - 10, y + 18, 0F, 4F, 4F, 0F, ColorManager.buttonOutline.getRGB());
        RenderUtils.customRounded(x + width - 5 - idk, y + 2, x + width + 3 - idk, y + 18, 4F, 0F, 0F, 4F, ColorManager.buttonOutline.getRGB());
        mc.fontRenderer.drawString(savedValue.get() + "", x + width + 6 - idk, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        mc.fontRenderer.drawString("-", x + width - 3 - idk, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        mc.fontRenderer.drawString("+", x + width - 17, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        return this.valueHeight;
    }

    @Override
    public void onClick(int mouseX, int mouseY, float x, float y, float width) {
        float sliderWidth = 150F;
        float idk = 30 + mc.fontRenderer.getStringWidth((int) savedValue.getMax() + savedValue.getInc() + "");
        float startPoint = x + width - 10 - 20 - sliderWidth - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk;
        float endPoint = x + width - 10 - mc.fontRenderer.getStringWidth(savedValue.getMax() + "") - idk;
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, startPoint, y + 5, endPoint, y + 15))
            dragged = true;
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 5 - idk, y + 2, x + width + 3 - idk, y + 18))
            savedValue.set((Number) (savedValue.get() - savedValue.getInc()));
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 18, y + 2, x + width - 10, y + 18))
            savedValue.set((Number) (savedValue.get() + savedValue.getInc()));
    }

    @Override
    public void onRelease(int mouseX, int mouseY, float x, float y, float width) {
        if (dragged)
            dragged = false;
    }
}
