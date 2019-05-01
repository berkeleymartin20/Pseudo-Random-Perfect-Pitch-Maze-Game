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
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final int PITCH_LIMIT = 128;
    private static final double MAX_DISTANCE = Math.sqrt((double)WIDTH*(double)WIDTH + (double)HEIGHT*(double)HEIGHT);
    private static final int TILE_SIZE = 16;
    private boolean hasntStarted = true;
    private boolean endGame = true;
    private TETile[][] world; //TODO
    private boolean startGame = false;
    private boolean doneChoosingCharacter = true;
    private boolean endGameScreen = false;
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
        Clip bleep = null;
        Clip done = null;
        try {
            String filename = "byow/Core/background.wav";
            String beep = "byow/Core/beep.wav";
            String finish = "byow/Core/finish.wav";
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
            AudioInputStream inputStream2 = AudioSystem.getAudioInputStream(new File(beep).getAbsoluteFile());
            AudioInputStream inputStream3 = AudioSystem.getAudioInputStream(new File(finish).getAbsoluteFile());
            clip = AudioSystem.getClip();
            bleep = AudioSystem.getClip();
            done = AudioSystem.getClip();
            clip.open(inputStream);
            bleep.open(inputStream2);
            done.open(inputStream3);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) { } //Background music at the lobby
        while(endGame) {
            //SPEC CODE

            if (!endGameScreen && doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_N)) {
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
                startGame = true;
            }
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_C)) {
                displayAvatar(P2);
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
                doneChoosingCharacter = false;
            }

            //navigate in the main lobby
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                if (P < 2) {
                    P++;
                }
                displayMenu(P);
                StdDraw.show();
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
            }
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                if (P > 0) {
                    P--;
                }
                displayMenu(P);
                StdDraw.show();
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
            }
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
                if (P == 0) { startGame = true; }
                if (P == 2) { displayAvatar(P2);
                    StdDraw.show();
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) { }
                    doneChoosingCharacter = false;
                }
                //if(P == 1) { displayInstruments(); }
            }

            //navigate in the character selection
            if (!endGameScreen && !doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                if (P2 > 0) {
                    P2--;
                }
                displayAvatar(P2);
                StdDraw.show();
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
            }
            if (!endGameScreen && !doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                if (P2< 3) {
                    P2++;
                }
                displayAvatar(P2);
                StdDraw.show();
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
            }
            if (!endGameScreen && !doneChoosingCharacter && StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                try {
                    bleep.setFramePosition(0);
                    bleep.start();
                    Thread.sleep(200);
                    bleep.stop();
                } catch (Exception e) {
                }
                if (P2 == 0) { MapGenerator.avatar = Tileset.AVATAR; }
                if (P2 == 1) { MapGenerator.avatar = Tileset.MOUNTAIN; }
                if (P2 == 2) { MapGenerator.avatar = Tileset.FLOWER; }
                if (P2 == 3) { MapGenerator.avatar = Tileset.TREE; }
                doneChoosingCharacter = true;
                displayMenu(P);
                StdDraw.show();
            }

            //start the game
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && startGame) {
                hasntStarted = false;
                TERenderer ter = new TERenderer();
                ter.initialize(WIDTH, HEIGHT);
                String seed = "n519788003164s";
                world = interactWithInputString(seed);
                ter.renderFrame(world);
                clip.stop();
            }

            // CHEAT: SHOW THE GOAL
            if (!endGameScreen && !hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                int[] goal = MapGenerator.convertIndextoXY(MapGenerator.goalLocation);
                world[goal[0]][goal[1]] = Tileset.FLOWER;
                ter.renderFrame(world);
            }

            //move the character
            if (!endGameScreen && !hasntStarted && startGame && StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                int[] move = new int[]{0, 1};
                update(move);
                ter.renderFrame(world);
                sound();
            }
            if (!endGameScreen && !hasntStarted && startGame && StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                int[] move = new int[]{0, -1};
                update(move);
                ter.renderFrame(world);
                sound();
            }
            if (!endGameScreen && !hasntStarted && startGame && StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                int[] move = new int[]{1, 0};
                update(move);
                ter.renderFrame(world);
                sound();
            }
            if (!endGameScreen && !hasntStarted && startGame && StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                int[] move = new int[]{-1, 0};
                update(move);
                ter.renderFrame(world);
                sound();
            }

            //game ended. Options to restart or quit
            if (!hasntStarted && MapGenerator.avatarLocation == MapGenerator.goalLocation) {
                endGameScreen = true;
                hasntStarted = true;
                displayEnding();
                StdDraw.show();
                done.setFramePosition(0);
                done.loop(Clip.LOOP_CONTINUOUSLY);
            }
            if(endGameScreen && StdDraw.isKeyPressed(KeyEvent.VK_N)) {
                try {Thread.sleep(100);} catch (Exception e) {}
                endGameScreen = false;
                startGame = false;
                doneChoosingCharacter = true;
                hasntStarted = true;
                displayMenu(P);
                done.stop();
                StdDraw.show();
            }


            //kill the game
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
        }  else if(world[coord[0] + move[0]][coord[1]+move[1]] == Tileset.FLOWER)    {
            world[coord[0] + move[0]][coord[1]+move[1]] = world[coord[0]][coord[1]];
            world[coord[0]][coord[1]] = Tileset.FLOOR;
            MapGenerator.avatarLocation = MapGenerator.convertXYtoIndex(coord[0] + move[0],coord[1]+move[1]);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public  TETile[][] interactWithInputString(String input) {
        int seed = 0;
        char[] chararray = input.toCharArray();
        for (int i = 0; i < chararray.length - 1; i++) {
            seed = seed * 10 + Character.getNumericValue(chararray[i]);
        }
        MapGenerator map = new MapGenerator(WIDTH, HEIGHT, seed);
        TETile[][] finalWorldFrame = map.getWorld();
        return finalWorldFrame;
    }


    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2 + 3, "START NEW GAME (N)");
        StdDraw.text(WIDTH/2, HEIGHT/2 , "LOAD GAME (L)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 3, "CHANGE AVATAR (C)");
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH/2, 6, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH/2, 2, "PRESS Q TO EXIT (Q)");
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
        StdDraw.text(WIDTH/2, HEIGHT/2 + 3, "START NEW GAME (N)");
        StdDraw.text(WIDTH/2, HEIGHT/2 , "LOAD GAME (L)");
        StdDraw.text(WIDTH/2, HEIGHT/2 - 3, "CHANGE AVATAR (C)");
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH/2, 6, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH/2, 2, "PRESS Q TO EXIT (Q)");
        double[] x = new double[]{WIDTH/2 -13, WIDTH/2 -13, WIDTH/2 - 12};
        double[] y = new double[]{HEIGHT/2 +3.5 - P*3, HEIGHT/2 + 2.5 - P*3, HEIGHT/2 +3 - P*3};
        StdDraw.filledPolygon(x,y);
        MapGenerator.avatar.draw(WIDTH/2 + 7,5.5);
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
    public void displayEnding() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH/2, HEIGHT/2, "CONGRATS YOU DID IT");
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH/2, 2, "PRESS N TO START A NEW GAME");
        StdDraw.text(WIDTH/2, 4, "PRESS Q TO QUIT");
    }
}
