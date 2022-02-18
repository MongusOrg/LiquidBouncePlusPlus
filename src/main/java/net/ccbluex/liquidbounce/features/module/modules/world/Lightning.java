/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

@ModuleInfo(name = "Lightning", description = "check lightning spawn", category = ModuleCategory.WORLD)
public class Lightning extends Module {
    public final BoolValue chatValue = new BoolValue("Chat", true);
    public final BoolValue notifValue = new BoolValue("Notification", false);

    @EventTarget
    public void onPacket(PacketEvent event){
        if(event.getPacket() instanceof S2CPacketSpawnGlobalEntity && ((S2CPacketSpawnGlobalEntity) event.getPacket()).func_149053_g() == 1){
            S2CPacketSpawnGlobalEntity entity = ((S2CPacketSpawnGlobalEntity) event.getPacket());
            final int distance = (int) mc.thePlayer.getDistance(entity.func_149051_d(), entity.func_149050_e(), entity.func_149049_f());

            if (chatValue.get()) 
                ClientUtils.displayChatMessage("§7[§6§lLightning§7] §fDetected lightning at §a" + entity.func_149051_d() + " " + entity.func_149050_e() + " " + entity.func_149049_f() + " §7(" + distance + " blocks away)");

            if (notifValue.get())
                LiquidBounce.hud.addNotification(new Notification("Detected lightning at " + entity.func_149051_d() + " " + entity.func_149050_e() + " " + entity.func_149049_f() + " (" + distance + " blocks away)", Notification.Type.WARNING, 3000L));
        }
    }
}