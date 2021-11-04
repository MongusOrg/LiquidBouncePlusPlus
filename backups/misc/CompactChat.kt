/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.event.*
import net.minecraft.network.play.server.S02PacketChat

@ModuleInfo(name = "CompactChat", description = "Reduce chat spams.", category = ModuleCategory.MISC)
class CompactChat : Module() {

    private var lastMessage : String = ""
    private var messageLine : Int = 0
    private var messageAmount : Int = 0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S02PacketChat) {
            val s02chat = event.packet as S02PacketChat
            val message = s02chat.chatComponent
            val formattedMessage = message.getFormattedText()
            val chatGUI = mc.ingameGUI.getChatGUI()
            if (lastMessage.equals(formattedMessage, ignoreCase = false)) {
                messageAmount++
                chatGUI.deleteChatLine(messageLine)
                message.appendText(" §7(x${messageAmount})")
            } else {
                messageAmount = 1
            }
            if (messageLine > 256)
                messageLine = 0
            else
                messageLine++
            lastMessage = formattedMessage
            chatGUI.printChatMessageWithOptionalDeletion(message, messageLine)
            event.cancelEvent()
        }
    }

}
