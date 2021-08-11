/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.minecraft.client.gui.*
import net.minecraft.client.resources.I18n
import java.awt.Color

import org.lwjgl.opengl.GL11

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    var slide: Float = 0F    
    var progress: Double = 0.0

    override fun initGui() {
        val defaultHeight = this.height / 4 + 48

        this.buttonList.add(GuiButton(100, this.width / 2 - 55, defaultHeight + 48, 110, 20, "Alt Manager"))

        this.buttonList.add(GuiButton(1, this.width / 2 - 55, defaultHeight, 110, 20, I18n.format("menu.singleplayer")))
        this.buttonList.add(GuiButton(2, this.width / 2 - 55, defaultHeight + 24, 110, 20, I18n.format("menu.multiplayer")))
        this.buttonList.add(GuiButton(102, this.width / 2 - 55, defaultHeight + 72, 110, 20, "Background"))
        this.buttonList.add(GuiButton(0, this.width / 2 - 55, defaultHeight + 96, 110, 20, "Settings"))
        this.buttonList.add(GuiButton(4, this.width / 2 - 55, defaultHeight + 120, 110, 20, "Mods/Scripts"))

        super.initGui()
        slide = 0F
        progress = 0.0
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (progress < 1.0) progress += 0.05 * (1F - partialTicks).toDouble()
        else progress = 1.0

        drawBackground(0)
        //calc functions
        slide = EaseUtils.easeOutQuart(progress).toFloat()

        GL11.glPushMatrix()
        GL11.glScalef(1F, 1F + (1F - slide) * 2F, 1F)
        GL11.glTranslatef(0F, (1F - slide) * height.toFloat(), 0F)
        //Gui.drawRect(width / 2 - 70, height / 4 + 35, width / 2 + 70, height / 4 + 197, Color(14, 14, 14, 255).rgb)
        RenderUtils.drawRoundedRect(width / 2F - 70F, height / 4F + 35F, width / 2F + 70F, height / 4F + 197F, 6F, Integer.MIN_VALUE)

        Fonts.fontGothic70.drawCenteredString(LiquidBounce.CLIENT_NAME, this.width / 2F, height / 4F + 5F, -1, true)

        if (LiquidBounce.fileManager.hasConverted) Fonts.fontSFUI40.drawCenteredString("Your old folder has been converted.", this.width / 2F, this.height / 4F + 150F + 48F + 10F, -1, true)
        Fonts.fontSFUI40.drawString("${LiquidBounce.CLIENT_NAME} build ${LiquidBounce.CLIENT_VERSION}.", 3F, this.height - 13F, -1, true)
        super.drawScreen(mouseX, mouseY, partialTicks)
        GL11.glPopMatrix()
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            1 -> mc.displayGuiScreen(GuiSelectWorld(this))
            2 -> mc.displayGuiScreen(GuiMultiplayer(this))
            4 -> mc.displayGuiScreen(GuiModsMenu(this))
            100 -> mc.displayGuiScreen(GuiAltManager(this))
            102 -> mc.displayGuiScreen(GuiBackground(this))
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}