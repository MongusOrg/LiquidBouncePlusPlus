/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
//import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S1DPacketEntityEffect

import kotlin.concurrent.thread

@ModuleInfo(name = "AntiBan", spacedName = "Anti Ban", description = "Anti staff on BlocksMC. Automatically leaves a map if detected known staffs.", category = ModuleCategory.MISC)
class AntiBan : Module() {

    private var obStaffs = "_"
    private var detected = false
    private var timeOut = false
    private var msTimer = MSTimer()
    private var onCount = 0
    private var totalCount = 0

    override fun onInitialize() {
        thread {
            try {
                val obStaff = HttpUtils.get("http://add-my-brain.exit-scammed.repl.co/staff/")
                if (obStaff.equals("checking", true)) {
                    timeOut = true
                    println("[Staff list] still checking")
                } else {
                    obStaffs = obStaff
                    timeOut = false
                    println("[Staff list] " + obStaffs)
                }
                msTimer.reset()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread {
            while (true) {
                if (msTimer.hasTimePassed(if (timeOut) 15000L else 80000L)) {
                    val obStaff = HttpUtils.get("http://add-my-brain.exit-scammed.repl.co/staff/")
                    if (obStaff.contains("checking", true)) {
                        timeOut = true
                    } else {
                        timeOut = false
                        obStaffs = obStaff

                        var counter = HttpUtils.get("http://add-my-brain.exit-scammed.repl.co/").split("\n")
                        try {
                            onCount = counter[0].toInt()
                            totalCount = counter[1].toInt()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (state) LiquidBounce.hud.addNotification(Notification("An error has occurred while trying to collect data.", Notification.Type.ERROR))
                        }
                    }

                    println("[Staff list] ok")
                    msTimer.reset()
                }
            }
        }
    }

    override fun onEnable() {
        detected = false
    }

    @EventTarget
    fun onWorld(e: WorldEvent) {
        detected = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        if (mc.theWorld == null || mc.thePlayer == null) return

        val packet = event.packet // smart convert
        if (packet is S1DPacketEntityEffect) {
            val entity = mc.theWorld.getEntityByID(packet.entityId)
            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    LiquidBounce.hud.addNotification(Notification("Detected BlocksMC staff members. Leaving.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S14PacketEntity) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    LiquidBounce.hud.addNotification(Notification("Detected BlocksMC staff members. Leaving.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
    }

    override val tag: String
        get() = if (timeOut) "Checking" else "${onCount}/${totalCount}"
}