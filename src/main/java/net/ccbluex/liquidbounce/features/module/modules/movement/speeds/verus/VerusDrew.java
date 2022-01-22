/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.verus;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Strafe;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.minecraft.util.MathHelper;

public class VerusDrew extends SpeedMode {

    public VerusDrew() {
        super("VerusDrew");
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }

    @Override
    public void onMotion() {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);
        if(speed == null)
            return;

        if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown())
            return;

        mc.timer.timerSpeed = speed.verusTimer.get();
        
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            if(mc.thePlayer.isSprinting()) {
                double f = mc.thePlayer.rotationYaw * 0.017453292;
                mc.thePlayer.motionX -= MathHelper.sin(f) * 0.2;
                mc.thePlayer.motionZ += MathHelper.cos(f) * 0.2;
            }
        }

        // double speed, float forward, float strafing, float yaw
        float forward = mc.thePlayer.movementInput.moveForward;
        float strafing = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;

        // check movement
        if (forward == 0.0F && strafing == 0.0F) return;
        boolean reversed = forward < 0.0F;

        float strafingYaw = 90.0F * (forward > 0.0F ? forward / 2.0F : reversed ? -(forward / 2.0F) : 1.0F);
        if (reversed) yaw += 180.0F;
        if (strafing > 0.0F) yaw -= strafingYaw;
        else if (strafing < 0.0F) yaw += strafingYaw;
        mc.thePlayer.motionX = Math.cos((yaw + 90.0) * 0.017453292519943295) * 0.36;
        mc.thePlayer.motionZ = Math.cos((yaw) * 0.017453292519943295) * 0.36;
    }

    @Override
    public void onUpdate() {}

    @Override
    public void onMove(MoveEvent event) {}
}
