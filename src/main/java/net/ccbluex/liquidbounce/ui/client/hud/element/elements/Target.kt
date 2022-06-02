/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.TeleportAura
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

/**
 * A target hud
 */
@ElementInfo(name = "Target", disableScale = true, retrieveDamage = true)
class Target : Element() {

    val styleList = mutableListOf<TargetStyle>()

    val styleValue: ListValue

    // Global variables
    val blurValue = BoolValue("Blur", false)
    val blurStrength = FloatValue("Blur-Strength", 1F, 0F, 40F, { blurValue.get() })

    val fadeValue = BoolValue("FadeAnim", false)
    val fadeSpeed = FloatValue("Fade-Speed", 1F, 0F, 5F, { fadeValue.get() })

    val noAnimValue = BoolValue("No-Animation", false)
    val globalAnimSpeed = FloatValue("Global-AnimSpeed", 3F, 1F, 9F, { !noAnimValue.get() })

    val showWithChatOpen = BoolValue("Show-ChatOpen", true)

    val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "Slowly", "Fade", "Mixer", "Health"), "Custom")
    val redValue = IntegerValue("Red", 252, 0, 255)
    val greenValue = IntegerValue("Green", 96, 0, 255)
    val blueValue = IntegerValue("Blue", 66, 0, 255)
    val saturationValue = FloatValue("Saturation", 1F, 0F, 1F)
    val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    val waveSecondValue = IntegerValue("Seconds", 2, 1, 10)
    val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    val bgGreenValue = IntegerValue("Background-Green", 0, 0, 255)
    val bgBlueValue = IntegerValue("Background-Blue", 0, 0, 255)
    val bgAlphaValue = IntegerValue("Background-Alpha", 160, 0, 255)

    private val allValues: List<Value<*>>
        get() = listOf(styleValue, // style
                    blurValue, blurStrength, // blur
                    fadeValue, fadeSpeed, // fade anim
                    noAnimValue, globalAnimSpeed, // global anim
                    showWithChatOpen, // if not found any target and chat is open then pick mc.thePlayer
                    colorModeValue, // color mode
                    redValue, greenValue, blueValue, // global rgb
                    saturationValue, brightnessValue, waveSecondValue, // wave colors stuffs
                    bgRedValue, bgGreenValue, bgBlueValue, bgAlphaValue) // background global rgba 

    override val values: List<Value<*>>
        get() {
            val retrieveList = mutableListOf<Value<*>>()
            styleList.forEach { retrieveList.addAll(it.values) }
            return allValues + retrieveList
        }

    init {
        styleValue = ListValue("Style", addStyles(
            /*Chill(),
            Chillest(),
            Exhibition(),
            Flux(),
            NewFlux(),*/
            LiquidBounce(this),
            Slowly(this)
            /*LiquidBouncePlus(),
            Novoline(),
            Remix(),
            Rise(),
            Simplified(),
            Slowly()*/
        ).toTypedArray(), "LiquidBounce")
    }

    var mainTarget: EntityPlayer? = null
    var animProgress = 0F

    var barColor = Color(-1)
    var bgColor = Color(-1)

    override fun drawElement(): Border? {
        val mainStyle = getCurrentStyle(styleValue.get()) ?: return null

        val kaTarget = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        val taTarget = (LiquidBounce.moduleManager[TeleportAura::class.java] as TeleportAura).lastTarget

        val actualTarget = if (kaTarget != null && kaTarget is EntityPlayer) kaTarget 
                            else if (taTarget != null &&  taTarget is EntityPlayer) taTarget
                            else if ((mc.currentScreen is GuiChat && showWithChatOpen.get()) || mc.currentScreen is GuiHudDesigner) mc.thePlayer 
                            else null

        val preBarColor = when (colorModeValue.get()) {
            "Rainbow" -> Color(RenderUtils.getRainbowOpaque(waveSecondValue.get(), saturationValue.get(), brightnessValue.get(), 0))
            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (actualTarget != null) BlendUtils.getHealthColor(actualTarget.health, actualTarget.maxHealth) else Color.green
            "Mixer" -> ColorMixer.getMixedColor(0, waveSecondValue.get())
            else -> ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())!!
        }

        val preBgColor = Color(bgRedValue.get(), bgGreenValue.get(), bgBlueValue.get(), bgAlphaValue.get())

        if (fadeValue.get())
            animProgress += (0.0075F * fadeSpeed.get() * RenderUtils.deltaTime * if (actualTarget != null) -1F else 1F)
        else animProgress = 0F

        animProgress = animProgress.coerceIn(0F, 1F)

        barColor = ColorUtils.reAlpha(preBarColor, preBarColor.alpha / 255F * (1F - animProgress))
        bgColor = ColorUtils.reAlpha(preBgColor, preBgColor.alpha / 255F * (1F - animProgress))

        if (actualTarget != null || !fadeValue.get())
            mainTarget = actualTarget
        else if (animProgress >= 1F)
            mainTarget = null

        val returnBorder = mainStyle.getBorder(mainTarget) ?: return null
        val borderWidth = returnBorder.x2 - returnBorder.x
        val borderHeight = returnBorder.y2 - returnBorder.y

        if (mainTarget == null) return returnBorder
        val convertTarget = mainTarget!! as EntityPlayer
        
        val calcScaleX = animProgress * (4F / (borderWidth / 2F))
        val calcScaleY = animProgress * (4F / (borderHeight / 2F))
        val calcTranslateX = borderWidth / 2F * calcScaleX
        val calcTranslateY = borderHeight / 2F * calcScaleY

        if (blurValue.get()) {
            val floatX = renderX.toFloat()
            val floatY = renderY.toFloat()

            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            BlurUtils.blur(floatX + returnBorder.x, floatY + returnBorder.y, floatX + returnBorder.x2, floatY + returnBorder.y2, blurStrength.get() * (1F - animProgress), false) {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mainStyle.handleBlur(convertTarget)
                GL11.glPopMatrix()
            }
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        if (fadeValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
            GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        }
        
        mainStyle.drawTarget(convertTarget)

        if (fadeValue.get())
            GL11.glPopMatrix()

        GlStateManager.resetColor()
        return returnBorder
    }

    override fun handleDamage(ent: EntityPlayer) {
        getCurrentStyle(styleValue.get())?.handleDamage(ent)
    }

    fun getFadeProgress() = animProgress

    @SafeVarargs
    fun addStyles(vararg styles: TargetStyle): List<String> {
        val nameList = mutableListOf<String>()
        styles.forEach { 
            styleList.add(it) 
            nameList.add(it.name)
        }
        return nameList
    }

    fun getCurrentStyle(styleName: String): TargetStyle? = styleList.find { it.name.equals(styleName, true) }
    
}