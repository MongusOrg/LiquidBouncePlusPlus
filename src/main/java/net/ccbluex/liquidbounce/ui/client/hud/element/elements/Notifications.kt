/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.Date

import org.lwjgl.opengl.GL11

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    val smoothYTransition = BoolValue("Smooth-YTransition", true)
    val blurValue = BoolValue("Blur", false)
    val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 30F)
    val styleValue = ListValue("Style", arrayOf("Compact", "Full", "New"), "Compact")
    val newAnimValue = BoolValue("UseNewAnim", true)
    val animationSpeed = FloatValue("Anim-Speed", 0.5F, 0.01F, 1F, { newAnimValue.get() })
    val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    val bgGreenValue = IntegerValue("Background-Red", 0, 0, 255)
    val bgBlueValue = IntegerValue("Background-Red", 0, 0, 255)
    val bgAlphaValue = IntegerValue("Background-Alpha", 190, 0, 255)

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notification", Notification.Type.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()

        for (i in hud.notifications)
            notifications.add(i)
        
        if (mc.currentScreen !is GuiHudDesigner || !notifications.isEmpty()) 
            for(i in notifications)
                i.drawNotification(animationY, this)
                    .also { animationY += when (styleValue.get().toLowerCase()) {
                        "compact" -> 20
                        "full" -> 30
                        else -> 30
                    } * if (side.vertical == Side.Vertical.DOWN) 1F else -1F}
        else
            exampleNotification.drawNotification(animationY, this)

        if (mc.currentScreen is GuiHudDesigner) {
            exampleNotification.fadeState = Notification.FadeState.STAY
            //exampleNotification.stayTimer.reset()
            exampleNotification.x = exampleNotification.textLength + 8F
            if (exampleNotification.stayTimer.hasTimePassed(exampleNotification.displayTime)) 
                exampleNotification.stayTimer.reset()

            return if (styleValue.get().equals("compact", true)) Border(-102F, -48F, 0F, -30F) else Border(-130F, -58F, 0F, -30F)
        }

        return null
    }

}
class Notification(message : String, type : Type, displayLength: Long) {
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
        this.firstY = 19190F
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

    fun drawNotification(animationY: Float, parent: Notifications) {
        val delta = RenderUtils.deltaTime
        val width = textLength.toFloat() + 8.0f

        val smooth = parent.smoothYTransition.get()
        val newAnim = parent.newAnimValue.get()
        val animSpeed = parent.animationSpeed.get()
        val side = parent.side
        val style = parent.styleValue.get()
        val blur = parent.blurValue.get()
        val strength = parent.blurStrength.get()
        val originalX = parent.renderX.toFloat()
        val originalY = parent.renderY.toFloat()

        val backgroundColor = Color(parent.bgRedValue.get(), parent.bgGreenValue.get(), parent.bgBlueValue.get(), parent.bgAlphaValue.get())
        
        if (smooth) {
            if (firstY == 19190.0F) {
                firstY = animationY
            }
            firstY += (animationY - firstY) * 0.25F
        } else {
            firstY = animationY
        }

        var y = firstY

        when (style.toLowerCase()) {
            "compact" -> {
                GlStateManager.resetColor()
                if (blur) {
                    GL11.glTranslatef(-originalX, -originalY, 0F)
                    GL11.glPushMatrix()
                    BlurUtils.blurAreaRounded(originalX + -x - 5F, originalY + -18F - y, originalX + -x + 8F + textLength, originalY + -y, 3F, strength)
                    GL11.glPopMatrix()
                    GL11.glTranslatef(originalX, originalY, 0F)
                } 

                RenderUtils.customRounded(-x + 8F + textLength, -y, -x - 2F, -18F - y, 0F, 3F, 3F, 0F, backgroundColor.rgb)
                RenderUtils.customRounded(-x - 2F, -y, -x - 5F, -18F - y, 3F, 0F, 0F, 3F, when(type) {
                        Type.SUCCESS -> Color(80, 255, 80).rgb
                        Type.ERROR -> Color(255, 80, 80).rgb
                        Type.INFO -> Color(255, 255, 255).rgb
                        Type.WARNING -> Color(255, 255, 0).rgb
                    })  

                GlStateManager.resetColor()
                Fonts.font40.drawString(message, -x + 3, -13F - y, -1)
            }
            "full" -> {
                val dist = (x + 1 + 26F) - (x - 8 - textLength)
                val kek = -x - 1 - 26F

                GlStateManager.resetColor()
                if (blur) {
                    GL11.glTranslatef(-originalX, -originalY, 0F)
                    GL11.glPushMatrix()
                    BlurUtils.blurArea(originalX + kek, originalY + -28F - y, originalX + -x + 8 + textLength, originalY + -y, strength)
                    GL11.glPopMatrix()
                    GL11.glTranslatef(originalX, originalY, 0F)
                } 

                RenderUtils.drawRect(-x + 8 + textLength, -y, kek, -28F - y, backgroundColor.rgb)

                GL11.glPushMatrix()
                GlStateManager.disableAlpha()
                RenderUtils.drawImage2(when (type) {
                    Type.SUCCESS -> imgSuccess
                    Type.ERROR -> imgError
                    Type.WARNING -> imgWarning
                    Type.INFO -> imgInfo
                }, kek, -27F - y, 26, 26)
                GlStateManager.enableAlpha()
                GL11.glPopMatrix()


                //notification bar xd
                GlStateManager.resetColor()
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
                Fonts.font40.drawString(message, -x + 2, -18F - y, -1)
            }
            "new" -> {
                val dist = (x + 1 + 26F) - (x - 8 - textLength)
                val kek = -x - 1 - 26F

                val toolong = dist * if (stayTimer.hasTimePassed(displayTime)) 0F else ((displayTime - (System.currentTimeMillis() - stayTimer.time)).toFloat() / displayTime.toFloat())

                GlStateManager.resetColor()
                if (blur) {
                    GL11.glTranslatef(-originalX, -originalY, 0F)
                    GL11.glPushMatrix()
                    BlurUtils.blurAreaRounded(originalX + kek, originalY + -28F - y, originalX + -x + 8 + textLength, originalY + -y, 3F, strength)
                    GL11.glPopMatrix()
                    GL11.glTranslatef(originalX, originalY, 0F)
                } 

                Stencil.write(true)
                RenderUtils.drawRoundedRect(-x + 8 + textLength, -y, kek, -28F - y, 3F, backgroundColor.rgb)
                Stencil.erase(true)

                GlStateManager.resetColor()
                if (fadeState == FadeState.STAY && !stayTimer.hasTimePassed(displayTime))
                    RenderUtils.newDrawRect(kek, -y, kek + toolong, -28F - y, when(type) {
                        Type.SUCCESS -> Color(80, 255, 80, backgroundColor.alpha / 2).rgb
                        Type.ERROR -> Color(255, 80, 80, backgroundColor.alpha / 2).rgb
                        Type.INFO -> Color(255, 255, 255, backgroundColor.alpha / 2).rgb
                        Type.WARNING -> Color(255, 255, 0, backgroundColor.alpha / 2).rgb
                    })
                else if (fadeState == FadeState.IN)
                    RenderUtils.newDrawRect(kek, -y, kek + dist, -28F - y, when(type) {
                        Type.SUCCESS -> Color(80, 255, 80, backgroundColor.alpha / 2).rgb
                        Type.ERROR -> Color(255, 80, 80, backgroundColor.alpha / 2).rgb
                        Type.INFO -> Color(255, 255, 255, backgroundColor.alpha / 2).rgb
                        Type.WARNING -> Color(255, 255, 0, backgroundColor.alpha / 2).rgb
                    })

                Stencil.dispose()

                GL11.glPushMatrix()
                GlStateManager.disableAlpha()
                GlStateManager.resetColor()
                GL11.glColor4f(1F, 1F, 1F, 1F)
                RenderUtils.drawImage2(when (type) {
                    Type.SUCCESS -> imgSuccess
                    Type.ERROR -> imgError
                    Type.WARNING -> imgWarning
                    Type.INFO -> imgInfo
                }, kek, -27F - y, 26, 26)
                GlStateManager.enableAlpha()
                GL11.glPopMatrix()
                
                Fonts.font40.drawString(message, -x + 2, -18F - y, -1)
            }
        }

        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    if (newAnim) 
                        x = net.ccbluex.liquidbounce.utils.AnimationUtils.animate(width, x, animSpeed * 0.025F * delta)
                    else 
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
                if (newAnim) 
                    x = net.ccbluex.liquidbounce.utils.AnimationUtils.animate(-width, x, animSpeed * 0.025F * delta)
                else 
                    x = AnimationUtils.easeOut(fadeStep, width) * width

                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> hud.removeNotification(this)
        }        
    }
}