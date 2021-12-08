/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes.render.item;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin_HexColorTooltip {
    @Redirect(
        method = "getTooltip",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;toHexString(I)Ljava/lang/String;")
    )
    private String patcher$fixHexColorString(int i) {
        return String.format("%06X", i);
    }
}
