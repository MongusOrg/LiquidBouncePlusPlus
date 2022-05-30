/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets

import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

import org.lwjgl.opengl.GL11

abstract class TargetStyle(val name: String): MinecraftInstance() {

    var easingHealth = 0F
    val shieldIcon = ResourceLocation("liquidbounce+/shield.png")

    abstract fun drawTarget(entity: EntityPlayer, element: Target): Border?
    
    open fun updateAnim(targetHealth: Float, animSpeed: Float, instant: Boolean) {
        if (instant)
            easingHealth = targetHealth
        else
            easingHealth += ((targetHealth - easingHealth) / 2.0F.pow(10.0F - animSpeed)) * RenderUtils.deltaTime
    }

    open fun handleDamage(player: EntityPlayer) {}

    open fun handleBlur(blurAmount: Float) {}
    
    open fun handleShadow(shadowAmount: Float) {}

    fun drawHead(skin: ResourceLocation, x: Int = 2, y: Int = 2, width: Int, height: Int, alpha: Float = 1F) {
        GL11.glColor4f(1F, 1F, 1F, alpha)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
                64F, 64F)
    }

    fun drawHead(skin: ResourceLocation, x: Float, y: Float, scale: Float, width: Int, height: Int, red: Float, green: Float, blue: Float) {
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

    fun drawArmorIcon(x: Int, y: Int, width: Int, height: Int) {
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(shieldIcon, x, y, width, height)
        GlStateManager.enableAlpha()
    }

}