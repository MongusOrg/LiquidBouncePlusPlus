/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenBook.class)
public abstract class GuiScreenBookMixin_ResolveRenderLayer extends GuiScreen {

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreenBook;handleComponentHover(Lnet/minecraft/util/IChatComponent;II)V"))
    private void patcher$callSuper(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreenBook;handleComponentHover(Lnet/minecraft/util/IChatComponent;II)V", shift = At.Shift.AFTER), cancellable = true)
    private void patcher$cancelFurtherRendering(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();
    }
}
