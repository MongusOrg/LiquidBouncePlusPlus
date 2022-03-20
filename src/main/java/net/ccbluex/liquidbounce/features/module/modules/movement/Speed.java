/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
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
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.verus.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.spartan.SpartanYPort;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
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
            new AACBHop(),
            new AAC2BHop(),
            new AAC3BHop(),
            new AAC4BHop(),
            new AAC5BHop(),
            new AAC6BHop(),
            new AAC7BHop(),
            new OldAACBHop(),
            new AACPort(),
            new AACLowHop(),
            new AACLowHop2(),
            new AACLowHop3(),
            new AACGround(),
            new AACGround2(),
            new AACHop350(),
            new AACHop3313(),
            new AACYPort(),
            new AACYPort2(),

            // Hypixel
            new HypixelBoost(),
            new HypixelStable(),
            //new HypixelHop(),

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
            new YPort2(),
        
            // Verus
            new VerusHop(),
            new VerusLowHop(),
            new VerusHard()
    };

    public final ListValue typeValue = new ListValue("Type", new String[]{"NCP", "AAC", "Spartan", "Hypixel", "Verus", "Custom", "Other"}, "NCP") {

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

    public final ListValue ncpModeValue = new ListValue("NCP-Mode", new String[]{"BHop", "FHop", "SBHop", "Hop", "YPort"}, "BHop", () -> { return typeValue.get().equalsIgnoreCase("ncp"); }) {

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

    public final ListValue aacModeValue = new ListValue("AAC-Mode", new String[]{
        "4Hop", 
        "4SlowHop", 
        "v4BHop",
        "BHop",
        "2BHop",
        "3BHop",
        "4BHop",
        "5BHop",
        "6BHop",
        "7BHop",
        "OldBHop",
        "Port",
        "LowHop",
        "LowHop2",
        "LowHop3",
        "Ground",
        "Ground2",
        "Hop3.5.0",
        "Hop3.3.13",
        "YPort",
        "YPort2"
        }, "4Hop", () -> { return typeValue.get().equalsIgnoreCase("aac"); }) {

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

    public final ListValue hypixelModeValue = new ListValue("Hypixel-Mode", new String[]{"Boost", "Stable"}, "Stable", () -> { return typeValue.get().equalsIgnoreCase("hypixel"); }) { // the worst hypixel bypass ever existed

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

    public final ListValue otherModeValue = new ListValue("Other-Mode", new String[]{"YPort", "YPort2", "Boost", "Frame", "MiJump", "OnGround", "SlowHop", "Jump", "Legit", "AEMine"}, "Boost", () -> { return typeValue.get().equalsIgnoreCase("other"); }) {

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
    
    public final ListValue verusModeValue = new ListValue("Verus-Mode", new String[]{"Hop", "LowHop", "Hard"}, "Hop", () -> { return typeValue.get().equalsIgnoreCase("verus"); }) {

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

    public final BoolValue modifySprint = new BoolValue("ModifySprinting", false);

    public final FloatValue verusTimer = new FloatValue("Verus-Timer", 1F, 0.1F, 10F, () -> { return getModeName().equalsIgnoreCase("verushard"); });

    public final FloatValue speedValue = new FloatValue("CustomSpeed", 1.6f, 0.2f, 2f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final FloatValue launchSpeedValue = new FloatValue("CustomLaunchSpeed", 1.6f, 0.2f, 2f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final FloatValue addYMotionValue = new FloatValue("CustomAddYMotion", 0f, 0f, 2f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final FloatValue yValue = new FloatValue("CustomY", 0f, 0f, 4f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final FloatValue upTimerValue = new FloatValue("CustomUpTimer", 1f, 0.1f, 2f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final FloatValue downTimerValue = new FloatValue("CustomDownTimer", 1f, 0.1f, 2f, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final ListValue strafeValue = new ListValue("CustomStrafe", arrayOf("Strafe", "Boost", "Plus", "PlusOnlyUp", "Non-Strafe"), "Boost", () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final IntegerValue groundStay = new IntegerValue("CustomGroundStay", 0, 0, 10, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final BoolValue groundResetXZValue = new BoolValue("CustomGroundResetXZ", false, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final BoolValue resetXZValue = new BoolValue("CustomResetXZ", false, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final BoolValue resetYValue = new BoolValue("CustomResetY", false, () -> { return typeValue.get().equalsIgnoreCase("custom"); });
    public final BoolValue doLaunchSpeedValue = new BoolValue("CustomDoLaunchSpeed", true, () -> { return typeValue.get().equalsIgnoreCase("custom"); });

    public final BoolValue jumpStrafe = new BoolValue("JumpStrafe", false, () -> { return typeValue.get().equalsIgnoreCase("other"); });

    public final BoolValue sendJumpValue = new BoolValue("SendJump", true, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    public final BoolValue recalcValue = new BoolValue("ReCalculate", true, () -> { return (typeValue.get().equalsIgnoreCase("hypixel") && sendJumpValue.get()); });
    public final FloatValue glideStrengthValue = new FloatValue("GlideStrength", 0.03F, 0F, 0.05F, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    public final FloatValue moveSpeedValue = new FloatValue("MoveSpeed", 1.47F, 1F, 1.7F, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    public final FloatValue jumpYValue = new FloatValue("JumpY", 0.42F, 0F, 1F, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    public final FloatValue baseStrengthValue = new FloatValue("BaseMultiplier", 1F, 0.5F, 1F, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    public final FloatValue baseTimerValue = new FloatValue("BaseTimer", 1.5F, 1F, 3F, () -> { return getModeName().equalsIgnoreCase("hypixelboost"); });
    public final FloatValue baseMTimerValue = new FloatValue("BaseMultiplierTimer", 1F, 0F, 3F, () -> { return getModeName().equalsIgnoreCase("hypixelboost"); });
    public final BoolValue bypassWarning = new BoolValue("BypassWarning", true, () -> { return typeValue.get().equalsIgnoreCase("hypixel"); });
    

    public final FloatValue portMax = new FloatValue("AAC-PortLength", 1, 1, 20, () -> { return typeValue.get().equalsIgnoreCase("aac"); });
    public final FloatValue aacGroundTimerValue = new FloatValue("AACGround-Timer", 3F, 1.1F, 10F, () -> { return typeValue.get().equalsIgnoreCase("aac"); });
/*
    public final FloatValue cubecraftPortLengthValue = new FloatValue("CubeCraft-PortLength", 1F, 0.1F, 2F);
    public final FloatValue mineplexGroundSpeedValue = new FloatValue("MineplexGround-Speed", 0.5F, 0.1F, 1F);
*/
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        if(MovementUtils.isMoving() && modifySprint.get())
            mc.thePlayer.setSprinting(!getModeName().equalsIgnoreCase("verushard"));

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onUpdate();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if(mc.thePlayer.isSneaking() || event.getEventState() != EventState.PRE)
            return;

        if(MovementUtils.isMoving() && modifySprint.get())
            mc.thePlayer.setSprinting(!getModeName().equalsIgnoreCase("verushard"));

        final SpeedMode speedMode = getMode();

        if(speedMode != null) {
            speedMode.onMotion(event);
            speedMode.onMotion();
        }
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

        if (bypassWarning.get() && typeValue.get().equalsIgnoreCase("hypixel")) {
            LiquidBounce.hud.addNotification(new Notification("If you have understood the risk, you can turn this notification off in settings.", Notification.Type.INFO, 3000L));
            LiquidBounce.hud.addNotification(new Notification("Don't use Hypixel speeds on Hypixel as they patched Strafe and will ban you really fast!", Notification.Type.WARNING, 3000L));
        }

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
        mc.gameSettings.keyBindJump.pressed = (mc.thePlayer != null && (mc.inGameHasFocus || LiquidBounce.moduleManager.getModule(GuiMove.class).getState()) && !(mc.currentScreen instanceof GuiIngameMenu || mc.currentScreen instanceof GuiChat) && GameSettings.isKeyDown(mc.gameSettings.keyBindJump));

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onDisable();
    }

    @Override
    public String getTag() {
        return typeValue.get() == "Other" ? otherModeValue.get() : typeValue.get() == "Custom" ? "Custom" : typeValue.get() + ", " + getOnlySingleName();
    }

    private String getOnlySingleName() {
        String mode = "";
        switch (typeValue.get()) {
            case "NCP":
            mode = ncpModeValue.get();
            break;
            case "AAC":
            mode = aacModeValue.get();
            break;
            case "Spartan":
            mode = "Spartan";
            break;
            case "Hypixel":
            mode = hypixelModeValue.get();
            break;
            case "Verus":
            mode = verusModeValue.get();
            break;
        }
        return mode;
    }

    public String getModeName() {
        String mode = "";
        switch (typeValue.get()) {
            case "NCP":
            if (ncpModeValue.get().equalsIgnoreCase("SBHop")) mode = "SNCPBHop";
            else mode = "NCP" + ncpModeValue.get();
            break;
            case "AAC":
            if (aacModeValue.get().equalsIgnoreCase("oldbhop")) mode = "OldAACBHop";
            else mode = "AAC" + aacModeValue.get();
            break;
            case "Spartan":
            mode = "SpartanYPort";
            break;
            case "Hypixel":
            mode = "Hypixel" + hypixelModeValue.get();
            break;
            case "Verus":
            mode = "Verus" + verusModeValue.get();
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
