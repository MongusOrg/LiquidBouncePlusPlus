/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.util.enhancement.hash;

import net.ccbluex.liquidbounce.patcher.util.enhancement.hash.impl.AbstractHash;

public class StringHash extends AbstractHash {
    public StringHash(String text, float red, float green, float blue, float alpha, boolean shadow) {
        super(text, red, green, blue, alpha, shadow);
    }
}
