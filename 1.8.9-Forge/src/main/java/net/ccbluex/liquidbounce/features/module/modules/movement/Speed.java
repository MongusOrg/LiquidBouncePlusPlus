/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.hypixel.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.spartan.SpartanYPort;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.settings.GameSettings;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    public final SpeedMode[] speedModes = new SpeedMode[] {
            // NCP
            new NCPBHop(),
            new NCPFHop(),
            new SNCPBHop(),
            new NCPHop(),
            new NCPYPort(),

            // AAC
            new AAC4Hop(),
            new AAC4SlowHop(),
            new AACv4BHop(),

            // Hypixel
            new HypixelReduceHop(),
            new HypixelLowHop(),

            // Spartan
            new SpartanYPort(),

            // Other
            new SlowHop(),
            new CustomSpeed(),
            new Jump(),
            new Legit(),
            new AEMine(),
            new Boost(),
            new Frame(),
            new MiJump(),
            new OnGround(),
            new YPort(),
            new YPort2()
    };

    public final ListValue typeValue = new ListValue("Type", new String[]{"NCP", "AAC", "Spartan", "Hypixel", "Custom", "Other"}, "NCP") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final ListValue ncpModeValue = new ListValue("NCP-Mode", new String[]{"BHop", "FHop", "SBHop", "Hop", "YPort"}, "BHop") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final ListValue aacModeValue = new ListValue("AAC-Mode", new String[]{"4Hop", "4SlowHop", "v4BHop"}, "4Hop") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final ListValue hypixelModeValue = new ListValue("Hypixel-Mode", new String[]{"ReduceHop", "LowHop"}, "ReduceHop") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final ListValue otherModeValue = new ListValue("Other-Mode", new String[]{"YPort", "YPort2", "Boost", "Frame", "MiJump", "OnGround", "SlowHop", "Jump", "Legit", "AEMine"}, "Boost") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final FloatValue customSpeedValue = new FloatValue("CustomSpeed", 1.0F, 0.2F, 10F);
    public final FloatValue customYValue = new FloatValue("CustomY", 0F, 0F, 4F);
    public final FloatValue customTimerValue = new FloatValue("CustomTimer", 1F, 0.1F, 2F);
    public final BoolValue customStrafeValue = new BoolValue("CustomStrafe", true);
    public final BoolValue resetXZValue = new BoolValue("CustomResetXZ", false);
    public final BoolValue resetYValue = new BoolValue("CustomResetY", false);

    public final BoolValue jumpStrafe = new BoolValue("JumpStrafe", false);
/*
    public final FloatValue portMax = new FloatValue("AAC-PortLength", 1, 1, 20);
    public final FloatValue aacGroundTimerValue = new FloatValue("AACGround-Timer", 3F, 1.1F, 10F);
    public final FloatValue cubecraftPortLengthValue = new FloatValue("CubeCraft-PortLength", 1F, 0.1F, 2F);
    public final FloatValue mineplexGroundSpeedValue = new FloatValue("MineplexGround-Speed", 0.5F, 0.1F, 1F);
*/
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        if(MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onUpdate();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if(mc.thePlayer.isSneaking() || event.getEventState() != EventState.PRE)
            return;

        if(MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onMotion();
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onMove(event);
    }

    @EventTarget
    public void onTick(final TickEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onTick();
    }

    @Override
    public void onEnable() {

        if(mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onEnable();
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;
        mc.gameSettings.keyBindJump.pressed = (mc.thePlayer != null && (mc.inGameHasFocus || LiquidBounce.moduleManager.getModule(InventoryMove.class).getState()) && !(mc.currentScreen instanceof GuiIngameMenu || mc.currentScreen instanceof GuiChat) && GameSettings.isKeyDown(mc.gameSettings.keyBindJump));

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onDisable();
    }

    @Override
    public String getTag() {
        return typeValue.get();
    }

    public String getModeName() {
        String mode = "";
        switch (typeValue.get()) {
            case "NCP":
            if (ncpModeValue.get().equalsIgnoreCase("SBHop")) mode = "SNCPBHop";
            else mode = "NCP" + ncpModeValue.get();
            break;
            case "AAC":
            mode = "AAC" + aacModeValue.get();
            break;
            case "Spartan":
            mode = "SpartanYPort";
            break;
            case "Hypixel":
            mode = "Hypixel" + hypixelModeValue.get();
            break;
            case "Custom":
            mode = "Custom";
            break;
            case "Other":
            mode = otherModeValue.get();
            break;
        }
        return mode;
    }

    public SpeedMode getMode() {
        for(final SpeedMode speedMode : speedModes)
            if(speedMode.modeName.equalsIgnoreCase(getModeName()))
                return speedMode;

        return null;
    }
}
