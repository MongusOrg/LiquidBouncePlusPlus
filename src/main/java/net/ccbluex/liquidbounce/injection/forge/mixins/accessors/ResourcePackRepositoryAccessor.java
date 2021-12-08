/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.accessors;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;
import java.util.List;

@Mixin(ResourcePackRepository.class)
public interface ResourcePackRepositoryAccessor {
    @Invoker
    List<File> callGetResourcePackFiles();

    @Accessor
    void setRepositoryEntriesAll(List<ResourcePackRepository.Entry> entries);
}
