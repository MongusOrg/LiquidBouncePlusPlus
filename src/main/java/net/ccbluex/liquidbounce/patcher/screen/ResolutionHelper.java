/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.screen;

public class ResolutionHelper {
    private static int currentScaleOverride = -1;
    private static int scaleOverride = -1;

    public static int getCurrentScaleOverride() {
        return currentScaleOverride;
    }

    public static void setCurrentScaleOverride(int currentScaleOverride) {
        ResolutionHelper.currentScaleOverride = currentScaleOverride;
    }

    public static int getScaleOverride() {
        return scaleOverride;
    }

    public static void setScaleOverride(int scaleOverride) {
        ResolutionHelper.scaleOverride = scaleOverride;
    }
}
