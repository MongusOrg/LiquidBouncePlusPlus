/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.hypixel;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class HypixelStable extends SpeedMode {

    public HypixelStable() {
        super("HypixelStable");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        
    }

    @Override
    public void onMove(MoveEvent event) {
        final TargetStrafe targetStrafe = (TargetStrafe) LiquidBounce.moduleManager.getModule(TargetStrafe.class);
        if (targetStrafe == null) return;
        if(MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            mc.timer.timerSpeed = 1F;
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                event.setY(mc.thePlayer.motionY = 0.42);
                mc.thePlayer.jumpTicks = 10;
            }

            if (!mc.thePlayer.onGround && mc.thePlayer.motionY < -0.2333333333333)  {
                mc.thePlayer.motionY *= 1.08;
            }
            
            double moveSpeed = Math.max(MovementUtils.getSpeed(), MovementUtils.getBaseMoveSpeed());
            if (targetStrafe.getCanStrafe()) targetStrafe.strafe(event, moveSpeed); else MovementUtils.setSpeed(event, moveSpeed);
        } 
    }
}
