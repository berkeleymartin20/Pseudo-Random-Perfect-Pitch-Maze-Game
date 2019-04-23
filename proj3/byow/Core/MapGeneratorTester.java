package byow.Core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapGeneratorTester {

    private String arrayToString(int[] p) {
        return "(" + p[0] + "," + p[1] + ")";
    }

    @Test
    public void testCoordinateConversion() {

        // 3x3 grid [[0,1,2],[3,4,5],[6,7,8]]
        int[] answer3x3 = new int[]{2,1};
        MapGenerator mg3x3 = new MapGenerator(3,3);
        assertEquals(arrayToString(answer3x3), arrayToString(mg3x3.convertIndextoXY(5)));
        assertEquals(5, mg3x3.convertXYtoIndex(answer3x3[0], answer3x3[1]));

        // 4x4 grid [[0,1,2,3],[4,5,6,7],[8,9,10,11],[12,13,14,15]]
        int[] answer4x4 = new int[]{2,2};
        MapGenerator mg4x4 = new MapGenerator(4,4);
        assertEquals(arrayToString(answer4x4), arrayToString(mg4x4.convertIndextoXY(10)));
        assertEquals(10, mg4x4.convertXYtoIndex(answer4x4[0], answer4x4[1]));

        // 3x4 grid [[0,1,2],[3,4,5],[6,7,8],[9,10,11]]
        int[] answer3x4 = new int[]{0,3};
        MapGenerator mg3x4 = new MapGenerator(3,4);
        assertEquals(arrayToString(answer3x4), arrayToString(mg3x4.convertIndextoXY(9)));
        assertEquals(9, mg3x4.convertXYtoIndex(answer3x4[0], answer3x4[1]));


    }
}
