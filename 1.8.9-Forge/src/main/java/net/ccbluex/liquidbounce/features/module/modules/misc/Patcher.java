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

@ModuleInfo(name = "Patcher", description = "Bring many Patcher mod features into LiquidBounce+. (see settings for more info)", category = ModuleCategory.MISC, canEnable = false)
public class Patcher extends Module {

    public final BoolValue betterFontRenderer = new BoolValue("BetterVanillaFontRenderer", false);
    public final BoolValue betterFontRendererStringCache = new BoolValue("BVFR-StringCache", false);

    public static boolean getPatcherValue(String v) {
        Patcher patcher = (Patcher) LiquidBounce.moduleManager.getModule(Patcher.class);
        if (patcher == null)
            return false;

        for (Value va : patcher.getValues()) {
            if (va instanceof BoolValue && va.getName() == v)
                return ((BoolValue) va).get();
        }
    }

    public static boolean getPatcherValue(int index) {
        Patcher patcher = (Patcher) LiquidBounce.moduleManager.getModule(Patcher.class);
        if (patcher == null)
            return false;

        if (index > patcher.getValues().size() - 1)
            return false;

        return ((BoolValue) patcher.getValues().get(index)).get();
    }

}