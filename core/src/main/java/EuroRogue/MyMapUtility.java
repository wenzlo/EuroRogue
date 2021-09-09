package EuroRogue;

import squidpony.squidgrid.gui.gdx.MapUtility;
import squidpony.squidgrid.gui.gdx.SColor;

public class MyMapUtility extends MapUtility
{
    /**
     * Produces a float[][] of packed float colors that corresponds to appropriate default background colors for the
     * usual meanings of the chars in map.
     * <br>
     * This takes its values from {@link SColor#LIMITED_PALETTE}, and if that field is changed then the
     * colors this returns will also change. Most backgrounds will be black; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[0]}. Deep water ('~') will be dark blue-green; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[24]}. Shallow water (',') will be a lighter blue-green; this can be changed by
     * editing {@code SColor.LIMITED_PALETTE[23]}. Grass ('"') will be dark green; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[21]}. Bridges (':') will be a medium-dark beige color; this can be changed by
     * editing {@code SColor.LIMITED_PALETTE[35]}. You can adjust the brightness of the backgrounds using
     * {@link #generateLightnessModifiers(char[][])}, or if you want water and grass to ripple, you can use the overload
     * {@link #generateLightnessModifiers(char[][], double, char, char)} with some rising frame or millisecond count.
     *
     * @param map a char[][] containing foreground characters (this gets their background color)
     * @return a 2D array of background Colors with the same size as map, that can be used for the corresponding chars
     */

    public static float[][] generateDefaultBGColorsFloat(char[][] map) {
        return fillDefaultBGColorsFloat(new float[map.length][map[0].length], map);
    }
    /**
     * Fills an existing float[][] with packed float colors that correspond to appropriate default background colors for
     * the usual meanings of the chars in map. The sizes of map and packed should be identical.
     * <br>
     * This takes its values from {@link SColor#LIMITED_PALETTE}, and if that field is changed then the
     * colors this returns will also change. Most backgrounds will be black; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[0]}. Deep water ('~') will be dark blue-green; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[24]}. Shallow water (',') will be a lighter blue-green; this can be changed by
     * editing {@code SColor.LIMITED_PALETTE[23]}. Grass ('"') will be dark green; this can be changed by editing
     * {@code SColor.LIMITED_PALETTE[21]}. Bridges (':') will be a medium-dark beige color; this can be changed by
     * editing {@code SColor.LIMITED_PALETTE[35]}. You can adjust the brightness of the backgrounds using
     * {@link #generateLightnessModifiers(char[][])}, or if you want water and grass to ripple, you can use the overload
     * {@link #generateLightnessModifiers(char[][], double, char, char)} with some rising frame or millisecond count.
     * @param packed a float[][] that will be modified, filled with packed float colors; must match map's size
     * @param map a char[][] containing foreground characters (this gets their background color)
     * @return a 2D array of background Colors with the same size as map, that can be used for the corresponding chars
     */
    public static float[][] fillDefaultBGColorsFloat(float[][] packed, char[][] map) {
        int width = map.length;
        int height = map[0].length;
        float   bridge = SColor.LIMITED_PALETTE[35].toFloatBits(),
                shallow_water = SColor.LIMITED_PALETTE[23].toFloatBits(),
                deep_water = SColor.LIMITED_PALETTE[24].toFloatBits(),
                grass = SColor.LIMITED_PALETTE[21].toFloatBits(),
                wall = SColor.LIMITED_PALETTE[7].toFloatBits(),
                other = SColor.LIMITED_PALETTE[0].toFloatBits();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                switch (map[i][j]) {
                    case ':':
                        packed[i][j] = bridge;
                        break;
                    case ',':
                        packed[i][j] = shallow_water;
                        break;
                    case '~':
                        packed[i][j] = deep_water;
                        break;
                    case '"':
                        packed[i][j] = grass;
                        break;
                    case '#':
                        packed[i][j] = wall;
                    default:
                        packed[i][j] = other;
                }
            }
        }
        return packed;
    }

}
