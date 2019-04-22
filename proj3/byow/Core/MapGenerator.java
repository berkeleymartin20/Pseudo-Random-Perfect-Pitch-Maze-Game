package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * @author Martin Lee and Claire Tsau
 * @Version 1.0 4/21/19
 */
public class MapGenerator {

    private static int WIDTH;
    private static int LENGTH;
    //grid that keeps track of what the state of the pixel is at a given (x,y) coordinate
    TETile[][] world;
    //keeps track of connected pixels/blocks
    UnionFind path;

    /**
     * @param width  The width of the screen
     * @param length The length of the screen
     */
    public MapGenerator(int width, int length) {
        WIDTH = width;
        LENGTH = length;

        world = new TETile[WIDTH][LENGTH];
        path = new UnionFind(WIDTH*LENGTH);

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < LENGTH; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

    }

    /**
     * convertXYtoIndex converts an (x,y) coordinate to an index.
     * x = 0 y = 0 is the top left corner of the screen
     * This will be used to smooth operations between world and path smoother
     *
     * @param x x coordiante
     * @param y y coordiante
     * @return returns index of the position. e.g.) 3x3 grid is [[0,1,2],[3,4,5],[6,7,8]]
     */
    public int convertXYtoIndex(int x, int y) {
        if (y == 0) {
            return x;
        } else {
            return y * WIDTH + x;
        }
    }

    /**
     * convertIndextoXY converts an index to an XY coordinate.
     * x = 0 y = 0 is the top left corner of the screen
     * This will be used to smooth operations between world and path smoother
     *
     * @param p index of the position
     * @return returns an array where coord[0] = x position and coord[1] = y position
     */
    public int[] convertIndextoXY(int p) {
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;
        return coord;
    }

}
