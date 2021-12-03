/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.render.BlockOverlay;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.block.PlaceInfo;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.TickTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Tower", description = "Automatically builds a tower beneath you.", category = ModuleCategory.WORLD, keyBind = Keyboard.KEY_O)
public class Tower extends Module {

    /**
     * OPTIONS
     */

    private final ListValue modeValue = new ListValue("Mode", new String[] {
            "Jump", "Motion", "ConstantMotion", "MotionTP", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4"
    }, "Motion");
    private final ListValue autoBlockMode = new ListValue("AutoBlock", new String[]{"Packet", "Off"}, "Packet");
    private final BoolValue stayAutoBlock = new BoolValue("StayAutoBlock", false);
    private final BoolValue swingValue = new BoolValue("Swing", true);
    private final BoolValue stopWhenBlockAbove = new BoolValue("StopWhenBlockAbove", false);
    private final BoolValue rotationsValue = new BoolValue("Rotations", true);

    private final FloatValue maxTurnSpeed = new FloatValue("MaxTurnSpeed", 180F, 0F, 180F) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = minTurnSpeed.get();

            if (i > newValue)
                set(i);
        }
    };

    private final FloatValue minTurnSpeed = new FloatValue("MinTurnSpeed", 180F, 0F, 180F) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = maxTurnSpeed.get();

            if (i < newValue)
                set(i);
        }
    };

    public final ListValue rotationModeValue = new ListValue("RotationMode", new String[]{"Normal","AAC"}, "Normal");
    private final BoolValue keepRotationValue = new BoolValue("KeepRotation", false);
    private final IntegerValue keepLengthValue = new IntegerValue("KeepRotationLength", 0, 0, 20);
    private final BoolValue onJumpValue = new BoolValue("OnJump", false);
    private final ListValue placeModeValue = new ListValue("PlaceTiming", new String[]{"Pre", "Post"}, "Post");

    private final FloatValue timerValue = new FloatValue("Timer", 1F, 0F, 10F);


    // Jump mode
    private final FloatValue jumpMotionValue = new FloatValue("JumpMotion", 0.42F, 0.3681289F, 0.79F);
    private final IntegerValue jumpDelayValue = new IntegerValue("JumpDelay", 0, 0, 20);

    // ConstantMotion
    private final FloatValue constantMotionValue = new FloatValue("ConstantMotion", 0.42F, 0.1F, 1F);
    private final FloatValue constantMotionJumpGroundValue = new FloatValue("ConstantMotionJumpGround", 0.79F, 0.76F, 1F);

    // Teleport
    private final FloatValue teleportHeightValue = new FloatValue("TeleportHeight", 1.15F, 0.1F, 5F);
    private final IntegerValue teleportDelayValue = new IntegerValue("TeleportDelay", 0, 0, 20);
    private final BoolValue teleportGroundValue = new BoolValue("TeleportGround", true);
    private final BoolValue teleportNoMotionValue = new BoolValue("TeleportNoMotion", false);

    // Render
    private final ListValue counterDisplayValue = new ListValue("Counter", new String[]{"Off", "Simple", "Advanced", "Sigma", "Novoline"}, "Simple");

    private final BoolValue blurValue = new BoolValue("Blur-Advanced", false);
    private final FloatValue blurStrength = new FloatValue("Blur-Strength", 1F, 0F, 30F);

    /**
     * MODULE
     */

    // Target block
    private PlaceInfo placeInfo;

    // Rotation lock
    private Rotation lockRotation;

    // Mode stuff
    private final TickTimer timer = new TickTimer();
    private double jumpGround = 0;

    // AutoBlock
    private int slot;

    // Render thingy
    private float progress = 0;
    private long lastMS = 0L;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        slot = mc.thePlayer.inventory.currentItem;
        lastMS = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;

        progress = 0;
        mc.timer.timerSpeed = 1F;
        lockRotation = null;

        if (slot != mc.thePlayer.inventory.currentItem)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (onJumpValue.get() && !mc.gameSettings.keyBindJump.isKeyDown())
            return;

        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null)
            RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, lockRotation, RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));

        mc.timer.timerSpeed = timerValue.get();

        final EventState eventState = event.getEventState();

        if (placeModeValue.get().equalsIgnoreCase(eventState.getStateName()))
            place();

        if (eventState == EventState.PRE) {
            placeInfo = null;
            timer.update();

            final boolean isHeldItemBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
            if (autoBlockMode.get().equalsIgnoreCase("Packet") ? InventoryUtils.findAutoBlockBlock() != -1 || isHeldItemBlock : isHeldItemBlock) {
                if (!stopWhenBlockAbove.get() || BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX,
                        mc.thePlayer.posY + 2, mc.thePlayer.posZ)) instanceof BlockAir)
                    move();

                final BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir) {
                    if (search(blockPos) && rotationsValue.get()) {
                        final VecRotation vecRotation = RotationUtils.faceBlock(blockPos);

                        if (vecRotation != null) {
                            RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, vecRotation.getRotation(), RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));
                            placeInfo.setVec3(vecRotation.getVec());
                        }
                    }
                }
            }
        }
    }

    //Send jump packets, bypasses Hypixel.
    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    /**
     * Move player
     */
    private void move() {
        switch (modeValue.get().toLowerCase()) {
            case "jump":
                if (mc.thePlayer.onGround && timer.hasTimePassed(jumpDelayValue.get())) {
                    fakeJump();
                    mc.thePlayer.motionY = jumpMotionValue.get();
                    timer.reset();
                }
                break;
            case "motion":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.42D;
                } else if (mc.thePlayer.motionY < 0.1D) mc.thePlayer.motionY = -0.3D;
                break;
            case "motiontp":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.42D;
                } else if (mc.thePlayer.motionY < 0.23D)
                    mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                break;
            case "packet":
                if (mc.thePlayer.onGround && timer.hasTimePassed(2)) {
                    fakeJump();
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                            mc.thePlayer.posY + 0.753D, mc.thePlayer.posZ, false));
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1D, mc.thePlayer.posZ);
                    timer.reset();
                }
                break;
            case "teleport":
                if (teleportNoMotionValue.get())
                    mc.thePlayer.motionY = 0;

                if ((mc.thePlayer.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump();
                    mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + teleportHeightValue.get(), mc.thePlayer.posZ);
                    timer.reset();
                }
                break;
            case "constantmotion":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    jumpGround = mc.thePlayer.posY;
                    mc.thePlayer.motionY = constantMotionValue.get();
                }

                if (mc.thePlayer.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump();
                    mc.thePlayer.setPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ);
                    mc.thePlayer.motionY = constantMotionValue.get();
                    jumpGround = mc.thePlayer.posY;
                }
                break;
            case "aac3.3.9":
                if (mc.thePlayer.onGround) {
                    fakeJump();
                    mc.thePlayer.motionY = 0.4001;
                }
                mc.timer.timerSpeed = 1F;

                if (mc.thePlayer.motionY < 0) {
                    mc.thePlayer.motionY -= 0.00000945;
                    mc.timer.timerSpeed = 1.6F;
                }
                break;
            case "aac3.6.4":
                if (mc.thePlayer.ticksExisted % 4 == 1) {
                    mc.thePlayer.motionY = 0.4195464;
                    mc.thePlayer.setPosition(mc.thePlayer.posX - 0.035, mc.thePlayer.posY, mc.thePlayer.posZ);
                } else if (mc.thePlayer.ticksExisted % 4 == 0) {
                    mc.thePlayer.motionY = -0.5;
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 0.035, mc.thePlayer.posY, mc.thePlayer.posZ);
                }
                break;

        }
    }

    /**
     * Place target block
     */
    private void place() {
        if(placeInfo == null)
            return;

        // AutoBlock
        int blockSlot = -1;
        ItemStack itemStack = mc.thePlayer.getHeldItem();

        if(mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            if (!autoBlockMode.get().equalsIgnoreCase("Packet"))
                return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1)
                return;

            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
            itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
        }

        // Place block
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, this.placeInfo.getBlockPos(), placeInfo.getEnumFacing(), placeInfo.getVec3())) {
            if (swingValue.get())
                mc.thePlayer.swingItem();
            else
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }

        this.placeInfo = null;

        // make tower compatible with scaffold.
        final Scaffold scaffold = (Scaffold) LiquidBounce.moduleManager.getModule(Scaffold.class);
        if(scaffold == null)
            return;
        if(LiquidBounce.moduleManager.getModule(Scaffold.class).getState()){
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionX *= scaffold.speedModifierValue.get();
                mc.thePlayer.motionZ *= scaffold.speedModifierValue.get();
            }
        }

        // Switch back to old slot when using auto block
        if (!stayAutoBlock.get() && blockSlot >= 0)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @return
     */
    private boolean search(final BlockPos blockPosition) {
        if (!BlockUtils.isReplaceable(blockPosition))
            return false;

        final boolean staticYawMode = rotationModeValue.get().equalsIgnoreCase("AAC");

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
                mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        PlaceRotation placeRotation = null;

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor))
                continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());

            for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
                for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                    for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if ((eyesPos.squareDistanceTo(hitVec) > 18D ||
                                distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) ||
                                mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false,
                                        true, false) != null))
                            continue;

                        // face block
                        for (int i = 0; i < (staticYawMode ? 2 : 1); i++) {
                            final double diffX = staticYawMode && i == 0 ? 0 : hitVec.xCoord - eyesPos.xCoord;
                            final double diffY = hitVec.yCoord - eyesPos.yCoord;
                            final double diffZ = staticYawMode && i == 1 ? 0 : hitVec.zCoord - eyesPos.zCoord;

                            final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                            final Rotation rotation = new Rotation(
                                    MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                                    MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                            );

                            final Vec3 rotationVector = RotationUtils.getVectorForRotation(rotation);
                            final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                            final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false,
                                    false, true);

                            if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                                continue;

                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) <
                                    RotationUtils.getRotationDifference(placeRotation.getRotation()))
                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        }
                    }
                }
            }
        }

        if (placeRotation == null)
            return false;

        if (rotationsValue.get()) {
            RotationUtils.setTargetRotation((RotationUtils.limitAngleChange(RotationUtils.serverRotation, placeRotation.getRotation(), RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get()))), keepLengthValue.get());
            lockRotation = placeRotation.getRotation();
        }

        placeInfo = placeRotation.getPlaceInfo();
        return true;
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if(mc.thePlayer == null)
            return;

        final Packet<?> packet = event.getPacket();

        if(packet instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) packet;

            slot = packetHeldItemChange.getSlotId();
        }
    }

    /**
     * Tower visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        progress = (float) (System.currentTimeMillis() - lastMS) / 100F;
        if (progress >= 1) progress = 1;

        final Scaffold scaffold = (Scaffold) LiquidBounce.moduleManager.getModule(Scaffold.class);
        if (scaffold.getState() && !scaffold.counterDisplayValue.get().equalsIgnoreCase("off")) return;
        String counterMode = counterDisplayValue.get();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final String info = getBlocksAmount() + " blocks";
        int infoWidth = Fonts.fontSFUI40.getStringWidth(info);
        int infoWidth2 = Fonts.minecraftFont.getStringWidth(getBlocksAmount()+"");
        if (counterMode.equalsIgnoreCase("simple")) {
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 36, -1, false);
        }
        if (counterMode.equalsIgnoreCase("advanced")) {
            boolean canRenderStack = (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock);
            if (blurValue.get())
                BlurUtils.blurArea(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 39, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - (canRenderStack ? 5 : 26), blurStrength.get());

            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 40, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 39, (getBlocksAmount() > 1 ? 0xFFFFFFFF : 0xFFFF1010));
            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 39, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 26, 0xA0000000);

            if (canRenderStack) {
                RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 8, scaledResolution.getScaledHeight() / 2 - 25, scaledResolution.getScaledWidth() / 2 - 8);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.fontSFUI40.drawCenteredString(info, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 - 36, -1);
        }
        if (counterMode.equalsIgnoreCase("sigma")) {
            GlStateManager.translate(0, -14F - (progress * 4F), 0);
            //GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(0.15F, 0.15F, 0.15F, progress);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 - 3, scaledResolution.getScaledHeight() - 60);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() - 57);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 + 3, scaledResolution.getScaledHeight() - 60);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            //GL11.glPopMatrix();
            RenderUtils.drawRoundedRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() - 60, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() - 74, 2F, new Color(0.15F, 0.15F, 0.15F, progress).getRGB());
            GlStateManager.resetColor();
            Fonts.fontSFUI35.drawCenteredString(info, scaledResolution.getScaledWidth() / 2 + 0.1F, scaledResolution.getScaledHeight() - 70, new Color(1F, 1F, 1F, 0.8F * progress).getRGB(), false);
            GlStateManager.translate(0, 14F + (progress * 4F), 0);
        }
        if (counterMode.equalsIgnoreCase("novoline")) {
            if (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock) {
                //RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 22, scaledResolution.getScaledHeight() / 2 + 16, scaledResolution.getScaledWidth() / 2 - 22);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.minecraftFont.drawString(getBlocksAmount()+" blocks", scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 20, -1, true);
        }
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if (onJumpValue.get())
            event.cancelEvent();
    }

    /**
     * @return hotbar blocks amount
     */
    private int getBlocksAmount() {
        int amount = 0;

        for(int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                if (mc.thePlayer.getHeldItem() == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block))
                    amount += itemStack.stackSize;
            }
        }

        return amount;
    }

    @Override
    public String getTag() {
        return placeModeValue.get();
    }
}
