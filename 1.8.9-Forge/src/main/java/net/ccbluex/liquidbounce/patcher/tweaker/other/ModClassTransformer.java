/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.tweaker.other;

import net.ccbluex.liquidbounce.patcher.asm.optifine.*;
import net.ccbluex.liquidbounce.patcher.asm.optifine.reflectionoptimizations.common.*;
import net.ccbluex.liquidbounce.patcher.optifine.OptiFineGenerations;
import net.ccbluex.liquidbounce.patcher.tweaker.ClassTransformer;
import net.ccbluex.liquidbounce.patcher.tweaker.transform.PatcherTransformer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

/**
 * Used for editing other mods (OptiFine, LevelHead, TNT Timer, etc.) after they've loaded.
 */
public class ModClassTransformer implements IClassTransformer {

    private final Logger logger = LogManager.getLogger("Patcher - Mod Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();

    public ModClassTransformer() {
        MixinEnvironment.getCurrentEnvironment().addTransformerExclusion(getClass().getName());
        // OptiFine loads these classes after we do, overwriting our changes,
        // so transform it AFTER OptiFine loads.

        // OptiFine uses Reflection for compatibility between Forge & itself,
        // and since we know they're using Forge, we're able to change methods back
        // to how they normally were (using Forge's changes).
        //
        // Only I7 and above are supported due to them being the biggest versions of OptiFine.
        final String optifineVersion = ClassTransformer.optifineVersion;
        final OptiFineGenerations generations = ClassTransformer.generations;
        if (generations.getIGeneration().contains(optifineVersion)) {
            registerCommonTransformers();
        } else if (generations.getLGeneration().contains(optifineVersion)) {
            registerCommonTransformers();
        } else if (generations.getMGeneration().contains(optifineVersion) || generations.getFutureGeneration().contains(optifineVersion)) {
            registerCommonTransformers();
        } else {
            logger.info("User has either an old OptiFine version, or no OptiFine present. Aborting reflection optimizations.");
        }
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return ClassTransformer.createTransformer(transformedName, bytes, transformerMap, logger);
    }

    private void registerCommonTransformers() {
        registerTransformer(new BakedQuadReflectionOptimizer());
        registerTransformer(new FaceBakeryReflectionOptimizer());
        registerTransformer(new ModelRotationReflectionOptimizer());
        registerTransformer(new ExtendedBlockStorageReflectionOptimizer());
        registerTransformer(new EntityRendererReflectionOptimizer());

        registerTransformer(new LagometerTransformer());
        registerTransformer(new GuiIngameForgeTransformer());
        registerTransformer(new OptifineFontRendererTransformer());
        registerTransformer(new OptiFineHookTransformer());
        //registerTransformer(new FullbrightTickerTransformer());
        registerTransformer(new EntityCullingTransformer());
        registerTransformer(new WorldVertexBufferUploaderTransformer());
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}