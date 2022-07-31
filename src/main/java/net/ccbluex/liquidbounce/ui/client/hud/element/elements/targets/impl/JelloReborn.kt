package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

class JelloReborn(inst: Target): TargetStyle("JelloReborn", inst, false) {

    override fun drawTarget(entity: EntityPlayer) {
        updateAnim(entity.health)

        val healthString = "${decimalFormat2.format(entity.health)} Health"

        // background
        RenderUtils.newDrawRect(1F, 1F, 145F, 48F, getColor(Color(82, 82, 82)).rgb)

        // health bar
        RenderUtils.newDrawRect(4F, 40F, 3.5F + (easingHealth / entity.maxHealth).coerceIn(0F, 1F) * 138F, 43F, targetInstance.barColor.rgb)

        // name
        Fonts.font35.drawString(entity.name, 41F, 12F, getColor(-1).rgb)

        // Info
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null) {
            // actual head
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, 5, 5, 32, 32, 1F - targetInstance.getFadeProgress())

            Fonts.font35.drawString(healthString, 41F, 19F, getColor(-1).rgb)
        }
    }

    override fun getBorder(entity: EntityPlayer?): Border? {
        return Border(0F, 0F, 146F, 49F)
    }
}
