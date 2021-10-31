/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.accessors;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngame.class)
public interface GuiIngameAccessor {
    @Accessor
    String getDisplayedTitle();

    @Accessor
    String getDisplayedSubTitle();

    @Accessor
    void setDisplayedTitle(String title);

    @Accessor
    void setDisplayedSubTitle(String subTitle);

    @Invoker
    boolean callShowCrosshair();
}
