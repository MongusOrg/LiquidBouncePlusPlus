/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoDisable.DisableEvent
import net.ccbluex.liquidbounce.utils.ClientUtils

class AutoDisableCommand : Command("autodisable", arrayOf("ad")) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            when (args[1].toLowerCase()) {
                "list" -> {
                    chat("§c§lAutoDisable modules:")
                    LiquidBounce.moduleManager.modules.filter { it.autoDisable != DisableEvent.NONE }.forEach {
                        ClientUtils.displayChatMessage("§6> §c${it.name} §7| §a${it.autoDisable.name.toLowerCase()}")
                    }
                    return
                }
                "clear" -> {
                    LiquidBounce.moduleManager.modules.filter { it.autoDisable != DisableEvent.NONE }.forEach {
                        it.autoDisable = DisableEvent.NONE
                    }
                    chat("Successfully cleared the AutoDisable list.")
                    return
                }
            }
        }
        else if (args.size > 2) {
            // Get module by name
            val module = LiquidBounce.moduleManager.getModule(args[1])

            if (module == null) {
                chat("Module §a§l${args[1]}§3 not found.")
                return
            }

            try {
                val disableWhen = DisableEvent.valueOf(args[2].toUpperCase())

                val disableType = when (disableWhen) {
                    DisableEvent.FLAG -> "when you get flagged."
                    DisableEvent.WORLD_CHANGE -> "when you change the world."
                    else -> null
                }

                // Find key by name and change
                module.autoDisable = disableWhen

                // Response to user
                chat("Module §a§l${module.name}§3 ${if (disableType == null) "is removed from the AutoDisable list." else "should be disabled $disableType"}")
                playEdit()
                return
            } catch (e: IllegalArgumentException) {
                chat("§c§lWrong auto disable type!")
                chatSyntax("autodisable <module> <none/flag/world_change>")
                return
            }
        }

        chatSyntax("autodisable <module/list> <none/flag/world_change>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> LiquidBounce.moduleManager.modules
                    .map { it.name }
                    .filter { it.startsWith(moduleName, true) }
                    .toList()
            2 -> listOf<String>("none", "flag", "world_change").filter { it.startsWith(args[1], true) }
            else -> emptyList()
        }
    }

}