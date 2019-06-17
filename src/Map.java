/**
 * Map class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

class Map extends JFrame {

    //Main Test
    public static void main(String[] args) throws NullPointerException {
        Map test = new Map();
        System.out.println(test.saveMap(new File("./src/-test_save2.txt")));
    }

    //Instance variables
    private MapComponent[][][] map, subMap;
    private int mapHeight = 100, mapWidth = 100, subMapHeight = 9, subMapWidth = 16, tileSize;
    private Tile subMapTile, playerTile = new Tile((subMapHeight - 1) / 2, (subMapWidth - 1) / 2);
    private Tile selectedTile;
    private boolean isSelecting = false;
    private boolean dead = false;
    private boolean[] keys = new boolean[255];
    private DrawArea mapArea;
    private MissionTextArea mta;

    // note that caps don't work
    private char MOVE_UP = 'w';
    private char MOVE_LEFT = 'a';
    private char MOVE_DOWN = 's';
    private char MOVE_RIGHT = 'd';

    // for final mission
    public static int totalMonsters = 0;

    final static int GROUND_LAYER = 0;
    final static int ITEM_LAYER = 1;

    final static int NORTH = 0;
    final static int WEST = 1;
    final static int SOUTH = 2;
    final static int EAST = 3;

    final static String LINE_SEPARATOR = " !!! ";

    final static int ASPECT_16_9 = 0;
    final static int ASPECT_4_3 = 1;

    final static int WASD = 0;
    final static int ARROW_KEYS = 1;
    public static char DROP_KEY = 'q';

    static Player p;

    //Constructor
    public Map() {
        //Set up the window
        File directory = new File("./");
        System.out.println(directory.getAbsolutePath());

        setTitle("Survival Island");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setVisible(true);
        adaptToScreen(); //Set tile size based on screen resolution

        // Setting up options
        setAspectRatio(ASPECT_16_9);
        setMovementKeys(WASD);

        //Map
        map = new MapComponent[2][mapHeight][mapWidth];

        //Generate map, player tile, subMap tile
        MapGenerator m = new MapGenerator(2121);
        m.generate(mapHeight, mapWidth);
        map = m.getMap();
        Tile spawnTile = m.getSpawnTile();

        subMapTile = new Tile(spawnTile.getRow() - playerTile.getRow(), spawnTile.getColumn() - playerTile.getColumn());
        System.out.println(subMapTile.getRow() + " " + subMapTile.getColumn());

        //subMap
        setSubMap(subMapTile);

        // Mission Text Area
        mta = new MissionTextArea();

        //Player
        p = new Player();
        try {
            Player.importTextures();
        } catch (IOException ex) {}

        //Monster timer
        Timer monsterTimer = new Timer();
        TimerTask monsterTick = new TimerTask(){
            @Override
            public void run() {
                updateMonster();

            }
        };
        monsterTimer.schedule(monsterTick, 0, 500); //Every 500 ms
        Timer playerTimer = new Timer();
        TimerTask playerTick = new TimerTask(){
            @Override
            public void run() {
                updatePlayer();
            }
        };
        playerTimer.schedule(playerTick, 0, 17); //~60Hz

        //Initialize textures
        try {
            MapComponent.importTextures();
            Item.importTextures();
            //Player.importTextures();
        }
        catch(IOException e) { System.out.println("Image import error!"); }

        //DrawArea
        mapArea = new DrawArea(subMapWidth * tileSize, subMapHeight * tileSize);
        add(mapArea, BorderLayout.CENTER);

        //KeyListener
        addKeyListener(new ActionProcessor());
        addMouseMotionListener(new ActionProcessor());
        addMouseListener(new ActionProcessor());
        // addKeyListener(new AttackListener());

        pack();
    }

    // Methods for options
    public void setAspectRatio(int ratio){
        if(ratio == ASPECT_16_9){
            subMapHeight = 9;
            subMapWidth = 16;
        }
        else if(ratio == ASPECT_4_3){
            subMapHeight = 12;
            subMapWidth = 16;
        }
    }

    public void setMovementKeys(int bind){
        if(bind == WASD){
            MOVE_UP = 'w';
            MOVE_LEFT = 'a';
            MOVE_DOWN = 's';
            MOVE_RIGHT = 'd';
        }
        else if(bind == ARROW_KEYS){
            MOVE_UP = 38;
            MOVE_LEFT = 37;
            MOVE_DOWN = 40;
            MOVE_RIGHT = 39;
        }
    }

    // Methods
    /*
    subMap contains the area that the player can see at the moment, for efficiency
     */
    public void setSubMap(Tile t) throws ArrayIndexOutOfBoundsException {
        MapComponent[][][] temp = new MapComponent[2][subMapHeight][subMapWidth]; //we need a temp because we don't want to change subMap if this throws an exception
        //Copy map to subMap
        for(int h = 0; h < 2; h++) {
            for(int r = 0; r < subMapHeight; r++) {
                for(int c = 0; c < subMapWidth; c++) {
                    temp[h][r][c] = map[h][t.getRow() + r][t.getColumn() + c];
                }
            }
        }
        subMap = temp; //temp is destroyed upon exit
    }

    public void setMapFromSubMap() {
        for(int l = 0; l <= Map.ITEM_LAYER; l++) {
            for(int r = 0; r < subMapHeight; r++) {
                for(int c = 0; c < subMapWidth; c++) {
                    map[l][subMapTile.getRow() + r][subMapTile.getColumn() + c] = new MapComponent(subMap[l][r][c]);
                }
            }
        }
    }

    public void updatePlayer() {
        if(System.currentTimeMillis() - p.getLastMovement() > 1000 / p.getMovementSpeed()) { // ensures that the player has a cap on movement
            if(keys[MOVE_UP]) {
                walk(NORTH);
            } else if (keys[MOVE_LEFT]) {
                walk(WEST);
            } else if (keys[MOVE_DOWN]) {
                walk(SOUTH);
            } else if (keys[MOVE_RIGHT]) {
                walk(EAST);
            } else if (keys[DROP_KEY]) {
                p.dropItem();
            }
            p.setLastMovement(System.currentTimeMillis());
        }

        repaint();
    }

    public void updateMonster() {
        MapComponent[][][] newSubMap = Monster.updateMonster(subMap, playerTile);
        setMapFromSubMap();
        p.monsterAttack(subMap, playerTile); // checks for monsters attacking the player
        repaint();
    }

    // handles collision for player
    public boolean collision(Tile t, int direction) { //for subMap
        MapComponent target1 = new MapComponent(), target2 = new MapComponent();
        if(direction == NORTH) {
            target1 = subMap[ITEM_LAYER][t.getRow() - 1][t.getColumn()];
            target2 = subMap[GROUND_LAYER][t.getRow() - 1][t.getColumn()];
        } else if(direction == WEST) {
            target1 = subMap[ITEM_LAYER][t.getRow()][t.getColumn() - 1];
            target2 = subMap[GROUND_LAYER][t.getRow()][t.getColumn() - 1];
        } else if(direction == SOUTH) {
            target1 = subMap[ITEM_LAYER][t.getRow() + 1][t.getColumn()];
            target2 = subMap[GROUND_LAYER][t.getRow() + 1][t.getColumn()];
        } else if(direction == EAST) {
            target1 = subMap[ITEM_LAYER][t.getRow()][t.getColumn() + 1];
            target2 = subMap[GROUND_LAYER][t.getRow()][t.getColumn() + 1];
        }

        return !(target1.getWalkable() && target2.getWalkable()); //if both ground and item layer are ok, then is no collision
    }

    // used to allow the player to walk
    public void walk(int direction) {
        Tile temp = new Tile(subMapTile.getRow(), subMapTile.getColumn()); //so tile is changed only is new subMap is valid

        if (direction == NORTH) {
            if(!collision(playerTile, NORTH)) {
                temp.setRow(subMapTile.getRow() - 1); //move up by one
                p.walkAnimation();
            }
        } else if (direction == WEST) {
            if(!collision(playerTile, WEST)) {
                temp.setColumn(subMapTile.getColumn() - 1); //move left by one
                p.walkAnimation();
            }
        } else if (direction == SOUTH) {
            if(!collision(playerTile, SOUTH)) {
                temp.setRow(subMapTile.getRow() + 1); //move down by one
                p.walkAnimation();
            }
        } else if (direction == EAST) {
            if (!collision(playerTile, EAST)) {
                temp.setColumn(subMapTile.getColumn() + 1); //move left by one
                p.walkAnimation();
            }
        }

        try {
            setSubMap(temp); //change the submap
            subMapTile = temp; //if line above doesn't throw exception
        } catch (ArrayIndexOutOfBoundsException ex) {} // creates invisible walls at the border to prevent errors
    }

    // attempts to fit native resolution
    public void adaptToScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        tileSize = (int)(screenSize.getWidth() / subMapWidth);
    }

    // Win and lose conditions
    public static void winGame(){               // the win condition of the game
        System.out.println("what a bro moment. ");
    }
    public static void loseGame(){
        System.out.println("you are a nibber");
    }

    // uses data from mouseListener for interactions
    public void updateSelected(int mouseX, int mouseY) {
        selectedTile = new Tile(mouseY / tileSize, mouseX / tileSize);
        int dX = Math.abs(selectedTile.getColumn() - playerTile.getColumn());
        int dY = Math.abs(selectedTile.getRow() - playerTile.getRow());
        if(dX <= p.getRange() && dY <= p.getRange()) {
            isSelecting = true;
        }
        else isSelecting = false;
        repaint();
    }

    //DrawArea and KeyListener and InventoryBar
    class DrawArea extends JPanel {

        // to draw HUD
        public DrawArea(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setLayout(null); //Required for setBounds

            int HUDY = (subMapHeight * tileSize) - ( 3 * tileSize / 2);

            //Vitals Bar
            VitalsBar vb = new VitalsBar();
            int vbX = tileSize / 2;
            vb.setBounds(vbX, HUDY, 3 * tileSize, tileSize);
            add(vb);

            //Inventory bar
            InventoryBar inv = new InventoryBar();
            int invX = 8 * tileSize / 2;
            inv.setBounds(invX, HUDY, 5 * tileSize, tileSize);
            add(inv);

            //Mission Text Area
            int mtaX = 19 * tileSize / 2;
            mta.setBounds(mtaX, HUDY, 7 * tileSize, tileSize);
            add(mta);

        }

        @Override
        public void paintComponent(Graphics g) {
            //Map
            int x = 0, y = 0;
            for(MapComponent[][] layer : subMap) {
                for(MapComponent[] row : layer) {
                    for(MapComponent item : row) {
                        int itemID = item.getMapComponentID();
                        BufferedImage itemTexture = MapComponent.texture[itemID];
                        g.drawImage(itemTexture, x, y, tileSize * (int)item.getComponentSize().getWidth(), tileSize * (int)item.getComponentSize().getHeight(), null);
                        //Health bar for entity
                        if(item.isEntity()) {
                            g.setColor(Color.RED);
                            g.fillRect(x, y - tileSize / 10, tileSize, tileSize / 10);
                            g.setColor(Color.GREEN);
                            g.fillRect(x, y - tileSize / 10, (int)(1.0 * tileSize * item.getHealth() / item.getMaxHealth()), tileSize / 10);
                        }
                        x += tileSize; //advance to next item
                    }
                    x = 0;
                    y += tileSize; //advance to next line
                }
                x = 0;
                y = 0;
            }

            //Player
            g.drawImage(p.getTexture(), playerTile.getColumn() * tileSize, playerTile.getRow() * tileSize, tileSize, tileSize, null );

            //Selected tile
            g.setColor(Color.WHITE);
            if(isSelecting) g.drawRect(selectedTile.getColumn() * tileSize, selectedTile.getRow() * tileSize, tileSize, tileSize);

        }

    }

    // combines all the listeners
    class ActionProcessor implements KeyListener, MouseMotionListener, MouseListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            char key = e.getKeyChar();
            if(key == 61 || key == 43)
                saveMap(new File("./src/-test_save.txt"));
            try {
                keys[key] = true;
            } catch (ArrayIndexOutOfBoundsException ex) {}
            if(key >= '1' && key <= '5') {
                p.updateSelectedIndex(key - '1');
                updateSelected((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
            } else if(key == DROP_KEY) {
                p.dropItem();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            try {
                keys[e.getKeyChar()] = false;
            } catch (ArrayIndexOutOfBoundsException ex) {}
            p.setWalkState(Player.STILL);
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            updateSelected(e.getX(), e.getY());
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            updateSelected(e.getX(), e.getY());
            if(isSelecting) {
                boolean missionCompletion = p.interact(subMap[ITEM_LAYER][selectedTile.getRow()][selectedTile.getColumn()] , selectedTile, playerTile, mta.getCurrentMission());
                if(missionCompletion){
                    mta.completeCurrentMission();
                }
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class VitalsBar extends JPanel {
        BufferedImage texture;

        public VitalsBar() {
            try {
                texture = ImageIO.read(new File("./images/hud/_HUD3.png"));
            } catch (IOException ex) {}
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(texture, 0, 0, 3 * tileSize, tileSize, null);
            int barX = (int)((16.5 / 32) * tileSize);
            int healthBarY = (int)((5.5 / 32) * tileSize);
            int hungerBarY = (int)((18.5 / 32) * tileSize);
            int barHeight = (int)((8.75 / 32) * tileSize);
            int healthWidth = (int)((1.0 * p.getHealth() / p.getMaxHealth()) * ((75.0 / 96) * 3 * tileSize));
            int hungerWidth = (int)((1.0 * p.getHunger() / p.getMaxHunger()) * ((75.0 / 96) * 3 * tileSize));
            g.setColor(new Color(31, 133, 36));
            g.fillRect(barX, healthBarY, healthWidth, barHeight);
            g.fillRect(barX, hungerBarY, hungerWidth, barHeight);
        }
    }

    class InventoryBar extends JPanel implements MouseListener {
        private int invHeight, invWidth;
        BufferedImage invBar;

        public InventoryBar() {
            invWidth = 5 * tileSize;
            invHeight = tileSize;
            try {
                invBar = ImageIO.read(new File("./images/hud/_HUD1.png"));
            } catch (IOException ex) {}
            addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            //Draw inventory bar
            g.drawImage(invBar,0, 0, invWidth, invHeight, null);
            //Red rectangle around selected index
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(p.getSelectedIndex() * tileSize, 0, tileSize, tileSize);
            //Draw item icons
            Item[] inventory = p.inventory;
            g.setFont(new Font("Verdana", Font.BOLD, tileSize / 5));
            g.setColor(Color.BLACK);
            for(int i = 0; i < inventory.length; i++) {
                int y = (int)(4.0 * tileSize / 32);
                int x = i * tileSize + y;
                int size = (int)(24.0 * tileSize / 32);
                try {
                    g.drawImage(Item.texture[inventory[i].getItemID()], x, y, size, size, null);
                    String text = "" + inventory[i].getStackSize();
                    int textWidth = g.getFontMetrics().stringWidth(text);
                    boolean stackable = inventory[i].getStackSize() > 1;
                    if(stackable) g.drawString(text, (int)(i * tileSize + tileSize * 0.85) - textWidth, (int)(tileSize * 0.85));
                } catch (NullPointerException ex) {g.drawImage(Item.texture[Item.NULL], x, y, size, size, null);}
            }
        }

        //Detect which inventory slot was selected
        @Override
        public void mousePressed(MouseEvent e) {
            int mouseX = e.getX();
            p.updateSelectedIndex(mouseX / tileSize);
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    // To communicate missions to the player
    class MissionTextArea extends JPanel {
        // Instance variables
        private ArrayList<String> missions;
        private int currentMission;
        private BufferedImage textBox;

        // Constants
        final int titleX = tileSize;
        final int titleY = (int)(tileSize / 3.1);
        final int textX = (int)(tileSize / 5.5);
        final int textY = (int)(tileSize / 1.7);
        final int text2Y = textY + (int)(tileSize / 4.2);

        final Font titleFont = new Font("Comic Sans MS", Font.BOLD, (int)(tileSize / 4.5));
        final Font textFont = new Font("Comic Sans MS", Font.PLAIN, (int)(tileSize / 5.1));
        final Color titleColour = Color.black;
        final Color textColour = Color.black;
        //final int[] cutoffIndices = {60, 59, 57, 60, 57, 56, 53, -1, -1, 57, 56};

        // Constructor
        public MissionTextArea() {
            missions = new ArrayList<>();
            currentMission = 0;

            // missions hardcoded in
            missions.add("INTRODUCTION !!! Hi. I am the Wise Rock. You just woke up from a plane crash killing all but you. Click on the map to continue.");
            missions.add("INTRODUCTION !!! Let me show you around. First, find me and click on me so we can go talk.");
            missions.add("MISSION 1 !!! First things first. I want you to go and search the plane for a survival kit. It is near the nose.");
            missions.add("MISSION 2 !!! Good. Now, I want you to find a water source and fill up your canteen.");
            missions.add("MISSION 3 !!! That should last you a few days. Now, you need to find some food. Wander around, see if you can find some berries.");
            missions.add("  !!! Great. You can eat them by pressing the tile of your character. Or, you can cook them.");
            missions.add("  !!! Make sure you select a new inventory slot by either using your mouse or 1 through 5 before picking something else up.");
            missions.add("MISSION 4 !!! Let's go make a campfire. Collect 2 rocks and 10 twigs and bring them back to me.");
            missions.add("MISSION 5 !!! Let's try throwing those berries into the campfire. Choose a place where you want to set up your fire.");
            missions.add("  !!! Nice. Now you have cooked berries. As you might have found out, not everything is safe to eat raw.");
            missions.add("MISSION 6 !!! You're not bad. Now, I want you to find some string. I can help you make a slingshot.");
            missions.add("MISSION 7 !!! You're almost set. How about collecting some twigs? Bring me 2 sticks piles and I'll help you craft a slingshot.");
            missions.add("MISSION 8 !!! Cool. Now let's go out and adventure. Kill a rabbit and get its meat.");
            missions.add("MISSION 9 !!! Nice work. I hope you know not to eat raw food, so head on back to the campfire to cook up that meat.");
            missions.add("  !!! I think you're getting the hang of survival. Now, let's try to get out of here");
            missions.add("MISSION 10 !!! You just need to find the three pieces of the radio. First, look for the antenna.");
            missions.add("MISSION 11 !!! Find the transmitter. You need this to call for help.");
            missions.add("MISSION 12 !!! You're almost there. I need you to find the circuit board.");
            missions.add("MISSION 13 !!! You can't call for help yet. I want to you to eliminate all the monsters. Let the final battle begin!");
            missions.add("CONCLUSION !!! Congratulations! You vanquished all the monsters. Now, the rescue crews are on their way...");

            try {
                textBox = ImageIO.read(new File("./images/hud/_HUD2.png"));
            } catch (IOException ex) {System.out.println("_HUD2.png not found!");}
        }

        // Methods
        public ArrayList<String> getMissions() { return missions; }
        public int getCurrentMission() { return currentMission; }
        public void setCurrentMission(int newMission) { currentMission = newMission; }
        public boolean completeCurrentMission(){
            if(currentMission >= missions.size() - 1)               // every mission completed: win condition
                return true;
            currentMission++;
            return false;
        }
        private int splitIndex(String str){
            int index = 52; // I CHANGED THIS TO MAKE IT NOT ERROR BUT NOW IT DOESN'T SHOW THE TEXT FOR THE RADIO MISSIONS
            while(index < str.length()){
                if(str.charAt(index) == ' ')
                    return index;
                index++;
            }
            return index;
        }

        // Graphical methods
        @Override
        public void paintComponent(Graphics g){
            g.drawImage(textBox, 0, 0, 6 * tileSize, tileSize, null);

            String[] tmp = missions.get(currentMission).split(LINE_SEPARATOR);
            int split = splitIndex(tmp[1]);
            String first = tmp[1].substring(0, split);

            g.setColor(titleColour);
            g.setFont(titleFont);
            g.drawString(tmp[0], titleX, titleY);
            g.setColor(textColour);
            g.setFont(textFont);
            g.drawString(first, textX, textY);

            try {
                String second = tmp[1].substring(split + 1);
                g.drawString(second, textX, text2Y);
            }
            catch(IndexOutOfBoundsException e) {}
        }
    }

    // IO Methods
    public boolean saveMap(File saveFile) {
        try {
            if(!saveFile.exists()){
                saveFile.createNewFile();
            }

            FileWriter fw = new FileWriter(saveFile);
            BufferedWriter bw = new BufferedWriter(fw);

            for (MapComponent[][] i : map) {                        // saving the map
                for (MapComponent[] j : i) {
                    for (MapComponent k : j) {
                        bw.write(k.getMapComponentID() + " ");
                    }
                    bw.newLine();
                    bw.flush();
                }
                bw.write(LINE_SEPARATOR + "\n");
            }

            for(Item i : p.inventory){                          // saving the inventory
                try {
                    bw.write(i.getItemID() + " ");
                }
                catch(NullPointerException e) {}
            }
            if(p.inventory.length == 0)
                bw.write(" ");
            bw.newLine();

            bw.write(mta.getCurrentMission());                      // mission progress

            bw.close();
            return true;
        }
        catch(IOException e){
            return false;
        }
        // Implement saving inventory, progress, etc.
    }

    public boolean loadMap(File loadFile){
        ArrayList<String> inputData = new ArrayList<>();
        String line = " ";
        try {
            FileReader fw = new FileReader(loadFile);
            BufferedReader br = new BufferedReader(fw);

            while(line != null && !line.equals(LINE_SEPARATOR)){     // ground layer
                inputData.add(line);
            }
            fillLayer(GROUND_LAYER, inputData);
            inputData.clear();
            while(line != null && !line.equals(LINE_SEPARATOR)){     // textures layer
                inputData.add(line);
            }
            fillLayer(ITEM_LAYER, inputData);

            line = br.readLine();                                   // inventory
            String[] items_arr = line.split(" ");
            p.inventory = new Item[5];
            /*
            for(String i : items_arr){
                p.inventory.add(new Item(Integer.parseInt(i)));
            }
             */

            line = br.readLine();
            try {
                mta.setCurrentMission(Integer.parseInt(line));
            }
            catch(Exception e){
                mta.setCurrentMission(0);
            }

            return true;
        }
        catch(IOException e){
            return false;
        }
    }

    private void fillLayer(int layer, ArrayList<String> data){      // helper method
        mapHeight = data.size();
        int row = 0, col = 0;
        for(String i : data){
            String[] data_split = i.split(" ");
            mapWidth = data_split.length;

            for(String j : data_split){
                map[layer][row][col] = new MapComponent(Integer.parseInt(j));
                col++;
            }
            row++;
        }
    }

    public void checkComponentDeath(MapComponent mc) {
        if (mc.getHealth() <= 0)
            mc = null;
        System.out.println("where the love go");
    }
}