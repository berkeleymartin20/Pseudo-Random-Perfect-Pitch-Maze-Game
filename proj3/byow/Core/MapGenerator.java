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
    private static int HEIGHT;
    //grid that keeps track of what the state of the pixel is at a given (x,y) coordinate
    private TETile[][] world;
    //keeps track of connected pixels/blocks
    UnionFind path;
    private Random RANDOM; //dunno how to deal with seed

    /**
     * @param width  The width of the screen
     * @param height The length of the screen
     */
    public MapGenerator(int width, int height, int seed) {

        WIDTH = width;
        HEIGHT = height;
        RANDOM = new Random(seed);

        world = new TETile[HEIGHT][WIDTH];
        path = new UnionFind(WIDTH * HEIGHT);

        for (int r = 0; r < HEIGHT; r += 1) {
            for (int c = 0; c < WIDTH; c += 1) {
                world[r][c] = Tileset.NOTHING;
            }
        }

        int r = RANDOM.nextInt(HEIGHT - 2) + 1;
        int c = RANDOM.nextInt(WIDTH - 2) + 1;
        int n = RANDOM.nextInt(WIDTH * HEIGHT) + 1; //up to change
        int direction = RANDOM.nextInt(4);
        step(n, r, c, direction);
        walls();

    }

    /**
     * given a position
     * if empty, make into inside tile
     * check surroundings to created array of weights for next step
     * calls random direction to get next position
     * step to next position
     *
     * @param n         : countdown to end step
     * @param r,c       : coordinates of the world array
     * @param direction : 0,1,2,3
     */
    public void step(int n, int r, int c, int direction) {
        if (n == 0) {
            return;
        }

        world[r][c] = Tileset.FLOOR;

        int up = 1;
        int down = 1;
        int left = 1;
        int right = 1;

        if (r <= 1) {
            up = 0;
        }
        if (r >= HEIGHT - 2) {
            down = 0;
        }
        if (c <= 1) {
            left = 0;
        }
        if (c >= WIDTH - 2) {
            right = 0;
        }

        switch (direction) {
            case 0:
                up = up * 2;
                break;
            case 1:
                down = down * 2;
                break;
            case 2:
                left = left * 2;
                break;
            case 3:
                right = right * 2;
                break;
            default:
                break;
        }

        if (up == 0 && down == 0 && left == 0 && right == 0) {
            return;
        }
        int nextDirection = weightedRandomDirection(up, down, left, right);

        switch (nextDirection) {
            case 0:
                step(n - 1, r - 1, c, nextDirection);
                break;
            case 1:
                step(n - 1, r + 1, c, nextDirection);
                break;
            case 2:
                step(n - 1, r, c - 1, nextDirection);
                break;
            case 3:
                step(n - 1, r, c + 1, nextDirection);
                break;
            default:
                break;
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
        for (int i = 1; i < HEIGHT - 1; i++) {
            for (int j = 1; j < WIDTH - 1; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    /*for (Position p : neighbors(i, j)) {
                        if (world[p.x][p.y] == Tileset.NOTHING) {
                            world[p.x][p.y] = Tileset.WALL;
                        }
                    }*/
                    for (int x = i - 1; x <= i + 1; x++) {
                        for (int y = j - 1; y <= j + 1; y++) {
                            if (world[x][y] == Tileset.NOTHING) {
                                world[x][y] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    private HashSet<Position> neighbors(int x, int y) { //this method doesn't work
        HashSet<Position> result = new HashSet<>();
        result.add(new Position(x - 1, y + 1));
        result.add(new Position(x - 1, y));
        result.add(new Position(x - 1, y - 1));
        result.add(new Position(x, y + 1));
        result.add(new Position(x, y - 1));
        result.add(new Position(x + 1, y + 1));
        result.add(new Position(x + 1, y));
        result.add(new Position(x + 1, y - 1));
        return result;
    }

    private class Position {
        int x;
        int y;

        Position(int X, int Y) {
            x = X;
            y = Y;
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
     * @return 0 = up, 1 = down, 2 = left, or 3 = right
     */
    private int weightedRandomDirection(int up, int down, int left, int right) {
        int[] frequencies = new int[]{up, down, left, right};
        int result = RandomUtils.discrete(RANDOM, frequencies);
        return result;
    }

    public TETile[][] world() {
        return world;
    }

}
