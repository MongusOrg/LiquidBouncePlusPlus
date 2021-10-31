/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.performance;

import net.ccbluex.liquidbounce.features.module.modules.misc.Patcher;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldClient.class)
public class WorldClientMixin_AnimationTick {
    @ModifyConstant(method = "doVoidFogParticles", constant = @Constant(intValue = 1000))
    private int patcher$lowerTickCount(int original) {
        return Patcher.lowAnimationTick.get() ? 100 : original;
    }
}
