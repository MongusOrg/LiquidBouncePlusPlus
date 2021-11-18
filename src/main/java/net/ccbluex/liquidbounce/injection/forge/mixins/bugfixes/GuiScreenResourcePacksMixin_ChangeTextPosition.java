/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiScreenResourcePacks.class)
public class GuiScreenResourcePacksMixin_ChangeTextPosition {
    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 77))
    private int patcher$moveInformationText(int original) {
        return !Loader.isModLoaded("ResourcePackOrganizer") ? 102 : original;
    }
}
