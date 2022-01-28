/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.event.WorldEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "AntiStaff", spacedName = "Anti Staff", description = "(TESTING PURPOSE) Detects suspicious entities spawn and auto leave if possible to prevent further ban.", category = ModuleCategory.MISC)
public class AntiStaff extends Module {

    private final ListValue detectionAction = new ListValue("Detection-Action", new String[] {"LeaveMap", "LeaveServer", "None"}, "LeaveMap");
    private final BoolValue waitForGameStart = new BoolValue("WaitForStartGame", true);
    private final BoolValue invisibleCheck = new BoolValue("InvisibleCheck", true);
    private final IntegerValue minimumAliveTick = new IntegerValue("Minimum-AliveTick", 20, 0, 200);
    private final IntegerValue minimumEntityTick = new IntegerValue("Minimum-EntityTick", 1, 0, 200);
    private final IntegerValue collectDelay = new IntegerValue("GarbageCollect-Delay", 40, 1, 200);

    private final List<int[]> possiblePositions = new ArrayList<>();
    private final List<Entity> possibleStaffs = new ArrayList<>();
    private final List<Entity> possiblePlayers = new ArrayList<>();
    private boolean shouldActive = false;
    private boolean sentAction = false;

    @Override
    public void onDisable() {
        clearAll();
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || !shouldActive)
            return;

        if (mc.thePlayer.ticksExisted < minimumAliveTick.get())
            return;

        if (mc.thePlayer.ticksExisted % collectDelay.get() == 0)
            possiblePositions.clear();

        possiblePositions.add(new int[] { (int)mc.thePlayer.posX, (int)mc.thePlayer.posZ });

        if (!possibleStaffs.isEmpty() && !sentAction) {
            switch (detectionAction.get().toLowerCase()) {
                case "leavemap":
                    mc.thePlayer.sendChatMessage("/leave");
                    LiquidBounce.hud.addNotification(new Notification("Attempted to leave the game. (BlocksMC)", Notification.Type.SUCCESS));
                    break;
                case "leaveserver":
                    mc.theWorld.sendQuittingDisconnectingPacket();
			        LiquidBounce.hud.addNotification(new Notification("Attempted to disconnect from the server. (may crash if you leave)", Notification.Type.SUCCESS));
                    break;
            }
            sentAction = true;
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S02PacketChat && !shouldActive) {
            S02PacketChat packetChat = (S02PacketChat) packet;

            if (packetChat.getChatComponent().getUnformattedText().toLowerCase().startsWith("cages opened")) {
                LiquidBounce.hud.addNotification(new Notification("Activated staff checks.", Notification.Type.SUCCESS));
                for (EntityPlayer entity : mc.theWorld.playerEntities) {
                    if (invisibleCheck.get() && entity.isInvisible() && !possibleStaffs.contains(entity)) // check if entity never existed in the world before
                    {
                        LiquidBounce.hud.addNotification(new Notification("Found an unknown invisible entity but game is not started yet: " + entity.getName(), Notification.Type.ERROR));
                        possibleStaffs.add(entity);
                        continue;
                    }
                    possiblePlayers.add(entity);
                }
                shouldActive = true;
            }
        }

        if (packet instanceof S14PacketEntity && shouldActive) {
            final S14PacketEntity packetEntity = (S14PacketEntity) event.getPacket();
            final Entity entity = packetEntity.getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer && (!possiblePlayers.contains(entity) && !possibleStaffs.contains(entity))) {
                // position check, most important part
                if (entity.ticksExisted >= minimumEntityTick.get()) { // might have tp delay
                    if (invisibleCheck.get() && entity.isInvisible() && !possiblePlayers.contains(entity)) // check if entity never existed in the world before
                    {
                        LiquidBounce.hud.addNotification(new Notification("Found an unknown invisible entity, possible staff: " + entity.getName(), Notification.Type.ERROR));
                        possibleStaffs.add(entity);
                        return;
                    }
                    for (int[] positions : possiblePositions) {
                        if ((positions[0] == (int)entity.prevPosX && positions[1] == (int)entity.prevPosZ) 
                            || (positions[0] == (int)entity.posX && positions[1] == (int)entity.posZ)) {
                            LiquidBounce.hud.addNotification(new Notification("Found a suspicious teleported entity, possible staff: " + entity.getName(), Notification.Type.ERROR));
                            possibleStaffs.add(entity);
                            return;
                        }
                    }
                }
            }

            possiblePlayers.add(entity);
        }
    }

    @EventTarget
    public void onWorld(final WorldEvent event) {
        clearAll();
    }

    private void clearAll() {
        shouldActive = !waitForGameStart.get();
        sentAction = false;
        possiblePositions.clear();
        possibleStaffs.clear();
        possiblePlayers.clear();
    }

}
