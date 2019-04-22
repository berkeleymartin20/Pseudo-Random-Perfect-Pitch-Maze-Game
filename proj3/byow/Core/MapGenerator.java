package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

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
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED); //dunno how to deal with seed

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
     * @param x x coordinate
     * @param y y coordinate
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

    /**
     * randomly selects one of four directions, with one of them being weighted
     * the weighted direction will have weight = 2
     * the others have weight = 1
     *
     * @param up,down,left,right, in that order, weights/frequencies of each direction (may be 0)
     * @return "up", "down", "left", or "right"
     */
    private String weightedRandomDirection(int up, int down, int left, int right){
        int[] frequencies = new int[]{up,down,left,right};
        int result = RandomUtils.discrete(RANDOM, frequencies);
        switch (result) {
            case 0: return "up";
            case 1: return "down";
            case 2: return "left";
            case 4: return "right";
        }
    }

}
