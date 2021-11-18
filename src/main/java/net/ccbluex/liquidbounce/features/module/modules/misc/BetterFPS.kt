/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue

import net.ccbluex.liquidbounce.event.*

@ModuleInfo(name = "BetterFPS", description = "Replace MathHelper's sin/cos functions with other (faster) methods.", category = ModuleCategory.MISC, canEnable = false)
class BetterFPS : Module() {
    val sinMode = ListValue("SinMode", arrayOf("Vanilla", "Taylor", "LibGDX", "RivensFull", "RivensHalf", "Rivens", "Java", "1.16"), "Vanilla")
    val cosMode = ListValue("CosMode", arrayOf("Vanilla", "Taylor", "LibGDX", "RivensFull", "RivensHalf", "Rivens", "Java", "1.16"), "Vanilla")
}