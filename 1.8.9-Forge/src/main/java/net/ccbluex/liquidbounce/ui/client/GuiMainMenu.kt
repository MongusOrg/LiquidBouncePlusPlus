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
import net.minecraft.util.ResourceLocation
import java.awt.Color

import org.lwjgl.opengl.GL11

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    override fun initGui() {
        val defaultHeight = this.height / 4 + 48

        this.buttonList.add(GuiButton(100, this.width / 2 - 55, defaultHeight + 48, 110, 20, "Alt Manager"))

        this.buttonList.add(GuiButton(1, this.width / 2 - 55, defaultHeight, 110, 20, I18n.format("menu.singleplayer")))
        this.buttonList.add(GuiButton(2, this.width / 2 - 55, defaultHeight + 24, 110, 20, I18n.format("menu.multiplayer")))
        this.buttonList.add(GuiButton(102, this.width / 2 - 55, defaultHeight + 72, 110, 20, "Background"))
        this.buttonList.add(GuiButton(0, this.width / 2 - 55, defaultHeight + 96, 110, 20, "Settings"))
        this.buttonList.add(GuiButton(4, this.width / 2 - 55, defaultHeight + 120, 110, 20, "Mods/Scripts"))

        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glPushMatrix()
        moveMouseEffect(mouseX, mouseY, 10F)

        super.drawScreen(mouseX, mouseY, partialTicks)
        GL11.glPopMatrix()
    }

    fun moveMouseEffect(mX: Int, mY: Int, strength: Float) {
        mX -= width / 2
        mY -= height / 2
        val xDelta = mX.toFloat() / (width / 2).toFloat()
        val yDelta = mY.toFloat() / (height / 2).toFloat()
        
        GL11.glTranslatef(xDelta * strength, yDelta * strength, 0F)
    }

    enum class ImageButton(val buttonName: String, val texture: ResourceLocation) {
        Single("Singleplayer", ResourceLocation("liquidbounce+/menu/singleplayer.png")),
        Multi("Multiplayer", ResourceLocation("liquidbounce+/menu/multiplayer.png")),
        Alts("Alts", ResourceLocation("liquidbounce+/menu/alt.png")),
        Settings("Settings", ResourceLocation("liquidbounce+/menu/settings.png")),
        Mods("Mods/Customize", ResourceLocation("liquidbounce+/menu/mods.png")),
        Exit("Exit", ResourceLocation("liquidbounce+/menu/exit.png"))
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}