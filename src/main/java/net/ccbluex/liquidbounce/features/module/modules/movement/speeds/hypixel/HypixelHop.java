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
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class HypixelHop extends SpeedMode {

    public HypixelHop() {
        super("HypixelHop");
    }

    private boolean skipAccelerate = false;
    private double lastDist = 0.0;

    @Override
    public void onMotion(MotionEvent eventMotion) {
        final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);
        if(speed == null) return;

        final TargetStrafe targetStrafe = (TargetStrafe) LiquidBounce.moduleManager.getModule(TargetStrafe.class);
        if (targetStrafe == null) return;

        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

        double lastSpeed = (mc.thePlayer.fallDistance < 0.1D && !this.skipAccelerate && eventMotion.getY() < 0.1D) ? MovementUtils.applyFriction(Math.max(MovementUtils.getSpeed(), speed.moveSpeedValue.get()), lastDist, MovementUtils.getBaseMoveSpeed() * speed.baseStrengthValue.get()) : Math.max(MovementUtils.getSpeed(), speed.moveSpeedValue.get());
        if (mc.thePlayer.onGround)
            if (this.mc.thePlayer.isCollidedHorizontally) {
                this.skipAccelerate = true;
                eventMotion.setY(MovementUtils.getJumpBoostModifier(speed.jumpYValue.get()));
            } else {
                this.skipAccelerate = false;
                eventMotion.setY(0.0724D);
                if (!this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    lastSpeed = 0.12D;
                } else if (this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() == 0) {
                    lastSpeed = 0.18D;
                } else if (this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() == 1) {
                    lastSpeed = 0.2D;
                } 
            }

        if (targetStrafe.getCanStrafe()) targetStrafe.strafe(event, lastSpeed); else MovementUtils.setSpeed(event, lastSpeed);
    }

    @Override
    public void onUpdate() {
        
    }
    
    @Override
    public void onMotion() {
        
    }

    @Override
    public void onMove(MoveEvent event) {
        
    }
}
