/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(S2EPacketCloseWindow.class)
public class MixinS2EPacketCloseWindow {
    @Inject(method = "processPacket(Lnet/minecraft/network/play/INetHandlerPlayClient;)V", at = @At("HEAD"), cancellable = true)
    private void patcher$noCloseMyChat(INetHandlerPlayClient handler, CallbackInfo ci) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            ci.cancel();
        }
    }
}
