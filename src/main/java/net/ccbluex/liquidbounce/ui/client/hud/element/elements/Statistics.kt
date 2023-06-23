/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import org.lwjgl.opengl.GL11
import java.awt.Color
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.ui.font.Fonts

@ElementInfo(name = "Statistics")
class Statistics(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val blurValue = BoolValue("Blur", false)
    private val barValue = BoolValue("Bar", false)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 50F, { blurValue.get() })
    private val redValue = IntegerValue("Background-Red", 0, 0, 255)
    private val greenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val blueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val barRedValue = IntegerValue("Bar-Red", 0, 0, 255, { barValue.get() })
    private val barGreenValue = IntegerValue("Bar-Green", 0, 0, 255, { barValue.get() })
    private val barBlueValue = IntegerValue("Bar-Blue", 0, 0, 255, { barValue.get() })
    private val alpha = IntegerValue("Background-Alpha", 120, 0, 255, { barValue.get() })
    private val barAlpha = IntegerValue("Bar-Alpha", 120, 0, 255, { barValue.get() })
    private var fontValue = FontValue("Font", Fonts.font40)

    override fun drawElement(): Border? {
        
        val font = fontValue.get()

        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()

        if (blurValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()

            BlurUtils.blurAreaRounded(floatX, floatY, floatX + 155F, floatY + 39.5F, 3.5F, blurStrength.get())

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        RenderUtils.drawRoundedRect(0F, 0F, 155F, 39F, 3.0F, (if(!blurValue.get()) Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb else Color(redValue.get(), greenValue.get(), blueValue.get(), 135).rgb))
        RenderUtils.drawRect(0F, 0F, 155F, 3.5F, Color(barRedValue.get(), barGreenValue.get(), barBlueValue.get(), barAlpha.get()).rgb)

        font.drawStringWithShadow("Session info", 50F, 7f, Color(255,255,255).rgb)
        font.drawStringWithShadow("Username: " + mc.session.username, 3f, 20f, Color(255,255,255).rgb)
        font.drawStringWithShadow("Session Time: " + SessionUtils.getFormatSessionTime(), 3f, 30f, Color(255,255,255).rgb)

     return Border(0F, 0F, 155F, 39.5F)
    }
}
