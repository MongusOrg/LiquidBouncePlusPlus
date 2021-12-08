/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.hooks.font;

import net.minecraft.client.gui.FontRenderer;

public class OptiFineHook {

    public float getCharWidth(FontRenderer renderer, char c) {//Remapped by OptiFineHookTransformer to Optifine if needed
        return renderer.getCharWidth(c);
    }

    public float getOptifineBoldOffset(FontRenderer renderer) { //Remapped by FontRendererHookTransformer to Optifine if needed
        return 1;
    }

}