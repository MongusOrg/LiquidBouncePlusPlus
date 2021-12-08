/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.performance;

import net.ccbluex.liquidbounce.features.module.modules.misc.Patcher;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public class ModelRendererMixin_BatchDrawing {
    @Shadow private boolean compiled;

    private boolean patcher$compiledState;

    @Inject(method = "render", at = @At("HEAD"))
    private void patcher$resetCompiled(float j, CallbackInfo ci) {
        if (patcher$compiledState != Patcher.batchModelRendering.get()) {
            this.compiled = false;
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/Tessellator;getWorldRenderer()Lnet/minecraft/client/renderer/WorldRenderer;"))
    private void patcher$beginRendering(CallbackInfo ci) {
        this.patcher$compiledState = Patcher.batchModelRendering.get();
        if (Patcher.batchModelRendering.get()) {
            Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEndList()V", remap = false))
    private void patcher$draw(CallbackInfo ci) {
        if (Patcher.batchModelRendering.get()) {
            Tessellator.getInstance().draw();
        }
    }
}
