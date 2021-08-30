/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.UiUtils
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val decimalFormat2 = DecimalFormat("##0.0", DecimalFormatSymbols(Locale.ENGLISH))
    private val decimalFormat3 = DecimalFormat("0.#", DecimalFormatSymbols(Locale.ENGLISH))
    private val styleValue = ListValue("Style", arrayOf("LiquidBounce", "Flux", "Novoline", "Slowly", "Rise"), "LiquidBounce")
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val showUrselfWhenChatOpen = BoolValue("DisplayWhenChat", true)
    private val riseParticle = BoolValue("Rise-Particle", true)
    private val riseParticleFade = BoolValue("Rise-Particle-Fade", true)
    private val riseAsync = BoolValue("Rise-FPSAsync", true)
    private val gradientAmountValue = IntegerValue("Rise-Gradient-Amount", 4, 1, 40)
    private val distanceValue = IntegerValue("Rise-Distance", 50, 1, 200)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Sky", "LiquidSlowly", "Fade", "Mixer", "Health"), "Custom")
    private val redValue = IntegerValue("Red", 252, 0, 255)
    private val greenValue = IntegerValue("Green", 96, 0, 255)
    private val blueValue = IntegerValue("Blue", 66, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    private val mixerSecondsValue = IntegerValue("Mixer-Seconds", 2, 1, 10)
    private val backgroundColorRedValue = IntegerValue("Background-Red", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 160, 0, 255)
    private val borderColorRedValue = IntegerValue("Liquid-Border-Red", 0, 0, 255)
    private val borderColorGreenValue = IntegerValue("Liquid-Border-Green", 0, 0, 255)
    private val borderColorBlueValue = IntegerValue("Liquid-Border-Blue", 0, 0, 255)
    private val borderColorAlphaValue = IntegerValue("Liquid-Border-Alpha", 0, 0, 255)

    private val shieldIcon = ResourceLocation("liquidbounce+/shield.png")

    private var easingHealth: Float = 0F
    private var lastTarget: Entity? = null

    private val particleList = mutableListOf<Particle>()

    private var gotDamaged: Boolean = false

    override fun drawElement(): Border {
        val target = if ((mc.currentScreen is GuiChat && showUrselfWhenChatOpen.get()) || mc.currentScreen is GuiHudDesigner) mc.thePlayer else (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        val barColor = when (colorModeValue.get()) {
            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (target != null && target is EntityPlayer) BlendUtils.getHealthColor(target.health, target.maxHealth) else Color.green
            "Mixer" -> ColorMixer.getMixedColor(0, mixerSecondsValue.get())
            else -> ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())!!
        }
        val bgColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())
        val borderColor = Color(borderColorRedValue.get(), borderColorGreenValue.get(), borderColorBlueValue.get(), borderColorAlphaValue.get())

        if (target is EntityPlayer) {
            when (styleValue.get()) {
                "LiquidBounce" -> {
                    if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                        abs(easingHealth - target.health) < 0.01) {
                        easingHealth = target.health
                    }

                    val width = (38 + Fonts.font40.getStringWidth(target.name))
                            .coerceAtLeast(118)
                            .toFloat()

                    // Draw rect box
                    RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, borderColor.rgb, bgColor.rgb)

                    // Damage animation
                    if (easingHealth > target.health)
                        RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                                36F, Color(252, 185, 65).rgb)

                    // Health bar
                    RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                            36F, barColor.rgb)

                    // Heal animation
                    if (easingHealth < target.health)
                        RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                                (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)

                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    Fonts.font40.drawString(target.name, 36, 3, 0xffffff)
                    Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                    if (playerInfo != null) {
                        Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                                36, 24, 0xffffff)

                        // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }

                "Flux" -> {
                    val width = (26F + Fonts.fontSFUI35.getStringWidth(target.name)).coerceAtLeast(26F + Fonts.fontSFUI35.getStringWidth("Health: ${decimalFormat2.format(target.health)}")).toFloat() + 10F
                    RenderUtils.drawRoundedRect(-1F, -1F, 1F + width, 47F, 1F, Color(35, 35, 40, 230).rgb)
                    RenderUtils.drawBorder(0F, 0F, 25F, 25F, 0.75F, Color(45, 255, 45).rgb)
                    drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 0, 0, 25, 25)
                    Fonts.fontSFUI35.drawString(target.name, 30F, 4F, 0xFFFFFF) // Draw target name
                    Fonts.fontSFUI35.drawString("Health: ${decimalFormat2.format(target.health)}", 30F, 15F, 0xFFFFFF) // Draw target health   

                    // bar icon
                    Fonts.fontSFUI35.drawString("❤", 2F, 29F, -1)
                    drawArmorIcon(2, 38, 7, 7)

                    easingHealth += ((target.health - easingHealth) / Math.pow(2.0, 10.0 - 3.0)).toFloat() * RenderUtils.deltaTime.toFloat()

                    // bar bg
                    RenderUtils.drawRect(12F, 30F, 12F + width - 15F, 33F, Color(20, 20, 20, 255).rgb)
                    RenderUtils.drawRect(12F, 40F, 12F + width - 15F, 43F, Color(20, 20, 20, 255).rgb)

                    // Health bar
                    if (easingHealth < 0 || easingHealth > target.maxHealth) {
                        easingHealth = target.health.toFloat()
                    }
                    if (easingHealth > target.health) {
                        RenderUtils.drawRect(12F, 30F, 12F + (easingHealth / target.maxHealth) * (width - 15F), 33F, Color(231, 182, 0, 255).rgb)
                    } // Damage animation

                    RenderUtils.drawRect(12F, 30F, 12F + (target.health / target.maxHealth) * (width - 15F), 33F, Color(0, 224, 84, 255).rgb)

                    if (target.getTotalArmorValue() != 0) {
                        RenderUtils.drawRect(12F, 40F, 12F + (target.getTotalArmorValue() / 20F) * (width - 15F), 43F, Color(77, 128, 255, 255).rgb) // Draw armor bar
                    }
                }

                "Novoline" -> {
                    val font = Fonts.minecraftFont
                    val fontHeight = font.FONT_HEIGHT
                    val mainColor = barColor
                    val percent = target.health.toFloat()/target.maxHealth.toFloat() * 100F
                    val nameLength = (font.getStringWidth(target.name)).coerceAtLeast(font.getStringWidth("${decimalFormat2.format(percent)}%")).toFloat() + 10F
                    val barWidth = (target.health / target.maxHealth).coerceIn(0F, target.maxHealth.toFloat()) * (nameLength - 2F)

                    RenderUtils.drawRect(-2F, -2F, 3F + nameLength + 30F, 2F + 30F, Color(24, 24, 24, 255).rgb)
                    RenderUtils.drawRect(-1F, -1F, 2F + nameLength + 30F, 1F + 30F, Color(31, 31, 31, 255).rgb)
                    drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 0, 0, 30, 30)
                    font.drawStringWithShadow(target.name, 2F + 30F, 2F, -1)
                    RenderUtils.drawRect(2F + 30F, 14F, 30F + nameLength, 24F, Color(24, 24, 24, 255).rgb)

                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    val animateThingy = (easingHealth.coerceIn(target.health, target.maxHealth) / target.maxHealth) * (nameLength - 2F)

                    if (easingHealth > target.health)
                        RenderUtils.drawRect(2F + 30F, 14F, 2F + 30F + animateThingy, 24F, mainColor.darker().rgb)
                    
                    RenderUtils.drawRect(2F + 30F, 14F, 2F + 30F + barWidth, 24F, mainColor.rgb)
                    
                    font.drawStringWithShadow("${decimalFormat2.format(percent)}%", 2F + 30F + (nameLength - 2F) / 2F - font.getStringWidth("${decimalFormat2.format(percent)}%").toFloat() / 2F, 15F, -1)
                }

                "Slowly" -> {
                    val font = Fonts.minecraftFont

                    val length = 60.coerceAtLeast(font.getStringWidth(target.name)).coerceAtLeast(font.getStringWidth("${decimalFormat2.format(target.health)} ❤")).toFloat() + 10F
                    RenderUtils.drawRect(0F, 0F, 32F + length, 36F, bgColor.rgb)
                    drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 1, 1, 30, 30)
                    font.drawStringWithShadow(target.name, 33F, 2F, -1)
                    font.drawStringWithShadow("${decimalFormat2.format(target.health)} ❤", length + 32F - 1F - font.getStringWidth("${decimalFormat2.format(target.health)} ❤").toFloat(), 22F, barColor.rgb)

                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    RenderUtils.drawRect(0F, 32F, (easingHealth / target.maxHealth.toFloat()).coerceIn(0F, target.maxHealth.toFloat()) * (length + 32F), 36F, barColor.rgb)
                }

                // without the new rise update i would never think of recreating this targethud lol
                "Rise" -> {
                    val font = Fonts.fontSFUI40
                    val name = "Name ${target.name}"
                    val info = "Distance ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}"

                    easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    val healthName = decimalFormat2.format(easingHealth).toString()

                    val length = font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F

                    RenderUtils.drawRoundedRect(0F, 0F, 10F + length, 55F, 2.5F, bgColor.rgb, true)

                    if (riseParticle.get()) {
                        if (target.hurtTime > target.maxHurtTime / 2) {
                            if (!gotDamaged) {
                                for (j in 0..8) 
                                    particleList.add(Particle(BlendUtils.blendColors(floatArrayOf(0F, 1F), arrayOf<Color>(Color.white, barColor), if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.4F, 1.0F) else 0F), RandomUtils.nextFloat(-30F, 30F), RandomUtils.nextFloat(-30F, 30F), RandomUtils.nextFloat(0.5F, 2.5F)))

                                gotDamaged = true
                            }
                        } else if (gotDamaged) {
                            gotDamaged = false
                        }

                        val deleteQueue = mutableListOf<Particle>()

                        particleList.forEach { particle ->
                            if (particle.alpha > 0F)
                                particle.render(5F + 15F, 5 + 15F, riseParticleFade.get(), riseAsync.get())
                            else
                                deleteQueue.add(particle)
                        }

                        for (p in deleteQueue)
                            particleList.remove(p)
                    }

                    val scaleHT = (target.hurtTime.toFloat() / target.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
                    if (mc.netHandler.getPlayerInfo(target.uniqueID) != null) drawHead(mc.netHandler.getPlayerInfo(target.uniqueID).locationSkin, 
                            5F + 15F * (scaleHT * 0.2F), 
                            5F + 15F * (scaleHT * 0.2F), 
                            1F - scaleHT * 0.2F, 
                            30, 30, 
                            1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F)

                    val maxHealthLength = font.getStringWidth(decimalFormat2.format(target.maxHealth).toString()).toFloat()

                    GlStateManager.resetColor()
                    font.drawString(name, 9F + 30F, 11F, -1)
                    font.drawString(info, 9F + 30F, 23F, -1)

                    val barWidth = (length - 5F - maxHealthLength) * (easingHealth / target.maxHealth.toFloat()).coerceIn(0F, 1F)

                    // no gradient: RenderUtils.drawRect(5F, 40F, 5F + barWidth, 50F, barColor.rgb)

                    when (colorModeValue.get().toLowerCase()) {
                        "custom" -> RenderUtils.drawRect(5F, 40F, 5F + barWidth, 50F, barColor.rgb)
                        "health" -> RenderUtils.drawRect(5F, 40F, 5F + barWidth, 50F, BlendUtils.getHealthColor(easingHealth, target.maxHealth).rgb) // da animation
                        else -> { //perform the for-loop gradient trick.
                            GL11.glPushMatrix()
                            GL11.glEnable(3089)
                            RenderUtils.makeScissorBox(5F + renderX.toFloat(), 40F + renderY.toFloat(), 5F + renderX.toFloat() + barWidth, 50F + renderY.toFloat())
                            for (i in 0..(gradientAmountValue.get()-1)) {
                                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                                RenderUtils.drawGradientSideways(5.0 + barStart, 40.0, 5.0 + barEnd, 50.0, 
                                when (colorModeValue.get()) {
                                    "Sky" -> RenderUtils.SkyRainbow(i * distanceValue.get(), saturationValue.get(), brightnessValue.get())
                                    "LiquidSlowly" -> ColorUtils.LiquidSlowly(System.nanoTime(), i * distanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
                                    "Mixer" -> ColorMixer.getMixedColor(i * distanceValue.get(), mixerSecondsValue.get()).rgb
                                    "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), 100).rgb
                                    else -> -1
                                },
                                when (colorModeValue.get()) {
                                    "Sky" -> RenderUtils.SkyRainbow((i + 1) * distanceValue.get(), saturationValue.get(), brightnessValue.get())
                                    "LiquidSlowly" -> ColorUtils.LiquidSlowly(System.nanoTime(), (i + 1) * distanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
                                    "Mixer" -> ColorMixer.getMixedColor((i + 1) * distanceValue.get(), mixerSecondsValue.get()).rgb
                                    "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), (i + 1) * distanceValue.get(), 100).rgb
                                    else -> -1
                                })
                            }
                            GL11.glDisable(3089)
                            GL11.glPopMatrix()
                        }
                    }

                    GlStateManager.resetColor()

                    font.drawString(healthName, 10F + barWidth, 41F, -1)
                }
            }
        } else if (target == null) {
            easingHealth = 0F
            gotDamaged = false
            particleList.clear()
        }

        lastTarget = target
        return when (styleValue.get()) {
            "LiquidBounce" -> Border(0F, 0F, 90F, 36F)
            "Flux" -> Border(0F, 0F, 90F, 46F)
            "Novoline" -> Border(-1F, -1F, 90F, 30F)
            "Slowly" -> Border(0F, 0F, 90F, 36F)
            else -> Border(0F, 0F, 90F, 55F)
        }
    }
    
    private class Particle(var color: Color, var distX: Float, var distY: Float, var radius: Float) {
        var alpha = 1F
        var progress = 0.0
        fun render(x: Float, y: Float, fade: Boolean, async: Boolean) {
            if (progress >= 1F) {
                if (fade) alpha -= 0.1F
                if (alpha < 0F) alpha = 0F
            } else
                progress += if (async) 0.1 * (1.0 - mc.timer.renderPartialTicks!!.toDouble()) else 0.025 

            if (alpha <= 0F) return

            var reColored = Color(color.red / 255.0F, color.green / 255.0F, color.blue / 255.0F, alpha)
            var easeOut = EaseUtils.easeOutQuart(progress).toFloat()

            RenderUtils.drawFilledCircle(x + distX * easeOut, y + distY * easeOut, radius, reColored)
        }
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
                64F, 64F)
    }

    private fun drawHead(skin: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
                64F, 64F)
    }

    private fun drawHead(skin: ResourceLocation, x: Float, y: Float, scale: Float, width: Int, height: Int, red: Float, green: Float, blue: Float) {
        GL11.glPushMatrix()
        GL11.glTranslatef(x, y, 0F)
        GL11.glScalef(scale, scale, scale)
        GL11.glColor4f(red.coerceIn(0F, 1F), green.coerceIn(0F, 1F), blue.coerceIn(0F, 1F), 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
                64F, 64F)
        GL11.glPopMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f)
    }

    private fun drawArmorIcon(x: Int, y: Int, width: Int, height: Int) {
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(shieldIcon, x, y, width, height)
        GlStateManager.enableAlpha()
    }
}