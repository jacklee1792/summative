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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class Map extends JFrame {

    //Main Test
    public static void main(String[] args) {
        Map test = new Map();
        System.out.println(test.saveMap(new File("src\\-test_save2.txt")));
    }

    //Instance variables
    private MapComponent[][][] map, subMap;
    private Tile subMapTile, playerTile = new Tile(4, 7);
    private Tile selectedTile;
    private boolean isSelecting = false;
    private int mapHeight = 100, mapWidth = 100, subMapHeight = 9, subMapWidth = 16, tileSize;
    private boolean[] keys = new boolean[255];
    private MissionTextArea mta = new MissionTextArea(new File("src\\-missions_test.txt"));

    final static int GROUND_LAYER = 0;
    final static int ITEM_LAYER = 1;

    final static int NORTH = 0;
    final static int WEST = 1;
    final static int SOUTH = 2;
    final static int EAST = 3;
    final static String LINE_SEPARATOR = " !!! ";

    static Player p;

    //Constructor
    public Map() {
        //Set up the window
        File directory = new File("./");
        System.out.println(directory.getAbsolutePath());

        setTitle("Binecraft");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setVisible(true);
        adaptToScreen(); //Set tile size based on screen resolution

        //Map
        map = new MapComponent[2][mapHeight][mapWidth];

        //Generate map, player tile, subMap tile
        MapGenerator m = new MapGenerator(212169);
        m.generate(mapHeight, mapWidth);
        map = m.getMap();
        Tile spawnTile = m.getSpawnTile();

        subMapTile = new Tile(spawnTile.getRow() - playerTile.getRow(), spawnTile.getColumn() - playerTile.getColumn());
        System.out.println(subMapTile.getRow() + " " + subMapTile.getColumn());

        //subMap
        setSubMap(subMapTile);



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
        monsterTimer.schedule(monsterTick, 0, 300); //Every 300 ms
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
        } catch(IOException e) { System.out.println("Image import error!"); }

        //DrawArea
        DrawArea mapArea = new DrawArea(subMapWidth * tileSize, subMapHeight * tileSize);
        add(mapArea, BorderLayout.CENTER);

        //KeyListener
        addKeyListener(new ActionProcessor());
        addMouseMotionListener(new ActionProcessor());
        addMouseListener(new ActionProcessor());
        // addKeyListener(new AttackListener());

        //Pack
        pack();
    }

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

    public void updatePlayer() {
        if(System.currentTimeMillis() - p.getLastMovement() > 1000 / p.getMovementSpeed()) {
            if(keys['w']) {
                walk(NORTH);
            } else if (keys['a']) {
                walk(WEST);
            } else if (keys['s']) {
                walk(SOUTH);
            } else if (keys['d']) {
                walk(EAST);
            }
            p.setLastMovement(System.currentTimeMillis());
        }

        repaint();
    }

    public void updateMonster() {
        ArrayList<Tile> monsterList = new ArrayList<>();
        for(int r = 0; r < subMapHeight; r++) {
            for(int c = 0; c < subMapWidth; c++) {
                //Write locations to monster list
                if(subMap[ITEM_LAYER][r][c].getMapComponentID() == MapComponent.MONSTER) {
                    monsterList.add(new Tile(r + subMapTile.getRow(), c + subMapTile.getColumn()));
                    map[ITEM_LAYER][r + subMapTile.getRow()][c + subMapTile.getColumn()] = new MapComponent(MapComponent.NULL);
                    if (subMap[ITEM_LAYER][r][c].isDead())
                    {subMap[ITEM_LAYER][r][c] = new MapComponent(MapComponent.NULL);
                        repaint();}
                }
            }
        }
        ArrayList<Tile> newList = Monster.updateMonster(subMap, subMapTile, playerTile, monsterList);
        for(Tile t : newList) {
            map[ITEM_LAYER][t.getRow()][t.getColumn()] = new MapComponent(MapComponent.MONSTER);
        }
        setSubMap(subMapTile); //Update the subMap
        repaint();
    }

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
        } catch (ArrayIndexOutOfBoundsException ex) {}
    }

    public void adaptToScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        tileSize = (int)(screenSize.getWidth() / 16);
    }

    // Methods
    public void checkMission(){
        int currentMission = mta.getCurrentMission();

        if(currentMission == 1){

        }
        else if(currentMission == 2){

        }
        else if(currentMission == 3){

        }
    }

    //DrawArea and KeyListener and InventoryBar
    class DrawArea extends JPanel {

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

    class ActionProcessor implements KeyListener, MouseMotionListener, MouseListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            char key = e.getKeyChar();
            if(key == 61 || key == 43)
                saveMap(new File("src\\-test_save.txt"));
            try {
                keys[key] = true;
            } catch (ArrayIndexOutOfBoundsException ex) {}

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
            selectedTile = new Tile(e.getY() / tileSize, e.getX() / tileSize);
            int dX = Math.abs(selectedTile.getColumn() - playerTile.getColumn());
            int dY = Math.abs(selectedTile.getRow() - playerTile.getRow());
            if(dX <= p.getRange() && dY <= p.getRange()) {
                isSelecting = true;
            }
            else isSelecting = false;
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(isSelecting) {
                p.interact(subMap[ITEM_LAYER][selectedTile.getRow()][selectedTile.getColumn()] , selectedTile);
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
                texture = ImageIO.read(new File("./src/_HUD3.png"));
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
                invBar = ImageIO.read(new File("./src/_HUD1.png"));
            } catch (IOException ex) {}
            addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            //Draw inventory bar
            g.drawImage(invBar,0, 0, invWidth, invHeight, null);
            //Red rectangle around selected index
            g.setColor(Color.RED);
            g.drawRect(p.getSelectedIndex() * tileSize, 0, tileSize, tileSize);
            //Draw item icons
            ArrayList<Item> inventory = p.getInventory();
            for(int i = 0; i < inventory.size(); i++) {
                int x = (int)((i * invWidth / 5) + (0.1 * invWidth / 5));
                int y = 2;
                int size = (int)(0.8 * tileSize);
                g.drawImage(Item.texture[inventory.get(i).getItemID()], x, y, size, size, null);
            }
        }

        //Detect which inventory slot was selected
        @Override
        public void mousePressed(MouseEvent e) {
            int mouseX = e.getX();
            p.setSelectedIndex(mouseX / tileSize);
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

    class MissionTextArea extends JPanel {
        // Instance variables
        private ArrayList<Mission> missions;
        private int currentMission;
        private BufferedImage textBox;

        // Constants
        final int titleX = tileSize, titleY = (int)(tileSize / 3.3), textX = tileSize / 6, textY = (int)(tileSize / 2.1);
        final Font titleFont = new Font("Comic Sans MS", Font.BOLD, 20), textFont = new Font("Comic Sans MS", Font.BOLD, 16);
        final Color titleColour = Color.black, textColour = Color.black;

        // Constructor
        public MissionTextArea(File loadFile) {
            missions = new ArrayList<>();
            try {
                FileReader fr = new FileReader(loadFile);
                BufferedReader br = new BufferedReader(fr);

                String line = " ";
                while(!line.equals("")){
                    line = br.readLine();
                    missions.add(new Mission(line));
                }

            } catch(Exception ex) {System.out.println("Load file not found!");}

            try {
                textBox = ImageIO.read(new File("./src/_HUD2.png"));
            } catch (IOException ex) {System.out.println("_HUD2.png not found!");}

        }

        // Methods
        public ArrayList<Mission> getMissions() { return missions; }
        public String getCurrentTitle() { return missions.get(currentMission).getTitle(); }
        public String runCurrentMission() { return missions.get(currentMission).getText(); }
        public int getCurrentMission() { return currentMission; }
        public void setCurrentMission(int newMission) { currentMission = newMission; }
        public boolean isComplete(int missionNum) throws ArrayIndexOutOfBoundsException { return missions.get(missionNum).getCompleteness(); }
        public boolean completeCurrentMission(){
            missions.get(currentMission).setCompleteness(true);
            if(currentMission >= missions.size() - 1)               // every mission completed: win condition
                return true;
            currentMission++;
            return false;
        }

        // Graphical methods
        @Override
        public void paintComponent(Graphics g){
            g.drawImage(textBox, 0, 0, 6 * tileSize, tileSize, null);
            g.setColor(titleColour);
            g.setFont(titleFont);
            g.drawString(missions.get(currentMission).getTitle(), titleX, titleY);
            g.setColor(textColour);
            g.setFont(textFont);
            g.drawString(missions.get(currentMission).getText(), textX, textY);
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

            ArrayList<Item> items = p.getInventory();               // saving the inventory
            for(Item i : items){
                bw.write(i.getItemID() + " ");
            }
            if(items.size() == 0)
                bw.write(" ");
            bw.newLine();
            ArrayList<Mission> mission_arr = mta.getMissions();
            for(Mission i : mission_arr){
                if(i.getCompleteness())
                    bw.write("1 ");
                else
                    bw.write("0 ");
            }

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
            p.inventory.clear();
            for(String i : items_arr){
                p.inventory.add(new Item(Integer.parseInt(i)));
            }

            // Implement inventory, progress

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