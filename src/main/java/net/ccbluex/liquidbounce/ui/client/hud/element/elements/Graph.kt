/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.sqrt
import java.lang.Math.pow

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Graph")
class Graph(x: Double = 75.0, y: Double = 110.0, scale: Float = 1F,
                 side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    // general
    private val graphValue = ListValue("Graph-Value", arrayOf("Speed", "BPS", "Packet-In", "Packet-Out"), "Speed")
    private val updateDelay = IntegerValue("Update-Delay", 1000, 0, 5000)
    private val xMultiplier = FloatValue("xMultiplier", 7F, 1F, 20F)
    private val yMultiplier = FloatValue("yMultiplier", 7F, 0.1F, 20F)
    private val maxGraphValues = IntegerValue("MaxGraphValues", 150, 100, 300)
    private val maxHeight = FloatValue("MaxHeight", 50F, 30F, 150F)
    private val thickness = FloatValue("Thickness", 2F, 1F, 3F)

    private val fontValue = FontValue("Font", Fonts.minecraftFont)

    // average settings
    private val showAverageLine = BoolValue("Show-Average", true)
    private val averageLayer = ListValue("Average-Layer", arrayOf("Top", "Bottom"), "Bottom")
    private val avgUpdateDelay = IntegerValue("Average-Update-Delay", 1000, 0, 5000)

    // bg color
    private val bgredValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bggreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bgblueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val bgalphaValue = IntegerValue("Background-Alpha", 120, 0, 255)
    private val bordredValue = IntegerValue("Border-Red", 255, 0, 255)
    private val bordgreenValue = IntegerValue("Border-Green", 255, 0, 255)
    private val bordblueValue = IntegerValue("Border-Blue", 255, 0, 255)
    private val bordalpha = IntegerValue("Border-Alpha", 255, 0, 255)
    private val bordRad = FloatValue("Border-Width", 3F, 0F, 10F)

	private val valueStore = arrayListOf<Float>()
	private val timer = MSTimer()
    private val avgtimer = MSTimer()
	private var averageNumber = 0F

    private var lastX = 0.0
    private var lastZ = 0.0
    private var speedVal = 0F

    private var lastValue = ""

    override fun updateElement() {
        if (mc.thePlayer == null) return
        speedVal = sqrt(pow(lastX - mc.thePlayer.posX, 2.0) + pow(lastZ - mc.thePlayer.posZ, 2.0)).toFloat() * 20F * mc.timer.timerSpeed
        lastX = mc.thePlayer.posX
        lastZ = mc.thePlayer.posZ
    }

    override fun drawElement(): Border {
        val font = fontValue.get()
        val defaultX = 0F

        if (mc.thePlayer == null || lastValue != graphValue.get()) {
			valueStore.clear()
			averageNumber = 0F
		}

        lastValue = graphValue.get()

        if (timer.hasTimePassed(updateDelay.get().toLong())) {
            when (graphValue.get().toLowerCase()) {
                "speed" -> valueStore.add(MovementUtils.getSpeed() * 10F)
                "bps" -> valueStore.add(speedVal)
                "packet-in" -> valueStore.add(PacketUtils.avgInBound.toFloat())
                "packet-out" -> valueStore.add(PacketUtils.avgOutBound.toFloat())
            }
            while (valueStore.size > maxGraphValues.get()) 
			    valueStore.remove(0)
            timer.reset()
        }
		
		if (avgtimer.hasTimePassed(avgUpdateDelay.get().toLong())) {
			averageNumber = (averageNumber + valueStore[valueStore.size - 1]) / 2F
			avgtimer.reset()
		}

        if (bgalphaValue.get() > 0F) {
            // draw background
            val bgColor = Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()).rgb
            val borderColor = Color(bordredValue.get(), bordgreenValue.get(), bordblueValue.get(), bordalpha.get()).rgb
            RenderUtils.drawBorderedRect(-2F, -1F, maxGraphValues.get() * xMultiplier.get() + 2F, maxHeight.get() + 1F, bordRad.get(), borderColor, bgColor)
        }

		val avgheight = Math.min(averageNumber * yMultiplier.get(), maxHeight.get())
		val firstheight = Math.min(valueStore[valueStore.size - 1] * yMultiplier.get(), maxHeight.get())
		val working = String.format("%.2f", valueStore[valueStore.size - 1])
		val average = String.format("%.2f", averageNumber)

		if (showAverageLine.get()) font.drawStringWithShadow(average, defaultX - font.getStringWidth(working) - 5F, maxHeight.get() - avgheight - font.FONT_HEIGHT / 2F, Color(0.1F, 1F, 0.1F).rgb)

		GlStateManager.pushMatrix()
		GlStateManager.enableBlend()
		GlStateManager.disableTexture2D()
		GL11.glEnable(GL11.GL_LINE_SMOOTH)
		GL11.glLineWidth(thickness.get())
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
		GlStateManager.color(1F, 1F, 1F, 1F)
		val tessellator = Tessellator.getInstance()
		val worldRenderer = tessellator.getWorldRenderer()
        if (showAverageLine.get() && averageLayer.get().equals("bottom", true)) {
            GlStateManager.color(0.1F, 1F, 0.1F, 1F)
		    worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
		    worldRenderer.pos(100.0, (maxHeight.get() - avgheight).toDouble(), 0.0).endVertex()
		    worldRenderer.pos((defaultX - xMultiplier.get()).toDouble(), (maxHeight.get() - avgheight).toDouble(), 0.0).endVertex()
		    tessellator.draw()
        }
		worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
		for (valu in valueStore) {
			val height = Math.min(valu * yMultiplier.get(), maxHeight.get())
			worldRenderer.pos(defaultX.toDouble(), (maxHeight.get() - height).toDouble(), 0.0).endVertex()
			defaultX += xMultiplier.get()
		}
		tessellator.draw()
        if (showAverageLine.get() && averageLayer.get().equals("top", true)) {
            GlStateManager.color(0.1F, 1F, 0.1F, 1F)
		    worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
		    worldRenderer.pos(100.0, (maxHeight.get() - avgheight).toDouble(), 0.0).endVertex()
		    worldRenderer.pos((defaultX - xMultiplier.get()).toDouble(), (maxHeight.get() - avgheight).toDouble(), 0.0).endVertex()
		    tessellator.draw()
        }
		GL11.glDisable(GL11.GL_LINE_SMOOTH)
		GlStateManager.enableTexture2D()
		GlStateManager.disableBlend()
		GlStateManager.popMatrix()

		font.drawStringWithShadow(working, defaultX - xMultiplier.get() + 5F, maxHeight.get() - firstheight - font.FONT_HEIGHT / 2F, -1)

        return Border(0F, 0F, maxGraphValues.get() * xMultiplier.get(), maxHeight.get() + 2F)
    }
}