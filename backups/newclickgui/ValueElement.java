package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.value.Value;

public abstract class ValueElement<T> extends MinecraftInstance {

    protected final Value<T> value;
    protected float valueHeight = 20F;

    protected ValueElement(Value value) {
        this.value = value;
    }

    public abstract float drawElement(int mouseX, int mouseY, float x, float y, float width, int backgroundColor);
    public abstract void onClick(int mouseX, int mouseY, float x, float y, float width);
    public void onRelease(int mouseX, int mouseY, float x, float y, float width) {}
    public boolean onKeyPress(char typed, int keyCode) {
        return false;
    }

    public float getValueHeight() {
        return this.valueHeight;
    }

    public boolean isDisplayable() {
        return (boolean) this.value.getCanDisplay().invoke();
    }

}
