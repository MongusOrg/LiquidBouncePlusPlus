/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/ExploitFix. Please credit them when you use this in your repository.
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacketBuffer.class)
public abstract class PacketBufferMixin_FixLog4jRCE {

    @Shadow
    public abstract String readStringFromBuffer(int maxLength);

    /**
     * @author liulihaocai
     */
    @Inject(method = "readChatComponent", at = @At("HEAD"), cancellable = true)
    public void readChatComponent(CallbackInfoReturnable<IChatComponent> cir) {
        String str = this.readStringFromBuffer(32767);
        int exploitIndex = str.indexOf("${");
        if(exploitIndex != -1 && str.lastIndexOf("}") > exploitIndex) { // log4j RCE exploit
            str = str.replaceAll("\\$\\{", "\\$\u0000{");
        }

        // font renderer in minecraft won't render \u0000
        cir.setReturnValue(IChatComponent.Serializer.jsonToComponent(str));
        cir.cancel(); // dont use overwrite to make compatibility with other mods
    }
}