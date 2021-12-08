/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.util.enhancement;

import net.ccbluex.liquidbounce.patcher.util.enhancement.text.EnhancedFontRenderer;

import java.util.HashMap;
import java.util.Map;

public class EnhancementManager {

    private static final EnhancementManager instance = new EnhancementManager();
    private final Map<Class<? extends Enhancement>, Enhancement> enhancementMap = new HashMap<>();

    public EnhancementManager() {
        enhancementMap.put(EnhancedFontRenderer.class, new EnhancedFontRenderer());
    }

    public void tick() {
        for (Map.Entry<Class<? extends Enhancement>, Enhancement> entry : enhancementMap.entrySet()) {
            entry.getValue().tick();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Enhancement> T getEnhancement(Class<T> enhancement) {
        return (T) enhancementMap.get(enhancement);
    }

    public static EnhancementManager getInstance() {
        return instance;
    }
}