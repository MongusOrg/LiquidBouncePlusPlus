/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AmoClub/lbplusplus_new/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoHypixel
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.features.module.modules.misc.BanChecker
import java.text.DecimalFormat

@ElementInfo(name = "Statistics")
class SessionInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 50F, { blurValue.get() })
    private val redValue = IntegerValue("Background-Red", 0, 0, 255)
    private val greenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val blueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val alpha = IntegerValue("Background-Alpha", 120, 0, 255)
    private var fontValue = FontValue("Font", Fonts.font40)

    override fun drawElement(): Border? {
        
        val font = fontValue.get()

        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()

        if (blurValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()

            BlurUtils.blurAreaRounded(floatX, floatY, floatX + 155F, floatY + 63F, 3.5F, blurStrength.get())

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        RenderUtils.whatRoundedRect(0F, 0F, 155F, 56F, if(!blurValue.get()) Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb else Color(redValue.get(), greenValue.get(), blueValue.get(), 135).rgb, 3.5F)
        RenderUtils.whatRoundedRect(0F, 0F, 155F, 5F, RenderUtils.getRainbowOpaque(2, 0.9f, 1.0f, 0), 3.5F)

        font.drawString("Username: " + mc.session.username, 3, 10, -1)
        font.drawString("Killed: " + if (StatisticsUtils.getKills == 0) "None" else StatisticsUtils.getKills.toString(), 3, 20, -1)
        font.drawString("Game wons: " + if (StatisticsUtils.getWins == 0) "None" else StatisticsUtils.getWins.toString(), 3, 30, -1)
        font.drawString("Session Time: " + SessionUtils.getFormatSessionTime(), 3, 50, -1)
        
     return Border(0F, 0F, 155F, 63F)
    }
}
