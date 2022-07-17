package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module;

import net.ccbluex.liquidbounce.module.Module;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components.ToggleSwitch;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.ValueElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl.BooleanElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl.ListElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.value.impl.NumberElement;
import net.ccbluex.liquidbounce.utils.ColorUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.Stencil;
import net.ccbluex.liquidbounce.value.Value;
import net.ccbluex.liquidbounce.value.BooleanValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleElement extends MinecraftInstance {

    protected static final ResourceLocation EXPAND_ICON = new ResourceLocation("twilight/expand.png");

    private final Module module;
    private final ToggleSwitch toggleSwitch = new ToggleSwitch();

    private List<ValueElement> valueElements = new ArrayList<>();

    private float animHeight = 0F, fadeKeybind = 0F, animPercent = 0F;

    private boolean listeningToKey = false, expanded = false;

    public ModuleElement(Module module) {
        this.module = module;
        for (Value v : module.getSettings()) {
            if (v instanceof BooleanValue)
                valueElements.add(new BooleanElement((BooleanValue) v));
            if (v instanceof ListValue)
                valueElements.add(new ListElement((ListValue) v));
            if (v instanceof NumberValue)
                valueElements.add(new NumberElement((NumberValue) v));
        }
    }

    public final Module getModule() {
        return this.module;
    }

    public float drawElement(int mouseX, int mouseY, float x, float y, float width, float height) {
        float expectedHeight = 0F;
        animPercent = AnimationUtils.animate(expanded ? 100F : 0F, animPercent, 0.25F * RenderUtils.deltaTime * 0.0075F);
        for (ValueElement ve : valueElements) {
            if (ve.isDisplayable())
                expectedHeight += ve.getValueHeight();
        }
        animHeight = animPercent / 100F * (expectedHeight + 10F);

        RenderUtils.originalRoundedRect(x + 9.5F, y + 4.5F, x + width - 9.5F, y + height + animHeight - 4.5F, 4F, ColorManager.buttonOutline.getRGB());
        Stencil.write(true);
        RenderUtils.originalRoundedRect(x + 10, y + 5, x + width - 10, y + height + animHeight - 5, 4F, ColorManager.moduleBackground.getRGB());
        Stencil.erase(true);
        Gui.drawRect(x + 10, y + height - 5, x + width - 10, y + height - 4.5, 0xFF303030);
        mc.fontRenderer.drawString(module.getName(), x + 20, y + height / 2F - mc.fontRenderer.FONT_HEIGHT, -1);
        mc.fontRendererSmall.drawString(module.getDescription(), x + 20, y + height / 2F + 4F, 0xA0A0A0);

        final String keyName = listeningToKey ? "Listening" : Keyboard.getKeyName(module.getKeyCode());

        if (MouseUtils.mouseWithinBounds(mouseX, mouseY,
                x + 25 + mc.fontRenderer.getStringWidth(module.getName()),
                y + height / 2F - mc.fontRenderer.FONT_HEIGHT - 2F,
                x + 35 + mc.fontRenderer.getStringWidth(module.getName()) + mc.fontRendererSmall.getStringWidth(keyName),
                y + height / 2F))
            fadeKeybind += 0.1F;
        else
            fadeKeybind -= 0.1F;
        fadeKeybind = MathHelper.clamp_float(fadeKeybind, 0F, 1F);

        RenderUtils.originalRoundedRect(
                x + 25 + mc.fontRenderer.getStringWidth(module.getName()),
                y + height / 2F - mc.fontRenderer.FONT_HEIGHT - 2F,
                x + 35 + mc.fontRenderer.getStringWidth(module.getName()) + mc.fontRendererSmall.getStringWidth(keyName),
                y + height / 2F, 2F, ColorUtils.blend(new Color(0xFF454545), new Color(0xFF353535), fadeKeybind).getRGB());
        mc.fontRendererSmall.drawString(keyName, x + 30 + mc.fontRenderer.getStringWidth(module.getName()), y + height / 2F - mc.fontRenderer.FONT_HEIGHT + 1.5F, -1);

        if (module.isEnabled())
            toggleSwitch.enable();
        else
            toggleSwitch.disable();

        if (module.getSettings().size() > 0) {
            Gui.drawRect(x + width - 40, y + 5, x + width - 39.5, y + height - 5, 0xFF303030);
            GlStateManager.resetColor();
            GL11.glPushMatrix();
            GL11.glTranslated(x + width - 25, y + height / 2F, 0F);
            GL11.glPushMatrix();
            GL11.glRotatef(180F * (animHeight / (expectedHeight + 10)), 0F, 0F, 1F);
            GL11.glColor4f(1, 1, 1, 1);
            RenderUtils.drawImg(EXPAND_ICON, -4F, -4F, 8F, 8F);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            toggleSwitch.onDraw(x + width - 70, y + height / 2F - 5F, 20F, 10F, 0xFF252525);
        } else
            toggleSwitch.onDraw(x + width - 40, y + height / 2F - 5F, 20F, 10F, 0xFF252525);

        if (expanded || animHeight > 0F) {
            float startYPos = y + height;
            for (ValueElement ve : valueElements)
                if (ve.isDisplayable())
                    startYPos += ve.drawElement(mouseX, mouseY, x + 10, startYPos, width - 20, 0xFF252525);
        }
        Stencil.dispose();

        return height + animHeight;
    }

    public void handleClick(int mouseX, int mouseY, float x, float y, float width, float height) {
        if (listeningToKey) {
            resetState();
            return;
        }
        final String keyName = listeningToKey ? "Listening" : Keyboard.getKeyName(module.getKeyCode());
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY,
                x + 25 + mc.fontRenderer.getStringWidth(module.getName()),
                y + height / 2F - mc.fontRenderer.FONT_HEIGHT - 2F,
                x + 35 + mc.fontRenderer.getStringWidth(module.getName()) + mc.fontRendererSmall.getStringWidth(keyName),
                y + height / 2F)) {
            listeningToKey = true;
            return;
        }
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - (module.getSettings().size() > 0 ? 70 : 40), y, x + width - (module.getSettings().size() > 0 ? 50 : 20), y + height))
            module.toggle();
        if (module.getSettings().size() > 0 && MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 40, y, x + width - 10, y + height))
            expanded = !expanded;
        if (expanded) {
            float startY = y + height;
            for (ValueElement ve : valueElements) {
                if (!ve.isDisplayable()) continue;
                ve.onClick(mouseX, mouseY, x + 10, startY, width - 20);
                startY += ve.getValueHeight();
            }
        }
    }

    public void handleRelease(int mouseX, int mouseY, float x, float y, float width, float height) {
        if (expanded) {
            float startY = y + height;
            for (ValueElement ve : valueElements) {
                if (!ve.isDisplayable()) continue;
                ve.onRelease(mouseX, mouseY, x + 10, startY, width - 20);
                startY += ve.getValueHeight();
            }
        }
    }

    public boolean handleKeyTyped(char typed, int code) {
        if (listeningToKey) {
            if (code == 1) {
                module.setKey(0);
                listeningToKey = false;
            } else {
                module.setKey(code);
                listeningToKey = false;
            }
            return true;
        }
        if (expanded)
            for (ValueElement ve : valueElements)
                if (ve.isDisplayable() && ve.onKeyPress(typed, code)) return true;
        return false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public float getAnimHeight() {
        return animHeight;
    }

    public boolean listeningKeybind() {
        return listeningToKey;
    }

    public void resetState() {
        listeningToKey = false;
    }
}
