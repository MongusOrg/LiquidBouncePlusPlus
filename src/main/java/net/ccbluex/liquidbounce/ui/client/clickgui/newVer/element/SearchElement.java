package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element;

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.ModuleElement;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.Stencil;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class SearchElement extends MinecraftInstance {

    private Color accentColor;

    private final float xPos, yPos, width, height;

    private float scrollHeight = 0F, animScrollHeight = 0F, lastHeight = 0F;

    private final SearchBox searchBox;

    public SearchElement(float x, float y, float width, float height) {
        this(new Color(0, 140, 255), x, y, width, height);
    }

    public SearchElement(final Color accentColor, float x, float y, float width, float height) {
        this.xPos = x;
        this.yPos = y;
        this.width = width;
        this.height = height;
        this.accentColor = accentColor;
        this.searchBox = new SearchBox(0, (int) xPos + 2, (int) yPos + 2, (int) width - 4, (int) height - 2);
    }

    public boolean drawBox(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.originalRoundedRect(xPos - 0.5F, yPos - 0.5F, xPos + width + 0.5F, yPos + height + 0.5F, 4F, ColorManager.buttonOutline.getRGB());
        Stencil.write(true);
        RenderUtils.originalRoundedRect(xPos, yPos, xPos + width, yPos + height, 4F, ColorManager.textBox.getRGB());
        Stencil.erase(true);
        if (searchBox.isFocused()) {
            Gui.drawRect(xPos, yPos + height - 1F, xPos + width, yPos + height, accentColor.getRGB());
            searchBox.drawTextBox();
        } else if (searchBox.getText().length() <= 0) {
            searchBox.setText("Search");
            searchBox.drawTextBox();
            searchBox.setText("");
        } else
            searchBox.drawTextBox();

        Stencil.dispose();
        return searchBox.getText().length() > 0;
    }

    public void drawPanel(int mouseX, int mouseY, float x, float y, float w, float h, int wheel, List<CategoryElement> ces) {
        lastHeight = 0F;
        for (CategoryElement ce : ces) {
            for (ModuleElement me : ce.moduleElements) {
                if (me.getModule().getName().toLowerCase().startsWith(searchBox.getText().toLowerCase()))
                    lastHeight += me.getAnimHeight() + 40;
            }
        }
        if (lastHeight >= 10F) lastHeight -= 10F;
        handleScrolling(wheel, h);
        drawScroll(x, y + 50, w, h);
        Fonts.font72.drawString("Search", x + 10, y + 10, -1);
        float startY = y + 50;
        if (mouseY < y + 50 || mouseY >= y + h)
            mouseY = -1;
        RenderUtils.makeScissorBox(x, y + 50, x + w, y + h);
        GL11.glEnable(3089);
        for (CategoryElement ce : ces) {
            for (ModuleElement me : ce.moduleElements) {
                if (me.getModule().getName().toLowerCase().startsWith(searchBox.getText().toLowerCase())) {
                    if (startY + animScrollHeight > y + h || startY + animScrollHeight + 40 + me.getAnimHeight() < y + 50)
                        startY += 40 + me.getAnimHeight();
                    else
                        startY += me.drawElement(mouseX, mouseY, x, startY + animScrollHeight, w, 40);
                }
            }
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

    public void handleMouseClick(int mouseX, int mouseY, int mouseButton, float x, float y, float w, float h, List<CategoryElement> ces) {
        searchBox.mouseClicked(mouseX, mouseY, mouseButton);
        if (searchBox.getText().length() <= 0) return;
        if (mouseY < y + 50 || mouseY >= y + h)
            mouseY = -1;
        float startY = y + 50;
        for (CategoryElement ce : ces) {
            for (ModuleElement me : ce.moduleElements) {
                if (me.getModule().getName().toLowerCase().startsWith(searchBox.getText().toLowerCase())) {
                    me.handleClick(mouseX, mouseY, x, startY + animScrollHeight, w, 40);
                    startY += 40 + me.getAnimHeight();
                }
            }
        }

    }

    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton, float x, float y, float w, float h, List<CategoryElement> ces) {
        if (searchBox.getText().length() <= 0) return;
        if (mouseY < y + 50 || mouseY >= y + h)
            mouseY = -1;
        float startY = y + 50;
        for (CategoryElement ce : ces) {
            for (ModuleElement me : ce.moduleElements) {
                if (me.getModule().getName().toLowerCase().startsWith(searchBox.getText().toLowerCase())) {
                    me.handleRelease(mouseX, mouseY, x, startY + animScrollHeight, w, 40);
                    startY += 40 + me.getAnimHeight();
                }
            }
        }
    }

    public boolean handleTyping(char typedChar, int keyCode, float x, float y, float w, float h, List<CategoryElement> ces) {
        searchBox.textboxKeyTyped(typedChar, keyCode);
        if (searchBox.getText().length() <= 0) return false;
        for (CategoryElement ce : ces) {
            for (ModuleElement me : ce.moduleElements) {
                if (me.getModule().getName().toLowerCase().startsWith(searchBox.getText().toLowerCase())) {
                    if (me.handleKeyTyped(typedChar, keyCode))
                        return true;
                }
            }
        }
        return false;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
    }

    public boolean isTyping() {
        return searchBox.getText().length() > 0;
    }

}
