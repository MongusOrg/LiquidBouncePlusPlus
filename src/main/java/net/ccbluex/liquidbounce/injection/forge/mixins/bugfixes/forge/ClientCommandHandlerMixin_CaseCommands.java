/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes.forge;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(ClientCommandHandler.class)
public class ClientCommandHandlerMixin_CaseCommands {
    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyArg(method = {"executeCommand", "func_71556_a"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), remap = false)
    private Object patcher$makeLowerCaseForGet(Object s) {
        if (s instanceof String) {
            return ((String) s).toLowerCase(Locale.ENGLISH);
        }
        return s;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = {"executeCommand", "func_71556_a"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void patcher$checkForSlash(ICommandSender sender, String message, CallbackInfoReturnable<Integer> cir) {
        if (!message.trim().startsWith("/")) {
            cir.setReturnValue(0);
        }
    }
}
