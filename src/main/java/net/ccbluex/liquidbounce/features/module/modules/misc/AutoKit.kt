/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.event.ClickEvent
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.IChatComponent
import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "Auto Kit", spacedName = "Auto Kit", description = "Automatically selects kits for you in BlocksMC Skywars.", category = ModuleCategory.MISC)
class AutoKit : Module() {

    private val kitNameValue = TextValue("Kit-Name", "Armorer")

    // for easier selection
    private val editMode = BoolValue("Edit-Mode", false)
    private val debugValue = BoolValue("Debug", false)

    private var clickStage = 0

    private var availableForSelect = false
    private var expectSlot = -1

    private var timeoutTimer = TickTimer()
    private var delayTimer = MSTimer()

    private fun debug(s: String) {
        if (debugValue.get()) ClientUtils.displayChatMessage("§7[§4§lAuto Knight§7] §r$s")
    }

    override fun onEnable() {
        clickStage = 0
        availableForSelect = false
        expectSlot = -1

        timeoutTimer.reset()
        delayTimer.reset()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (clickStage == 1 && delayTimer.hasTimePassed(1000L)) { // minimum requirement in case of duplicated s2f packets
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(expectSlot - 36))
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(expectSlot).getStack()))
            clickStage = 2
            delayTimer.reset()
            debug("clicked kit selector")
        } else {
            delayTimer.reset()
        }

        if (clickStage == 2) {
            timeoutTimer.update()
            if (timeoutTimer.hasTimePassed(40)) {
                // close the things and notify
                clickStage = 0
                availableForSelect = false
                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                LiquidBounce.hud.addNotification(Notification("Kit checker timed out. Please use the right kit name.", Notification.Type.ERROR))
                debug("can't find any kit with that name")
            }
        } else {
            timeoutTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (availableForSelect && packet is S2DPacketOpenWindow && clickStage < 3 && !editMode.get())
            event.cancelEvent()

        if (packet is S2FPacketSetSlot) {
            val item = packet.func_149174_e() ?: return
            val windowId = packet.func_149175_c()
            val slot = packet.func_149173_d()
            val itemName = item.unlocalizedName
            val displayName = item.displayName

            if (!availableForSelect && clickStage == 0 && windowId == 0 && itemName.contains("bow", true) && displayName.contains("kit selector", true)) { // dynamic for solo/teams
                if (editMode.get()) {
                    availableForSelect = true
                    debug("found item, listening to kit selection cuz of edit mode")
                } else {
                    expectSlot = slot
                    clickStage = 1
                    availableForSelect = true
                    debug("found item, sent trigger")
                }
            }

            if (clickStage == 2 && displayName.contains(kitNameValue.get(), true)) {
                timeoutTimer.reset()
                clickStage = 3
                debug("detected kit selection")
                Timer().schedule(150L) {
                    mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                    mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
                    mc.netHandler.addToSendQueue(C0DPacketCloseWindow(windowId))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    debug("selected")
                }
            }
        }
        
        if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText

            if (text.contains("kit has been selected", true)) {
                if (editMode.get()) {
                    val kitName = text.replace(" kit has been selected!", "")
                    kitNameValue.set(kitName)
                    editMode.set(false)
                    clickStage = 0
                    availableForSelect = false
                    debug("finished detecting kit")
                    LiquidBounce.hud.addNotification(Notification("Successfully detected $kitName kit.", Notification.Type.SUCCESS))
                } else {
                    debug("finished")
                    event.cancelEvent()
                    LiquidBounce.hud.addNotification(Notification("Successfully selected ${kitNameValue.get()} kit.", Notification.Type.SUCCESS))
                }
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clickStage = 0
        availableForSelect = false
        expectSlot = -1

        timeoutTimer.reset()
        delayTimer.reset()
    }

    override val tag: String
        get() = kitNameValue.get()
}
