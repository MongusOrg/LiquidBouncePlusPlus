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

public class HypixelBoost extends SpeedMode {

    public HypixelBoost() {
        super("HypixelBoost");
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
        mc.timer.timerSpeed = 1F;
        if(MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(mc.thePlayer.motionY = 0.4199999999);
            }
            
            mc.timer.timerSpeed = Math.max(1F + Math.abs((float)mc.thePlayer.motionY) * 3F, 1F);
            
            double moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed() * 1.045, MovementUtils.getSpeed());
            if (targetStrafe.getCanStrafe()) targetStrafe.strafe(event, moveSpeed); else MovementUtils.setSpeed(event, moveSpeed);
        } 
    }
}
