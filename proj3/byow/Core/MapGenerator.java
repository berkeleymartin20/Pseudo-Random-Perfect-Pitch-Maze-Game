package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashSet;
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
        path = new UnionFind(WIDTH * LENGTH);

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < LENGTH; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int x = RANDOM.nextInt(WIDTH - 1) + 1;
        int y = RANDOM.nextInt(LENGTH - 1) + 1;
        int n = RANDOM.nextInt(100)+1;
        int direction = RANDOM.nextInt(4);
        step(n,x,y,direction);
        walls();

    }

    /**
     * given a position
     * if empty, make into inside tile
     * check surroundings to created array of weights for next step
     * calls random direction to get next position
     * step to next position
     *
     * @param n    : countdown to end step
     * @param x,y: coordinates of the world array
     */
    public void step(int n, int x, int y, int direction) {
        if (n == 0) {
            return;
        }
        if (world[x][y] == Tileset.NOTHING) { //maybe if statement is unnecessary
            world[x][y] = Tileset.FLOOR;
        }

        int up = 1;
        int down = 1;
        int left = 1;
        int right = 1;

        if (y <= 1) {
            up = 0;
        }
        if (y >= LENGTH) {
            down = 0;
        }
        if (x <= 1) {
            left = 0;
        }
        if (x >= WIDTH) {
            right = 0;
        }

        switch (direction) {
            case 0:
                up = up * 2;
            case 1:
                down = down * 2;
            case 2:
                left = left * 2;
            case 4:
                right = right * 2;
        }

        int nextDirection = weightedRandomDirection(up, down, left, right);

        switch (nextDirection) {
            case 0:
                step(n - 1, x, y + 1, nextDirection);
            case 1:
                step(n - 1, x, y - 1, nextDirection);
            case 2:
                step(n - 1, x - 1, y, nextDirection);
            default:
                step(n - 1, x + 1, y, nextDirection);
        }
    }

    /**
     * brute force method: (slow)
     * for every tile
     * if it is an inside tile
     * for every surrounding tile
     * if it is an outside tile
     * make it a wall tile
     */
    public void walls() {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < LENGTH - 1; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    for (TETile t : neighbors(i, j)) {
                        if (t == Tileset.NOTHING) {
                            t = Tileset.WALL; //i don't think this assignment works
                        }
                    }
                }
            }
        }
    }

    private HashSet<TETile> neighbors(int x, int y) {
        HashSet<TETile> result = new HashSet<>();
        result.add(world[x - 1][y + 1]);
        result.add(world[x - 1][y]);
        result.add(world[x - 1][y - 1]);
        result.add(world[x][y + 1]);
        result.add(world[x][y - 1]);
        result.add(world[x + 1][y + 1]);
        result.add(world[x + 1][y]);
        result.add(world[x + 1][y - 1]);
        return result;
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
     * @return 0 = up, 1 = down, 2 = left, or 3 = right
     */
    private int weightedRandomDirection(int up, int down, int left, int right) {
        int[] frequencies = new int[]{up, down, left, right};
        int result = RandomUtils.discrete(RANDOM, frequencies);
        return result;
    }

}
