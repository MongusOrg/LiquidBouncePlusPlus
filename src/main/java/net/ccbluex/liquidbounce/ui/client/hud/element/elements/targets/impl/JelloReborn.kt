/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

class JelloReborn(inst: Target): TargetStyle("JelloReborn", inst, true) {

    override fun drawTarget(entity: EntityPlayer) {
        updateAnim(entity.health)
        val healthString = "${entity.health.toInt()} Health"

        // background
        RenderUtils.newDrawRect(1F, 1F, 145F, 48F, getColor(Color(82, 82, 82)).rgb)

        // health bar
        RenderUtils.newDrawRect(4F, 40F, 3.5F + (easingHealth / entity.maxHealth).coerceIn(0F, 1F) * 138F, 43F, targetInstance.barColor.rgb)

        // name
        Fonts.fontSFUI40.drawStringWithShadow(entity.name, 41F, 12F, getColor(-1).rgb)

        // Info
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null) {
            // actual head
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, 5, 5, 32, 32, 1F - targetInstance.getFadeProgress())

            Fonts.fontSFUI40.drawStringWithShadow(healthString, 41F, 24F, getColor(-1).rgb)
        }
    }

    override fun handleBlur(entity: EntityPlayer) {
        val width = (38 + Fonts.font40.getStringWidth(entity.name))
                .coerceAtLeast(118)
                .toFloat()

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.quickDrawRect(0F, 0F, 145F, 36F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun getBorder(entity: EntityPlayer?): Border? {
        return Border(0F, 0F, 146F, 49F)
    }
}
