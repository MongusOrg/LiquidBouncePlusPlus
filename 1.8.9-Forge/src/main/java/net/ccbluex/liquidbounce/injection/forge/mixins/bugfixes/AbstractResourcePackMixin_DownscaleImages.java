/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.AbstractResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Mixin(AbstractResourcePack.class)
public abstract class AbstractResourcePackMixin_DownscaleImages {

    @Shadow protected abstract InputStream getInputStreamByName(String name) throws IOException;

    @Inject(method = "getPackImage", at = @At("HEAD"), cancellable = true)
    private void patcher$downscalePackImage(CallbackInfoReturnable<BufferedImage> cir) throws IOException {
        BufferedImage image = TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
        if (image == null) {
            cir.setReturnValue(null);
            return;
        }

        if (image.getWidth() <= 64 && image.getHeight() <= 64) {
            cir.setReturnValue(image);
            return;
        }

        BufferedImage downscaledIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = downscaledIcon.getGraphics();
        graphics.drawImage(image, 0, 0, 64, 64, null);
        graphics.dispose();
        cir.setReturnValue(downscaledIcon);
    }
}
