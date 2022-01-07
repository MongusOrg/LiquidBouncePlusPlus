/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class VulcanTimerGround extends SpeedMode {
    public VulcanTimerGround() {
        super("VulcanTimerGround");
    }
    @Override
    public  void onDisable() {
        mc.timer.timerSpeed = 1F;
    }
    @Override
    public void onMotion() {
        if (mc.thePlayer.ticksExisted % 15 == 0)
            mc.timer.timerSpeed = 0.15F;
        else
            mc.timer.timerSpeed = 3F;

        if (mc.thePlayer.onGround)
            MovementUtils.strafe();
    }
    @Override
    public void onUpdate() {
    }
    @Override
    public void onMove(MoveEvent event) {
    }
}