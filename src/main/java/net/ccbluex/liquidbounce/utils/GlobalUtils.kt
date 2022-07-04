/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.SessionEvent
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import kotlin.concurrent.thread

object GlobalUtils : MinecraftInstance(), Listenable {
    
    val checkTimer = MSTimer()
    var onlinePlayers = ""

    fun startCheckThread() {
        thread {
            try {
                HttpUtils.postData(mc.session.username)
            } catch (ek: Exception) {
                ek.printStackTrace()
            }
            while (true) {
                if (checkTimer.hasTimePassed(15000L)) {
                    try {
                        onlinePlayers = HttpUtils.getAllData()
                    } catch (e: Exception) {
                        LiquidBounce.hud.addNotification(Notification("Error while checking online players.", Notification.Type.ERROR))
                    }
                    checkTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onSession(event: SessionEvent) {
        try {
            HttpUtils.postData(mc.session.username)
        } catch (ek: Exception) {
            ek.printStackTrace()
        }
    }

    @JvmStatic
    fun isLBPlayer(name: String): Boolean = onlinePlayers.contains(name, false)

    override fun handleEvents() = true

}