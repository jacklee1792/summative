/**
 * World class
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Map extends JFrame {

    //Main Test
    public static void main(String[] args) {
        new Map();
    }

    //Instance variables
    private MapComponent[][][] map, subMap;
    private Tile subMapTile = new Tile(50, 50), playerTile = new Tile(4, 7);
    private int mapHeight = 100, mapWidth = 100, subMapHeight = 9, subMapWidth = 16, tileSize = 120;
    boolean fullScreen = true;

    final static int GROUND_LAYER = 0;
    final static int ITEM_LAYER = 1;

    final static int NORTH = 0;
    final static int WEST = 1;
    final static int SOUTH = 2;
    final static int EAST = 3;

    static Player p;

    //Constructor
    public Map() {
        //Set up the window
        setTitle("Binecraft");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setVisible(true);

        //Map
        map = new MapComponent[2][mapHeight][mapWidth];

        //Generate map
        MapGenerator m = new MapGenerator(2121);
        map = m.generate(mapHeight, mapWidth);

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

    public boolean checkWater(Tile t, int direction) { //also for subMap
        MapComponent target = new MapComponent(); //need to initialize
        if(direction == NORTH) {
            target = subMap[GROUND_LAYER][t.getRow() - 1][t.getColumn()];
        } else if(direction == WEST) {
            target = subMap[GROUND_LAYER][t.getRow()][t.getColumn() - 1];
        } else if(direction == SOUTH) {
            target = subMap[GROUND_LAYER][t.getRow() + 1][t.getColumn()];
        } else if(direction == EAST) {
            target = subMap[GROUND_LAYER][t.getRow()][t.getColumn() + 1];
        }
        boolean water = target.getMapComponentID() == MapComponent.WATER;
        return water; //return whether or not the cell is water
    }

    public boolean checkCollision(Tile t, int direction) { //for subMap
        MapComponent target = new MapComponent(); //need to initialize
        if(direction == NORTH) {
            target = subMap[ITEM_LAYER][t.getRow() - 1][t.getColumn()];
        } else if(direction == WEST) {
            target = subMap[ITEM_LAYER][t.getRow()][t.getColumn() - 1];
        } else if(direction == SOUTH) {
            target = subMap[ITEM_LAYER][t.getRow() + 1][t.getColumn()];
        } else if(direction == EAST) {
            target = subMap[ITEM_LAYER][t.getRow()][t.getColumn() + 1];
        }
        boolean isItem = ("" + target.getClass()).equals("class Item");

        return (target.getMapComponentID() != MapComponent.NULL && !isItem); //if not null, and not item, collision is true
    }

    //DrawArea and KeyListener and InventoryBar
    class DrawArea extends JPanel {

        public DrawArea(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setLayout(null);
            //Inventory bar
            InventoryBar inv = new InventoryBar();
            int invX = tileSize / 2;
            int invY = subMapHeight * tileSize - (3 * invX);
            inv.setBounds(invX, invY, 5 * tileSize, tileSize);
            add(inv);
        }

        @Override
        public void paintComponent(Graphics g) {
            //Map
            int x = 0, y = 0;
            for(MapComponent[][] layer : subMap) {
                for(MapComponent[] row : layer) {
                    for(MapComponent item : row) {
                        BufferedImage itemTexture = MapComponent.texture[item.getMapComponentID()];
                        g.drawImage(itemTexture, x, y, tileSize, tileSize, null);
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
            Tile temp = new Tile(subMapTile.getRow(), subMapTile.getColumn()); //so tile is changed only is new subMap is valid
            if (key == 'w') {
                p.setOrientation(NORTH);
                if(!checkCollision(playerTile, NORTH) && !checkWater(playerTile, NORTH)) {
                    temp.setRow(subMapTile.getRow() - 1); //move up by one
                }
            } else if (key == 'a') {
                p.setOrientation(WEST);
                if(!checkCollision(playerTile, WEST) && !checkWater(playerTile, WEST)) {
                    temp.setColumn(subMapTile.getColumn() - 1); //move left by one
                }
            } else if (key == 's') {
                p.setOrientation(SOUTH);
                if(!checkCollision(playerTile, SOUTH) && !checkWater(playerTile, SOUTH)) {
                    temp.setRow(subMapTile.getRow() + 1); //move down by one
                }
            } else if (key == 'd') {
                p.setOrientation(EAST);
                if(!checkCollision(playerTile, EAST) && !checkWater(playerTile, EAST)) {
                    temp.setColumn(subMapTile.getColumn() + 1); //move left by one
                }
            } else if (key == 'f') {
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
            try {
                setSubMap(temp); //change the submap
                subMapTile = temp; //if line above doesn't throw exception
            } catch (ArrayIndexOutOfBoundsException ex) {}
            repaint();
        }
        @Override
        public void keyReleased(KeyEvent e) {
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
}