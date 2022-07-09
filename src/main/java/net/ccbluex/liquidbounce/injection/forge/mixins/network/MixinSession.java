/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import net.ccbluex.liquidbounce.features.special.FakeUUID;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Session.class)
public class MixinSession {

    @Shadow @Final private String playerID;

    @Overwrite
    public String getPlayerID() {
        return FakeUUID.getSpoofID() != null ? FakeUUID.getSpoofID() : this.playerID;
    }

}