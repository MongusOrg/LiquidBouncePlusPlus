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
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.event.ClickEvent
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.IChatComponent
import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoKnight", spacedName = "Auto Knight", description = "Automatically selects Knight kit for you in BlocksMC Skywars.", category = ModuleCategory.MISC)
class AutoKnight : Module() {
    private var clickStage = 0
    private var kitSelected = false

    override fun onEnable() {
        clickStage = 0
        kitSelected = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (!kitSelected && clickStage == 1 && packet is S2DPacketOpenWindow)
            event.cancelEvent()

        if (!kitSelected && packet is S2FPacketSetSlot) {
            val item = packet.func_149174_e() ?: return
            val windowId = packet.func_149175_c()
            val slot = packet.func_149173_d()
            val itemName = item.unlocalizedName
            val displayName = item.displayName

            if (clickStage == 0 && windowId == 0 && itemName.contains("bow", true) && displayName.contains("kit selector", true)) {
                Timer().schedule(500L) {
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(0))
                    mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    clickStage++
                }
            } else if (clickStage == 1 && windowId != 0 && itemName.contains("bow", true) && displayName.contains("Knight", true)) {
                mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 727))
                clickStage++
            }
        }
        
        if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText

            if (text.contains("has been selected", true)) {
                kitSelected = true
                LiquidBounce.hud.addNotification(Notification("Successfully selected Knight kit.", Notification.Type.SUCCESS))
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clickStage = 0
        kitSelected = false
    }
}
