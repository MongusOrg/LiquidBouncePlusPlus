/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityBannerRenderer.class)
public class TileEntityBannerRendererMixin_BannerAnimation {
    @Redirect(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityBanner;DDDFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTotalWorldTime()J"))
    private long patcher$resolveOverflow(World world) {
        return world.getTotalWorldTime() % 100L;
    }
}
