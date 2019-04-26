package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.UF;

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
    public int[][] rooms;

    //ith element is ith room. 0 element is the outside. true if checked
    private Room[] myRooms;
    private Random RANDOM; //dunno how to deal with seed

    private UF roomsUnion;

    /**
     * class Room
     */
    private class Room {
        int r;
        int c;

        public Room(int R, int C) {
            r = R;
            c = C;
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

        world = new TETile[WIDTH][HEIGHT];
        rooms = new int[WIDTH][HEIGHT];

        for (int r = 0; r < WIDTH; r += 1) {
            for (int c = 0; c < HEIGHT; c += 1) {
                world[r][c] = Tileset.NOTHING;
                rooms[r][c] = 0;
            }
        }

        int numRooms = RANDOM.nextInt(HEIGHT * WIDTH / 100) + 1; //number of rooms [1,10]
        myRooms = new Room[numRooms + 1];
        for (int i = 0; i < numRooms + 1; i++) {
            myRooms[i] = new Room(0, 0);
        }
        roomsUnion = new UF(numRooms + 1);
        roomsUnion.union(0, 1);
        for (int i = 1; i < numRooms + 1; i++) {
            generateRoom(i);
        }

        for (int i = 1; i < numRooms + 1; i++) {
            while (!roomsUnion.connected(1, i)) {
                step(myRooms[i].r, myRooms[i].c, RANDOM.nextInt(4), i);
            }
        }

        /*for (int r = 0; r < WIDTH; r += 1) {
            for (int c = 0; c < HEIGHT; c += 1) {
                if (rooms[r][c] < 10) {
                    System.out.print("0" + rooms[r][c] + "   ");
                } else {
                    System.out.print(rooms[r][c] + "   ");
                }
            }
            System.out.println();
        }*/

        walls();

    }

    private int pickR() {
        int max = HEIGHT - 3;
        if (max != 0) {
            return RANDOM.nextInt(WIDTH - 3) + 1;
        }
        return max;
    }

    private int pickC() {
        int max = HEIGHT - 3;
        if (max != 0) {
            return RANDOM.nextInt(max) + 1;
        }
        return max;
    }

    private void generateRoom(int roomNum) {

        int r = pickR();
        int c = pickC();

        while (world[r][c] == Tileset.FLOOR) {
            r = pickR();
            c = pickC();
        }

        int width = RANDOM.nextInt(WIDTH - r - 2)+1;
        int height = RANDOM.nextInt(HEIGHT - c - 2)+1;

        myRooms[roomNum].r = r;
        myRooms[roomNum].c = c;

        for (int i = r; i <= r + width / 4; i++) {
            for (int j = c; j <= c + height / 4; j++) {
                connectIfRoom(i, j, roomNum);
                if (world[i][j] == Tileset.FLOOR) {
                    break;
                }
                world[i][j] = Tileset.FLOOR;
                rooms[i][j] = roomNum;
            }
        }

    }

    private void connectIfRoom(int r, int c, int roomNum) {
        int room = rooms[r][c];
        if (room != 0 && !roomsUnion.connected(roomNum, room)) {
            roomsUnion.union(roomNum, room);
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
    public void step(int r, int c, int direction, int roomNum) {
        while (!roomsUnion.connected(1, roomNum)) {
            connectIfRoom(r, c, roomNum);

            world[r][c] = Tileset.FLOOR;
            rooms[r][c] = roomNum;

            int up = 1;
            int down = 1;
            int left = 1;
            int right = 1;

            if (r <= 1) {
                up = 0;
            }
            if (r >= WIDTH - 2) {
                down = 0;
            }
            if (c <= 1) {
                left = 0;
            }
            if (c >= HEIGHT - 2) {
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
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j] == Tileset.FLOOR) {
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
