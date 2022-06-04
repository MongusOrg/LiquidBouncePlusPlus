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
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

class Exhibition(inst: Target): TargetStyle("Exhibition", inst) {

    override fun drawTarget(entity: EntityPlayer) {
        val font = Fonts.fontTahoma
        val minWidth = 140F.coerceAtLeast(55F + font.getStringWidth(entity.name))

        RenderUtils.drawExhiRect(0F, 0F, minWidth, 45F, 1F - targetInstance.getFadeProgress())

        RenderUtils.drawRect(2.5F, 2.5F, 42.5F, 42.5F, getColor(Color(59, 59, 59)).rgb)
        RenderUtils.drawRect(3F, 3F, 42F, 42F, getColor(Color(19, 19, 19)).rgb)

        GL11.glColor4f(1f, 1f, 1f, 1f - targetInstance.getFadeProgress())
        RenderUtils.drawEntityOnScreen(22, 40, 15, entity)

        font.drawString(entity.name, 46, 5, getColor(-1).rgb)

        val barLength = 60F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(45F, 16F, 45F + 60F, 19F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker().darker().darker()).rgb)
        RenderUtils.drawRect(45F, 16F, 45F + barLength, 19F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth)).rgb)

        for (i in 0..9) {
            GL11.glPushMatrix()
            GL11.glTranslatef(45F + i * 6F, 16F, 0F)
            GL11.glScalef(0.5F, 0.5F, 0.5F)
            RenderUtils.drawBorder(0F, 0F, 12F, 6F, 0.25F, getColor(Color.black).rgb)
            GL11.glPopMatrix()
        }

        GL11.glPushMatrix()
        GL11.glTranslatef(46F, 21F, 0F)
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        Fonts.minecraftFont.drawString("HP: ${entity.health.toInt()} | Dist: ${mc.thePlayer.getDistanceToEntityBox(entity).toInt()}", 0, 0, getColor(-1).rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()

        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - targetInstance.getFadeProgress())
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        

        val renderItem = mc.renderItem

        var x = 45
        var y = 26

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
        entity ?: return Border(0F, 0F, 140F, 45F)

        val font = Fonts.fontTahoma
        val minWidth = 140F.coerceAtLeast(55F + font.getStringWidth(entity.name))

        return Border(0F, 0F, minWidth, 45F)
    }

}