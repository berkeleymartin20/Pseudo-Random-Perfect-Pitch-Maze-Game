package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapGeneratorTester {

    private String arrayToString(int[] p) {
        return "(" + p[0] + "," + p[1] + ")";
    }

    @Test
    public void testCoordinateConversion() {

        /*TETile[][] tiles = new TETile[10][5];
        for(int x= 0;x<10; x++){
            for(int y = 0; y<5; y++){
                if(x==0){
                    tiles[x][y] = Tileset.FLOOR;
                }
                else{
                    tiles[x][y] = Tileset.GRASS;
                }
            }
        }
        for (int x = 0; x < 10; x += 1) {
            for (int y = 0; y < 5; y += 1) {
                System.out.print(tiles[x][y].description()+" ");
            }
            System.out.println();
        }*/


        // 3x3 grid [[0,1,2],[3,4,5],[6,7,8]]
        int[] answer3x3 = new int[]{2, 1};
        MapGenerator mg3x3 = new MapGenerator(3, 3, 3);
        assertEquals(arrayToString(answer3x3), arrayToString(mg3x3.convertIndextoXY(5)));
        assertEquals(5, mg3x3.convertXYtoIndex(answer3x3[0], answer3x3[1]));
        for (int x = 0; x < 3; x += 1) {
            for (int y = 0; y < 3; y += 1) {
                System.out.print(mg3x3.world[x][y].description() + " ");
            }
            System.out.println();
        }

        // 4x4 grid [[0,1,2,3],[4,5,6,7],[8,9,10,11],[12,13,14,15]]
        int[] answer4x4 = new int[]{2, 2};
        MapGenerator mg4x4 = new MapGenerator(4, 4, 3);
        assertEquals(arrayToString(answer4x4), arrayToString(mg4x4.convertIndextoXY(10)));
        assertEquals(10, mg4x4.convertXYtoIndex(answer4x4[0], answer4x4[1]));
        for (int x = 0; x < 4; x += 1) {
            for (int y = 0; y < 4; y += 1) {
                System.out.print(mg4x4.world[x][y].description() + " ");
            }
            System.out.println();
        }

        // 3x4 grid [[0,1,2],[3,4,5],[6,7,8],[9,10,11]]
        int[] answer3x4 = new int[]{0, 3};
        MapGenerator mg3x4 = new MapGenerator(3, 4, 3);
        assertEquals(arrayToString(answer3x4), arrayToString(mg3x4.convertIndextoXY(9)));
        assertEquals(9, mg3x4.convertXYtoIndex(answer3x4[0], answer3x4[1]));
        for (int x = 0; x < 4; x += 1) {
            for (int y = 0; y < 3; y += 1) {
                System.out.print(mg3x4.world[x][y].description() + " ");
            }
            System.out.println();
        }

        TERenderer ter = new TERenderer();
        Engine engine = new Engine();
        TETile[][] map = engine.interactWithInputString("n12323s");
        /*for (int x = 0; x < map.length; x += 1) {
            for (int y = 0; y < map[0].length; y += 1) {
                System.out.print(map[x][y].description() + " ");
            }
            System.out.println();
        }*/
        ter.initialize(map.length, map[0].length);
        ter.renderFrame(map);
        ter.renderFrame(mg3x4.world);

    }
}
