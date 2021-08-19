/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.hypixel;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class HypixelLowHop extends SpeedMode {

    public HypixelLowHop() {
        super("HypixelLowHop");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        
    }

    @Override
    public void onMove(MoveEvent event) {
        TargetStrafe targetStrafe = (TargetStrafe) LiquidBounce.moduleManager.getModule(TargetStrafe.class);
        if (targetStrafe == null) return;

        if(MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.jumpTicks == 0) {
                mc.timer.timerSpeed = 1.2F;
                event.setY(mc.thePlayer.motionY = 0.26);
                mc.thePlayer.jumpTicks = 5;
            } else if (event.getY() < 0) {
                mc.timer.timerSpeed = 0.97F;
            }

            if (targetStrafe.getCanStrafe())
                targetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed() * 1.0075);
            else
                MovementUtils.setSpeed(event, MovementUtils.getBaseMoveSpeed() * 1.0075);
        } 
    }
}
