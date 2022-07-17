package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.element.components

import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.ColorManager
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.util.MathHelper

import java.awt.Color

class Slider(var accentColor: Color = Color(0, 140, 255)) {
    private var smooth = 0F
    private var value = 0F

    fun onDraw(x: Float, y: Float, width: Float) {
        smooth = AnimationUtils.animate(value, smooth, 0.5F * RenderUtils.deltaTime * 0.0075F)
        RenderUtils.originalRoundedRect(x - 1F, y - 1F, x + width * (smooth / 100F) + 1F, y + 1F, 1F, this.accentColor.rgb)
        RenderUtils.drawFilledCircle(x + width * (smooth / 100F), y, 5F, Color.white)
        RenderUtils.drawFilledCircle(x + width * (smooth / 100F), y, 3F, ColorManager.background)
    }

    fun setValue(desired: Float, min: Float, max: Float) {
        value = (desired - min) / (max - min) * 100F
    }
}
