/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
 */
package net.ccbluex.liquidbounce.injection.access;

import net.minecraft.world.IBlockAccess;

public interface IBlock {
    int getLightValue(IBlockAccess var1, int var2, int var3, int var4);

    int getLightOpacity(IBlockAccess var1, int var2, int var3, int var4);
}

