/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Container.class)
public class ContainerMixin_PlaySound {

    @Inject(method = "putStackInSlot", at = @At("HEAD"))
    private void patcher$playArmorBreakingSound(int slotID, ItemStack stack, CallbackInfo ci) {
        if (!Minecraft.getMinecraft().theWorld.isRemote || stack != null) {
            return;
        }

        Container container = (Container) (Object) this;
        if (slotID >= 5 && slotID <= 8 && container instanceof ContainerPlayer) {
            Slot slot = container.getSlot(slotID);
            if (slot != null) {
                ItemStack slotStack = slot.getStack();
                if (slotStack != null && slotStack.getItem() instanceof ItemArmor && slotStack.getItemDamage() > slotStack.getMaxDamage() - 2) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.break")));
                }
            }
        }
    }
}
