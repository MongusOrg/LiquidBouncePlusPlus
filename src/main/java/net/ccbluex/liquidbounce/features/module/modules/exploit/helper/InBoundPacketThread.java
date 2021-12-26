/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.exploit.helper;

import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;

public class InBoundPacketThread extends Thread {
    private final Packet<INetHandlerPlayClient> packet;
    private final long sendDelay;

    public InBoundPacketThread(Packet<INetHandlerPlayClient> packet, long delay) {
        this.packet = packet;
        this.sendDelay = delay;
    }

    @Override
    public void run() {
        try {
            sleep(this.sendDelay);
            packet.processPacket(Minecraft.getMinecraft().getNetHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}