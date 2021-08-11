/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */
@ElementInfo(name = "Inventory")
class Inventory(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private var inventoryRows = 0
    private val lowerInv: IInventory? = null
    private val Mode = ListValue("Background-Mode", arrayOf("Bordered", "Rounded"), "Bordered")
    private val width = IntegerValue("BorderWidth", 1, 0, 10)
    private val redValue = IntegerValue("Red", 0, 0, 255)
    private val greenValue = IntegerValue("Green", 0, 0, 255)
    private val blueValue = IntegerValue("Blue", 0, 0, 255)
    private val alpha = IntegerValue("Alpha", 120, 0, 255)
    private val bordredValue = IntegerValue("BorderRed", 255, 0, 255)
    private val bordgreenValue = IntegerValue("BorderGreen", 255, 0, 255)
    private val bordblueValue = IntegerValue("BorderBlue", 255, 0, 255)
    private val bordalpha = IntegerValue("BorderAlpha", 255, 0, 255)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        if (Mode.get() == "Rounded") {
            drawRoundedRect(0F, this.inventoryRows * 18F + 17F, 176F, 96F, Color(bordredValue.get(), bordgreenValue.get(), bordblueValue.get(), bordalpha.get()).rgb, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
        }
        if (Mode.get() == "Bordered") {
            RenderUtils.drawBorderedRect(0F, this.inventoryRows * 18F + 17F, 176F, 96F, width.get().toFloat(), Color(bordredValue.get(), bordgreenValue.get(), bordblueValue.get(), bordalpha.get()).rgb, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
        }
        if (lowerInv != null) {
            this.inventoryRows = lowerInv.getSizeInventory()
        }
        renderInventory1(mc.thePlayer)
        renderInventory2(mc.thePlayer)
        renderInventory3(mc.thePlayer)
        return Border(0F, this.inventoryRows * 18F + 17F, 176F, 96F)
    }

    private fun renderInventory1(player: EntityPlayer) {
        var armourStack: ItemStack?
        var renderStack = player.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 9..17) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 30)
            xOffset += 18
        }
    }

    private fun renderInventory2(player: EntityPlayer) {
        var armourStack: ItemStack?
        var renderStack = player.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 18..26) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 48)
            xOffset += 18
        }
    }

    private fun renderInventory3(player: EntityPlayer) {
        var armourStack: ItemStack?
        var renderStack = player.inventory.mainInventory
        var xOffset = 8
        renderStack = player.inventory.mainInventory
        for (index in 27..35) {
            armourStack = renderStack[index]
            if (armourStack != null) this.renderItemStack(armourStack, xOffset, 66)
            xOffset += 18
        }
    }

    private fun renderItemStack(stack: ItemStack, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun drawRect(x: Float, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x.toDouble(), y)
        GL11.glVertex2d(x.toDouble(), y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f
        GlStateManager.color(red, green, blue, alpha)
    }
    fun rectangleBordered(x: Double, y: Double, x1: Double, y1: Double, width: Double, internalColor: Int,
                          borderColor: Int) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        rectangle(x + width, y, x1 - width, y + width, borderColor)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        rectangle(x, y, x + width, y1, borderColor)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        rectangle(x1 - width, y, x1, y1, borderColor)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }
    fun rectangle(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x, y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }
    fun enableGL2D() {
        GL11.glDisable(2929)
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glDepthMask(true)
        GL11.glEnable(2848)
        GL11.glHint(3154, 4354)
        GL11.glHint(3155, 4354)
    }

    fun disableGL2D() {
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glEnable(2929)
        GL11.glDisable(2848)
        GL11.glHint(3154, 4352)
        GL11.glHint(3155, 4352)
    }
    fun drawRoundedRect(x: Float, y: Float, x1: Float, y1: Float, borderC: Int, insideC: Int) {
        var x = x
        var y = y
        var x1 = x1
        var y1 = y1
        enableGL2D()
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        drawVLine(2.0f.let { x *= it; x }, 2.0f.let { y *= it; y } + 1.0f, 2.0f.let { y1 *= it; y1 } - 2.0f, borderC)
        drawVLine(2.0f.let { x1 *= it; x1 } - 1.0f, y + 1.0f, y1 - 2.0f, borderC)
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC)
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC)
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC)
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC)
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC)
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC)
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC)
        GL11.glScalef(2.0f, 2.0f, 2.0f)
        disableGL2D()
        Gui.drawRect(0, 0, 0, 0, 0)
    }
    fun drawHLine(par1: Float, par2: Float, par3: Float, par4: Int) {
        var par1 = par1
        var par2 = par2
        if (par2 < par1) {
            val var5 = par1
            par1 = par2
            par2 = var5
        }
        RenderUtils.drawRect(par1, par3, par2 + 1.0f, par3 + 1.0f, par4)
    }

    fun drawVLine(x: Float, y: Float, x1: Float, y1: Int) {
        var y = y
        var x1 = x1
        if (x1 < y) {
            val var5 = y
            y = x1
            x1 = var5
        }
        RenderUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1)
    }
}