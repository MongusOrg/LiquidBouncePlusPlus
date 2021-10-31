/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.performance.forge;

import net.ccbluex.liquidbounce.patcher.hooks.font.FontRendererHook;
import com.google.common.base.CharMatcher;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FMLClientHandler.class)
@SuppressWarnings("UnstableApiUsage")
public class FMLClientHandlerMixin_Optimization {
    private static final CharMatcher patcher$DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf(FontRendererHook.characterDictionary).negate();

    /**
     * @author LlamaLad7
     * @reason Performance improvement
     */
    @Overwrite(remap = false)
    public String stripSpecialChars(String var1) {
        return patcher$DISALLOWED_CHAR_MATCHER.removeFrom(StringUtils.stripControlCodes(var1));
    }

    @Redirect(method = "addModAsResource", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/registry/LanguageRegistry;loadLanguagesFor(Lnet/minecraftforge/fml/common/ModContainer;Lnet/minecraftforge/fml/relauncher/Side;)V"), remap = false)
    private void patcher$avoidLanguageLoading(LanguageRegistry languageRegistry, ModContainer container, Side side) {
        // No-op
    }
}
