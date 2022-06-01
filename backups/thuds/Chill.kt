/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Chill: TargetStyle("Chill") {

    override fun drawTarget(entity: EntityPlayer, element: Target) {
        /*val name = entity.name
        val health = entity.health
        val tWidth = (45F + Fonts.font40.getStringWidth(name).coerceAtLeast(Fonts.font72.getStringWidth(decimalFormat.format(health)))).coerceAtLeast(if (chillHealthBarValue.get()) 150F else 90F)
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)

        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()

        val calcScaleX = (progressChill * (4F / (tWidth / 2F)))
        val calcScaleY = if (chillHealthBarValue.get()) (progressChill * (4F / 24F))
                        else (progressChill * (4F / 19F))
        val calcTranslateX = floatX + tWidth / 2F * calcScaleX
        val calcTranslateY = floatY + if (chillHealthBarValue.get()) (24F * (progressChill * (4F / 24F))) 
                                            else (19F * (progressChill * (4F / 19F)))

        // translation/scaling
        GL11.glScalef(1f, 1f, 1f)
        GL11.glPopMatrix()

        GL11.glPushMatrix()

        if (chillFadingValue.get()) {
            GL11.glTranslatef(
                calcTranslateX, calcTranslateY, 0F)
            GL11.glScalef(
                1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        } else {
            GL11.glTranslated(renderX, renderY, 0.0)
            GL11.glScalef(1F, 1F, 1F)
        }

        // background
        RenderUtils.drawRoundedRect(0F, 0F, tWidth, if (chillHealthBarValue.get()) 48F else 38F, 7F, reColorBg.rgb)
        GlStateManager.resetColor()
        GlStateManager.color(1F, 1F, 1F, 1F)
        
        // head
        if (playerInfo != null) {
            Stencil.write(false)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 8F)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            Stencil.erase(true)
            drawHead(playerInfo.locationSkin, 4, 4, 30, 30)
            Stencil.dispose()
        }

        GlStateManager.color(1F, 1F, 1F, 1F)

        // name + health
        Fonts.font40.drawString(name, 38F, 6F, -1, false)
        numberRenderer.renderChar(health, calcTranslateX, calcTranslateY, 38F, 17F, calcScaleX, calcScaleY, false, chillFontSpeed.get(), -1)
        
        // health bar
        if (chillHealthBarValue.get()) {
            RenderUtils.drawRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F, reColorBar.darker().darker().darker().rgb)

            Stencil.write(false)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderUtils.fastRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F)
            GL11.glDisable(GL11.GL_BLEND)
            Stencil.erase(true)
            if (chillRoundValue.get())
                RenderUtils.customRounded(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, 0F, 3F, 3F, 0F, reColorBar.rgb)
            else
                RenderUtils.drawRect(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, reColorBar.rgb)

            Stencil.dispose()
        }*/
    }

    private class CharRenderer(val small: Boolean) {
        var moveY = FloatArray(20)
        var moveX = FloatArray(20)

        private val numberList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")

        private val deFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))

        init {
            for (i in 0..19) {
                moveX[i] = 0F
                moveY[i] = 0F
            }
        }

        fun renderChar(number: Float, orgX: Float, orgY: Float, initX: Float, initY: Float, scaleX: Float, scaleY: Float, shadow: Boolean, fontSpeed: Float, color: Int): Float {
            val reFormat = deFormat.format(number.toDouble()) // string
            val fontRend = if (small) Fonts.font40 else Fonts.font72
            val delta = RenderUtils.deltaTime
            val scaledRes = ScaledResolution(mc)

            var indexX = 0
            var indexY = 0
            var animX = 0F

            val cutY = initY + fontRend.FONT_HEIGHT.toFloat() * (3F / 4F)

            GL11.glEnable(3089)
            RenderUtils.makeScissorBox(0F, orgY + initY - 4F * scaleY, scaledRes.getScaledWidth().toFloat(), orgY + cutY - 4F * scaleY)
            for (char in reFormat.toCharArray()) {
                moveX[indexX] = AnimationUtils.animate(animX, moveX[indexX], fontSpeed * 0.025F * delta)
                animX = moveX[indexX]

                val pos = numberList.indexOf("$char")
                val expectAnim = (fontRend.FONT_HEIGHT.toFloat() + 2F) * pos
                val expectAnimMin = (fontRend.FONT_HEIGHT.toFloat() + 2F) * (pos - 2)
                val expectAnimMax = (fontRend.FONT_HEIGHT.toFloat() + 2F) * (pos + 2)
                
                if (pos >= 0) {
                    moveY[indexY] = AnimationUtils.animate(expectAnim, moveY[indexY], fontSpeed * 0.02F * delta)

                    GL11.glTranslatef(0F, initY - moveY[indexY], 0F)
                    numberList.forEachIndexed { index, num ->
                        if ((fontRend.FONT_HEIGHT.toFloat() + 2F) * index >= expectAnimMin && (fontRend.FONT_HEIGHT.toFloat() + 2F) * index <= expectAnimMax) {
                            fontRend.drawString(num, initX + moveX[indexX], (fontRend.FONT_HEIGHT.toFloat() + 2F) * index, color, shadow)
                        }
                    }
                    GL11.glTranslatef(0F, -initY + moveY[indexY], 0F)
                } else {
                    moveY[indexY] = 0F
                    fontRend.drawString("$char", initX + moveX[indexX], initY, color, shadow)
                }

                animX += fontRend.getStringWidth("$char")
                indexX++
                indexY++
            }
            GL11.glDisable(3089)

            return animX
        }
    }

}