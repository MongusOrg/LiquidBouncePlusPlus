/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.hooks.misc;

import net.ccbluex.liquidbounce.injection.forge.mixins.accessors.ResourcePackRepositoryAccessor;
import net.minecraft.client.resources.ResourcePackRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ResourcePackRepositoryHook {
    public static void updateRepositoryEntriesAll(ResourcePackRepository repository) {
        ResourcePackRepositoryAccessor accessor = (ResourcePackRepositoryAccessor) repository;

        final Map<Integer, ResourcePackRepository.Entry> all = new HashMap<>();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntriesAll()) {
            all.put(entry.hashCode(), entry);
        }

        final Set<ResourcePackRepository.Entry> newSet = new LinkedHashSet<>();
        for (File file : accessor.callGetResourcePackFiles()) {
            final ResourcePackRepository.Entry entry = repository.new Entry(file);
            final int entryHash = entry.hashCode();
            if (!all.containsKey(entryHash)) {
                try {
                    entry.updateResourcePack();
                    newSet.add(entry);
                } catch (Exception ignored) {
                    newSet.remove(entry);
                }
            } else {
                newSet.add(all.get(entryHash));
            }
        }

        for (ResourcePackRepository.Entry entry : all.values()) {
            if (!newSet.contains(entry)) {
                entry.closeResourcePack();
            }
        }

        accessor.setRepositoryEntriesAll(new ArrayList<>(newSet));
    }
}
