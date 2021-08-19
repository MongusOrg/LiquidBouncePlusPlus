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

public class HypixelReduceHop extends SpeedMode {

    public HypixelReduceHop() {
        super("HypixelReduceHop");
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
                event.setY(mc.thePlayer.motionY = 0.41);
                mc.thePlayer.jumpTicks = 9;
            } else if (mc.thePlayer.motionY < 0) {
                event.setY(mc.thePlayer.motionY *= 1.001255);
            }

            if (targetStrafe.getCanStrafe())
                targetStrafe.strafe(event, MovementUtils.getBaseMoveSpeed() * 1.0095);
            else
                MovementUtils.setSpeed(event, MovementUtils.getBaseMoveSpeed() * 1.0095);
        }
    }
}
