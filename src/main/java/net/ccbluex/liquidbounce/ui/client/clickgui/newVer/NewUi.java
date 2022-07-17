package net.ccbluex.liquidbounce.ui.client.clickgui.newVer;

import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.CategoryElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.SearchElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.module.ModuleElement;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MouseUtils;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.Stencil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class NewUi extends GuiScreen {

    private static NewUi instance;
    public static final NewUi getInstance() {
        return instance == null ? instance = new NewUi() : instance;
    }

    private NewUi() {
        for (ModuleCategory c : ModuleCategory.values())
            categoryElements.add(new CategoryElement(c));
        categoryElements.get(0).setFocused(true);
    }

    public final List<CategoryElement> categoryElements = new ArrayList<>();

    private float startYAnim = height / 2F;
    private float endYAnim = height / 2F;

    private SearchElement searchElement;

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        for (CategoryElement ce : categoryElements) {
            for (ModuleElement me : ce.getModuleElements()) {
                if (me.listeningKeybind())
                    me.resetState();
            }
        }
        searchElement = new SearchElement(40F, 115F, 180F, 20F);
        super.initGui();
    }

    public void onGuiClosed() {
        for (CategoryElement ce : categoryElements) {
            if (ce.getFocused())
                ce.handleMouseRelease(-1, -1, 0, 0, 0, 0, 0);
        }
        Keyboard.enableRepeatEvents(false);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // will draw reduced ver once it gets under 1140x780.
        drawFullSized(mouseX, mouseY, partialTicks);
    }

    private void drawFullSized(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.originalRoundedRect(30F, 30F, this.width - 30F, this.height - 30F, 8F, 0xFF101010);
        Stencil.write(true);
        RenderUtils.drawFilledCircle(65F, 80F, 25F, new Color(45, 45, 45));
        Stencil.erase(true);
        if (mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
            final ResourceLocation skin = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getLocationSkin();
            glPushMatrix();
            glTranslatef(40F, 55F, 0F);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glDepthMask(false);
            OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
            glColor4f(1f, 1f, 1f, 1f);
            mc.getTextureManager().bindTexture(skin);
            Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, 50, 50,
                    64F, 64F);
            glDepthMask(true);
            glDisable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            glPopMatrix();
        }
        Stencil.dispose();

        if (Fonts.font72.getStringWidth(mc.thePlayer.getGameProfile().getName()) > 70)
            Fonts.font72.drawString(Fonts.font72.trimStringToWidth(mc.thePlayer.getGameProfile().getName(), 70) + "...", 100, 78 - Fonts.font40.FONT_HEIGHT, -1);
        else
            Fonts.font72.drawString(mc.thePlayer.getGameProfile().getName(), 100, 78 - Fonts.font40.FONT_HEIGHT, -1);
        Fonts.font40.drawString("hi", 100, 85, -1);

        if (searchElement.drawBox(mouseX, mouseY, partialTicks)) {
            searchElement.drawPanel(mouseX, mouseY, 230, 50, width - 260, height - 80, Mouse.getDWheel(), categoryElements);
            return;
        }

        final float elementHeight = 24;
        float startY = 140F;
        for (CategoryElement ce : categoryElements) {
            ce.drawLabel(mouseX, mouseY, 30F, startY, 200F, elementHeight);
            if (ce.getFocused()) {
                startYAnim = AnimationUtils.animate(startY + 6F, startYAnim, (startYAnim - (startY + 5F) > 0 ? 0.65F : 0.55F) * RenderUtils.deltaTime * 0.0075F);
                endYAnim = AnimationUtils.animate(startY + elementHeight - 6F, endYAnim, (endYAnim - (startY + elementHeight - 5F) < 0 ? 0.65F : 0.55F) * RenderUtils.deltaTime * 0.0075F);

                ce.drawPanel(mouseX, mouseY, 230, 50, width - 260, height - 80, Mouse.getDWheel());
            }
            startY += elementHeight;
        }
        RenderUtils.originalRoundedRect(32F, startYAnim, 34F, endYAnim, 1F, new Color(0, 140, 255).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final float elementHeight = 24;
        float startY = 140F;
        searchElement.handleMouseClick(mouseX, mouseY, mouseButton, 230, 50, width - 260, height - 80, categoryElements);
        if (!searchElement.isTyping()) for (CategoryElement ce : categoryElements) {
            if (ce.getFocused())
                ce.handleMouseClick(mouseX, mouseY, mouseButton, 230, 50, width - 260, height - 80);
            if (MouseUtils.mouseWithinBounds(mouseX, mouseY, 30F, startY, 230F, startY + elementHeight) && !searchElement.isTyping()) {
                categoryElements.forEach(e -> e.setFocused(false));
                ce.setFocused(true);
                return;
            }
            startY += elementHeight;
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (CategoryElement ce : categoryElements) {
            if (ce.getFocused()) {
                if (ce.handleKeyTyped(typedChar, keyCode))
                    return;
            }
        }
        if (searchElement.handleTyping(typedChar, keyCode, 230, 50, width - 260, height - 80, categoryElements))
            return;
        super.keyTyped(typedChar, keyCode);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        searchElement.handleMouseRelease(mouseX, mouseY, state, 230, 50, width - 260, height - 80, categoryElements);
        if (!searchElement.isTyping())
            for (CategoryElement ce : categoryElements) {
                if (ce.getFocused())
                ce.handleMouseRelease(mouseX, mouseY, state, 230, 50, width - 260, height - 80);
            }
        super.mouseReleased(mouseX, mouseY, state);
    }

}
