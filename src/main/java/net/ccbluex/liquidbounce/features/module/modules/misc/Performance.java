/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient, modified. Please credit them and us when using this code in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "Performance", category = ModuleCategory.MISC, description = "Optimize functions and improve render performance.", canEnable = false)
public class Performance extends Module {
    public static BoolValue staticParticleColorValue = new BoolValue("StaticParticleColor", false);
    public static BoolValue fastEntityLightningValue = new BoolValue("FastEntityLightning", false);
    public static BoolValue fastBlockLightningValue = new BoolValue("FastBlockLightning", false);
}