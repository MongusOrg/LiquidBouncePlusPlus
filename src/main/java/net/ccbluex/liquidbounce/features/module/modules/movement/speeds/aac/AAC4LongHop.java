/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class AAC4LongHop extends SpeedMode {
    public AAC4LongHop() {
        super("AAC4LongHop");
    }

    @Override
    public void onMotion() {
        
    }

    @Override
    public void onUpdate() {
        if(mc.thePlayer.isInWater()) return;
        if(!MovementUtils.isMoving()) return;
        
        if (mc.thePlayer.onGround) {
              mc.gameSettings.keyBindJump.pressed = false;
               mc.thePlayer.jump();
         }
         if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance <= 0.1) {
               mc.thePlayer.speedInAir = 0.02;
               mc.timer.timerSpeed = 1.5F;
          }
          if (mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 1.3) {
               mc.timer.timerSpeed = 0.7F;
          }
          if (mc.thePlayer.fallDistance >= 1.3) {
                mc.timer.timerSpeed = 1.0F;
                mc.thePlayer.speedInAir = 0.02;
          }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
