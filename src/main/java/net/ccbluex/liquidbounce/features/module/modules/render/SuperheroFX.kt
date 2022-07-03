/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EntityDamageEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal
import java.util.*
import kotlin.math.abs

@ModuleInfo(name = "SuperheroFX", spacedName = "Superhero FX", description = "Creates comic-like words as flying particles.", category = ModuleCategory.RENDER)
class SuperheroFX : Module() {

    private val debugValue = BoolValue("Debug", false)

    private val textParticles = mutableListOf<FXParticle>()
    private val generateTimer = MSTimer()

    @EventTarget
    fun onWorld(event: WorldEvent) = textParticles.clear()

    @EventTarget
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.damagedEntity
        if (mc.theWorld.loadedEntityList.contains(entity) && generateTimer.hasTimePassed(200L)) {
            generateTimer.reset()
            ClientUtils.displayChatMessage("added particle")
            textParticles.add(
                FXParticle(
                    entity.posX - 0.5 + Random(System.currentTimeMillis()).nextInt(5).toDouble() * 0.1,
                    entity.entityBoundingBox.minY + (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) / 2.0,
                    entity.posZ - 0.5 + Random(System.currentTimeMillis() + 1L).nextInt(5).toDouble() * 0.1
                )
            )
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val removeList = mutableListOf<FXParticle>()
        for (particle in textParticles) {
            if (particle.canRemove) {
                ClientUtils.displayChatMessage("removed")
                removeList.add(particle)
                continue
            }
            ClientUtils.displayChatMessage("drawn")
            particle.draw()
        }
        textParticles.removeAll(removeList)
    }

}
class FXParticle(val posX: Double, val posY: Double, val posZ: Double): MinecraftInstance() {
    private val messageString: String = listOf("kaboom", "bam", "zap", "smash", "fatality", "kapow", "wham").random()
    private val color: Color = Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255))

    private val fadeTimer = MSTimer()
    private val stringLength = Fonts.fontBangers.getStringWidth(messageString).toDouble()
    private val fontHeight = Fonts.fontBangers.FONT_HEIGHT.toDouble()

    var canRemove = false
    private var firstDraw = true

    fun draw() {
        val renderManager = mc.renderManager ?: return
        if (firstDraw) {
            fadeTimer.reset()
            firstDraw = false
        }
        val alpha = (if (fadeTimer.hasTimePassed(250L)) fadeTimer.hasTimeLeft(500L) else 250L - fadeTimer.hasTimeLeft(250L)).toFloat().coerceIn(0F, 250F) / 250F
        val progress = (if (fadeTimer.hasTimePassed(250L)) abs(fadeTimer.hasTimeLeft(250L) - 250L) else 250L - fadeTimer.hasTimeLeft(250L)).toFloat().coerceIn(0F, 500F) / 250F
        val textY = if (mc.gameSettings.thirdPersonView != 2) -1.0f else 1.0f
        if (progress >= 2F) {
            canRemove = true
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.enablePolygonOffset()
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f)
        GL11.glTranslated(posX - renderManager.renderPosX, posY - renderManager.renderPosY, posZ - renderManager.renderPosZ)
        GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GL11.glScalef(progress * -0.03F, progress * -0.03F, progress * 0.03F)
        GlStateManager.rotate(textY * renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GL11.glDepthMask(false)
        Fonts.fontBangers.drawString(messageString, 0F, 0F, ColorUtils.reAlpha(color, alpha).rgb)
        GL11.glColor4f(187.0f, 255.0f, 255.0f, 1.0f)
        GL11.glDepthMask(true)
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f)
        GlStateManager.disablePolygonOffset()
        GlStateManager.popMatrix()
    }
}