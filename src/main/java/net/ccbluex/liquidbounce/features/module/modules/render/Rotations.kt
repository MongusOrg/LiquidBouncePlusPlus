/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.BowAimbot
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.exploit.Disabler
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.RENDER)
class Rotations : Module() {

    val modeValue = ListValue("Mode", arrayOf("Chams", "Head", "Body"), "Chams")

    private var playerYaw: Float? = null
    private var noEvent: Boolean = false

    private lateinit var fakePlayer: EntityOtherPlayerMP?

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null) {
            if (modeValue.get().equals("head", true))
                mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
            
            if (modeValue.get().equals("chams", true) && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.getGameProfile() != null) {
                if (fakePlayer == null) {
                    fakePlayer = EntityOtherPlayerMP(mc.thePlayer, mc.thePlayer.getGameProfile())
                    fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer)
                    fakePlayer.rotationYaw = RotationUtils.serverRotation.yaw
                    fakePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
                    fakePlayer.renderYawOffset = fakePlayer.rotationYawHead
                    fakePlayer.rotationPitch = RotationUtils.serverRotation.pitch
                    mc.theWorld.addEntityToWorld(-72749, fakePlayer)
                }
                mc.getRenderManager().renderEntityStatic(fakePlayer!!, event.partialTicks, true)
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        fakePlayer = null
    }

    @EventTarget(priority = 1)
    fun onPacket(event: PacketEvent) {
        if (!modeValue.get().equals("body", true) || !shouldRotate() || mc.thePlayer == null)
            return

        val packet = event.packet
        if (packet is C03PacketPlayer.C06PacketPlayerPosLook || packet is C03PacketPlayer.C05PacketPlayerLook) {
            playerYaw = (packet as C03PacketPlayer).yaw
            mc.thePlayer.renderYawOffset = packet.getYaw()
            mc.thePlayer.rotationYawHead = packet.getYaw()
        } else {
            if (playerYaw != null)
                mc.thePlayer.renderYawOffset = this.playerYaw!!
            mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset
        }
    }

    private fun getState(module: Class<*>) = LiquidBounce.moduleManager[module]!!.state

    private fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        val disabler = LiquidBounce.moduleManager.getModule(Disabler::class.java)!! as Disabler
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Disabler::class.java) && disabler.canRenderInto3D) ||
                getState(BowAimbot::class.java) || getState(Fucker::class.java) ||
                getState(ChestAura::class.java) || getState(Fly::class.java)
    }
}
