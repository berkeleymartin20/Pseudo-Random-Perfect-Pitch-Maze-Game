package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;


/**
 * @author Martin Lee and Claire Tsau
 * @Version 1.0 4/21/19
 */
public class MapGenerator {

    private static int WIDTH;
    private static int HEIGHT;
    private TETile[][] world;

    //world of zeroes except at places with rooms (then it will be i for ith room)
    private int[][] rooms;

    //ith element is ith room. 0 element is the outside. true if checked
    private boolean[] roomCheck;
    private Random RANDOM; //dunno how to deal with seed

    public void print() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(world[i][j].description() + " ");
            }
            System.out.println();
        }
    }

    /**
     * @param width  The width of the screen
     * @param height The length of the screen
     */
    public MapGenerator(int width, int height, int seed) {

        WIDTH = width;
        HEIGHT = height;
        RANDOM = new Random(seed);

        world = new TETile[HEIGHT][WIDTH];
        rooms = new int[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r += 1) {
            for (int c = 0; c < WIDTH; c += 1) {
                world[r][c] = Tileset.NOTHING;
                rooms[r][c] = 0;
            }
        }

        int numRooms = RANDOM.nextInt(10) + 1; //number of rooms [1,10]
        roomCheck = new boolean[numRooms + 1];
        Arrays.fill(roomCheck, false);
        roomCheck[0] = true;
        for (int i = 0; i <= numRooms; i++) {
            generateRoom(i);
            System.out.println(" numRooms " + numRooms + " i " + i);
        }

        int r = RANDOM.nextInt(HEIGHT - 3) + 1;
        int c = RANDOM.nextInt(WIDTH - 3) + 1;
        int direction = RANDOM.nextInt(4);
        step(r, c, direction);

        walls();

    }

    private void generateRoom(int roomNum) {
        //need to make sure don't call random on 0
        int r = HEIGHT - 3;
        int c = WIDTH - 3;
        if (r != 0) {
            r = RANDOM.nextInt(r) + 1;
        }
        if (c != 0) {
            c = RANDOM.nextInt(c) + 1;
        }

        int height = Math.min(r - 1, HEIGHT - r - 2);
        if (height != 0) {
            height = RANDOM.nextInt(height);
        }
        int width = Math.min(c - 1, WIDTH - c - 2);
        if (width != 0) {
            width = RANDOM.nextInt(width);
        }

        for (int i = r - height; i <= r + height; i++) {
            for (int j = c - width; j <= c + width; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    roomCheck[rooms[i][j]] = true;
                }
                world[i][j] = Tileset.FLOOR;
                rooms[i][j] = roomNum;
            }
        }

    }

    private void checkRoom(int r, int c) {
        int room = rooms[r][c];
        if (!roomCheck[room]) {
            roomCheck[room] = true;
        }
    }

    /**
     * given a position
     * if empty, make into inside tile
     * check surroundings to created array of weights for next step
     * calls random direction to get next position
     * step to next position
     *
     * @param r,c       : coordinates of the world array
     * @param direction : 0,1,2,3
     */
    public void step(int r, int c, int direction) {
        while (!roomsConnected()) {
            checkRoom(r, c);

            world[r][c] = Tileset.FLOOR;
            print();

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
                    up = up * 10;
                    break;
                case 1:
                    down = down * 10;
                    break;
                case 2:
                    left = left * 10;
                    break;
                case 3:
                    right = right * 10;
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
                    r = r - 1;
                    direction = nextDirection;
                    break;
                case 1:
                    r = r + 1;
                    direction = nextDirection;
                    break;
                case 2:
                    c = c - 1;
                    direction = nextDirection;
                    break;
                case 3:
                    c = c + 1;
                    direction = nextDirection;
                    break;
                default:
                    break;
            }
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

    private boolean roomsConnected() {
        for (int i = 0; i < roomCheck.length; i++) {
            if (!roomCheck[i]) {
                return false;
            }
        }
        return true;
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
