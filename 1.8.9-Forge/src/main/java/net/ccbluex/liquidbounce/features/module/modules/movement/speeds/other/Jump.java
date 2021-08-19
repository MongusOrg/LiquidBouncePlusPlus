/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class Jump extends SpeedMode {

    public Jump() {
        super("Jump");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        
    }

    @Override
    public void onMove(MoveEvent event) {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);
        TargetStrafe targetStrafe = (TargetStrafe) LiquidBounce.moduleManager.getModule(TargetStrafe.class);

        if(speed == null || targetStrafe == null)
            return;
        if(MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) && mc.thePlayer.jumpTicks == 0) {
            mc.thePlayer.jump();
            event.setY(mc.thePlayer.motionY = 0.42);
            mc.thePlayer.jumpTicks = 10;
        }
        if (speed.jumpStrafe.get() && MovementUtils.isMoving() && !mc.thePlayer.onGround && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) 
            if (targetStrafe.getCanStrafe()) 
                targetStrafe.strafe(event, MovementUtils.getSpeed());
            else
                MovementUtils.setSpeed(event, MovementUtils.getSpeed());
    }
}
