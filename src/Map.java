/**
 * Map class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class Map extends JFrame {

    //Main Test
    public static void main(String[] args) {
        Map test = new Map();
        test.saveMap(new File("-test_save.txt"));
    }

    //Instance variables
    private MapComponent[][][] map, subMap;
    private Tile subMapTile, playerTile = new Tile(4, 7);
    private int mapHeight = 100, mapWidth = 100, subMapHeight = 9, subMapWidth = 16, tileSize;

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
        addKeyListener(new MovementListener());
        // addKeyListener(new AttackListener());

        //Pack
        pack();
    }

    public void setSubMap(Tile t) throws ArrayIndexOutOfBoundsException {
        MapComponent[][][] temp = new MapComponent[2][subMapHeight][subMapWidth]; //we need a temp because we don't want to change subMap if this throws an exception
        for(int h = 0; h < 2; h++) {
            for(int r = 0; r < subMapHeight; r++) {
                for(int c = 0; c < subMapWidth; c++) {
                    temp[h][r][c] = map[h][t.getRow() + r][t.getColumn() + c];
                }
            }
        }
        subMap = temp; //temp is destroyed upon exit
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

        System.out.print(target1.getWalkable() + " ");
        System.out.println(target2.getWalkable());

        return !(target1.getWalkable() && target2.getWalkable()); //if both ground and item layer are ok, then is no collision
    }

    public void walk(int direction) {
        p.setOrientation(direction);
        Tile temp = new Tile(subMapTile.getRow(), subMapTile.getColumn()); //so tile is changed only is new subMap is valid

        if (direction == NORTH) {
            p.setOrientation(NORTH);
            if(!collision(playerTile, NORTH)) {
                temp.setRow(subMapTile.getRow() - 1); //move up by one
                p.walkAnimation();
            }
        } else if (direction == WEST) {
            p.setOrientation(WEST);
            if(!collision(playerTile, WEST)) {
                temp.setColumn(subMapTile.getColumn() - 1); //move left by one
                p.walkAnimation();
            }
        } else if (direction == SOUTH) {
            p.setOrientation(SOUTH);
            if(!collision(playerTile, SOUTH)) {
                temp.setRow(subMapTile.getRow() + 1); //move down by one
                p.walkAnimation();
            }
        } else if (direction == EAST) {
            p.setOrientation(EAST);
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

    //DrawArea and KeyListener and InventoryBar
    class DrawArea extends JPanel {

        public DrawArea(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setLayout(null); //Required for setBounds

            //Inventory bar
            InventoryBar inv = new InventoryBar();
            int invX = tileSize / 2;
            int invY = subMapHeight * tileSize - (3 * invX);
            inv.setBounds(invX, invY, 5 * tileSize, tileSize);
            add(inv);

            //Mission Text Area
            MissionTextArea mta = new MissionTextArea(new File("-missions_test.txt"));
            int mtaX = 15 * tileSize / 2;
            int mtaY = subMapHeight * tileSize - (3 * invX);
            mta.setBounds(mtaX, mtaY, 8 * tileSize, tileSize);
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

        }

    }

    /*
    class AttackListener implements KeyListener{
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed (KeyEvent e){
            char key = e.getKeyChar;
            Tile temp = new Tile(sumMapTile.getRow(), subMapTile.getColumn());

            if (key == 'j'){
                if(p.getOrientation() == NORTH){
                    for (int r = subMapTile.getRow() - 1; r >= subMapTile.getRow() - p.getAttackRange; r--){
                        // make the tile red or smth
                        try {
                            temp.setRow(r);
                            Tile temp = new Tile(subMapTile.getRow(), subMapTile.getColumn());
                            setSubMap(temp); //change the submap
                            subMapTile = temp; //if line above doesn't throw exception
                        } catch (ArrayIndexOutOfBoundsException ex) {}
                        repaint();
                        Thread.sleep(50);
                        // check if there is a thing on the squares, which requires accessing entities through map
                    }
                }
                if(p.getOrientation() == WEST){
                    // copy paste
                }
                if(p.getOrientation() == SOUTH){

                }
                if(p.getOrientation() == EAST){

                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
    */

    class MovementListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            char key = e.getKeyChar();

            if(System.currentTimeMillis() - p.getLastMovement() > 1000 / p.getMovementSpeed()) {
                if(key == 'w') {
                    walk(NORTH);
                } else if (key == 'a') {
                    walk(WEST);
                } else if (key == 's') {
                    walk(SOUTH);
                } else if (key == 'd') {
                    walk(EAST);
                }
                p.setLastMovement(System.currentTimeMillis());
            }

            if (key == 'f') {
                if(p.getOrientation() == NORTH) {
                    p.interact(subMap[Map.ITEM_LAYER][playerTile.getRow() - 1][playerTile.getColumn()]);
                } else if(p.getOrientation() == WEST) {
                    p.interact(subMap[Map.ITEM_LAYER][playerTile.getRow()][playerTile.getColumn() - 1]);
                } else if(p.getOrientation() == SOUTH) {
                    p.interact(subMap[Map.ITEM_LAYER][playerTile.getRow() + 1][playerTile.getColumn()]);
                } else if(p.getOrientation() == EAST) {
                    p.interact(subMap[Map.ITEM_LAYER][playerTile.getRow()][playerTile.getColumn() + 1]);
                }
            }

            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            p.setWalkState(Player.STILL);
            repaint();
        }
    }

    class InventoryBar extends JPanel implements MouseListener {
        private int invHeight, invWidth;
        BufferedImage invBar;

        public InventoryBar() {
            invWidth = 5 * tileSize;
            invHeight = 1 * tileSize;
            try {
                invBar = ImageIO.read(MapComponent.class.getResourceAsStream("_HUD1.png"));
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

        // Constructor
        public MissionTextArea(File loadFile) {
            try {
                FileReader fr = new FileReader(loadFile);
                BufferedReader br = new BufferedReader(fr);

                String line = " ";

                while(line != null){
                    if(line == null)
                        break;
                    line = br.readLine();
                    missions.add(new Mission(line));
                }

            } catch(IOException ex) {System.out.println("Load file not found!");}

            try {
                textBox = ImageIO.read(Map.class.getResourceAsStream("_HUD2.png"));            // make texture for this
            } catch (IOException ex) {System.out.println("_HUD2.png not found!");}

        }

        // Methods
        public String getCurrentTitle() { return missions.get(currentMission).getTitle(); }
        public String runCurrentMission() { return missions.get(currentMission).getText(); }
        public int getCurrentMission() { return currentMission; }
        public void setCurrentMission(int newMission) { currentMission = newMission; }
        public boolean isComplete(int missionNum) throws ArrayIndexOutOfBoundsException { return missions.get(missionNum).complete(); }
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
            g.setColor(Color.RED);
            g.fillRect(0, 0, 8 * tileSize, tileSize);
            g.drawImage(textBox, 0, 0, 8 * tileSize, tileSize, null);
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

            for (MapComponent[][] i : map) {
                for (MapComponent[] j : i) {
                    for (MapComponent k : j) {
                        bw.write(k.getMapComponentID() + " ");
                    }
                    bw.newLine();
                    bw.flush();
                }
                bw.write(LINE_SEPARATOR);
                bw.newLine();
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
            inputData.clear();

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
}