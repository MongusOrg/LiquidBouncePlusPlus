/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "PostProcessing", spacedName = "Post Processing", description = "Adds visual effects (shadow, blur) to HUD elements.", category = ModuleCategory.RENDER)
class PostProcessing : Module() {
    private val blurValue = BoolValue("Blur", true)
    private val blurStrengthValue = FloatValue("Blur-Strength", 0.01F, 0.01F, 40F, { blurValue.get() })
    private val shadowValue = BoolValue("Shadow", true)
    private val shadowStrengthValue = FloatValue("Shadow-Strength", 0.01F, 0.01F, 40F, { shadowValue.get() })

    val blur: Boolean
        get() = blurValue.get()
    val shadow: Boolean
        get() = shadowValue.get()
    val blurStrength: Float
        get() = blurStrengthValue.get()
    val shadowStrength: Float
        get() = shadowStrengthValue.get()
}