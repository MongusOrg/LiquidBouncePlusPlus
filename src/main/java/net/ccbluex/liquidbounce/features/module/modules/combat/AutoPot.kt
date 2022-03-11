/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.potion.Potion

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@ModuleInfo(name = "AutoPot", spacedName = "Auto Pot", category = ModuleCategory.COMBAT, description = "Automatically throw pots for you.")
class AutoPot : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Jump", "Floor"), "Floor")

    private val healthValue = FloatValue("HP", 75F, 0F, 100F)
    private val delayValue = IntegerValue("Delay", 500, 500, 5000)
    private val resetDelayValue = IntegerValue("ResetDelay", 10000, 1000, 10000)
    private val spoofInvValue = BoolValue("InvSpoof", false)
    private val spoofDelayValue = IntegerValue("InvDelay", 500, 500, 5000, { spoofInvValue.get() })
    private val regenValue = BoolValue("Heal", true)
    private val utilityValue = BoolValue("Utility", true)
    private val debugValue = BoolValue("Debug", false)
    private val notifyValue = BoolValue("Notification", true)

    private var throwing = false
    private var potIndex = -1

    private var throwTimer = MSTimer()
    private var resetTimer = MSTimer()
    private var invTimer = MSTimer()

    private var queuedEffects = arrayListOf<Int>()

    override fun onEnable() {
        throwTimer.reset()
        resetTimer.reset()
        invTimer.reset()
        queuedEffects.clear()
    }

    private fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage("[AutoPot] $s")
    }

    @EventTarget(priority = 1)
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java)!! as KillAura
            val scaffold = LiquidBounce.moduleManager.getModule(Scaffold::class.java)!! as Scaffold

            if (!queuedEffects.isEmpty()) {
                val queueDeleteList = arrayListOf<Int>()
                for (pID in queuedEffects)
                    if (mc.thePlayer.isPotionActive(pID)) {
                        queueDeleteList.add(pID)
                        if (notifyValue.get())
                            LiquidBounce.hud.addNotification(Notification("Potted ${I18n.format(Potion.potionTypes[pID].getName(), arrayOf<Object>())}.", Notification.Type.INFO))
                        debug("${pID} removed due to effect detected")
                    }

                if (queueDeleteList.size > 0)
                    for (delID in queueDeleteList)
                        queuedEffects.remove(delID)

                if (resetTimer.hasTimePassed(resetDelayValue.get().toLong())) {
                    resetTimer.reset()
                    queuedEffects.clear()
                    debug("cleared queue")
                }
            }

            val potion = findPotion(36, 45)
            if (!throwing && potion != -1 && mc.currentScreen !is GuiContainer) {
                throwing = true
                potIndex = potion
                throwTimer.reset()

                debug("found pot, queueing")
            }

            if (throwing && mc.currentScreen !is GuiContainer && (!killAura.state || killAura.target == null) && !scaffold.state) {
                if (!throwTimer.hasTimePassed(delayValue.get().toLong())) return

                if (mc.thePlayer.onGround && modeValue.get().equals("jump", true)) {
                    mc.thePlayer.jump()
                    debug("jumped")
                }

                if ((mc.thePlayer.onGround && modeValue.get().equals("floor", true)) ||
                        (!mc.thePlayer.onGround && modeValue.get().equals("jump", true)))
                {
                    if (RotationUtils.targetRotation != null)
                        RotationUtils.setTargetRotation(Rotation(RotationUtils.targetRotation.yaw, 90F))
                    else
                        RotationUtils.setTargetRotation(Rotation(event.yaw, 90F))

                    event.pitch = 90F
                    debug("set rotation")
                }
            }
        }
    }

    @EventTarget(priority = -1)
    fun onMotionPost(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            if (throwing && mc.currentScreen !is GuiContainer && throwTimer.hasTimePassed(delayValue.get().toLong())) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(potIndex - 36))
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                potIndex = -1
                throwing = false
                debug("thrown")
            }

            if (spoofInvValue.get() && !throwing && mc.currentScreen !is GuiContainer) {
                val invPotion = findPotion(9, 36)
                if (invPotion != -1) {
                    if (invTimer.hasTimePassed(spoofDelayValue.get().toLong())) {
                        if (InventoryUtils.hasSpaceHotbar()) {
                            mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                            mc.playerController.windowClick(0, invPotion, 0, 1, mc.thePlayer)
                            mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
                            invTimer.reset()

                            debug("moved pot")
                            return
                        } else {
                            for (i in 36 until 45) {
                                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                                if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
                                    continue

                                mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                                mc.playerController.windowClick(0, invPotion, 0, 0, mc.thePlayer)
                                mc.playerController.windowClick(0, i, 0, 0, mc.thePlayer)
                                mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
                                invTimer.reset()

                                debug("moved pot")
                                break
                            }
                        }
                    }
                }
            } else {
                invTimer.reset()
            }
        }
    }

    private fun findPotion(startSlot: Int, endSlot: Int): Int {
        for (i in startSlot until endSlot) {
            if (findSinglePotion(i)) {
                return i
            }
        }
        return -1
    }

    private fun findSinglePotion(slot: Int): Boolean {
        val stack = mc.thePlayer.inventoryContainer.getSlot(slot).stack

        if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
            return false

        val itemPotion = stack.item as ItemPotion

        if (mc.thePlayer.health / mc.thePlayer.maxHealth * 100F < healthValue.get() && regenValue.get()) {
            for (potionEffect in itemPotion.getEffects(stack))
                if (potionEffect.potionID == Potion.heal.id)
                    return true

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && !queuedEffects.contains(Potion.regeneration.id))
                for (potionEffect in itemPotion.getEffects(stack)) {
                    if (potionEffect.potionID == Potion.regeneration.id) {
                        queuedEffects.add(Potion.regeneration.id)
                        debug("${Potion.regeneration.id} added to queue.")
                        return true
                    }
                }

        } else if (utilityValue.get()) {
            for (potionEffect in itemPotion.getEffects(stack)) {
                if (isUsefulPotion(potionEffect.potionID)) {
                    queuedEffects.add(potionEffect.potionID)
                    debug("${potionEffect.potionID} added to queue.")
                    return true
                }
            }
        }

        return false
    }

    private fun isUsefulPotion(id: Int): Boolean {
        if (id == Potion.regeneration.id || id == Potion.heal.id || id == Potion.poison.id
            || id == Potion.blindness.id || id == Potion.harm.id || id == Potion.wither.id
            || id == Potion.digSlowdown.id || id == Potion.moveSlowdown.id || id == Potion.weakness.id) {
            return false
        }
        return !mc.thePlayer.isPotionActive(id) && !queuedEffects.contains(id)
    }

    override val tag: String
        get() = "${if (modeValue.get().equals("jump", true)) "Jump Only" else "Floor"}"

}