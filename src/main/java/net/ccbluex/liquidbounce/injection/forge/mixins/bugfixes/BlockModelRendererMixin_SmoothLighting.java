/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")
public class BlockModelRendererMixin_SmoothLighting {
    @Redirect(
        method = "updateVertexBrightness(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;[FLjava/util/BitSet;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;isTranslucent()Z"
        )
    )
    private boolean patcher$betterSmoothLighting(Block block) {
        return !block.isVisuallyOpaque() || block.getLightOpacity() == 0;
    }
}
