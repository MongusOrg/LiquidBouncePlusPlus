/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;

public class RedeskyTimerHop extends SpeedMode {
    public RedeskyTimerHop() {
        super("RedeskyTimerHop");
    }

    @Override
    public void onMotion() {
        mc.timer.timerSpeed = 1F; //wtf

        if(mc.thePlayer.isInWater())
            return;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.onGround) {
                mc.timer.timerSpeed = 6F;
                mc.thePlayer.jump();
            } else if (mc.thePlayer.fallDistance > 0) {
                mc.timer.timerSpeed = 1.095F;
            }               
        }     
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
