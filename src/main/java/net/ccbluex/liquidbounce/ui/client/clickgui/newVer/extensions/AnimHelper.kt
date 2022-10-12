/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 *//*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.ui.client.clickgui.newVer.extensions

import net.ccbluex.liquidbounce.features.module.modules.render.NewGUI
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils

fun Float.animSmooth(target: Float, speed: Float) = if (NewGUI.fastRenderValue.get()) target else AnimationUtils.animate(target, this, speed * RenderUtils.deltaTime * 0.025F)
fun Float.animLinear(speed: Float, min: Float, max: Float) = if (NewGUI.fastRenderValue.get()) { if (speed < 0F) min else max } else (this + speed).coerceIn(min, max)
