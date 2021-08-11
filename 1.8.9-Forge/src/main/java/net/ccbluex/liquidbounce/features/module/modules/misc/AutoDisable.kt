package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "AutoDisable", description = "Automatically disable modules for you on flag or world respawn.", category = ModuleCategory.MISC, array = false)
class AutoDisable : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) disableModules(DisableEvent.FLAG)
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        disableModules(DisableEvent.WORLD_CHANGE)
    }

    fun disableModules(enumDisable: DisableEvent) {
        var moduleNames: Int = 0
        LiquidBounce.moduleManager.modules.filter { it.autoDisable == enumDisable && it.state }.forEach { it.toggle(); moduleNames++ }

        if (moduleNames <= 0) return
        LiquidBounce.hud.addNotification(Notification("Disabled $moduleNames modules due to ${if (enumDisable == DisableEvent.FLAG) "flag" else "world change"}.", Notification.Type.WARNING, 3000L))
    }

    enum class DisableEvent {
        NONE,
        FLAG,
        WORLD_CHANGE
    }
}