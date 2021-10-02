package EuroRogue;

import java.util.Map;

import squidpony.squidgrid.FOV;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.Coord;

public class MyDungeonUtility extends DungeonUtility
{


    /**
     * Given a char[][] for the map, produces a double[][] that can be used with FOV.calculateFOV(). It expects any
     * doors to be represented by '+' if closed or '/' if open (which can be caused by calling
     * DungeonUtility.closeDoors() ), any walls to be '#' or box drawing characters, and it doesn't care what other
     * chars are used (only doors, including open ones, and walls obscure light and thus have a resistance by default).
     * <br>
     * This is here for backwards-compatibility; this method delegates to {@link FOV#generateResistances(char[][])}.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class, with clear cells assigned 0.0 and blocked ones 1.0
     */
    public static double[][] generateResistances(char[][] map) {
        return MyFOV.generateResistances(map);
    }

    /**
     * Given a char[][] for the map that should use box drawing characters (as produced by
     * {@link #hashesToLines(char[][], boolean)}), produces a double[][] with triple width and triple height that can be
     * used with FOV.calculateFOV() in classes that use subcell lighting. Importantly, this only considers a "thin line"
     * of wall to be blocking (matching the box drawing character), instead of the whole 3x3 area. This expects any
     * doors to be represented by '+' if closed or '/' if open (which can be caused by calling
     * {@link #closeDoors(char[][])}), thick vegetation or other concealing obstructions to be '"', any normal walls to
     * be box drawing characters, any cells that block all subcells to be '#', and it doesn't care what other chars are
     * used (only doors, including open ones, vegetation, and walls obscure light and thus have a resistance normally).
     * <br>
     * This is here for backwards-compatibility; this method delegates to {@link FOV#generateResistances3x3(char[][])}
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class and subcell lighting, with triple width/height
     */
    public static double[][] generateResistances3x3(char[][] map) {
        return MyFOV.generateResistances3x3(map);
    }

    /**
     * Given a char[][] for the map, produces a double[][] that can be used with FOV.calculateFOV(), but does not treat
     * any cells as partly transparent, only fully-blocking or fully-permitting light. This is mainly useful if you
     * expect the FOV radius to be very high or (effectively) infinite, since anything less than complete blockage would
     * be passed through by infinite-radius FOV. This expects any doors to be represented by '+' if closed or '/' if
     * open (most door placement defaults to a mix of '+' and '/', so by calling
     * {@link DungeonUtility#closeDoors(char[][])} you can close all doors at the start), and any walls to be '#' or
     * box drawing characters. This will assign 1.0 resistance to walls and closed doors or 0.0 for any other cell.
     * <br>
     * This is here for backwards-compatibility; this method delegates to {@link FOV#generateSimpleResistances(char[][])}.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class, but with no partially transparent cells
     */
    public static double[][] generateSimpleResistances(char[][] map) {
        return MyFOV.generateSimpleResistances(map);
    }

    /**
     * Given a char[][] for the map that should use box drawing characters (as produced by
     * {@link #hashesToLines(char[][], boolean)}), produces a double[][] with triple width and triple height that can be
     * used with FOV.calculateFOV() in classes that use subcell lighting. This expects any doors to be represented by
     * '+' if closed or '/' if open (most door placement defaults to a mix of '+' and '/', so by calling
     * {@link DungeonUtility#closeDoors(char[][])} you can close all doors at the start), any walls to be box drawing
     * characters, and any cells that block all subcells within their area to be '#'. This will assign 1.0 resistance to
     * walls and closed doors where a line of the box drawing char would block light, or 0.0 for any other subcell.
     * <br>
     * This is here for backwards-compatibility; this method delegates to {@link FOV#generateSimpleResistances3x3(char[][])}.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class and subcell lighting, with triple width/height
     */
    public static double[][] generateSimpleResistances3x3(char[][] map) {
        return MyFOV.generateSimpleResistances3x3(map);
    }

    /**
     * Given a char[][] for the map, a Map of Character keys to Double values that will be used to determine costs, and
     * a double value for unhandled characters, produces a double[][] that can be used as a costMap by DijkstraMap. It
     * expects any doors to be represented by '+' if closed or '/' if open (which can be caused by calling
     * DungeonUtility.closeDoors() ) and any walls to be '#' or box drawing characters. In the parameter costs, there
     * does not need to be an entry for '#' or any box drawing characters, but if one is present for '#' it will apply
     * that cost to both '#' and all box drawing characters, and if one is not present it will default to a very high
     * number. For any other entry in costs, a char in the 2D char array that matches the key will correspond
     * (at the same x,y position in the returned 2D double array) to that key's value in costs. If a char is used in the
     * map but does not have a corresponding key in costs, it will be given the value of the parameter defaultValue.
     * <p/>
     * The values in costs are multipliers, so should not be negative, should only be 0.0 in cases where you want
     * infinite movement across all adjacent squares of that kind, should be higher than 1.0 for difficult terrain (2.0
     * and 3.0 are reasonable), should be between 0.0 and 1.0 for easy terrain, and should be 1.0 for normal terrain.
     * If a cell should not be possible to enter for this character, 999.0 should be a reasonable value for a cost.
     * <p/>
     * An example use for this would be to make a creature unable to enter any non-water cell (like a fish),
     * unable to enter doorways (like some mythological versions of vampires), or to make a wheeled vehicle take more
     * time to move across rubble or rough terrain.
     * <p/>
     * A potentially common case that needs to be addressed is NPC movement onto staircases in games that have them;
     * some games may find it desirable for NPCs to block staircases and others may not, but in either case you should
     * give both '&gt;' and '&lt;', the standard characters for staircases, the same value in costs.
     *
     * @param map          a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors() .
     * @param costs        a Map of Character keys representing possible elements in map, and Double values for their cost.
     * @param defaultValue a double that will be used as the cost for any characters that don't have a key in costs.
     * @return a cost map suitable for use with DijkstraMap
     */
    public static double[][] generateCostMap(char[][] map, Map<Character, Double> costs, double defaultValue) {
        int width = map.length;
        int height = map[0].length;
        double[][] portion = new double[width][height];
        char current;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                current = map[i][j];
                if (costs.containsKey(current)) {
                    portion[i][j] = costs.get(current);
                } else {
                    switch (current) {
                        case '\1':
                        case '├':
                        case '┤':
                        case '┴':
                        case '┬':
                        case '┌':
                        case '┐':
                        case '└':
                        case '┘':
                        case '│':
                        case '─':
                        case '┼':
                        case '#':
                            portion[i][j] = (costs.containsKey('#'))
                                    ? costs.get('#')
                                    : squidpony.squidai.DijkstraMap.WALL;
                            break;
                        default:
                            portion[i][j] = defaultValue;
                    }
                }
            }
        }
        return portion;
    }
}
