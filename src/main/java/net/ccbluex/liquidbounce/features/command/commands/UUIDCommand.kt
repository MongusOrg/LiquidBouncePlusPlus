/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.special.FakeUUID

class UUIDCommand : Command("uuid", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            val theName = args[1]

            if (theName.equals("reset", true)) {
                FakeUUID.spoofedUUID = ""
                chat("§aSuccessfully resetted your UUID.")
                return
            }

            // Get target player data
            val targetPlayer = mc.theWorld.playerEntities
                    .filter { !AntiBot.isBot(it) && it.name.equals(theName, true) }
                    .firstOrNull()

            if (targetPlayer == null)
                FakeUUID.spoofedUUID = theName
            else
                FakeUUID.spoofedUUID = targetPlayer.getUniqueID().toString()
            chat("§aSuccessfully changed your UUID to §6${FakeUUID.spoofedUUID}.")
            return
        } 

        if (args.size == 1)
            chat("§6Your UUID is §7${mc.session.playerID}.")

        chatSyntax("uuid <player's name in current world/uuid/reset>")
    }

}