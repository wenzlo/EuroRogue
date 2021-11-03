package EuroRogue;

import squidpony.squidgrid.FOV;
import squidpony.squidgrid.mapping.DungeonUtility;

public class MyFOV extends FOV
{

    /**
     * Given a char[][] for the map, produces a double[][] that can be used with any FOV methods that expect a
     * resistance map (like {@link #reuseFOV(double[][], double[][], int, int, double)}), but does not treat
     * any cells as partly transparent, only fully-blocking or fully-permitting light. This is mainly useful if you
     * expect the FOV radius to be very high or (effectively) infinite, since anything less than complete blockage would
     * be passed through by infinite-radius FOV. This expects any doors to be represented by '+' if closed or '/' if
     * open (most door placement defaults to a mix of '+' and '/', so by calling
     * {@link DungeonUtility#closeDoors(char[][])} you can close all doors at the start), and any walls to be '#' or
     * box drawing characters. This will assign 1.0 resistance to walls and closed doors or 0.0 for any other cell.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class, but with no partially transparent cells
     */
    public static double[][] generateSimpleResistances(char[][] map) {
        int width = map.length;
        int height = map[0].length;
        double[][] portion = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                switch (map[i][j]) {
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
                    case '+':
                    //case '§':
                        portion[i][j] = 1.0;
                        break;
                    default:
                        portion[i][j] = 0.0;
                }
            }
        }
        return portion;
    }

    /**
     * Given a char[][] for the map that should use box drawing characters (as produced by
     * {@link DungeonUtility#hashesToLines(char[][], boolean)}), produces a double[][] with triple width and triple
     * height that can be used with FOV's methods that expect a resistance map (like
     * {@link #reuseFOV(double[][], double[][], int, int, double)}) in classes that use subcell lighting. This expects
     * any doors to be represented by '+' if closed or '/' if open (most door placement defaults to a mix of '+' and
     * '/', so by calling {@link DungeonUtility#closeDoors(char[][])} you can close all doors at the start), any walls
     * to be box drawing characters, and any cells that block all subcells within their area to be '#'. This will assig
     * 1.0 resistance to walls and closed doors where a line of the box drawing char would block light, or 0.0 for an
     * other subcell.
     *
     * @param map a dungeon, width by height, with any closed doors as '+' and open doors as '/' as per closeDoors()
     * @return a resistance map suitable for use with the FOV class and subcell lighting, with triple width/height
     */
    public static double[][] generateSimpleResistances3x3(char[][] map) {
        int width = map.length;
        int height = map[0].length;
        double[][] portion = new double[width * 3][height * 3];
        for (int i = 0, x = 0; i < width; i++, x+=3) {
            for (int j = 0, y = 0; j < height; j++, y+=3) {
                switch (map[i][j]) {
                    case '\1':

                    case '+':
                    //case '§':
                        portion[x][y] = portion[x+1][y] = portion[x+2][y] =
                                portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                        portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] = 1.0;
                        break;
                    case '├':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┤':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                    /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┴':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                    /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┬':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┌':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┐':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '└':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┘':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                    /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '│':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '─':
                        portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] = 1.0;
                        break;
                    case '╴':
                        portion[x][y+1] = portion[x+1][y+1] = 1.0;
                        break;
                    case '╵':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/ 1.0;
                        break;
                    case '╶':
                        portion[x+1][y+1] = portion[x+2][y+1] = 1.0;
                        break;
                    case '╷':
                        /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┼':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                    /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;

                    case '#':
                        if((x+y)%3==0)
                        {
                            portion[x][y] = /*portion[x+1][y] = */portion[x+2][y] =
                                    /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                                    portion[x][y+2] =/* portion[x+1][y+2] = */portion[x+2][y+2] =1.0;

                        } else if((x+y)%3==1){

                            /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                                    portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;

                        } else {

                            portion[x][y] = /*portion[x+1][y] = */portion[x+2][y] =
                                    portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                    portion[x][y+2] =/* portion[x+1][y+2] = */portion[x+2][y+2] =1.0;

                        }


                        break;
                }
            }
        }
        return portion;
    }
    public static double[][] generateUIResistances3x3(char[][] map) {
        int width = map.length;
        int height = map[0].length;
        double[][] portion = new double[width * 3][height * 3];
        for (int i = 0, x = 0; i < width; i++, x+=3) {
            for (int j = 0, y = 0; j < height; j++, y+=3) {
                switch (map[i][j]) {
                    case '\1':

                    case '+':
                    case'#':
                        //case '§':
                        portion[x][y] = portion[x+1][y] = portion[x+2][y] =
                                portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                        portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] = 1.0;
                        break;
                    case '├':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┤':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                    /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┴':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                    /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┬':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┌':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┐':
                        /*portion[x][y] = portion[x+1][y] = portion[x+2][y] =*/
                        portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '└':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = portion[x+2][y+1] =
                            /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┘':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                                    /*portion[x][y+2] = portion[x+1][y+2] = portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '│':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '─':
                        portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] = 1.0;
                        break;
                    case '╴':
                        portion[x][y+1] = portion[x+1][y+1] = 1.0;
                        break;
                    case '╵':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/ 1.0;
                        break;
                    case '╶':
                        portion[x+1][y+1] = portion[x+2][y+1] = 1.0;
                        break;
                    case '╷':
                        /*portion[x][y+1] =*/ portion[x+1][y+1] = /*portion[x+2][y+1] =*/
                            /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;
                    case '┼':
                        /*portion[x][y] =*/ portion[x+1][y] = /*portion[x+2][y] =*/
                            portion[x][y+1] = portion[x+1][y+1] = portion[x+2][y+1] =
                                    /*portion[x][y+2] =*/ portion[x+1][y+2] = /*portion[x+2][y+2] =*/ 1.0;
                        break;

                }
            }
        }
        return portion;
    }
}
