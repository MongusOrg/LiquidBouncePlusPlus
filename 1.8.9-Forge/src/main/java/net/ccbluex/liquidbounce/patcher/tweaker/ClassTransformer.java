/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.tweaker;

import net.ccbluex.liquidbounce.patcher.asm.forge.LightUtilTransformer;
import net.ccbluex.liquidbounce.patcher.optifine.OptiFineGenerations;
import net.ccbluex.liquidbounce.patcher.tweaker.transform.PatcherTransformer;
import net.ccbluex.liquidbounce.utils.misc.MiscUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassTransformer implements IClassTransformer {

    public static final boolean outputBytecode = "true".equals(System.getProperty("patcher.debugBytecode", "false"));
    public static String optifineVersion = "NONE";
    private final Logger logger = LogManager.getLogger("Patcher - Class Transformer");
    private final Multimap<String, PatcherTransformer> transformerMap = ArrayListMultimap.create();

    public static boolean smoothFontDetected;
    public static final Set<String> supportedOptiFineVersions = new HashSet<>();
    public static OptiFineGenerations generations;

    public ClassTransformer() {
        MixinEnvironment.getCurrentEnvironment().addTransformerExclusion(getClass().getName());
        try {
            // detect SmoothFont
            final ClassLoader classLoader = this.getClass().getClassLoader();
            if (classLoader.getResource("bre/smoothfont/mod_SmoothFont.class") != null) {
                smoothFontDetected = true;
                this.logger.warn("SmoothFont detected, disabling FontRenderer optimizations.");
            }

            // OptiFine stuff
            this.fetchSupportedOptiFineVersions();
            this.updateOptiFineGenerations();
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader("Config");
            classReader.accept(classNode, ClassReader.SKIP_CODE);
            for (FieldNode fieldNode : classNode.fields) {
                if (fieldNode.name.equals("OF_RELEASE")) {
                    optifineVersion = (String) fieldNode.value;
                    break;
                }
            }

            if (!supportedOptiFineVersions.contains(optifineVersion)) {
                logger.info("User has outdated OptiFine. (version: OptiFine-{})", optifineVersion);
                this.haltForOptifine("OptiFine " + optifineVersion + " has been detected, which is not supported by Patcher and will crash.\n" +
                    "Please update to a newer version of OptiFine (i7 and above are supported) before trying to launch.");
                return;
            }
        } catch (IOException ignored) {
        }

        registerTransformer(new LightUtilTransformer());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] createTransformer(String transformedName, byte[] bytes, Multimap<String, PatcherTransformer> transformerMap, Logger logger) {
        if (bytes == null) return null;

        Collection<PatcherTransformer> transformers = transformerMap.get(transformedName);
        if (transformers.isEmpty()) return bytes;

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (PatcherTransformer transformer : transformers) {
            transformer.transform(classNode, transformedName);
        }

        PatcherClassWriter classWriter = new PatcherClassWriter(PatcherClassWriter.COMPUTE_FRAMES);

        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            logger.error("Exception when transforming {} : {}", transformedName, e.getClass().getSimpleName(), e);
        }

        if (outputBytecode) {
            File bytecodeDirectory = new File("bytecode");
            if (!bytecodeDirectory.exists()) bytecodeDirectory.mkdirs();

            int lastIndex = transformedName.lastIndexOf('.');
            if (lastIndex != -1) {
                transformedName = transformedName.substring(lastIndex + 1) + ".class";
            }

            try {
                File bytecodeOutput = new File(bytecodeDirectory, transformedName);
                if (!bytecodeOutput.exists()) bytecodeOutput.createNewFile();

                try (FileOutputStream os = new FileOutputStream(bytecodeOutput)) {
                    os.write(classWriter.toByteArray());
                } catch (IOException e) {
                    logger.error("Failed to create bytecode output for {}.", transformedName, e);
                }
            } catch (Exception ignored) {
            }
        }

        return classWriter.toByteArray();
    }

    private void registerTransformer(PatcherTransformer transformer) {
        for (String cls : transformer.getClassName()) {
            transformerMap.put(cls, transformer);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        return createTransformer(transformedName, bytes, transformerMap, logger);
    }

    private void haltForOptifine(String message) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton openOptifine = new JButton("Open OptiFine Website");
        openOptifine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    MiscUtils.showURL("https://optifine.net/downloads/");
                } catch (Exception ex) {
                    JLabel label = new JLabel();
                    label.setText("Failed to open OptiFine website.");
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    label.setAlignmentY(Component.CENTER_ALIGNMENT);
                }
            }
        });

        JButton close = new JButton("Close");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PatcherTweaker.invokeExit();
            }
        });

        Object[] options = {openOptifine, close};
        JOptionPane.showOptionDialog(null, message, "Launch Aborted", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        PatcherTweaker.invokeExit();
    }

    private void fetchSupportedOptiFineVersions() {
        HttpsURLConnection connection = null;
        try {
            final URL optifineVersions = new URL("https://static.sk1er.club/patcher/optifine.txt");
            connection = (HttpsURLConnection) optifineVersions.openConnection();
            connection.setRequestProperty("User-Agent", "Patcher OptiFine Fetcher");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String version;
                while ((version = reader.readLine()) != null) {
                    supportedOptiFineVersions.add(version);
                }
            }
        } catch (Exception e) {
            this.logger.error("Failed to read supported OptiFine versions, adding defaults.", e);
            supportedOptiFineVersions.addAll(Arrays.asList("I7", "L5", "M5", "M6_pre1", "M6"));
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void updateOptiFineGenerations() {
        HttpsURLConnection connection = null;
        try {
            final URL optifineGenerations = new URL("https://static.sk1er.club/patcher/optifine_generations.json");
            connection = (HttpsURLConnection) optifineGenerations.openConnection();
            connection.setRequestProperty("User-Agent", "Patcher OptiFine Fetcher");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (final Reader reader = new InputStreamReader(connection.getInputStream())) {
                generations = new Gson().fromJson(reader, OptiFineGenerations.class);
            }
        } catch (Exception e) {
            this.logger.error("Failed to read OptiFine generations list. Supplying default supported generations.", e);
            generations = new OptiFineGenerations();
            generations.getIGeneration().add("I7");

            generations.getLGeneration().add("L5");
            generations.getLGeneration().add("L6");

            generations.getMGeneration().add("M5");
            generations.getMGeneration().add("M6-pre1");
            generations.getMGeneration().add("M6");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public static boolean isDevelopment() {
        Object o = Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return o != null && (boolean) o;
    }
}