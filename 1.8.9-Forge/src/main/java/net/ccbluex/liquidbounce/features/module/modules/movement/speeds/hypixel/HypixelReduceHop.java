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

public class HypixelReduceHop extends SpeedMode {

    public HypixelReduceHop() {
        super("HypixelReduceHop");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        if(MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
            if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.42;
                mc.thePlayer.jumpTicks = 8;
            } else if (mc.thePlayer.motionY < 0) {
                mc.thePlayer.motionY *= 1.195;
            }
            MovementUtils.strafe(MovementUtils.getSpeed() * 0.98888855F);
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
