/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes.crashes;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(ResourcePackRepository.class)
public class ResourcePackRepositoryMixin_ResolveCrash {

    @Shadow @Final private File dirServerResourcepacks;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Inject(method = "deleteOldServerResourcesPacks", at = @At("HEAD"))
    private void patcher$createDirectory(CallbackInfo ci) {
        if (!this.dirServerResourcepacks.exists()) {
            this.dirServerResourcepacks.mkdirs();
        }
    }
}
