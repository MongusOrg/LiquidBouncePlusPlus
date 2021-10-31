/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes.crashes;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin_ResolveCrash {

    @Shadow @Final private List<ServerListEntryNormal> serverListInternet;
    @Shadow @Final private GuiListExtended.IGuiListEntry lanScanEntry;

    @Inject(method = "getListEntry", at = @At("HEAD"), cancellable = true)
    private void patcher$resolveIndexError(int index, CallbackInfoReturnable<GuiListExtended.IGuiListEntry> cir) {
        if (index > this.serverListInternet.size()) {
            cir.setReturnValue(this.lanScanEntry);
        }
    }
}
