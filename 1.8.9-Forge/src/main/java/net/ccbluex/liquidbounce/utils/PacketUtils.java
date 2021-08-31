/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.TickEvent;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;

import net.ccbluex.liquidbounce.utils.timer.MSTimer;

import java.util.ArrayList;

public class PacketUtils extends MinecraftInstance implements Listenable {

    private static int inBound, outBound = 0;
    public static int avgInBound, avgOutBound = 0;

    private static ArrayList<Packet<INetHandlerPlayServer>> packets = new ArrayList<Packet<INetHandlerPlayServer>>();

    private static MSTimer packetTimer = new MSTimer();

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket().getClass().getSimpleName().startsWith("C")) outBound++;
        else inBound++;
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (packetTimer.hasTimePassed(1000L)) {
            avgInBound = inBound; avgOutBound = outBound;
            inBound = outBound = 0;
            packetTimer.reset();
        }
    }

    public static void sendPacketNoEvent(Packet<INetHandlerPlayServer> packet) {
        packets.add(packet);
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static boolean handleSendPacket(Packet<?> packet) {
        if (packets.contains(packet)) {
            packets.remove(packet);
            return true;
        }
        return false;
    }

    /**
     * @return wow
     */
    @Override
    public boolean handleEvents() {
        return true;
    }
    
}