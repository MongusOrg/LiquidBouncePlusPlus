/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.color;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.BlendUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import java.awt.Color;

import java.lang.reflect.Field;

@ModuleInfo(name = "ColorMixer", description = "Mix two colors together.", category = ModuleCategory.COLOR, canEnable = false)
public class ColorMixer extends Module {

    private static float[] lastFraction = new float[]{};
    public static Color[] lastColors = new Color[]{};

    public static final IntegerValue blendAmount = new IntegerValue("Mixer-Amount", 2, 2, 10) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    @Override
    public void onInitialize() {
        regenerateColors();
    }

    public static IntegerValue col1RedValue = new IntegerValue("Color1-Red", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col1GreenValue = new IntegerValue("Color1-Green", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col1BlueValue = new IntegerValue("Color1-Blue", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col2RedValue = new IntegerValue("Color2-Red", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col2GreenValue = new IntegerValue("Color2-Green", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col2BlueValue = new IntegerValue("Color2-Blue", 255, 0, 255) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col3RedValue = new IntegerValue("Color3-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 3); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col3GreenValue = new IntegerValue("Color3-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 3); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col3BlueValue = new IntegerValue("Color3-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 3); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col4RedValue = new IntegerValue("Color4-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 4); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col4GreenValue = new IntegerValue("Color4-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 4); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col4BlueValue = new IntegerValue("Color4-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 4); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col5RedValue = new IntegerValue("Color5-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 5); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col5GreenValue = new IntegerValue("Color5-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 5); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col5BlueValue = new IntegerValue("Color5-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 5); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col6RedValue = new IntegerValue("Color6-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 6); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col6GreenValue = new IntegerValue("Color6-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 6); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col6BlueValue = new IntegerValue("Color6-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 6); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col7RedValue = new IntegerValue("Color7-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 7); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col7GreenValue = new IntegerValue("Color7-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 7); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col7BlueValue = new IntegerValue("Color7-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 7); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col8RedValue = new IntegerValue("Color8-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 8); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col8GreenValue = new IntegerValue("Color8-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 8); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col8BlueValue = new IntegerValue("Color8-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 8); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col9RedValue = new IntegerValue("Color9-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 9); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col9GreenValue = new IntegerValue("Color9-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 9); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col9BlueValue = new IntegerValue("Color9-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 9); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static IntegerValue col10RedValue = new IntegerValue("Color10-Red", 255, 0, 255, () -> { return (blendAmount.get() >= 10); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col10GreenValue = new IntegerValue("Color10-Green", 255, 0, 255, () -> { return (blendAmount.get() >= 10); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };
    public static IntegerValue col10BlueValue = new IntegerValue("Color10-Blue", 255, 0, 255, () -> { return (blendAmount.get() >= 10); }) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            regenerateColors();
        }
    };

    public static Color getMixedColor(int index, int seconds) {
        final ColorMixer colMixer = (ColorMixer) LiquidBounce.moduleManager.getModule(ColorMixer.class);
        if (colMixer == null || lastColors.length <= 0 || lastFraction.length <= 0) return Color.white;

        return BlendUtils.blendColors(lastFraction, lastColors, (System.currentTimeMillis() + index) % (seconds * 1000) / (float) (seconds * 1000));
    }

    public static void regenerateColors() {
        final ColorMixer colMixer = (ColorMixer) LiquidBounce.moduleManager.getModule(ColorMixer.class);
            
        // color generation
        if (lastColors.length <= 0 || lastColors.length != (blendAmount.get() * 2) - 1) {
            Color[] generator = new Color[(blendAmount.get() * 2) - 1];

            // reflection is cool
            for (int i = 1; i <= blendAmount.get(); i++) {
                Color result = Color.white;
                try {                
                    Field red = ColorMixer.class.getField("col"+i+"RedValue");
                    Field green = ColorMixer.class.getField("col"+i+"GreenValue");
                    Field blue = ColorMixer.class.getField("col"+i+"BlueValue");

                    int r = ((IntegerValue)red.get(colMixer)).get();
                    int g = ((IntegerValue)green.get(colMixer)).get();
                    int b = ((IntegerValue)blue.get(colMixer)).get();

                    result = new Color(Math.max(0, Math.min(r, 255)), Math.max(0, Math.min(g, 255)), Math.max(0, Math.min(b, 255)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                generator[i - 1] = result;
            }

            int h = blendAmount.get();
            for (int z = blendAmount.get() - 2; z >= 0; z--) {
                generator[h] = generator[z];
                h++;
            }

            lastColors = generator;
        }

        // cache thingy
        if (lastFraction.length <= 0 || lastFraction.length != (blendAmount.get() * 2) - 1) {
            // color frac regenerate if necessary
            float[] colorFraction = new float[(blendAmount.get() * 2) - 1];    

            for (int i = 0; i <= (blendAmount.get() * 2) - 2; i++)
            {
                colorFraction[i] = (float)i / (float)((blendAmount.get() * 2) - 2);
            }

            lastFraction = colorFraction;
        }
    }
    
}