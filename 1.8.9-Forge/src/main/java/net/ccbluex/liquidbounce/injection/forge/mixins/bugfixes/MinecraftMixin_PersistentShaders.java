/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.ccbluex.liquidbounce.features.module.modules.misc.Patcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin_PersistentShaders {
    @Redirect(
        method = "runTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V")
    )
    private void patcher$keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {
        if (!Patcher.keepShadersOnPerspectiveChange.get()) {
            entityRenderer.loadEntityShader(entityIn);
        }
    }
}
