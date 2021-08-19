/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.minecraft.potion.Potion;

public class RedeskyHop extends SpeedMode {

    public RedeskyHop() {
        super("RedeskyHop");
    }

    private boolean slowCum=false;
    private double cumSpeed=0;

    @Override
    public void onMotion() {
        if(MovementUtils.isMoving()) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = 0.39;
                cumSpeed = 1.02875;
                slowCum=true;
            }else {
                if (slowCum) {
                    cumSpeed *= 0.969;
                    slowCum = false;
                }
                if (mc.thePlayer.fallDistance < 0.1) {
                    cumSpeed *= 1.004;
                } else {
                    cumSpeed *= 0.995;
                    if (mc.thePlayer.fallDistance > 0.93) {
                        mc.timer.timerSpeed = 1.14f;
                    }
                }
            }
            mc.thePlayer.motionX *= cumSpeed;
            mc.thePlayer.motionZ *= cumSpeed;
        }else{
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(final MoveEvent event) {
    }
}