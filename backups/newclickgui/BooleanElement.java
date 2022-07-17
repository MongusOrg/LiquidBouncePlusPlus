package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl;

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components.Checkbox;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.ValueElement;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.value.Value;
import net.ccbluex.liquidbounce.value.impl.BooleanValue;

public class BooleanElement extends ValueElement<Boolean> {
    public BooleanElement(BooleanValue value) {
        super(value);
    }

    private Checkbox checkbox = new Checkbox();

    @Override
    public float drawElement(int mouseX, int mouseY, float x, float y, float width, int backgroundColor) {
        if (value.get())
            checkbox.enable();
        else
            checkbox.disable();

        checkbox.onDraw(x + 10, y + 5, 10, 10, backgroundColor);
        mc.fontRenderer.drawString(value.getName(), x + 25, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
        return this.valueHeight;
    }

    @Override
    public void onClick(int mouseX, int mouseY, float x, float y, float width) {
        if (this.isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + 20F))
            value.set(!value.get());
    }
}
