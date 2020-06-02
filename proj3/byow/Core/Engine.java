package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Engine {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final String DEFAULT_SEED = "n5748040917015821252sws";
    private static final int HUD_RATIO = 4;
    private static final double HUD_SCALING
            = (double) HUD_RATIO / (1 + (double) HUD_RATIO);
    private static final int PITCH_LIMIT = 128;
    private static final double MAX_DISTANCE = Math.sqrt((double) WIDTH
            * (double) WIDTH + (double) HEIGHT * (double) HEIGHT);
    private static final int TILE_SIZE = 16;
    private TERenderer ter = new TERenderer();
    private boolean hasntStarted = true;
    private boolean endGame = true;
    private TETile[][] world;
    private MapGenerator map;

    private int countDown = HEIGHT + WIDTH;
    /**
     * MEMEMEMEMEMEMEMEMEME
     */
    private TETile[][] meme;
    private MapGenerator memeMap;

    private List<String> words;
    private boolean startGame = false;
    private boolean doneChoosingCharacter = true;
    private boolean endGameScreen = false;
    private boolean seedEntered = false;
    private boolean prepareToSave = false;
    private boolean memeLand = false;
    private String seed = "";
    private String phrase = "";
    private String keysPressed = "";
    private MidiChannel midi;
    private TETile av;
    private String previousLoad = "";

    private int p = 0;
    private int p2 = 0;
    private int counter = 0;

    private Clip clip = null;
    private Clip bleep = null;
    private Clip done = null;
    private Clip rick = null;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            midi = synthesizer.getChannels()[4];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } //initialize midi
        try {
            String wow = "byow/Core/wow.txt";
            File file = new File(wow).getAbsoluteFile();
            Scanner scanner = new Scanner(new FileInputStream(file));
            words = new ArrayList<>();
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } //initialize the text file for words of wisdom
        av = Tileset.AVATAR;
        displayMenu();
        StdDraw.enableDoubleBuffering();
        tryAudio();
        while (endGame) {
            //navigate in the main lobby
            if (!seedEntered && !endGameScreen && doneChoosingCharacter && hasntStarted) {
                menu();
            }
            //input seed screen
            if (seedEntered && !endGameScreen && doneChoosingCharacter && hasntStarted) {
                collectSeed();
            }
            //navigate in the character selection
            if (!seedEntered && !endGameScreen && !doneChoosingCharacter) {
                chooseAvatar();
            }
            //start the game
            if (!endGameScreen && doneChoosingCharacter && hasntStarted && startGame) {
                startGame();
            }
            // game
            if (!endGameScreen && !hasntStarted && countDown > 0) {
                gameMap();
            }
            if (!endGameScreen && !hasntStarted && countDown <= 0) {
                displayEnding("YOU RAN OUT OF STEPS :(");
                StdDraw.show();
                countDown = WIDTH + HEIGHT;
                endGameScreen = true;
                hasntStarted = true;
                done.setFramePosition(0);
            }
            //game ended. Options to restart or quit
            if (!hasntStarted && map.avatarLocation == map.goalLocation) {
                displayEnding("CONGRATS YOU DID IT");
                StdDraw.show();
                endGameScreen = true;
                hasntStarted = true;
                done.setFramePosition(0);
                done.loop(Clip.LOOP_CONTINUOUSLY);
            }
            if (endGameScreen && StdDraw.isKeyPressed(KeyEvent.VK_N)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seed = "";
                keysPressed = "";
                endGameScreen = false;
                startGame = false;
                doneChoosingCharacter = true;
                hasntStarted = true;
                seedEntered = false;
                displayMenu(p);
                done.stop();
                StdDraw.show();
            }
            //kill the game
            if (hasntStarted && StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                System.exit(0);
            }
        }
    }

    public void tryAudio() {
        try {
            String filename = "byow/Core/background.wav";
            String beep = "byow/Core/beep.wav";
            String finish = "byow/Core/finish.wav";
            String r = "byow/Core/rick.wav";
            AudioInputStream inputStream
                    = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
            AudioInputStream inputStream2
                    = AudioSystem.getAudioInputStream(new File(beep).getAbsoluteFile());
            AudioInputStream inputStream3
                    = AudioSystem.getAudioInputStream(new File(finish).getAbsoluteFile());
            AudioInputStream inputStream4
                    = AudioSystem.getAudioInputStream(new File(r).getAbsoluteFile());
            clip = AudioSystem.getClip();
            bleep = AudioSystem.getClip();
            done = AudioSystem.getClip();
            rick = AudioSystem.getClip();
            clip.open(inputStream);
            bleep.open(inputStream2);
            done.open(inputStream3);
            rick.open(inputStream4);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException | IOException
                | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void troll() {
        String file = "byow/Core/rickpic.png";
        StdDraw.picture((double) WIDTH / 2.0, (double) HEIGHT * (2.5 / 3.0), file,
                HEIGHT / 2, HEIGHT / 2, 10 * counter);
        StdDraw.show();
        counter++;
    }

    public void startGame() {
        hasntStarted = false;
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + (HEIGHT / HUD_RATIO));
        ter.renderFrame(world);

        clip.stop();
    }

    public void hud() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.rectangle(WIDTH / 2,
                ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - HEIGHT / (2 * HUD_RATIO),
                (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        StdDraw.show();
    }

    public void gameMap() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (startGame && !memeLand) {
            ter.renderFrame(world);
            hud();
            int mouseX = (int) Math.floor(StdDraw.mouseX());
            int mouseY = (int) Math.floor(StdDraw.mouseY());
            if (mouseX < WIDTH && mouseY < HEIGHT) {
                StdDraw.text(WIDTH / 15, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                        world[mouseX][mouseY].description());
                StdDraw.show();
            }
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int[] goal = convertIndextoXY(map.goalLocation);
            int[] m = convertIndextoXY(map.memeLocation);
            if (world[goal[0]][goal[1]] == Tileset.FLOOR) {
                world[goal[0]][goal[1]] = Tileset.FLOWER;
            } else {
                world[goal[0]][goal[1]] = Tileset.FLOOR;
            }
            if (world[m[0]][m[1]] == Tileset.FLOOR) {
                world[m[0]][m[1]] = Tileset.FLOWER;
            } else {
                world[m[0]][m[1]] = Tileset.FLOOR;
            }
            ter.renderFrame(world);
        }

        if (startGame && !memeLand) {

            StdDraw.text(9 * WIDTH / 10, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                    "STEPS REMAINING: " + countDown);
            StdDraw.show();

            if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                moveAvatar(new int[]{0, 1});
                keysPressed += "W";
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                moveAvatar(new int[]{0, -1});
                keysPressed += "S";
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                moveAvatar(new int[]{1, 0});
                keysPressed += "D";
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                moveAvatar(new int[]{-1, 0});
                keysPressed += "A";
            }
            //save if ":" + "Q" pressed
            if (StdDraw.isKeyPressed(KeyEvent.VK_SEMICOLON)
                    || StdDraw.isKeyPressed(KeyEvent.VK_COLON)) {
                keysPressed += ":";
                ter.renderFrame(world);
                prepareToSave = true;
                hud();
            }
            if (prepareToSave && StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                keysPressed += "Q";
                if (("" + keysPressed.charAt(keysPressed.length() - 2)).equals(":")) {
                    //saves position and seed
                    try (PrintWriter out = new PrintWriter("load.txt")) {
                        out.println(previousLoad + keysPressed);
                        out.println(countDown);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }

            if (map.avatarLocation == map.memeLocation) {
                memeLand = true;
                ter.renderFrame(meme);
                troll();
                StdDraw.show();
                rick.setFramePosition(0);
                rick.loop(Clip.LOOP_CONTINUOUSLY);
            }

        }

        if (startGame && memeLand) {
            troll();
            if (StdDraw.isKeyPressed(KeyEvent.VK_W)) {
                moveMeme(new int[]{0, 1});
                troll();
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                moveMeme(new int[]{0, -1});
                troll();
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                moveMeme(new int[]{1, 0});
                troll();
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                moveMeme(new int[]{-1, 0});
                troll();
            }
            if (memeMap.avatarLocation == memeMap.goalLocation) {
                memeLand = false;
                int temp = map.avatarLocation;
                moveAvatar(new int[]{0, -1});
                int temp2 = map.avatarLocation;
                if (temp == temp2) {
                    moveAvatar(new int[]{0, 1});
                    temp2 = map.avatarLocation;
                }
                if (temp == temp2) {
                    moveAvatar(new int[]{1, 0});
                    temp2 = map.avatarLocation;
                }
                if (temp == temp2) {
                    moveAvatar(new int[]{-1, 0});
                }
                ter.renderFrame(world);
                hud();
                memeMap = new MapGenerator(WIDTH, HEIGHT, map.avatar);
                meme = memeMap.getMeme();
                int[] m = convertIndextoXY(map.memeLocation);
                world[m[0]][m[1]] = Tileset.FLOWER;
                rick.stop();
            }
        }
    }

    public void chooseAvatar() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
            if (p2 > 0) {
                p2--;
            }
            displayAvatar(p2);
            StdDraw.show();
            bleep();
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
            if (p2 < 3) {
                p2++;
            }
            displayAvatar(p2);
            StdDraw.show();
            bleep();
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            bleep();
            if (p2 == 0) {
                av = Tileset.AVATAR;
            }
            if (p2 == 1) {
                av = Tileset.MOUNTAIN;
            }
            if (p2 == 2) {
                av = Tileset.FLOWER;
            }
            if (p2 == 3) {
                av = Tileset.TREE;
            }
            doneChoosingCharacter = true;
            displayMenu(p);
            StdDraw.show();
        }
    }

    public void collectSeed() {
        if (StdDraw.hasNextKeyTyped()) {
            /*try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            char add = StdDraw.nextKeyTyped();
            if (!checkIfNumber(add)) {
                return;
            } else {
                displaySeed(add); //displays current seed + add
                keysPressed += add;
                StdDraw.show();
                seed = seed + add;
            }
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) || StdDraw.isKeyPressed(KeyEvent.VK_S)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            keysPressed += "S";
            seedEntered = false;
            startGame = true;
            world = interactWithInputString(seed);
            map.avatar = av;
            int[] avLocation = convertIndextoXY(map.avatarLocation);
            world[avLocation[0]][avLocation[1]] = map.avatar;
            StdDraw.show();
        }
    }

    public void menu() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_N)
                || (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && p == 0)) {
            keysPressed += "N";
            countDown = HEIGHT + WIDTH;
            bleep();
            displaySeed();
            StdDraw.show();
            seedEntered = true;
            previousLoad = "";
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_C)
                || (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && p == 2)) {
            displayAvatar(p2);
            StdDraw.show();
            bleep();
            doneChoosingCharacter = false;
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_L)
                || (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && p == 1)) {
            bleep();
            seedEntered = false;
            startGame = true;
            String loadSeed = "";
            try {
                String load = "load.txt";
                File loadFile = new File(load).getAbsoluteFile();
                Scanner loadScanned = new Scanner(new FileInputStream(loadFile));
                loadSeed = loadScanned.next();
                countDown = Integer.parseInt(loadScanned.next());
                loadSeed = loadSeed.substring(0, loadSeed.length() - 2);
                previousLoad = loadSeed;
            } catch (IOException e) {
                e.printStackTrace();
            }
            world = interactWithInputString(loadSeed);
            map.avatar = av;
            int[] avLocation = convertIndextoXY(map.avatarLocation);
            world[avLocation[0]][avLocation[1]] = map.avatar;
            StdDraw.show();
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
            if (p < 2) {
                p++;
            }
            displayMenu(p);
            StdDraw.show();
            bleep();
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
            if (p > 0) {
                p--;
            }
            displayMenu(p);
            StdDraw.show();
            bleep();
        }
    }

    public void bleep() {
        try {
            bleep.setFramePosition(0);
            bleep.start();
            Thread.sleep(200);
            bleep.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveAvatar(int[] move) {
        update(move);
        ter.renderFrame(world);
        hud();
        sound();
        countDown--;
    }

    public void moveMeme(int[] move) {
        updateMeme(move);
        ter.renderFrame(meme);
        sound();
    }

    /**
     * pitch is calculated using the distance
     */
    public void sound() {
        int pitch = (int) (distance() * (PITCH_LIMIT / MAX_DISTANCE));
        play(PITCH_LIMIT - pitch);
        /*try {
            Thread.sleep(150);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
    }

    /**
     * plays sound based on the pitch (calculated using the distance)
     *
     * @param pitch
     */
    public void play(int pitch) {
        try {
            midi.noteOn(pitch, 50);
            Thread.sleep(100);
            midi.noteOff(pitch, 50);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * calculate the distance between avatar and goal
     *
     * @return distance
     */
    public double distance() {
        int[] start = new int[2];
        int[] goal = new int[2];
        start = convertIndextoXY(map.avatarLocation);
        goal = convertIndextoXY(map.goalLocation);
        return Math.sqrt(Math.pow((start[0] - goal[0]), 2) + Math.pow((start[1] - goal[1]), 2));
    }

    /**
     * updates the grid based on the move
     *
     * @param move
     */
    public void update(int[] move) {
        int p = map.avatarLocation;
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;

        if (world[coord[0] + move[0]][coord[1] + move[1]] == Tileset.FLOOR) {
            TETile temp = world[coord[0]][coord[1]];
            world[coord[0]][coord[1]] = world[coord[0] + move[0]][coord[1] + move[1]];
            world[coord[0] + move[0]][coord[1] + move[1]] = temp;
            map.avatarLocation = convertXYtoIndex(coord[0] + move[0], coord[1] + move[1]);
        } else if (world[coord[0] + move[0]][coord[1] + move[1]] == Tileset.FLOWER) {
            world[coord[0] + move[0]][coord[1] + move[1]] = world[coord[0]][coord[1]];
            world[coord[0]][coord[1]] = Tileset.FLOOR;
            map.avatarLocation = convertXYtoIndex(coord[0] + move[0], coord[1] + move[1]);
        }
    }

    public void updateMeme(int[] move) {
        int p = memeMap.avatarLocation;
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;

        if (meme[coord[0] + move[0]][coord[1] + move[1]] == Tileset.FLOOR) {
            TETile temp = meme[coord[0]][coord[1]];
            meme[coord[0]][coord[1]] = meme[coord[0] + move[0]][coord[1] + move[1]];
            meme[coord[0] + move[0]][coord[1] + move[1]] = temp;
            memeMap.avatarLocation = convertXYtoIndex(coord[0] + move[0], coord[1] + move[1]);
        } else if (meme[coord[0] + move[0]][coord[1] + move[1]] == Tileset.FLOWER) {
            meme[coord[0] + move[0]][coord[1] + move[1]] = meme[coord[0]][coord[1]];
            meme[coord[0]][coord[1]] = Tileset.FLOOR;
            memeMap.avatarLocation = convertXYtoIndex(coord[0] + move[0], coord[1] + move[1]);
        }
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
        int seedT = 0;
        int counter = 2;
        String moves = "";
        input = input.toLowerCase();
        if (input.equals("")) {
            input = DEFAULT_SEED;
        }
        if (input.charAt(0) == 'n') {
            for (int i = 1; input.charAt(i) != 's'; i++) {
                seedT = seedT * 10 + (input.charAt(i));
                counter++;
            }
            initialize(seedT);

            if (input.length() - 1 >= counter) {
                moves = input.substring(counter);
            }
            for (int x = 0; x < moves.length(); x++) {
                String temp = "" + moves.charAt(x);
                String test = "";
                if (x >= 1) {
                    test = "" + moves.charAt(x - 1);
                }
                if (temp.equals("q") && test.equals(":")) {
                    try (PrintWriter out = new PrintWriter("load.txt")) {
                        out.println(input.substring(0, input.length() - 3));
                        out.println(countDown);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                avatarUpdate(temp);
            }
        } else if (input.charAt(0) == 'l') {
            String movesCombined = "";
            String scanThis = "";
            try {
                String load = "load.txt";
                File loadFile = new File(load).getAbsoluteFile();
                Scanner loadScanned = new Scanner(new FileInputStream(loadFile));
                movesCombined = loadScanned.next();
                countDown = Integer.parseInt(loadScanned.next());
                movesCombined = movesCombined.substring(0, movesCombined.length() - 2);
                scanThis = movesCombined;
            } catch (IOException e) {
                e.printStackTrace();
            }
            movesCombined += input.substring(1);
            movesCombined = movesCombined.toLowerCase();
            for (int i = 1; scanThis.charAt(i) != 's'; i++, counter++) {
                seedT = seedT * 10 + (scanThis.charAt(i));
            }
            initialize(seedT);
            moves = movesCombined.substring(counter - 1);
            for (int x = 0; x < moves.length(); x++) {
                String temp = "" + moves.charAt(x);
                String test = "";
                if (x >= 1) {
                    test = "" + moves.charAt(x - 1);
                }
                if (temp.equals("q") && test.equals(":")) {
                    try (PrintWriter out = new PrintWriter("load.txt")) {
                        out.println(movesCombined.substring(0, movesCombined.length() - 2));
                        out.println(countDown);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                avatarUpdate(temp);
            }
        } else {
            for (int i = 0; i < input.length(); i++) {
                seedT = seedT * 10 + (input.charAt(i));
            }
            this.seed = "" + seedT;
            initialize(seedT);
        }
        return world;
    }

    public void initialize(int seed) {
        this.seed = "" + seed;
        map = new MapGenerator(WIDTH, HEIGHT, seed);
        world = map.getWorld();
        map.avatar = av;
        int[] avLocation = convertIndextoXY(map.avatarLocation);
        world[avLocation[0]][avLocation[1]] = map.avatar;
        memeMap = new MapGenerator(WIDTH, HEIGHT, av);
        meme = memeMap.getMeme();
    }

    public void avatarUpdate(String direction) {
        int[] move;
        if (direction.equals("w")) {
            move = new int[]{0, 1};
            update(move);
        } else if (direction.equals("s")) {
            move = new int[]{0, -1};
            update(move);
        } else if (direction.equals("a")) {
            move = new int[]{-1, 0};
            update(move);
        } else if (direction.equals("d")) {
            move = new int[]{1, 0};
            update(move);
        }
    }

    public boolean checkIfNumber(char c) {
        int x = c - '0';
        return x == 0 || x == 1 || x == 2 || x == 3 || x == 4
                || x == 5 || x == 6 || x == 7 || x == 8 || x == 9;
    }

    public int[] convertIndextoXY(int p) {
        int[] coord = new int[2];
        coord[0] = p % WIDTH;
        coord[1] = p / WIDTH;
        return coord;
    }

    public int convertXYtoIndex(int x, int y) {
        if (y == 0) {
            return x;
        } else {
            return y * WIDTH + x;
        }
    }


    public void wordsOfWisdom() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(words.size());
        phrase = words.get(randomIndex);
    }

    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * TILE_SIZE,
                HEIGHT * TILE_SIZE + (HEIGHT / HUD_RATIO) * TILE_SIZE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT * (1 + HUD_RATIO) / HUD_RATIO);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font HUDFont = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(HUDFont);
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                "USE ARROW KEYS TO NAVIGATE AND SPACE BAR TO SELECT");
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 4,
                "SHORTCUTS: (N) NEW GAME   (L) LOAD GAME   (S) ENTER CUSTOM SEED   (Q) EXIT");

        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 + 3), "START NEW GAME (N)");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2), "LOAD GAME (L)");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 - 3), "CHANGE AVATAR (C)");
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH / 2, 4, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH / 2, 2, "PRESS Q TO EXIT (Q)");
        double[] x = new double[]{WIDTH / 2 - 13, WIDTH / 2 - 13, WIDTH / 2 - 12};
        double[] y = new double[]{(HEIGHT / 2 + 3.5), (HEIGHT / 2 + 2.5), (HEIGHT / 2 + 3)};
        StdDraw.filledPolygon(x, y);
        av.draw(WIDTH / 2 + 7, 3.5);
    }

    private void displayMenu(int P) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        wordsOfWisdom();

        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font HUDFont = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(HUDFont);
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                "USE ARROW KEYS TO NAVIGATE AND SPACE BAR TO SELECT");
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 4,
                "SHORTCUTS: (N) NEW GAME   (L) LOAD GAME   (S) ENTER CUSTOM SEED   (Q) EXIT");

        Font font = new Font("Arial", Font.BOLD, 30);
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 + 3), "START NEW GAME (N)");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2), "LOAD GAME (L)");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 - 3), "CHANGE AVATAR (C)");
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH / 2, 4, "CURRENT AVATAR: ");
        StdDraw.text(WIDTH / 2, 2, "PRESS Q TO EXIT (Q)");
        double[] x = new double[]{WIDTH / 2 - 13, WIDTH / 2 - 13, WIDTH / 2 - 12};
        double[] y = new double[]{(HEIGHT / 2 + 3.5 - P * 3), (HEIGHT / 2 + 2.5 - P * 3),
                (HEIGHT / 2 + 3 - P * 3)};
        StdDraw.filledPolygon(x, y);
        av.draw(WIDTH / 2 + 7, 3.5);
    }

    private void displaySeed() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font HUDFont = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(HUDFont);
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                "TYPE YOUR SEED (IN NUMBERS) THEN PRESS SPACE BAR");
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 4,
                "PRESS Q TO EXIT");
        Font font = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font);
        seed = "";
        StdDraw.filledRectangle(WIDTH / 2, HUD_SCALING * (HEIGHT / 2),
                WIDTH / 8, HEIGHT / 15);
    }

    private void displaySeed(char add) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font HUDFont = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(HUDFont);
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                "TYPE YOUR SEED (IN NUMBERS) THEN PRESS SPACE BAR");
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 4,
                "PRESS Q TO EXIT");
        StdDraw.filledRectangle(WIDTH / 2, HUD_SCALING * (HEIGHT / 2),
                WIDTH / 8, HEIGHT / 15);
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(WIDTH / 2, HUD_SCALING * (HEIGHT / 2), seed + add);

    }

    public void displayAvatar(int P) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font HUDFont = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(HUDFont);
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 2,
                "USE ARROW KEYS TO NAVIGATE AND SPACE BAR TO SELECT");
        StdDraw.text(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO)) - 4,
                "Words of Wisdom: " + phrase);

        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 + 4.5), "@ SYMBOL");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 + 1.5), "MOUNTAIN");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 - 1.5), "FLOWER");
        StdDraw.text(WIDTH / 2, (HEIGHT / 2 - 4.5), "TREE");
        double[] x = new double[]{WIDTH / 2 - 13, WIDTH / 2 - 13, WIDTH / 2 - 12};
        double[] y = new double[]{(HEIGHT / 2 + 5 - P * 3), (HEIGHT / 2 + 4 - P * 3),
                (HEIGHT / 2 + 4.5 - P * 3)};
        StdDraw.filledPolygon(x, y);
    }

    public void displayEnding(String text) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        //HUD
        StdDraw.rectangle(WIDTH / 2, ((HEIGHT * (1 + HUD_RATIO) / HUD_RATIO))
                - HEIGHT / (2 * HUD_RATIO), (WIDTH - 0.9) / 2, HEIGHT / (2 * HUD_RATIO));
        Font font = new Font("Arial", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, (HEIGHT / 2), text);
        Font font2 = new Font("Arial", Font.BOLD, 20);
        StdDraw.setFont(font2);
        StdDraw.text(WIDTH / 2, 2, "PRESS N TO START A NEW GAME");
        StdDraw.text(WIDTH / 2, 4, "PRESS Q TO QUIT");
    }

}