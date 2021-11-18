/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class CustomSpeed extends SpeedMode {

    public CustomSpeed() {
        super("Custom");
    }

    @Override
    public void onMotion() {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);

        if(speed == null)
            return;
        if(MovementUtils.isMoving()) {
            mc.timer.timerSpeed = speed.customTimerValue.get();

            if(mc.thePlayer.onGround && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 10;
                mc.thePlayer.motionX *= speed.customSpeedValue.get();
                mc.thePlayer.motionZ *= speed.customSpeedValue.get();
                mc.thePlayer.motionY = speed.customYValue.get();
            }else if(speed.customStrafeValue.get())
                MovementUtils.strafe();
        }else if(speed.customStrafeValue.get())
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
    }

    @Override
    public void onEnable() {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);

        if(speed == null)
            return;

        if(speed.resetXZValue.get()) mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
        if(speed.resetYValue.get()) mc.thePlayer.motionY = 0D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
