/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.performance;

import net.ccbluex.liquidbounce.features.module.modules.misc.Patcher;
import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin_SkipProgress {
    @Inject(method = "setLoadingProgress", at = @At("HEAD"), cancellable = true)
    private void patcher$skipProgress(int progress, CallbackInfo ci) {
        if (progress < 0 || Patcher.optimizedWorldSwapping.get()) {
            ci.cancel();
        }
    }
}
