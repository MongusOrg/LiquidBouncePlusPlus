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
    private val barValue = BoolValue("Bar", true)
    private val newValue = BoolValue("New", true)
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
            i.drawNotification(animationY, smoothYTransition.get(), bgColor, side, barValue.get(), newValue.get()).also { /**if (!i.stayTimer.hasTimePassed(i.displayTime))*/ animationY += if (newValue.get()) (if (side.vertical == Side.Vertical.DOWN) 20 else -20) else (if (side.vertical == Side.Vertical.DOWN) 30 else -30) }
        else
            exampleNotification.drawNotification(animationY, smoothYTransition.get(), bgColor, side, barValue.get(), newValue.get())
        if (mc.currentScreen is GuiHudDesigner) {
            if (!hud.notifications.contains(exampleNotification))
                hud.addNotification(exampleNotification)

            exampleNotification.fadeState = Notification.FadeState.STAY
            //exampleNotification.stayTimer.reset()
            exampleNotification.x = exampleNotification.textLength + 8F

            return if (newValue.get()) Border(-102F, -48F, 0F, -30F) else Border(-128F, -58F, 0F, -30F)
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

    enum class Type {
        SUCCESS,
        INFO,
        WARNING,
        ERROR
    }

    enum class FadeState {
        IN,STAY,OUT,END
    }

    fun drawNotification(animationY: Float, smooth: Boolean, backgroundColor: Color, side: Side, bar: Boolean, new: Boolean) {
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

        if (new) {
            RenderUtils.customRounded(-x + 8F + textLength, -y, -x - 2F, -18F - y, 0F, 3F, 3F, 0F, backgroundColor.rgb)
            RenderUtils.customRounded(-x - 2F, -y, -x - 5F, -18F - y, 3F, 0F, 0F, 3F, when(type) {
                    Type.SUCCESS -> Color(80, 255, 80).rgb
                    Type.ERROR -> Color(255, 80, 80).rgb
                    Type.INFO -> Color(255, 255, 255).rgb
                    Type.WARNING -> Color(255, 255, 0).rgb
                })  

            Fonts.font40.drawString(message, -x + 3, -14F - y, -1)
        } else {
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
            if (bar) if (fadeState == FadeState.STAY && !stayTimer.hasTimePassed(displayTime)) {
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
            
            Fonts.font40.drawString(message, -x + 2, -18F - y, -1)
        }

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
}