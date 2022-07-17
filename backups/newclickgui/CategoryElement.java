package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element;

import net.ccbluex.liquidbounce.Twilight;
import net.ccbluex.liquidbounce.module.Category;
import net.ccbluex.liquidbounce.module.Module;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.ModuleElement;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.ccbluex.liquidbounce.utils.render.Stencil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CategoryElement extends MinecraftInstance {

    private final String name;
    private final ModuleCategory category;
    private boolean focused;

    private float scrollHeight = 0F, animScrollHeight = 0F, lastHeight = 0F;

    public final List<ModuleElement> moduleElements = new ArrayList<>();

    public CategoryElement(ModuleCategory category, String name) {
        this.category = category;
        this.name = name;

        for (Module module : Twilight.instance.moduleManager.getModules()) {
            if (module.getCategory() == category)
                moduleElements.add(new ModuleElement(module));
        }
    }

    public final String getName() {
        return this.name;
    }

    public void setFocused() {
        this.focused = true;
    }

    public void setUnfocused() {
        this.focused = false;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public final Category getCategory() {
        return this.category;
    }

    public void drawLabel(int mouseX, int mouseY, float x, float y, float width, float height) {
        if (focused)
            RenderUtils.originalRoundedRect(x + 3, y + 3, x + width - 3, y + height - 3, 3, ColorManager.dropDown.getRGB());
        else if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + height))
            RenderUtils.originalRoundedRect(x + 3, y + 3, x + width - 3, y + height - 3, 3, ColorManager.border.getRGB());
        mc.fontRenderer.drawString(name, x + 10, y + height / 2F - mc.fontRenderer.FONT_HEIGHT / 2F, -1);
    }

    public void drawPanel(int mouseX, int mouseY, float x, float y, float width, float height, int wheel) {
        lastHeight = 0F;
        for (ModuleElement moduleElement : moduleElements)
            lastHeight += 40 + moduleElement.getAnimHeight();
        if (lastHeight >= 10F) lastHeight -= 10F;
        handleScrolling(wheel, height);
        drawScroll(x, y + 50, width, height);
        mc.fontRendererTitle.drawString(ChatFormatting.GRAY + "Modules > " + ChatFormatting.RESET + name, x + 10, y + 10, -1);
        if (mouseY < y + 50 || mouseY >= y + height) {
            mouseX = -1;
            mouseY = -1;
        }
        RenderUtils.prepareScissorBox(x, y + 50, x + width, y + height);
        GL11.glEnable(3089);
        float startY = y + 50;
        for (ModuleElement moduleElement : moduleElements) {
            if (startY + animScrollHeight > y + height || startY + animScrollHeight + 40 + moduleElement.getAnimHeight() < y + 50)
                startY += 40 + moduleElement.getAnimHeight();
            else
                startY += moduleElement.drawElement(mouseX, mouseY, x, startY + animScrollHeight, width, 40);
        }
        GL11.glDisable(3089);
    }

    private void handleScrolling(int wheel, float height) {
        if (wheel != 0) {
            if (wheel > 0)
                scrollHeight += 50F;
            else
                scrollHeight -= 50F;
        }
        if (lastHeight > height - 60)
            scrollHeight = MathHelper.clamp_float(scrollHeight, -lastHeight + height - 60, 0F);
        else
            scrollHeight = 0F;
        animScrollHeight = AnimationUtils.animate(scrollHeight, animScrollHeight, 0.25F * RenderUtils.deltaTime * 0.0075F);
    }

    private void drawScroll(float x, float y, float width, float height) {
        if (lastHeight > height - 60) {
            float last = (height - 60F) - (height - 60F) * ((height - 60) / lastHeight);
            float multiply = last * MathHelper.clamp_float(Math.abs(animScrollHeight / (-lastHeight + height - 60)), 0F, 1F);
            RenderUtils.originalRoundedRect(x + width - 6F, y + 5 + multiply, x + width - 4F, y + 5 + (height - 60F) * ((height - 60) / lastHeight) + multiply, 1F, 0x50FFFFFF);
        }
    }

    public void handleMouseClick(int mouseX, int mouseY, int mouseButton, float x, float y, float width, float height) {
        if (mouseY < y + 50 || mouseY >= y + height) {
            mouseX = -1;
            mouseY = -1;
        }
        float startY = y + 50;
        if (mouseButton == 0)
            for (ModuleElement moduleElement : moduleElements) {
                moduleElement.handleClick(mouseX, mouseY, x, startY + animScrollHeight, width, 40);
                startY += 40 + moduleElement.getAnimHeight();
            }
    }

    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton, float x, float y, float width, float height) {
        if (mouseY < y + 50 || mouseY >= y + height) {
            mouseX = -1;
            mouseY = -1;
        }
        float startY = y + 50;
        if (mouseButton == 0)
            for (ModuleElement moduleElement : moduleElements) {
                moduleElement.handleRelease(mouseX, mouseY, x, startY + animScrollHeight, width, 40);
                startY += 40 + moduleElement.getAnimHeight();
            }
    }

    public boolean handleKeyTyped(char keyTyped, int keyCode) {
        for (ModuleElement moduleElement : moduleElements) {
            if (moduleElement.handleKeyTyped(keyTyped, keyCode))
                return true;
        }
        return false;
    }
}
