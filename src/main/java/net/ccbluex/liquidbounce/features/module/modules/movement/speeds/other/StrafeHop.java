/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class StrafeHop extends SpeedMode {

    public StrafeHop() {
        super("StrafeHop");
    }

    @Override
    public void onMotion() {
        if(mc.thePlayer.isInWater())
            return;

        if(MovementUtils.isMoving()) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                MovementUtils.strafe();
            } else
                MovementUtils.strafe();
        }else{
            mc.thePlayer.motionX = 0D;
            mc.thePlayer.motionZ = 0D;
        }
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
