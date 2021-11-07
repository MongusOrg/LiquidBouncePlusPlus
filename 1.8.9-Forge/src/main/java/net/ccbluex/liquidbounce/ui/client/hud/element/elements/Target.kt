/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */

/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
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
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.UiUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
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
    private val styleValue = ListValue("Style", arrayOf("LiquidBounce", "Flux", "Novoline", "Slowly", "Rise", "Exhibition", "LiquidBounce+"), "LiquidBounce")
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 30F)
    private val tSlideAnim = BoolValue("TSlide-Animation", true)
    private val showUrselfWhenChatOpen = BoolValue("DisplayWhenChat", true)
    private val riseShadow = BoolValue("Rise-Shadow", true)
    private val riseParticle = BoolValue("Rise-Particle", true)
    private val riseParticleFade = BoolValue("Rise-Particle-Fade", true)
    private val gradientAmountValue = IntegerValue("Rise-Gradient-Amount", 4, 1, 40)
    private val distanceValue = IntegerValue("Rise-Distance", 50, 1, 200)
    private val riseParticleSpeed = FloatValue("Rise-ParticleSpeed", 0.05F, 0.01F, 0.2F)
    private val exhiFontValue = FontValue("Exhi-Font", Fonts.fontSFUI35)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer", "Health"), "Custom")
    private val redValue = IntegerValue("Red", 252, 0, 255)
    private val greenValue = IntegerValue("Green", 96, 0, 255)
    private val blueValue = IntegerValue("Blue", 66, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    private val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10)
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

    private var progress: Float = 0F

    private var target: EntityPlayer? = null

    override fun drawElement(): Border {
        val kaTarget = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target

        val actualTarget = if ((mc.currentScreen is GuiChat && showUrselfWhenChatOpen.get()) || mc.currentScreen is GuiHudDesigner) mc.thePlayer 
                            else if (kaTarget is EntityPlayer) kaTarget else null

        val barColor = when (colorModeValue.get()) {
            "Rainbow" -> Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0))
            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (actualTarget != null) BlendUtils.getHealthColor(actualTarget.health, actualTarget.maxHealth) else Color.green
            "Mixer" -> ColorMixer.getMixedColor(0, mixerSecondsValue.get())
            else -> ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())!!
        }
        val bgColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())
        val borderColor = Color(borderColorRedValue.get(), borderColorGreenValue.get(), borderColorBlueValue.get(), borderColorAlphaValue.get())

        progress += 0.0025F * RenderUtils.deltaTime * if (actualTarget != null) -1F else 1F

        if (progress < 0F)
            progress = 0F
        else if (progress > 1F)
            progress = 1F

        if (actualTarget == null && tSlideAnim.get()) {
            if (progress >= 1F && target != null) 
                target = null
        } else 
            target = actualTarget

        val animProgress = EaseUtils.easeInQuart(progress.toDouble())
        val tHeight = getTBorder().y2 - getTBorder().y

        if (tSlideAnim.get() && !styleValue.get().equals("rise", true)) {
            GL11.glPushMatrix()
            GL11.glTranslated(0.0, (-renderY - tHeight.toDouble()) * animProgress, 0.0)
        }

        if (target != null) {
            val convertedTarget = target!! as EntityPlayer
            when (styleValue.get()) {
                "LiquidBounce" -> {
                    if (convertedTarget != lastTarget || easingHealth < 0 || easingHealth > convertedTarget.maxHealth ||
                        abs(easingHealth - convertedTarget.health) < 0.01) {
                        easingHealth = convertedTarget.health
                    }

                    val width = (38 + Fonts.font40.getStringWidth(convertedTarget.name))
                            .coerceAtLeast(118)
                            .toFloat()

                    // Draw rect box
                    RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, borderColor.rgb, bgColor.rgb)

                    // Damage animation
                    if (easingHealth > convertedTarget.health)
                        RenderUtils.drawRect(0F, 34F, (easingHealth / convertedTarget.maxHealth) * width,
                                36F, Color(252, 185, 65).rgb)

                    // Health bar
                    RenderUtils.drawRect(0F, 34F, (convertedTarget.health / convertedTarget.maxHealth) * width,
                            36F, barColor.rgb)

                    // Heal animation
                    if (easingHealth < convertedTarget.health)
                        RenderUtils.drawRect((easingHealth / convertedTarget.maxHealth) * width, 34F,
                                (convertedTarget.health / convertedTarget.maxHealth) * width, 36F, Color(44, 201, 144).rgb)

                    easingHealth += ((convertedTarget.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    Fonts.font40.drawString(convertedTarget.name, 36, 3, 0xffffff)
                    Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(convertedTarget))}", 36, 15, 0xffffff)

                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(convertedTarget.uniqueID)
                    if (playerInfo != null) {
                        Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                                36, 24, 0xffffff)

                        // Draw head
                        val locationSkin = playerInfo.locationSkin
                        drawHead(locationSkin, 30, 30)
                    }
                }

                "Flux" -> {
                    val width = (26F + Fonts.fontSFUI35.getStringWidth(convertedTarget.name)).coerceAtLeast(26F + Fonts.fontSFUI35.getStringWidth("Health: ${decimalFormat2.format(convertedTarget.health)}")).toFloat() + 10F
                    RenderUtils.drawRoundedRect(-1F, -1F, 1F + width, 47F, 1F, Color(35, 35, 40, 230).rgb)
                    //RenderUtils.drawBorder(1F, 1F, 26F, 26F, 1F, Color(115, 255, 115).rgb)
                    drawHead(mc.netHandler.getPlayerInfo(convertedTarget.uniqueID).locationSkin, 1, 1, 26, 26)
                    Fonts.fontSFUI35.drawString(convertedTarget.name, 30F, 6F, 0xFFFFFF) // Draw convertedTarget name
                    Fonts.fontSFUI35.drawString("Health: ${decimalFormat2.format(convertedTarget.health)}", 30F, 18F, 0xFFFFFF) // Draw convertedTarget health   

                    // bar icon
                    Fonts.fontSFUI35.drawString("❤", 2F, 29F, -1)
                    drawArmorIcon(2, 38, 7, 7)

                    easingHealth += ((convertedTarget.health - easingHealth) / Math.pow(2.0, 10.0 - 3.0)).toFloat() * RenderUtils.deltaTime.toFloat()

                    // bar bg
                    RenderUtils.drawRect(12F, 30F, 12F + width - 15F, 33F, Color(20, 20, 20, 255).rgb)
                    RenderUtils.drawRect(12F, 40F, 12F + width - 15F, 43F, Color(20, 20, 20, 255).rgb)

                    // Health bar
                    if (easingHealth < 0 || easingHealth > convertedTarget.maxHealth) {
                        easingHealth = convertedTarget.health.toFloat()
                    }
                    if (easingHealth > convertedTarget.health) {
                        RenderUtils.drawRect(12F, 30F, 12F + (easingHealth / convertedTarget.maxHealth) * (width - 15F), 33F, Color(231, 182, 0, 255).rgb)
                    } // Damage animation

                    RenderUtils.drawRect(12F, 30F, 12F + (convertedTarget.health / convertedTarget.maxHealth) * (width - 15F), 33F, Color(0, 224, 84, 255).rgb)

                    if (convertedTarget.getTotalArmorValue() != 0) {
                        RenderUtils.drawRect(12F, 40F, 12F + (convertedTarget.getTotalArmorValue() / 20F) * (width - 15F), 43F, Color(77, 128, 255, 255).rgb) // Draw armor bar
                    }
                }

                "Novoline" -> {
                    val font = Fonts.minecraftFont
                    val fontHeight = font.FONT_HEIGHT
                    val mainColor = barColor
                    val percent = convertedTarget.health.toFloat()/convertedTarget.maxHealth.toFloat() * 100F
                    val nameLength = (font.getStringWidth(convertedTarget.name)).coerceAtLeast(font.getStringWidth("${decimalFormat2.format(percent)}%")).toFloat() + 10F
                    val barWidth = (convertedTarget.health / convertedTarget.maxHealth).coerceIn(0F, convertedTarget.maxHealth.toFloat()) * (nameLength - 2F)

                    RenderUtils.drawRect(-2F, -2F, 3F + nameLength + 36F, 2F + 36F, Color(24, 24, 24, 255).rgb)
                    RenderUtils.drawRect(-1F, -1F, 2F + nameLength + 36F, 1F + 36F, Color(31, 31, 31, 255).rgb)
                    drawHead(mc.netHandler.getPlayerInfo(convertedTarget.uniqueID).locationSkin, 0, 0, 36, 36)
                    font.drawStringWithShadow(convertedTarget.name, 2F + 36F + 1F, 2F, -1)
                    RenderUtils.drawRect(2F + 36F, 15F, 36F + nameLength, 25F, Color(24, 24, 24, 255).rgb)

                    easingHealth += ((convertedTarget.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    val animateThingy = (easingHealth.coerceIn(convertedTarget.health, convertedTarget.maxHealth) / convertedTarget.maxHealth) * (nameLength - 2F)

                    if (easingHealth > convertedTarget.health)
                        RenderUtils.drawRect(2F + 36F, 15F, 2F + 36F + animateThingy, 25F, mainColor.darker().rgb)
                    
                    RenderUtils.drawRect(2F + 36F, 15F, 2F + 36F + barWidth, 25F, mainColor.rgb)
                    
                    font.drawStringWithShadow("${decimalFormat2.format(percent)}%", 2F + 36F + (nameLength - 2F) / 2F - font.getStringWidth("${decimalFormat2.format(percent)}%").toFloat() / 2F, 16F, -1)
                }

                "Slowly" -> {
                    val font = Fonts.minecraftFont

                    val length = 60.coerceAtLeast(font.getStringWidth(convertedTarget.name)).coerceAtLeast(font.getStringWidth("${decimalFormat2.format(convertedTarget.health)} ❤")).toFloat() + 10F
                    RenderUtils.drawRect(0F, 0F, 32F + length, 36F, bgColor.rgb)
                    drawHead(mc.netHandler.getPlayerInfo(convertedTarget.uniqueID).locationSkin, 1, 1, 30, 30)
                    font.drawStringWithShadow(convertedTarget.name, 33F, 2F, -1)
                    font.drawStringWithShadow("${decimalFormat2.format(convertedTarget.health)} ❤", length + 32F - 1F - font.getStringWidth("${decimalFormat2.format(convertedTarget.health)} ❤").toFloat(), 22F, barColor.rgb)

                    easingHealth += ((convertedTarget.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    RenderUtils.drawRect(0F, 32F, (easingHealth / convertedTarget.maxHealth.toFloat()).coerceIn(0F, convertedTarget.maxHealth.toFloat()) * (length + 32F), 36F, barColor.rgb)
                }

                // without the new rise update i would never think of recreating this Targethud lol
                "Rise" -> {
                    val font = Fonts.fontSFUI40
                    val name = "Name ${convertedTarget.name}"
                    val info = "Distance ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(convertedTarget))} Hurt ${convertedTarget.hurtTime}"

                    easingHealth += ((convertedTarget.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    val healthName = decimalFormat2.format(easingHealth).toString()

                    val length = font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F

                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()

                    if (blurValue.get()) {
                        GL11.glTranslated(-renderX, -renderY, 0.0)
                        GL11.glPushMatrix()
                        BlurUtils.blurAreaRounded(floatX, floatY, floatX + 10F + length, floatY + 55F, 3F, blurStrength.get())
                        GL11.glPopMatrix()
                        GL11.glTranslated(renderX, renderY, 0.0)
                    }

                    if (riseShadow.get()) 
                        UiUtils.shadowRoundedRect(0F, 0F, 10F + length, 55F, 3F, 4.5F, 0.75F, bgColor)
                    else
                        RenderUtils.drawRoundedRect(0F, 0F, 10F + length, 55F, 3F, bgColor.rgb)

                    if (riseParticle.get()) {
                        if (convertedTarget.hurtTime > convertedTarget.maxHurtTime / 2) {
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
                                particle.render(5F + 15F, 5 + 15F, riseParticleFade.get(), riseParticleSpeed.get())
                            else
                                deleteQueue.add(particle)
                        }

                        for (p in deleteQueue)
                            particleList.remove(p)
                    }

                    val scaleHT = (convertedTarget.hurtTime.toFloat() / convertedTarget.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
                    if (mc.netHandler.getPlayerInfo(convertedTarget.uniqueID) != null) drawHead(mc.netHandler.getPlayerInfo(convertedTarget.uniqueID).locationSkin, 
                            5F + 15F * (scaleHT * 0.2F), 
                            5F + 15F * (scaleHT * 0.2F), 
                            1F - scaleHT * 0.2F, 
                            30, 30, 
                            1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F)

                    val maxHealthLength = font.getStringWidth(decimalFormat2.format(convertedTarget.maxHealth).toString()).toFloat()

                    GlStateManager.resetColor()
                    font.drawString(name, 9F + 30F, 11F, -1)
                    font.drawString(info, 9F + 30F, 23F, -1)

                    val barWidth = (length - 5F - maxHealthLength) * (easingHealth / convertedTarget.maxHealth.toFloat()).coerceIn(0F, 1F)

                    // no gradient: RenderUtils.drawRect(5F, 40F, 5F + barWidth, 50F, barColor.rgb)

                    when (colorModeValue.get().toLowerCase()) {
                        "custom" -> RenderUtils.drawRect(5F, 41F, 5F + barWidth, 49F, barColor.rgb)
                        "health" -> RenderUtils.drawRect(5F, 41F, 5F + barWidth, 49F, BlendUtils.getHealthColor(easingHealth, convertedTarget.maxHealth).rgb) // da animation
                        else -> { //perform the for-loop gradient trick.
                            GL11.glPushMatrix()
                            GL11.glScalef(1f, 1f, 1f)
                            RenderUtils.makeScissorBox(5F * scale + renderX.toFloat() * scale, 0F, 5F * scale + renderX.toFloat() * scale + barWidth * scale, 49F * scale + renderY.toFloat() * scale)
                            GL11.glEnable(3089)
                            GL11.glScalef(scale, scale, scale)
                            for (i in 0..(gradientAmountValue.get()-1)) {
                                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                                RenderUtils.drawGradientSideways(5.0 + barStart, 41.0, 5.0 + barEnd, 49.0, 
                                when (colorModeValue.get()) {
                                    "Rainbow" -> RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), i * distanceValue.get())
                                    "Sky" -> RenderUtils.SkyRainbow(i * distanceValue.get(), saturationValue.get(), brightnessValue.get())
                                    "LiquidSlowly" -> ColorUtils.LiquidSlowly(System.nanoTime(), i * distanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
                                    "Mixer" -> ColorMixer.getMixedColor(i * distanceValue.get(), mixerSecondsValue.get()).rgb
                                    "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), 100).rgb
                                    else -> -1
                                },
                                when (colorModeValue.get()) {
                                    "Rainbow" -> RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), (i + 1) * distanceValue.get())
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

                "Exhibition" -> {
                    val font = exhiFontValue.get()
                    val minWidth = 140F.coerceAtLeast(45F + font.getStringWidth(convertedTarget.name))

                    RenderUtils.drawExhiRect(0F, 0F, minWidth, 45F)

                    RenderUtils.drawRect(2.5F, 2.5F, 42.5F, 42.5F, Color(59, 59, 59).rgb)
                    RenderUtils.drawRect(3F, 3F, 42F, 42F, Color(19, 19, 19).rgb)

                    GL11.glColor4f(1f, 1f, 1f, 1f)
                    RenderUtils.drawEntityOnScreen(22, 40, 15, convertedTarget)

                    font.drawString(convertedTarget.name, 46, 4, -1)

                    val barLength = 60F * (convertedTarget.health / convertedTarget.maxHealth).coerceIn(0F, 1F)
                    RenderUtils.drawRect(45F, 14F, 45F + 60F, 17F, BlendUtils.getHealthColor(convertedTarget.health, convertedTarget.maxHealth).darker().darker().darker().rgb)
                    RenderUtils.drawRect(45F, 14F, 45F + barLength, 17F, BlendUtils.getHealthColor(convertedTarget.health, convertedTarget.maxHealth).rgb)

                    for (i in 0..9) {
                        RenderUtils.drawBorder(45F + i * 6F, 14F, 45F + (i + 1F) * 6F, 17F, 0.25F, Color.black.rgb)
                    }

                    GL11.glPushMatrix()
                    GL11.glTranslatef(46F, 20F, 0F)
                    GL11.glScalef(0.5f, 0.5f, 0.5f)
                    Fonts.minecraftFont.drawString("HP: ${convertedTarget.health.toInt()} | Dist: ${mc.thePlayer.getDistanceToEntityBox(convertedTarget).toInt()}", 0, 0, -1)
                    GL11.glPopMatrix()

                    GlStateManager.resetColor()

                    GL11.glPushMatrix()
                    GL11.glColor4f(1f, 1f, 1f, 1f)
                    RenderHelper.enableGUIStandardItemLighting()

                    val renderItem = mc.renderItem

                    var x = 45
                    var y = 26

                    for (index in 3 downTo 0) {
                        val stack = convertedTarget.inventory.armorInventory[index] ?: continue

                        if (stack.getItem() == null)
                            continue

                        renderItem.renderItemIntoGUI(stack, x, y)
                        renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)

                        x += 18
                    }

                    val mainStack = convertedTarget.heldItem
                    if (mainStack != null && mainStack.getItem() != null) {
                        renderItem.renderItemIntoGUI(mainStack, x, y)
                        renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
                    }

                    RenderHelper.disableStandardItemLighting()
                    GlStateManager.enableAlpha()
                    GlStateManager.disableBlend()
                    GlStateManager.disableLighting()
                    GlStateManager.disableCull()
                    GL11.glPopMatrix()
                }

                "LiquidBounce+" -> {
                    if (convertedTarget != lastTarget || easingHealth < 0 || easingHealth > convertedTarget.maxHealth ||
                        abs(easingHealth - convertedTarget.health) < 0.01) {
                        easingHealth = convertedTarget.health
                    }

                    val width = (38 + Fonts.font40.getStringWidth(convertedTarget.name))
                            .coerceAtLeast(120)
                            .toFloat()

                    // Draw rect box
                    RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, borderColor.rgb, bgColor.rgb)

                    // Damage animation
                    if (easingHealth > convertedTarget.health)
                        RenderUtils.drawRect(0F, 34F, (easingHealth / convertedTarget.maxHealth) * width,
                                36F, Color(252, 185, 65).rgb)

                    // Health bar
                    RenderUtils.drawRect(0F, 34F, (convertedTarget.health / convertedTarget.maxHealth) * width,
                            36F, barColor.rgb)

                    easingHealth += ((convertedTarget.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

                    Fonts.font40.drawString(convertedTarget.name, 36, 3, 0xffffff)
                    Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(convertedTarget))}", 36, 15, 0xffffff)

                    // Draw info
                    val playerInfo = mc.netHandler.getPlayerInfo(convertedTarget.uniqueID)
                    if (playerInfo != null) {
                        Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                                36, 24, 0xffffff)

                        // Draw head
                        val locationSkin = playerInfo.locationSkin

                        val scaleHT = (convertedTarget.hurtTime.toFloat() / convertedTarget.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
                        drawHead(locationSkin, 
                            2F + 15F * (scaleHT * 0.2F), 
                            2F + 15F * (scaleHT * 0.2F), 
                            1F - scaleHT * 0.2F, 
                            30, 30, 
                            1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F)
                    }
                }
            }
        } else if (target == null) {
            easingHealth = 0F
            gotDamaged = false
            particleList.clear()
        }

        if (tSlideAnim.get() && !styleValue.get().equals("rise", true))
            GL11.glPopMatrix()
            
        GlStateManager.resetColor()

        lastTarget = target
        return getTBorder()
    }

    private fun getTBorder(): Border = when (styleValue.get()) {
            "LiquidBounce" -> Border(0F, 0F, 90F, 36F)
            "Flux" -> Border(0F, -1F, 90F, 47F)
            "Novoline" -> Border(-1F, -2F, 90F, 38F)
            "Slowly" -> Border(0F, 0F, 90F, 36F)
            "Rise" -> Border(0F, 0F, 90F, 55F)
            "Exhibition" -> Border(0F, 3F, 140F, 48F)
            else -> Border(0F, 0F, 120F, 38F)
        }
    
    private class Particle(var color: Color, var distX: Float, var distY: Float, var radius: Float) {
        var alpha = 1F
        var progress = 0.0
        fun render(x: Float, y: Float, fade: Boolean, speed: Float) {
            if (progress >= 1.0) {
                progress = 1.0
                if (fade) alpha -= 0.1F
                if (alpha < 0F) alpha = 0F
            } else
                progress += speed.toDouble()

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