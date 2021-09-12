/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.JumpEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "LongJump", spacedName = "Long Jump", description = "Allows you to jump further.", category = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    private final ListValue modeValue = new ListValue("Mode", new String[] {"NCP", "AACv1", "AACv2", "AACv3", "AACv4", "Mineplex", "Mineplex2", "Mineplex3", "RedeskyMaki", "Redesky", "InfiniteRedesky"}, "NCP");
    private final FloatValue ncpBoostValue = new FloatValue("NCPBoost", 4.25F, 1F, 10F);
    private final BoolValue autoJumpValue = new BoolValue("AutoJump", false);
    private final BoolValue redeskyTimerBoostValue = new BoolValue("Redesky-TimerBoost", false);
    private final BoolValue redeskyGlideAfterTicksValue = new BoolValue("Redesky-GlideAfterTicks", false);
    private final IntegerValue redeskyTickValue = new IntegerValue("Redesky-Ticks", 21, 1, 25);
    private final FloatValue redeskyYMultiplier = new FloatValue("Redesky-YMultiplier", 0.77F, 0.1F, 1F);
    private final FloatValue redeskyXZMultiplier = new FloatValue("Redesky-XZMultiplier", 0.9F, 0.1F, 1F);
    private final FloatValue redeskyTimerBoostStartValue = new FloatValue("Redesky-TimerBoostStart", 1.85F, 0.1F, 10F);
    private final FloatValue redeskyTimerBoostEndValue = new FloatValue("Redesky-TimerBoostEnd", 1.0F, 0.1F, 10F);
    private final IntegerValue redeskyTimerBoostSlowDownSpeedValue = new IntegerValue("Redesky-TimerBoost-SlowDownSpeed", 2, 1, 10);

    private boolean jumped;
    private boolean canBoost;
    private boolean teleported;
    private boolean canMineplexBoost;
    private int ticks = 0;
    private float currentTimer = 1F;

    public void onEnable() {
        if (modeValue.get().equalsIgnoreCase("redesky") && redeskyTimerBoostValue.get()) {
            currentTimer = redeskyTimerBoostStartValue.get();
        }
        ticks = 0;
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(jumped) {
            final String mode = modeValue.get();

            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false;
                canMineplexBoost = false;

                if (mode.equalsIgnoreCase("NCP")) {
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                }
                return;
            }

            switch (mode.toLowerCase()) {
                case "ncp":
                    MovementUtils.strafe(MovementUtils.getSpeed() * (canBoost ? ncpBoostValue.get() : 1F));
                    canBoost = false;
                    break;
                case "aacv1":
                    mc.thePlayer.motionY += 0.05999D;
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.08F);
                    break;
                case "aacv2":
                case "mineplex3":
                    mc.thePlayer.jumpMovementFactor = 0.09F;
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999;
                    mc.thePlayer.jumpMovementFactor = 0.08F;
                    MovementUtils.strafe();
                    break;
                case "aacv3":
                    final EntityPlayerSP player = mc.thePlayer;

                    if (player.fallDistance > 0.5F && !teleported) {
                        double value = 3;
                        EnumFacing horizontalFacing = player.getHorizontalFacing();
                        double x = 0;
                        double z = 0;
                        switch (horizontalFacing) {
                            case NORTH:
                                z = -value;
                                break;
                            case EAST:
                                x = +value;
                                break;
                            case SOUTH:
                                z = +value;
                                break;
                            case WEST:
                                x = -value;
                                break;
                        }

                        player.setPosition(player.posX + x, player.posY, player.posZ + z);
                        teleported = true;
                    }
                    break;
                case "mineplex":
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999;
                    mc.thePlayer.jumpMovementFactor = 0.08F;
                    MovementUtils.strafe();
                    break;
                case "mineplex2":
                    if (!canMineplexBoost)
                        break;

                    mc.thePlayer.jumpMovementFactor = 0.1F;

                    if (mc.thePlayer.fallDistance > 1.5F) {
                        mc.thePlayer.jumpMovementFactor = 0F;
                        mc.thePlayer.motionY = -10F;
                    }
                    MovementUtils.strafe();
                    break;
                // add timer to use longjump longer forward without boost
                case "aacv4":
                    mc.thePlayer.jumpMovementFactor = 0.05837456f;
                    mc.timer.timerSpeed = 0.5F;
                    break;
                //simple lmfao
                case "redeskymaki":
                    mc.thePlayer.jumpMovementFactor = 0.15f;
                    mc.thePlayer.motionY += 0.05F;
                    break;
                case "redesky":
                    if (redeskyTimerBoostValue.get()) {
                        mc.timer.timerSpeed = currentTimer;
                    }
                    if (ticks < redeskyTickValue.get()) {
                        mc.thePlayer.motionY *= redeskyYMultiplier.get();
                        mc.thePlayer.motionX *= redeskyXZMultiplier.get();
                        mc.thePlayer.motionZ *= redeskyXZMultiplier.get();

                        mc.thePlayer.jump();
                    } else {
                        if (redeskyGlideAfterTicksValue.get()) {
                            mc.thePlayer.motionY += 0.03F;
                        }
                        if (redeskyTimerBoostValue.get() && currentTimer > redeskyTimerBoostEndValue.get()) {
                            currentTimer -= 0.05F * redeskyTimerBoostSlowDownSpeedValue.get();
                        }
                    }
                    ticks++;
                    break;
                case "infiniteredesky":
                    if(mc.thePlayer.fallDistance > -0.6F) 
                        mc.thePlayer.motionY += 0.02F;
                
                    MovementUtils.strafe((float) Math.min(0.85, Math.max(0.25, MovementUtils.getSpeed() * 1.05878)));
            }
        }

        if(autoJumpValue.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
                jumped = true;
                mc.thePlayer.jump();

        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        final String mode = modeValue.get();

        if (mode.equalsIgnoreCase("mineplex3")) {
            if(mc.thePlayer.fallDistance != 0)
                mc.thePlayer.motionY += 0.037;
        } else if (mode.equalsIgnoreCase("ncp") && !MovementUtils.isMoving() && jumped) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            event.zeroXZ();
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onJump(final JumpEvent event) {
        jumped = true;
        canBoost = true;
        teleported = false;

        if(getState()) {
            switch(modeValue.get().toLowerCase()) {
                case "mineplex":
                    event.setMotion(event.getMotion() * 4.08f);
                    break;
                case "mineplex2":
                    if(mc.thePlayer.isCollidedHorizontally) {
                        event.setMotion(2.31f);
                        canMineplexBoost = true;
                        mc.thePlayer.onGround = false;
                    }
                    break;
                case "aacv4":
                    event.setMotion(event.getMotion() * 1.0799F);
               break;
            }
        }

    }

    public void onDisable(){
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
