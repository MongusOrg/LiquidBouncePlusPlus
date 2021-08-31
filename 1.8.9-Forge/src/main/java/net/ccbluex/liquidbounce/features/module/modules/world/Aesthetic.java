/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleInfo(name = "Aesthetic", description = "Change your world time and weather client-side.", category = ModuleCategory.WORLD)
public class Aesthetic extends Module {

    private final ListValue mode = new ListValue("Time-Mode", new String[] {"Static", "Cycle"}, "Static");
    private final ListValue weathermode = new ListValue("Weather-Mode", new String[] {"Clear", "Rain"}, "Clear");
    private final IntegerValue cycleSpeed = new IntegerValue("Cycle-Speed", 24, 1, 24);
    private final BoolValue reverseCycle = new BoolValue("Reverse-Cycle", false);
    private final IntegerValue time = new IntegerValue("Static-Time", 24000, 0, 24000);
    private final FloatValue rainstrength = new FloatValue("Rain-Strength", 0.1F, 0.1F, 0.5F);

    private final BoolValue displayTag = new BoolValue("Display-Tag", false);

    private int timeCycle = 0;

    public void onEnable() {
        timeCycle = 0; //reset
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mode.get().equalsIgnoreCase("static")) {
            mc.theWorld.setWorldTime(time.get());
        } else {
            mc.theWorld.setWorldTime(timeCycle);
            timeCycle += (reverseCycle.get() ? -cycleSpeed.get() : cycleSpeed.get()) * 10;

            if (timeCycle > 24000) {
                timeCycle = 0;
            } else if (timeCycle < 0) {
                timeCycle = 24000;
            }
        }
        if (weathermode.get().equalsIgnoreCase("clear")) {
            mc.theWorld.setRainStrength(0F);
        } else {
            mc.theWorld.setRainStrength(rainstrength.get());
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if(packet instanceof S03PacketTimeUpdate) {
            event.cancelEvent();
        }
    }

    @Override
    public String getTag() {
        return (displayTag.get() ? "Time: " + (mode.get().equalsIgnoreCase("cycle") ? "Cycle" + (reverseCycle.get() ? ", Reverse" : "") : "Static, " + time.get().toString()) + " | Weather: " + weathermode.get() : null);
    }
}
