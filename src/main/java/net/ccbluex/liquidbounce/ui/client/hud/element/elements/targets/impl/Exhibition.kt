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
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

class Exhibition(inst: Target): TargetStyle("Exhibition", inst) {

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.fontTahoma
        val minWidth = 140F.coerceAtLeast(40F + font.getStringWidth(entity.name))

        RenderUtils.drawExhiRect(0F, 0F, minWidth, 40F)

        RenderUtils.drawRect(2.5F, 2.5F, 37.5F, 37.5F, Color(59, 59, 59).rgb)
        RenderUtils.drawRect(3F, 3F, 37F, 37F, Color(19, 19, 19).rgb)

        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(17, 35, 12, entity)

        font.drawString(entity.name, 41, 4, -1)

        val barLength = 60F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(40F, 14F, 40F + 60F, 17F, BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker().darker().darker().rgb)
        RenderUtils.drawRect(40F, 14F, 40F + barLength, 17F, BlendUtils.getHealthColor(entity.health, entity.maxHealth).rgb)

        for (i in 0..9) {
            RenderUtils.drawBorder(40F + i * 6F, 14F, 40F + (i + 1F) * 6F, 17F, 0.25F, Color.black.rgb)
        }

        GL11.glPushMatrix()
        GL11.glTranslatef(41F, 20F, 0F)
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        Fonts.minecraftFont.drawString("HP: ${entity.health.toInt()} | Dist: ${mc.thePlayer.getDistanceToEntityBox(entity).toInt()}", 0, 0, -1)
        GL11.glPopMatrix()

        GlStateManager.resetColor()

        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        

        val renderItem = mc.renderItem

        var x = 41
        var y = 21

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.getItem() == null)
                continue

            renderItem.renderItemIntoGUI(stack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)

            x += 18
        }

        val mainStack = entity.heldItem
        if (mainStack != null && mainStack.getItem() != null) {
            renderItem.renderItemIntoGUI(mainStack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
    }

    override fun getBorder(entity: EntityPlayer?): Border? {
        entity ?: return Border(0F, 0F, 140F, 40F)

        val font = Fonts.fontTahoma
        val minWidth = 140F.coerceAtLeast(40F + font.getStringWidth(entity.name))

        return Border(0F, 0F, minWidth, 40F)
    }

}