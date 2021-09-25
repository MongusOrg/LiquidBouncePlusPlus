/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.event.WorldEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.entity.EntityPlayerSP;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

import net.minecraft.network.play.client.*;

@ModuleInfo(name = "BowJump", spacedName = "Bow Jump", description = "Allows you to jump further with auto bow shoot.", category = ModuleCategory.MOVEMENT)
public class BowJump extends Module {

    private final FloatValue boostValue = new FloatValue("Boost", 4.25F, 0F, 10F);
    private final FloatValue heightValue = new FloatValue("Height", 0.42F, 0F, 10F);
    private final IntegerValue delayBeforeLaunch = new IntegerValue("DelayBeforeArrowLaunch", 2, 2, 20);

    private int bowState = 0;
    private long lastPlayerTick = 0;

    public void onEnable() {
        if (mc.thePlayer == null) return;
        bowState = 0;
        lastPlayerTick = -1;

        MovementUtils.strafe(0);
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.onGround && bowState < 3)
            event.cancelEvent();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        switch (bowState) {
        case 0:
            int slot = getBowSlot();
            if (slot < 0 || !mc.thePlayer.inventory.hasItem(Items.arrow)) {
                bowState = 4;
                break; // nothing to shoot
            } else if (lastPlayerTick == -1) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot + 36).getStack();

                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.updateController();

                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, mc.thePlayer.onGround));
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventoryContainer.getSlot(slot + 36).getStack(), 0, 0, 0));

                lastPlayerTick = mc.thePlayer.ticksExisted;
                bowState = 1;
            }
            break;
        case 1:
            if (mc.thePlayer.ticksExisted - lastPlayerTick > delayBeforeLaunch.get()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, mc.thePlayer.onGround));
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                bowState = 2;
            }
            break;
        case 2:
            if (mc.thePlayer.hurtTime > 0)
                bowState = 3;
            break;
        case 3:
            MovementUtils.strafe(boostValue.get());
            mc.thePlayer.motionY = heightValue.get();
            bowState = 4;
            break;
        }

        if (bowState == 4) 
            this.setState(false);
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        this.setState(false); //prevent weird things
    }

    public void onDisable(){
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    private int getBowSlot() {
        for(int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBow) {
                return i - 36;
            }
        }
        return -1;
    }

    @Override
    public String getTag() {
        switch (bowState) {
            case 0:
            return "Idle...";
            case 1:
            return "Preparing...";
            case 2:
            return "Waiting for damage...";
            case 3:
            return "Boost!";
            default:
            return "Task completed.";
        }
    }
}
