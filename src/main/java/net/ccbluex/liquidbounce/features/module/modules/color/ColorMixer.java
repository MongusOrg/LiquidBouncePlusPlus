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

    public static ColorElement col1RedValue = new ColorElement(1, ColorElement.Material.RED);
    public static ColorElement col1GreenValue = new ColorElement(1, ColorElement.Material.GREEN);
    public static ColorElement col1BlueValue = new ColorElement(1, ColorElement.Material.BLUE);
    public static ColorElement col2RedValue = new ColorElement(2, ColorElement.Material.RED);
    public static ColorElement col2GreenValue = new ColorElement(2, ColorElement.Material.GREEN);
    public static ColorElement col2BlueValue = new ColorElement(2, ColorElement.Material.BLUE);
    public static ColorElement col3RedValue = new ColorElement(3, ColorElement.Material.RED, blendAmount);
    public static ColorElement col3GreenValue = new ColorElement(3, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col3BlueValue = new ColorElement(3, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col4RedValue = new ColorElement(4, ColorElement.Material.RED, blendAmount);
    public static ColorElement col4GreenValue = new ColorElement(4, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col4BlueValue = new ColorElement(4, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col5RedValue = new ColorElement(5, ColorElement.Material.RED, blendAmount);
    public static ColorElement col5GreenValue = new ColorElement(5, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col5BlueValue = new ColorElement(5, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col6RedValue = new ColorElement(6, ColorElement.Material.RED, blendAmount);
    public static ColorElement col6GreenValue = new ColorElement(6, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col6BlueValue = new ColorElement(6, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col7RedValue = new ColorElement(7, ColorElement.Material.RED, blendAmount);
    public static ColorElement col7GreenValue = new ColorElement(7, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col7BlueValue = new ColorElement(7, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col8RedValue = new ColorElement(8, ColorElement.Material.RED, blendAmount);
    public static ColorElement col8GreenValue = new ColorElement(8, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col8BlueValue = new ColorElement(8, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col9RedValue = new ColorElement(9, ColorElement.Material.RED, blendAmount);
    public static ColorElement col9GreenValue = new ColorElement(9, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col9BlueValue = new ColorElement(9, ColorElement.Material.BLUE, blendAmount);
    public static ColorElement col10RedValue = new ColorElement(10, ColorElement.Material.RED, blendAmount);
    public static ColorElement col10GreenValue = new ColorElement(10, ColorElement.Material.GREEN, blendAmount);
    public static ColorElement col10BlueValue = new ColorElement(10, ColorElement.Material.BLUE, blendAmount);

    public static Color getMixedColor(int index, int seconds) {
        final ColorMixer colMixer = (ColorMixer) LiquidBounce.moduleManager.getModule(ColorMixer.class);
        if (colMixer == null) return Color.white;

        if (lastColors.length <= 0 || lastFraction.length <= 0) regenerateColors(); // just to make sure it won't go white

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

                    int r = ((ColorElement)red.get(colMixer)).get();
                    int g = ((ColorElement)green.get(colMixer)).get();
                    int b = ((ColorElement)blue.get(colMixer)).get();

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