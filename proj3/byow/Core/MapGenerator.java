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
    public int[][] roomWorld;

    //list of rooms' indexPositions
    private int[] indexedRoomPosition;

    private UF roomsUnion;

    private int numRooms;

    private Random RANDOM;

    public static int avatarLocation;
    public static int goalLocation;
    public static TETile avatar;


    /**
     * @param width  The width of the screen
     * @param height The length of the screen
     */
    public MapGenerator(int width, int height, int seed /*TETile avtr*/) {

        WIDTH = width;
        HEIGHT = height;
        RANDOM = new Random(seed);
        //avatar = avtr;

        world = new TETile[WIDTH][HEIGHT];
        roomWorld = new int[WIDTH][HEIGHT];

        for (int r = 0; r < WIDTH; r += 1) {
            for (int c = 0; c < HEIGHT; c += 1) {
                world[r][c] = Tileset.NOTHING;
                roomWorld[r][c] = WIDTH*HEIGHT;
            }
        }

        numRooms = RANDOM.nextInt(HEIGHT * WIDTH / 60) + 2; //number of rooms at least 2
        indexedRoomPosition = new int[numRooms];
        roomsUnion = new UF(numRooms);
        for (int i = 0; i < numRooms; i++) {
            generateRoom(i);
        }

        for (int i = 0; i < numRooms; i++) {
            while (!roomsUnion.connected((i+1)%numRooms, i)) {
                int[] rc = convertIndextoXY(indexedRoomPosition[i]);
                step(rc[0], rc[1], RANDOM.nextInt(4), i);
            }
        }

        walls();

        avatarPoint();
        goalPoint();

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

        /*myRooms[roomNum].r = r;
        myRooms[roomNum].c = c;*/
        indexedRoomPosition[roomNum] = convertXYtoIndex(r,c);

        for (int i = r; i <= r + width / 4; i++) {
            for (int j = c; j <= c + height / 4; j++) {
                connectIfRoom(i, j, roomNum);
                if (world[i][j] == Tileset.FLOOR) {
                    break;
                }
                world[i][j] = Tileset.FLOOR;
                roomWorld[i][j] = roomNum;
            }
        }

    }

    private void connectIfRoom(int r, int c, int roomNum) {
        int room = roomWorld[r][c];
        if (room<numRooms && !roomsUnion.connected(roomNum, room)) {
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
        int targetRoomNum = (roomNum + 1)% numRooms;
        while (!roomsUnion.connected(roomNum, targetRoomNum)) {
            connectIfRoom(r, c, roomNum);

            world[r][c] = Tileset.FLOOR;
            roomWorld[r][c] = roomNum;

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

            int[] targetRC = convertIndextoXY(indexedRoomPosition[targetRoomNum]);

            int rToTarget = r - targetRC[0];
            int cToTarget = c - targetRC[1];


            if(rToTarget>0){
                up = up*5;
            }
            else{
                down = down*5;
            }
            if(cToTarget>0){
                left = left * 5;
            }
            else{
                right = right *5;
            }

            switch (direction) {
                case 0:
                    up = up * 15;
                    break;
                case 1:
                    down = down * 15;
                    break;
                case 2:
                    left = left * 15;
                    break;
                case 3:
                    right = right * 15;
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

    public TETile[][] getWorld() {
        return world;
    }

    /**
     * picks the initial starting point of the avatar
     */
    public void avatarPoint(){
        int r = pickR();
        int c = pickC();
        while(world[r][c]!= Tileset.FLOOR){
            r = pickR();
            c = pickC();
        }
        avatarLocation = convertXYtoIndex(r,c);
        world[r][c] = avatar;
    }

    /**
     * picks the initial starting point of the avatar
     */
    public void goalPoint(){
        int r = pickR();
        int c = pickC();
        while(world[r][c]!= Tileset.FLOOR &&
                (convertXYtoIndex(r,c)!=avatarLocation)){
            r = pickR();
            c = pickC();
        }
        goalLocation = convertXYtoIndex(r,c);
    }

    public static int convertXYtoIndex(int x, int y) {
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
    public static int[] convertIndextoXY(int p) {
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;
        return coord;
    }


}
