/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.AnimationUtils

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager

import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.util.ResourceLocation
import java.awt.Color

import org.lwjgl.opengl.GL11

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val smoothYTransition = BoolValue("Smooth-YTransition", true)
    private val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bgGreenValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bgBlueValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bgAlphaValue = IntegerValue("Background-Alpha", 190, 0, 255)

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notification", Notification.Type.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val bgColor = Color(bgRedValue.get(), bgGreenValue.get(), bgBlueValue.get(), bgAlphaValue.get())
        var animationY = 30F
        val notifications = mutableListOf<Notification>()
        for(i in hud.notifications)
            notifications.add(i)
        for(i in notifications)
            if(mc.currentScreen !is GuiHudDesigner)
            i.drawNotification(animationY, smoothYTransition.get(), bgColor, side).also { /**if (!i.stayTimer.hasTimePassed(i.displayTime))*/ animationY += (if (side.vertical == Side.Vertical.DOWN) 30 else -30) }
        else
            exampleNotification.drawNotification(animationY, smoothYTransition.get(), bgColor, side)
        if (mc.currentScreen is GuiHudDesigner) {
            if (!hud.notifications.contains(exampleNotification))
                hud.addNotification(exampleNotification)

            exampleNotification.fadeState = Notification.FadeState.STAY
            //exampleNotification.stayTimer.reset()
            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-98F, -58F, 0F, -30F)
        }

        return null
    }

}
class Notification(message : String,type : Type, displayLength: Long) {
    private val notifyDir = "liquidbounce+/notification/"
    private val imgSuccess = ResourceLocation("${notifyDir}checkmark.png")
    private val imgError = ResourceLocation("${notifyDir}error.png")
    private val imgWarning = ResourceLocation("${notifyDir}warning.png")
    private val imgInfo = ResourceLocation("${notifyDir}info.png")

    var x = 0f
    var textLength = 0
    private var stay = 0f
    private var fadeStep = 0f
    var fadeState = FadeState.IN
    var displayTime : Long = 0L
    var stayTimer = MSTimer()
    private var firstY = 0f
    private var message: String = ""
    private var type: Type
    init {
        this.message = message
        this.type = type
        this.displayTime = displayLength
        this.firstY = 1919F
        this.stayTimer.reset()
        this.textLength = Fonts.font40.getStringWidth(message)
    }

    constructor(message: String, type: Type) : this(message, type, 2000L)

    constructor(message: String) : this(message, Type.INFO, 500L)

    constructor(message: String, displayLength: Long) : this(message, Type.INFO, displayLength)

    enum class Type(var notifName: String) {
        SUCCESS("Success"),
        INFO("Info"),
        WARNING("Warning"),
        ERROR("Error")
    }

    enum class FadeState {
        IN,STAY,OUT,END
    }

    fun drawNotification(animationY: Float, smooth: Boolean, backgroundColor: Color, side: Side) {
        val delta = RenderUtils.deltaTime
        val width = textLength.toFloat() + 8.0f
        
        if (smooth) {
            if (firstY == 1919.0F) {
                firstY = animationY
            }
            firstY += (animationY - firstY) * 0.25F
        } else {
            firstY = animationY
        }

        var y = firstY

        //bg
        RenderUtils.drawRect(-x + 8 + textLength, -y, -x - 1 - 26F, -28F - y, backgroundColor.rgb)

        GL11.glPushMatrix()
        GlStateManager.disableAlpha()
        RenderUtils.drawImage2(when (type) {
            Type.SUCCESS -> imgSuccess
            Type.ERROR -> imgError
            Type.WARNING -> imgWarning
            Type.INFO -> imgInfo
        }, -x - 1 - 26F, -27F - y, 26, 26)
        GlStateManager.enableAlpha()
        GL11.glPopMatrix()
        
        val dist = (x + 1 + 26F) - (x - 8 - textLength)

        val kek = -x - 1 - 26F

        //notification bar xd
        if (fadeState == FadeState.STAY && !stayTimer.hasTimePassed(displayTime)) {
            RenderUtils.drawRect(kek, -y, kek + (dist * if (stayTimer.hasTimePassed(displayTime)) 0F else ((displayTime - (System.currentTimeMillis() - stayTimer.time)).toFloat() / displayTime.toFloat())), -1F - y, when(type) {
                Type.SUCCESS -> Color(80, 255, 80).rgb
                Type.ERROR -> Color(255, 80, 80).rgb
                Type.INFO -> Color(255, 255, 255).rgb
                Type.WARNING -> Color(255, 255, 0).rgb
            })
        } else if (fadeState == FadeState.IN) {
            RenderUtils.drawRect(kek, -y, kek + dist, -1F - y, when(type) {
                Type.SUCCESS -> Color(80, 255, 80).rgb
                Type.ERROR -> Color(255, 80, 80).rgb
                Type.INFO -> Color(255, 255, 255).rgb
                Type.WARNING -> Color(255, 255, 0).rgb
            })
        }

        GlStateManager.resetColor()

        //message thingy uwu
        Fonts.font40.drawString(message, -x + 2, -18F - y, -1)
        /*Fonts.fontSFUI40.drawString(if(message.contains("Enabled") || message.contains("Disabled")) "Module" else type.notifName, -x + 2, -23F - y,
            when(type) {
                Type.SUCCESS -> Color(80, 255, 80).rgb
                Type.ERROR -> Color(255, 80, 80).rgb
                Type.INFO -> Color(255, 255, 255).rgb
                Type.WARNING -> Color(255, 255, 0).rgb
            }
        )*/

        GlStateManager.resetColor()
        
        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
                stayTimer.reset()
            }

            FadeState.STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(displayTime))
                    fadeState = FadeState.OUT
            }

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 2F
            } else
                fadeState = FadeState.END

            FadeState.END -> hud.removeNotification(this)
        }        
    }
/*
    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun drawRect(x: Float, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x.toDouble(), y)
        GL11.glVertex2d(x.toDouble(), y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun rectangle(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x, y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }
    fun enableGL2D() {
        GL11.glDisable(2929)
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glDepthMask(true)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glHint(3155, 4354)
    }

    fun disableGL2D() {
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glEnable(2929)
        GL11.glDisable(2848)
        GL11.glHint(3154, 4352)
        GL11.glHint(3155, 4352)
    }
    fun RenderUtils.drawRoundedRect(x: Float, y: Float, x1: Float, y1: Float, borderC: Int, insideC: Int) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        var z = 0f

        //convert position
        if (x > x1) {
            z = x
            x = x1
            x1 = z
        }

        if (y > y1) {
            z = y
            y = y1
            y1 = z
        }

        enableGL2D()
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        drawVLine(2.0f.let { x *= it; x }, 2.0f.let { y *= it; y } + 1.0f, 2.0f.let { y1 *= it; y1 } - 2.0f, borderC)
        drawVLine(2.0f.let { x1 *= it; x1 } - 1.0f, y + 1.0f, y1 - 2.0f, borderC)
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC)
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC)
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC)
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC)
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC)
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC)
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC)
        GL11.glScalef(2.0f, 2.0f, 2.0f)
        disableGL2D()
        Gui.drawRect(0, 0, 0, 0, 0)
    }
    fun drawHLine(par1: Float, par2: Float, par3: Float, par4: Int) {
        var par1 = par1
        var par2 = par2
        if (par2 < par1) {
            val var5 = par1
            par1 = par2
            par2 = var5
        }
        RenderUtils.drawRect(par1, par3, par2 + 1.0f, par3 + 1.0f, par4)
    }

    fun drawVLine(x: Float, y: Float, x1: Float, y1: Int) {
        var y = y
        var x1 = x1
        if (x1 < y) {
            val var5 = y
            y = x1
            x1 = var5
        }
        RenderUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1)
    }
*/
}