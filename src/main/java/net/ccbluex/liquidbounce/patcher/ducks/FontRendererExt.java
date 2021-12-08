/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.ducks;

import net.ccbluex.liquidbounce.patcher.hooks.font.FontRendererHook;

public interface FontRendererExt {
    FontRendererHook patcher$getFontRendererHook();
}