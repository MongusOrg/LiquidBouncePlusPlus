/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11

import java.awt.Color

class Rise: TargetStyle("Rise") {

    override fun drawTarget(entity: EntityPlayer, element: Target) {
        
    }

    private class Particle(var color: Color, var distX: Float, var distY: Float, var radius: Float) {
        var alpha = 1F
        var progress = 0.0
        fun render(x: Float, y: Float, fade: Boolean, speed: Float, fadeSpeed: Float) {
            if (progress >= 1.0) {
                progress = 1.0
                if (fade) alpha -= (fadeSpeed * 0.02F * RenderUtils.deltaTime)
                if (alpha < 0F) alpha = 0F
            } else
                progress += (speed * 0.025F * RenderUtils.deltaTime).toDouble()

            if (alpha <= 0F) return

            var reColored = Color(color.red / 255.0F, color.green / 255.0F, color.blue / 255.0F, alpha)
            var easeOut = EaseUtils.easeOutQuart(progress).toFloat()

            RenderUtils.drawFilledCircle(x + distX * easeOut, y + distY * easeOut, radius, reColored)
        }
    }

}