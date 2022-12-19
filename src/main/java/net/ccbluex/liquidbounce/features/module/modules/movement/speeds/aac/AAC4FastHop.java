/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class AAC4FastHop extends SpeedMode {
    public AAC4FastHop() {
        super("AAC4FastHop");
    }

    @Override
    public void onMotion() {
        
    }

    @Override
    public void onUpdate() {
        if(mc.thePlayer.isInWater()) return;
        if(!MovementUtils.isMoving()) return;
        
        if (mc.thePlayer.onGround) {
             mc.thePlayer.jump();
             mc.thePlayer.speedInAir = 0.0201;
             mc.timer.timerSpeed = 0.94F;
        }
        
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
             mc.thePlayer.speedInAir = 0.02;
             mc.timer.timerSpeed = 1.8F;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
