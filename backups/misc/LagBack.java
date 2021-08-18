package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly;
import net.ccbluex.liquidbounce.features.module.modules.movement.LongJump;
import net.ccbluex.liquidbounce.features.module.modules.movement.HighJump;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.features.module.modules.world.Tower;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.awt.Color;

@ModuleInfo(name = "LagBack", description = "Simple module that auto disable some modules if you got flagback", category = ModuleCategory.MISC)
public class LagBack extends Module {

    private final IntegerValue notifyLength = new IntegerValue("Notify-Seconds", 5, 1, 15);
    private final IntegerValue minTicks = new IntegerValue("TicksExisted", 150, 0, 200);

    private final BoolValue killAuraValue = new BoolValue("KillAura", false);
    private final BoolValue speedValue = new BoolValue("Speed", false);
    private final BoolValue flyValue = new BoolValue("Fly", false);
    private final BoolValue longJumpValue = new BoolValue("LongJump", false);
    private final BoolValue highJumpValue = new BoolValue("HighJump", false);
    private final BoolValue scaffoldValue = new BoolValue("Scaffold", false);
    private final BoolValue towerValue = new BoolValue("Tower", false);

    private long lastSysTime = System.currentTimeMillis();
    boolean shouldDisplay = false;
    String displayMessage = "You can enable modules again after this disappears.";
    float yPos = -30;

    final Speed speed  = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);
    final Fly fly  = (Fly) LiquidBounce.moduleManager.getModule(Fly.class);
    final LongJump longjump  = (LongJump) LiquidBounce.moduleManager.getModule(LongJump.class);
    final HighJump highjump  = (HighJump) LiquidBounce.moduleManager.getModule(HighJump.class);
    final Scaffold scaffold  = (Scaffold) LiquidBounce.moduleManager.getModule(Scaffold.class);
    final Tower tower  = (Tower) LiquidBounce.moduleManager.getModule(Tower.class);
    KillAura killaura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        if(packet instanceof S08PacketPlayerPosLook && mc.thePlayer != null && mc.thePlayer.ticksExisted >= minTicks.get()) {
            if(killaura.getState() && killAuraValue.get()){
                killaura.setState(false);
            }
            if (scaffold.getState() && scaffoldValue.get()) {
                scaffold.setState(false);
            }
            if (tower.getState() && towerValue.get()) {
                tower.setState(false);
            }
            if(MovementUtils.isMoving()){
                if(speed.getState() && speedValue.get()){
                    speed.setState(false);
                }
                if(fly.getState() && flyValue.get()){
                    fly.setState(false);
                }
                if(longjump.getState() && longJumpValue.get()){
                    longjump.setState(false);
                }
                if (highjump.getState() && highJumpValue.get()) {
                    highjump.setState(false);
                }
            }

            shouldDisplay = true;
            lastSysTime = System.currentTimeMillis();
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        yPos += ((shouldDisplay ? 5 : -30) - yPos) * 0.5F * (1 - event.getPartialTicks());
        float strWidth = Fonts.fontSFUI40.getStringWidth(displayMessage);
        float length = (strWidth + 8F) * MathHelper.clamp_float(((notifyLength.get() * 1000L) - (System.currentTimeMillis() - lastSysTime)) / (notifyLength.get() * 1000F), 0F, 1F);

        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2F - (strWidth / 2F) - 4F, yPos, sr.getScaledWidth() / 2F + (strWidth / 2F) + 4F, yPos + 30F, 0.5F, new Color(0, 0, 0, 140).getRGB());
        RenderUtils.drawRect(sr.getScaledWidth() / 2F - (strWidth / 2F) - 4F, yPos + 29F, sr.getScaledWidth() / 2F - (strWidth / 2F) - 4F + length, yPos + 30F, 0.5F, new Color(255, 20, 20).getRGB());

        Fonts.fontSFUI40.drawString("You got flagged.", sr.getScaledWidth() / 2F - (strWidth / 2F), yPos + 4, 0xFFFF0000, false);
        Fonts.fontSFUI40.drawCenteredString(displayMessage, sr.getScaledWidth() / 2F, yPos + 16, -1, false);

        if (shouldDisplay && System.currentTimeMillis() - lastSysTime > notifyLength.get() * 1000L) shouldDisplay = false;
    }

}
