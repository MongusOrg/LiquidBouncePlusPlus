/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.optimize;

import net.ccbluex.liquidbounce.features.module.modules.misc.Performance;
import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityFX.class})
public class MixinEntityFX {
    @Redirect(method={"renderParticle"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/particle/EntityFX;getBrightnessForRender(F)I"))
    private int renderParticle(EntityFX entityFX, float f) {
        return Performance.staticParticleColorValue.get() ? 0xF000F0 : entityFX.getBrightnessForRender(f);
    }
}
