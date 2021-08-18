/*
 * Licensed under GPL-v3
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(name = "VanillaAura", description = "The most blatant aura ever, only intended for vanilla or server that has combat disabler (by commandblock2)",
    category = ModuleCategory.COMBAT)
class VanillaAura : Module() {
    private val prob = IntegerValue("Probability", 100, 0, 100)
    private val multiplier = IntegerValue("CPSMultiplier", 3, 0, 10)
    private val callAttackEvent = BoolValue("CallAttackEvent", false)
    private val multi = BoolValue("Multi", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (RandomUtils.nextInt(0, 101) > prob.get())
            return

        val killAura = (LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura)

        val targets = mc.theWorld.loadedEntityList.filter {
            killAura.isEnemy(it)
        }.sortedBy {
            mc.thePlayer.getDistanceToEntity(it)
        }

        if (targets.isEmpty())
            return

        val selected = if (multi.get()) targets else listOf(targets[0])

        val handNull = mc.thePlayer.heldItem == null
        if (!handNull && mc.thePlayer.heldItem.item is ItemSword)
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
            BlockPos.ORIGIN, EnumFacing.DOWN
            ))

        for (entity in selected){
            repeat(multiplier.get()) {
                if (callAttackEvent.get())
                    LiquidBounce.eventManager.callEvent(AttackEvent(entity))
                mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))
            }
        }

        if (!handNull && mc.thePlayer.heldItem.item is ItemSword)
            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
    }

}