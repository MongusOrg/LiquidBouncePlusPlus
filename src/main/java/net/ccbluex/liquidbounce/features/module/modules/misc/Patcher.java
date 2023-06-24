/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

import net.ccbluex.liquidbounce.value.*;
import org.lwjgl.opengl.Display;

import java.util.HashMap;

@ModuleInfo(name = "Patcher", description = "improving your experience without bloatware, aka. Essential.", category = ModuleCategory.MISC)
public class Patcher extends Module {

    public static final BoolValue noHitDelay = new BoolValue("NoHitDelay", false);
    public static final BoolValue tabOutReduceFPS = new BoolValue("ReduceFPSWhenNoFocus", false);
    public static final BoolValue jumpPatch = new BoolValue("JumpFix", true);
    public static final BoolValue chatPosition = new BoolValue("ChatPosition1.12", true);
    public static final BoolValue silentNPESP = new BoolValue("SilentNPE-SpawnPlayer", true);
    public static final BoolValue thirdPersonCrosshair = new BoolValue("ThirdPersonCrosshair", true);

    public int oldFPS = 0;

    @Override
    public void onEnable() {
        oldFPS = mc.gameSettings.limitFramerate;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(tabOutReduceFPS.get()) {
            if (!Display.isActive()) {
                mc.gameSettings.limitFramerate = 3;
            } else if (Display.isActive()){
                mc.gameSettings.limitFramerate = oldFPS;
            }
        }
    }
}
