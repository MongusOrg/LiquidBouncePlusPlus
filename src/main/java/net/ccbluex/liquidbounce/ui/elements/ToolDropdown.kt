/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.minecraft.client.gui.GuiButton
import java.awt.Color
import org.lwjgl.opengl.GL11.*

object ToolDropdown {

    private var fullHeight = 0F
    private var dropState = false

    fun handleDraw(button: GuiButton) {
        val gray = Color(100, 100, 100).rgb
        val bWidth = button.getButtonWidth().toFloat()

        if (!dropState && fullHeight == 0F) return
        fullHeight = AnimationUtils.animate(if (dropState) 100F else 0F, fullHeight, 0.01F * RenderUtils.deltaTime.toFloat())

        glPushMatrix()
        RenderUtils.makeScissorBox(button.xPosition.toFloat(), button.yPosition.toFloat() + 20F, button.xPosition.toFloat() + bWidth, button.yPosition.toFloat() + 20F + fullHeight)
        glEnable(GL_SCISSOR_TEST)
        glPushMatrix()
        glTranslatef(button.xPosition.toFloat(), button.yPosition.toFloat() + 20F, 0F)
        RenderUtils.newDrawRect(0F, 0F, bWidth, 100F, Color(24, 24, 24).rgb)
        Fonts.font40.drawString("AntiForge", 4F, 5F, -1)
        Fonts.font40.drawString("Block FML", 4F, 25F, if (AntiForge.enabled) -1 else gray)
        Fonts.font40.drawString("Block FML Proxy Packets", 4F, 45F, if (AntiForge.enabled) -1 else gray)
        Fonts.font40.drawString("Block Payload Packets", 4F, 65F, if (AntiForge.enabled) -1 else gray)
        Fonts.font40.drawString("BungeeCord Spoof", 4F, 85F, -1)
        drawToggleSwitch(bWidth - 20F, 6F, 16F, 8F, AntiForge.enabled)
        drawCheckbox(bWidth - 12F, 26F, 8F, AntiForge.blockFML)
        drawCheckbox(bWidth - 12F, 46F, 8F, AntiForge.blockProxyPacket)
        drawCheckbox(bWidth - 12F, 66F, 8F, AntiForge.blockPayloadPackets)
        drawToggleSwitch(bWidth - 20F, 86F, 16F, 8F, BungeeCordSpoof.enabled)
        glPopMatrix()
        glDisable(GL_SCISSOR_TEST)
        glPopMatrix()
    }

    fun handleClick(mouseX: Int, mouseY: Int, button: GuiButton) {
        val bX = button.xPosition.toFloat()
        val bY = button.yPosition.toFloat()
        val bWidth = button.getButtonWidth().toFloat()
        if (isMouseOver(mouseX, mouseY, bX, bY + 20F, bWidth, fullHeight)) {
            when {
                isMouseOver(mouseX, mouseY, bX, bY + 20F, bWidth, 20F) -> AntiForge.enabled = !AntiForge.enabled
                isMouseOver(mouseX, mouseY, bX, bY + 40F, bWidth, 20F) -> AntiForge.blockFML = !AntiForge.blockFML
                isMouseOver(mouseX, mouseY, bX, bY + 60F, bWidth, 20F) -> AntiForge.blockProxyPacket = !AntiForge.blockProxyPacket
                isMouseOver(mouseX, mouseY, bX, bY + 80F, bWidth, 20F) -> AntiForge.blockPayloadPackets = !AntiForge.blockPayloadPackets
                isMouseOver(mouseX, mouseY, bX, bY + 100F, bWidth, 20F) -> BungeeCordSpoof.enabled = !BungeeCordSpoof.enabled
            }
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig)
        }
    }

    private fun isMouseOver(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float) = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height

    fun toggleState() {
        dropState = !dropState
    }

    fun drawToggleSwitch(x: Float, y: Float, width: Float, height: Float, state: Boolean) {
        val borderColor = if (state) Color(0, 140, 255).rgb else Color(160, 160, 160).rgb
        val mainColor = if (state) borderColor else Color(24, 24, 24).rgb
        RenderUtils.originalRoundedRect(x - 0.25F, y - 0.25F, x + width + 0.25F, y + height + 0.25F, (height + 0.5F) / 2F, borderColor)
        RenderUtils.originalRoundedRect(x - 0.25F, y - 0.25F, x + width + 0.25F, y + height + 0.25F, (height + 0.5F) / 2F, mainColor)
        if (state)
            RenderUtils.drawFilledCircle(x + width - 2F - (height - 4F) / 2F, y + 2F + (height - 4F) / 2F, (height - 4F) / 2F, Color(24, 24, 24).rgb)
        else
            RenderUtils.drawFilledCircle(x + 2F - (height - 4F) / 2F, y + 2F + (height - 4F) / 2F, (height - 4F) / 2F, Color(0, 140, 255).rgb)
    }

    fun drawCheckbox(x: Float, y: Float, width: Float, state: Boolean) {
        val borderColor = if (state) Color(0, 140, 255).rgb else Color(160, 160, 160).rgb
        val mainColor = if (state) borderColor else Color(24, 24, 24).rgb
        RenderUtils.originalRoundedRect(x - 0.25F, y - 0.25F, x + width + 0.25F, y + width + 0.25F, 2F, borderColor)
        RenderUtils.originalRoundedRect(x - 0.25F, y - 0.25F, x + width + 0.25F, y + width + 0.25F, 2F, mainColor)
        if (state) {
            RenderUtils.drawLine(x / 4F, y / 2F, x / 2.05F, y / 4F * 3F, 0.5F)
            RenderUtils.drawLine(x / 2.05F, y / 4F * 3F, x / 3.99F * 3F, y / 4F, 0.5F)
        }
    }

}