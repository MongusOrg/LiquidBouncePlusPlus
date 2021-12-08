/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.util.world;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.culling.ICamera;

@SuppressWarnings("unused")
public class ParticleCulling {

    public static ICamera camera;

    public static boolean shouldRender(EntityFX entityFX) {
        return entityFX != null && (camera == null || entityFX.distanceWalkedModified > -1);
    }
}