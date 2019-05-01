package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final int PITCH_LIMIT = 128;
    private static final double MAX_DISTANCE = Math.sqrt((double)WIDTH*(double)WIDTH + (double)HEIGHT*(double)HEIGHT);
    private static final int TILE_SIZE = 16;
    private boolean hasntStarted = true;
    private boolean endGame = true;
    private static TETile[][] world;
    private boolean startGame = false;
    private boolean doneChoosingCharacter = true;
    private MidiChannel midi;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard () {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midi = synthesizer.getChannels()[4];
        } catch (MidiUnavailableException e) { } //initialize midi
        int P = 0;
        int P2 = 0;
        MapGenerator.avatar = Tileset.AVATAR;
        displayMenu();
        Clip clip = null;
        try {
            String filename = "byow/Core/background.wav";
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) { } //Background music at the lobby

        while(endGame) {

            //navigate in the main lobby
            if (doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                if (P < 2) {
                    P++;
                }
                displayMenu(P);
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }
            if (doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                if (P > 0) {
                    P--;
                }
                displayMenu(P);
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }
            if (doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (P == 0) { startGame = true; }
                if (P == 1) { displayAvatar(P2);
                    try { Thread.sleep(200);
                    } catch (Exception e) { }
                    doneChoosingCharacter = false;
                }
                //if(P == 2) { displayInstruments(); }
            }

            //navigate in the character selection
            if (!doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                if (P2 > 0) {
                    P2--;
                }
                displayAvatar(P2);
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }
            if (!doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                if (P2< 3) {
                    P2++;
                }
                displayAvatar(P2);
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }
            if (!doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (P2 == 0) { MapGenerator.avatar = Tileset.AVATAR; }
                if (P2 == 1) { MapGenerator.avatar = Tileset.MOUNTAIN; }
                if (P2 == 2) { MapGenerator.avatar = Tileset.FLOWER; }
                if (P2 == 3) { MapGenerator.avatar = Tileset.TREE; }
                try { Thread.sleep(200);
                } catch (Exception e) { }
                doneChoosingCharacter = true;
                displayMenu(P);
            }

            //start the game
            if (doneChoosingCharacter && hasntStarted && startGame) {
                hasntStarted = false;
                TERenderer ter = new TERenderer();
                ter.initialize(WIDTH, HEIGHT);
                String seed = "n519788084356903164s";
                world = interactWithInputString(seed);
                ter.renderFrame(world);
                clip.stop();
            }

            // CHEAT: SHOW THE GOAL
            if (!hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                int[] goal = MapGenerator.convertIndextoXY(MapGenerator.goalLocation);
                world[goal[0]][goal[1]] = Tileset.FLOWER;
                ter.renderFrame(world);
            }

            //move the character
            if (!hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                int[] move = new int[]{0, 1};
                update(move);
                ter.renderFrame(world);
                sound();
                if (MapGenerator.avatarLocation == MapGenerator.goalLocation) {
                    endGame = false;
                    displayEnding();
                }
            }
            if (!hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                int[] move = new int[]{0, -1};
                update(move);
                ter.renderFrame(world);
                sound();
                if (MapGenerator.avatarLocation == MapGenerator.goalLocation) {
                    endGame = false;
                    displayEnding();
                }
            }
            if (!hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                int[] move = new int[]{1, 0};
                update(move);
                ter.renderFrame(world);
                sound();
                if (MapGenerator.avatarLocation == MapGenerator.goalLocation) {
                    endGame = false;
                    displayEnding();
                }
            }
            if (!hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                int[] move = new int[]{-1, 0};
                update(move);
                ter.renderFrame(world);
                sound();
                if (MapGenerator.avatarLocation == MapGenerator.goalLocation) {
                    endGame = false;
                    displayEnding();
                }
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                System.exit(0);
            }
        }
    }

    /**
     * pitch is calculated using the distance
     */
    public void sound() {
        int pitch = (int)(distance()*(PITCH_LIMIT/MAX_DISTANCE));
        play(PITCH_LIMIT - pitch);
        try {
            Thread.sleep(150);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * plays sound based on the pitch (calculated using the distance)
     * @param pitch
     */
    public void play(int pitch) {
        try {
            midi.noteOn(pitch, 50);
            Thread.sleep(100);
            midi.noteOff(pitch, 50);
        } catch (InterruptedException ex) {

        }
    }

    /**
     * calculate the distance between avatar and goal
     * @return distance
     */
    public double distance(){
        int[] start = new int[2];
        int[] goal = new int[2];
        start = MapGenerator.convertIndextoXY(MapGenerator.avatarLocation);
        goal = MapGenerator.convertIndextoXY(MapGenerator.goalLocation);
        return Math.sqrt(Math.pow((start[0]-goal[0]),2) + Math.pow((start[1]-goal[1]),2));
    }

    /**
     * updates the grid based on the move
     * @param move
     */
    public void update(int[] move) {
        int p = MapGenerator.avatarLocation;
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;

        if(world[coord[0] + move[0]][coord[1]+move[1]] == Tileset.FLOOR) {
            TETile temp = world[coord[0]][coord[1]];
            world[coord[0]][coord[1]] = world[coord[0] + move[0]][coord[1]+move[1]];
            world[coord[0] + move[0]][coord[1]+move[1]] = temp;
            MapGenerator.avatarLocation = MapGenerator.convertXYtoIndex(coord[0] + move[0],coord[1]+move[1]);
        }
    }

    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 3, "START NEW GAME");
        StdDraw.text(WIDTH/2, HEIGHT/2 , "CHANGE AVATAR");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 3, "CHANGE INSTRUMENT");
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH/2, 6, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH/2, 2, "PRESS Q TO EXIT");
        double[] x = new double[]{WIDTH/2 -13, WIDTH/2 -13, WIDTH/2 - 12};
        double[] y = new double[]{HEIGHT/2 +3.5 , HEIGHT/2 + 2.5 , HEIGHT/2 +3 };
        StdDraw.filledPolygon(x,y);
        MapGenerator.avatar.draw(WIDTH/2 + 7,5.5);
    }
    private void displayMenu(int P) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 30);
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 3, "START NEW GAME");
        StdDraw.text(WIDTH/2, HEIGHT/2 , "CHANGE AVATAR");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 3, "CHANGE INSTRUMENT");
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH/2, 6, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH/2, 2, "PRESS Q TO EXIT");
        double[] x = new double[]{WIDTH/2 -13, WIDTH/2 -13, WIDTH/2 - 12};
        double[] y = new double[]{HEIGHT/2 +3.5 - P*3, HEIGHT/2 + 2.5 - P*3, HEIGHT/2 +3 - P*3};
        StdDraw.filledPolygon(x,y);
        MapGenerator.avatar.draw(WIDTH/2 + 7,5.5);
    }
    public void displayEnding() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2, "CONGRATS YOU DID IT");
    }
    public void displayAvatar(int P) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 4.5, "@ SYMBOL");
        StdDraw.text(WIDTH/2, HEIGHT/2 + 1.5, "MOUNTAIN");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 1.5, "FLOWER");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 4.5, "TREE");
        double[] x = new double[]{WIDTH/2 -13, WIDTH/2 -13, WIDTH/2 - 12};
        double[] y = new double[]{HEIGHT/2 + 5 - P*3, HEIGHT/2 + 4 - P*3, HEIGHT/2 +4.5 - P*3};
        StdDraw.filledPolygon(x,y);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        char[] chararray = input.toCharArray();

        //pick out seed
        int seed = 0;
        for (int i = 0; i < chararray.length - 1; i++) {
            seed = seed * 10 + Character.getNumericValue(chararray[i]);
        }

        //create map
        MapGenerator map = new MapGenerator(WIDTH,HEIGHT, seed);

        return map.getWorld();
    }

    /*public static void main(String[] args) {
        Engine yo = new Engine();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH,HEIGHT);
        TETile[][] world = yo.interactWithInputString("n23564s");
        ter.renderFrame(world);
    }*/
}
