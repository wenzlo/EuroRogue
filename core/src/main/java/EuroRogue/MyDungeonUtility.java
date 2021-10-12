package EuroRogue;

import java.util.Map;

import squidpony.squidgrid.FOV;
import squidpony.squidgrid.mapping.DungeonUtility;

public class MyDungeonUtility extends DungeonUtility
{
    private static final char[] wallLookupDoubleLines = new char[]
            {
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '║', '═', '╝', '═', '╩', '╗', '╣', '╦', '╣',
                    '#', '║', '═', '╚', '║', '║', '╔', '║', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╩',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '═', '╝',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '═', '╗', '╣', '╦', '╦',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '╣', '╦', '╬',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '═', '╗', '╣', '╦', '╗',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '║', '╦', '╠',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '═', '╗', '║', '╦', '╔',
                    '#', '║', '═', '╚', '║', '║', '╔', '╠', '═', '╝', '═', '╩', '╗', '║', '═', '╚',
                    '#', '║', '═', '╚', '║', '║', '╔', '║', '═', '╝', '═', '═', '╗', '║', '═', '\1'
            };

    /**
     * Takes a char[][] dungeon map that uses '#' to represent walls, and returns a new char[][] that uses unicode box
     * drawing characters to draw straight, continuous lines for walls, filling regions between walls (that were
     * filled with more walls before) with space characters, ' '. If keepSingleHashes is true, then '#' will be used if
     * a wall has no orthogonal wall neighbors; if it is false, then a horizontal line will be used for stand-alone
     * wall cells. If the lines "point the wrong way," such as having multiple horizontally adjacent vertical lines
     * where there should be horizontal lines, call transposeLines() on the returned map, which will keep the dimensions
     * of the map the same and only change the line chars. You will also need to call transposeLines if you call
     * hashesToLines on a map that already has "correct" line-drawing characters, which means hashesToLines should only
     * be called on maps that use '#' for walls. If you have a jumbled map that contains two or more of the following:
     * "correct" line-drawing characters, "incorrect" line-drawing characters, and '#' characters for walls, you can
     * reset by calling linesToHashes() and then potentially calling hashesToLines() again.
     *
     * @param map              a 2D char array indexed with x,y that uses '#' for walls
     * @param keepSingleHashes true if walls that are not orthogonally adjacent to other walls should stay as '#'
     * @return a copy of the map passed as an argument with box-drawing characters replacing '#' walls
     */
    public static char[][] hashesToDoubleLines(char[][] map, boolean keepSingleHashes) {
        int width = map.length + 2;
        int height = map[0].length + 2;

        char[][] dungeon = new char[width][height];
        for (int i = 1; i < width - 1; i++) {
            System.arraycopy(map[i - 1], 0, dungeon[i], 1, height - 2);
        }
        for (int i = 0; i < width; i++) {
            dungeon[i][0] = '\1';
            dungeon[i][height - 1] = '\1';
        }
        for (int i = 0; i < height; i++) {
            dungeon[0][i] = '\1';
            dungeon[width - 1][i] = '\1';
        }
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (map[x - 1][y - 1] == '#') {
                    int q = 0;
                    q |= (y <= 1 || map[x - 1][y - 2] == '#' || map[x - 1][y - 2] == '+' || map[x - 1][y - 2] == '/') ? 1 : 0;
                    q |= (x >= width - 2 || map[x][y - 1] == '#' || map[x][y - 1] == '+' || map[x][y - 1] == '/') ? 2 : 0;
                    q |= (y >= height - 2 || map[x - 1][y] == '#' || map[x - 1][y] == '+' || map[x - 1][y] == '/') ? 4 : 0;
                    q |= (x <= 1 || map[x - 2][y - 1] == '#' || map[x - 2][y - 1] == '+' || map[x - 2][y - 1] == '/') ? 8 : 0;

                    q |= (y <= 1 || x >= width - 2 || map[x][y - 2] == '#' || map[x][y - 2] == '+' || map[x][y - 2] == '/') ? 16 : 0;
                    q |= (y >= height - 2 || x >= width - 2 || map[x][y] == '#' || map[x][y] == '+' || map[x][y] == '/') ? 32 : 0;
                    q |= (y >= height - 2 || x <= 1 || map[x - 2][y] == '#' || map[x - 2][y] == '+' || map[x - 2][y] == '/') ? 64 : 0;
                    q |= (y <= 1 || x <= 1 || map[x - 2][y - 2] == '#' || map[x - 2][y - 2] == '+' || map[x - 2][y - 2] == '/') ? 128 : 0;
                    if (!keepSingleHashes && wallLookupDoubleLines[q] == '#') {
                        dungeon[x][y] = '─';
                    } else {
                        dungeon[x][y] = wallLookupDoubleLines[q];
                    }
                }
            }
        }
        char[][] portion = new char[width - 2][height - 2];
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (dungeon[i][j] == '\1') {
                    portion[i - 1][j - 1] = ' ';
                } else {
                    // ┼┌┘
                    portion[i - 1][j - 1] = dungeon[i][j];
                }
            }
        }
        return portion;
    }


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

    public static char[][] closeDoors(char[][] map) {

        int width = map.length;
        int height = map[0].length;
        char[][] portion = new char[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (map[i][j] == '/') portion[i][j] = '+';
                else portion[i][j] = map[i][j];

            }
        }
        return portion;
    }
}
