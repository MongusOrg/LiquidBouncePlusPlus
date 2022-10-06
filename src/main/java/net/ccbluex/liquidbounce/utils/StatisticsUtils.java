/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.event.EntityKilledEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.minecraft.entity.player.EntityPlayer;

public class StatisticsUtils implements Listenable {
    private static int kills;
    private static int won;

    @EventTarget
    public void onTargetKilled(EntityKilledEvent event) {
        if (!(event.getTargetEntity() instanceof EntityPlayer)) {
            return;
        }

        kills++;
    }

    public static void addWons() {
        won++;
    }

    public static int getWons() {
        return won;
    }

    public static int getKills() {
        return kills;
    }

    @Override
    public boolean handleEvents() { return true; }
}
