/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.resources;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.client.resources.DefaultResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultResourcePack.class)
public class MixinDefaultResourcePack {

    public static final Set wdl_defaultResourceDomains = ImmutableSet.of("minecraft", "realms", "wdl");

    @Overwrite
    public Set getResourceDomains() {
        return wdl_defaultResourceDomains;
    }

}
