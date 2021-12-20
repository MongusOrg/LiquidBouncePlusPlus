/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

import net.ccbluex.liquidbounce.value.*;

import java.util.HashMap;

@ModuleInfo(name = "Patcher", description = "Bring many Patcher mod features into LiquidBounce+. (see settings for more info)", category = ModuleCategory.MISC, canEnable = false)
public class Patcher extends Module {

    public static final BoolValue betterFontRenderer = new BoolValue("BetterVanillaFontRenderer", false);
    public static final BoolValue betterFontRendererStringCache = new BoolValue("BVFR-StringCache", false);
    public static final BoolValue keepShadersOnPerspectiveChange = new BoolValue("KeepShadersOnPerspectiveChange", false);
    public static final BoolValue optimizedWorldSwapping = new BoolValue("OptimizedWorldSnapping", true);
    public static final BoolValue batchModelRendering = new BoolValue("BatchModelRendering", false);
    public static final BoolValue labyModMoment = new BoolValue("LabyMod-Moment", false);
    public static final BoolValue lowAnimationTick = new BoolValue("LowAnimationTick", false);
    public static final BoolValue chatPosition = new BoolValue("ChatPosition1.12", true);
    public static final BoolValue silentNPESP = new BoolValue("SilentNPE-SpawnPlayer", true);

}