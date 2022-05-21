/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * liulihaocai, ProxyMod.
 */
package net.ccbluex.liquidbounce.features.special.proxy;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiProxies extends GuiScreen {

    private final GuiScreen parent;

    private GuiTextField txtProxyAddress;
    private GuiButton btnProxyType;
    private GuiButton btnProxyEnabled;

    public GuiProxies(final GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.txtProxyAddress = new GuiTextField(3, this.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.txtProxyAddress.setMaxStringLength(128);
        this.txtProxyAddress.setFocused(true);
        this.txtProxyAddress.setText(LiquidBounce.proxyManager.getProxyAddress());
        this.btnProxyType = new GuiButton(1, width / 2 - 100, height / 4 + 96, "");
        this.btnProxyEnabled = new GuiButton(2, width / 2 - 100, height / 4 + 120, "");
        updateButtons();
        this.buttonList.add(this.btnProxyType);
        this.buttonList.add(this.btnProxyEnabled);
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 144, "Done"));
    }

    private void updateButtons() {
        this.btnProxyType.displayString = "Type: " + LiquidBounce.proxyManager.getProxyType().name();
        this.btnProxyEnabled.displayString = LiquidBounce.proxyManager.isProxyEnabled() ? "§aEnabled" : "§cDisabled";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(mc.fontRendererObj, "Proxy Manager", width / 2, 34, 0xffffff);
        this.txtProxyAddress.drawTextBox();
        if (this.txtProxyAddress.getText().isEmpty() && !this.txtProxyAddress.isFocused()) {
            this.drawString(mc.fontRendererObj, "Enter proxy address here...", width / 2 - 100, 60, 0xffffff);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(this.parent);
                return;
            case 1:
                LiquidBounce.proxyManager.setProxyType(ProxyManager.ProxyType.values()[(LiquidBounce.proxyManager.getProxyType().ordinal() + 1) % ProxyManager.ProxyType.values().length]);
                break;
            case 2:
                LiquidBounce.proxyManager.setProxyEnabled(!LiquidBounce.proxyManager.isProxyEnabled());
                break;
        }
        updateButtons();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        LiquidBounce.proxyManager.setProxyAddress(this.txtProxyAddress.getText());
        LiquidBounce.proxyManager.saveConfig();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parent);
            return;
        }

        this.txtProxyAddress.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.txtProxyAddress.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.txtProxyAddress.updateCursorCounter();
        super.updateScreen();
    }
}