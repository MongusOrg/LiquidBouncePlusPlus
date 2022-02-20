/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.optimize;

import net.ccbluex.liquidbounce.injection.access.IMixinWorldAccess;
import net.minecraft.world.WorldManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value=WorldManager.class)
public abstract class MixinWorldManager implements IMixinWorldAccess {
    @Override
    public void notifyLightSet(int n, int n2, int n3) {
    }
}
