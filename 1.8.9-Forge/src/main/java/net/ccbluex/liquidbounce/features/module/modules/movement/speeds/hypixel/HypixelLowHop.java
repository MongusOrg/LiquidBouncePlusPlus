/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.hypixel;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
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
        if(MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.24;
                mc.thePlayer.jumpTicks = 5;
            } else if (mc.thePlayer.motionY < 0) {
                mc.thePlayer.motionY *= 1.0625;
            }
            MovementUtils.strafe(MovementUtils.getSpeed() * 1.06575F);
        } 
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
