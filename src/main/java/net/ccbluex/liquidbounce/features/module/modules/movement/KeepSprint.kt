/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "KeepSprint", spacedName = "Keep Sprint", description = "Keep you sprint. Hypixel auto ban.", category = ModuleCategory.MOVEMENT)
class KeepSprint: Module() {
	var attac = false
	var motX = 0.0
	var motZ = 0.0
	
	@EventTarget
    fun onUpdate(event: EventUpdate) {
    	if(attac) {
    	    mc.thePlayer.motionX = motX
            mc.thePlayer.motionZ = motZ
            mc.thePlayer.setSprinting(true)
            attac = false
        }
    }
    
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0BPacketEntityAction) 
            if (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                attac = true
                motX = mc.thePlayer.motionX
                motZ = mc.thePlayer.motionZ
            }
    }
}