/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

import kotlin.math.abs
import kotlin.math.pow

class LiquidBounce: TargetStyle("LiquidBounce") {

    private var lastTarget: EntityPlayer? = null

    override fun drawTarget(entity: EntityPlayer, element: Target) {
        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01) {
            easingHealth = entity.health
        }

        val width = (38 + Fonts.font40.getStringWidth(entity.name))
                .coerceAtLeast(118)
                .toFloat()

        // Draw rect box
        //RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, borderColor.rgb, element.bgColor.rgb)
        RenderUtils.drawRect(0F, 0F, width, 36F, element.bgColor.rgb)

        // Damage animation
        if (easingHealth > entity.health)
            RenderUtils.drawRect(0F, 34F, (easingHealth / entity.maxHealth) * width,
                    36F, getColor(Color(252, 185, 65), element).rgb)

        // Health bar
        RenderUtils.drawRect(0F, 34F, (entity.health / entity.maxHealth) * width,
                36F, element.barColor.rgb)

        // Heal animation
        if (easingHealth < entity.health)
            RenderUtils.drawRect((easingHealth / entity.maxHealth) * width, 34F,
                    (entity.health / entity.maxHealth) * width, 36F, getColor(Color(44, 201, 144), element).rgb)

        updateAnim(entity.health, element.globalAnimSpeed.get(), element.noAnimValue.get())

        Fonts.font40.drawString(entity.name, 36, 3, getColor(-1, element).rgb)
        Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(entity))}", 36, 15, getColor(-1, element).rgb)

        // Draw info
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)
        if (playerInfo != null) {
            Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                    36, 24, getColor(-1, element).rgb)

            // Draw head
            val locationSkin = playerInfo.locationSkin
            drawHead(skin = locationSkin, width = 30, height = 30, alpha = 1F - element.getFadeProgress())
        }

        lastTarget = entity
    }

    override fun handleBlur(entity: EntityPlayer) {
        val width = (38 + Fonts.font40.getStringWidth(entity.name))
                        .coerceAtLeast(118)
                        .toFloat()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(0F, 0F, width, 36F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun getBorder(entity: EntityPlayer?, element: Target): Border? {
        entity ?: return Border(0F, 0F, 118F, 36F)
        val width = (38 + Fonts.font40.getStringWidth(entity.name))
                        .coerceAtLeast(118)
                        .toFloat()
        return Border(0F, 0F, width, 36F)
    }

}