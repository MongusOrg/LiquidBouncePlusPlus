/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;

public class VulcanTimerGround extends SpeedMode {
    public VulcanTimerGround() {
        super("VulcanTimerGround");
    }
    private final MSTimer timer = new MSTimer();
    @Override
    public  void onDisable() {
        mc.timer.timerSpeed = 1F;
        timer.reset();
    }
    @Override
    public void onMotion() {
        if (timer.hasTimePassed(400L))
            timer.reset();
        mc.timer.timerSpeed = 1F + (float)timer.hasTimeLeft(250L) / 250F * 1.65F;
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