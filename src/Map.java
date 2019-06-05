/**
 * World class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Random;

class Map extends JFrame {

    //Main Test
    public static void main(String[] args) {
        new Map();
    }

    private MapComponent[][][] map;
    private boolean[][] collisionMap;
    private MapComponent[][][] subMap;
    private boolean[][] collisionSubMap;
    private Tile subMapTile = new Tile(50, 50);
    private Tile playerTile = new Tile(10, 10);
    private int mapHeight = 100, mapWidth = 100, subMapHeight = 21, subMapWidth = 21;
    private int tileSize = 40;

    final static int NORTH = 0;
    final static int WEST = 1;
    final static int SOUTH = 2;
    final static int EAST = 3;

    class DrawArea extends JPanel {
        public DrawArea(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        public void paintComponent(Graphics g) {
            int x = 0, y = 0;
            for(MapComponent[][] layer : subMap) {
                for(MapComponent[] row : layer) {
                    for(MapComponent item : row) {
                        g.drawImage(MapComponent.texture[item.getMapComponentID()], x, y, tileSize, tileSize, null);
                        x += tileSize; //advance to next item
                    }
                    x = 0;
                    y += tileSize; //advance to next line
                }
                x = 0;
                y = 0;
            }

            g.setColor(Color.RED);
            g.fillRect(playerTile.getColumn() * tileSize, playerTile.getColumn() * tileSize, tileSize, tileSize);
        }
    }

    class MovementListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            char key = e.getKeyChar();
            Tile temp = new Tile(subMapTile.getRow(), subMapTile.getColumn()); //so tile is changed only is new subMap is valid
            if (key == 'w') {
                if(!checkCollision(playerTile, NORTH)) temp.setRow(subMapTile.getRow() - 1); //move up by one
            } else if (key == 'a') {
                if(!checkCollision(playerTile, WEST)) temp.setColumn(subMapTile.getColumn() - 1); //move left by one
            } else if (key == 's') {
                if(!checkCollision(playerTile, SOUTH)) temp.setRow(subMapTile.getRow() + 1); //move down by one
            } else if (key == 'd') {
                if(!checkCollision(playerTile, EAST)) temp.setColumn(subMapTile.getColumn() + 1); //move left by one
            }
            try {
                setSubMap(temp); //change the submap
                setCollisionSubMap(temp); //change the collsion submap
                subMapTile = temp; //if line above doesn't throw exception
            } catch (ArrayIndexOutOfBoundsException ex) {}
            repaint();
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public Map() {
        //Set up the window
        setTitle("Binecraft");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Map and collision map
        map = new MapComponent[2][mapHeight][mapWidth];
        collisionMap = new boolean[mapHeight][mapWidth];

        //Fill the map with random shitto
        Random rand = new Random();
        for(int h = 0; h < 2; h++) {
            for(int r = 0; r < mapHeight; r++) {
                for(int c = 0; c < mapWidth; c++) {
                    if(h == 0) map[h][r][c] = new MapComponent(rand.nextInt(4) + 1);
                    else if(h == 1) {
                        double randDouble = Math.random();
                        if(randDouble < 0.15) {
                            map[h][r][c] = new MapComponent(rand.nextInt(3) + 5); //15% of spawning an item
                            collisionMap[r][c] = true;
                        }
                        else {
                            map[h][r][c] = new MapComponent(0);
                            collisionMap[r][c] = false;
                        }
                    }
                }
            }
        }

        //Testing for subMap
        setSubMap(subMapTile);

        //Set collision map
        setCollisionSubMap(subMapTile);

        //Initialize textures
        try {
            MapComponent.importTextures();
        } catch(IOException e) { System.out.println("Image import error!"); }

        //DrawArea
        DrawArea mapArea = new DrawArea(subMapWidth * tileSize, subMapHeight * tileSize);
        add(mapArea);

        //KeyListener
        addKeyListener(new MovementListener());

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

    public void setCollisionSubMap(Tile t) throws ArrayIndexOutOfBoundsException {
        boolean[][] temp = new boolean[subMapHeight][subMapWidth];
        for(int r = 0; r < subMapHeight; r++) {
            for(int c = 0; c < subMapWidth; c++) {
                temp[r][c] = collisionMap[t.getRow() + r][t.getColumn() + c];
            }
        }
        collisionSubMap = temp; //temp is destroyed upon exit
    }

    public boolean checkCollision(Tile t, int direction) { //for submap
        boolean result = false;
        if(direction == NORTH) {
            if(collisionSubMap[t.getRow() - 1][t.getColumn()]) return true;
        } else if(direction == WEST) {
            if(collisionSubMap[t.getRow()][t.getColumn() - 1]) return true;
        } else if(direction == SOUTH) {
            if(collisionSubMap[t.getRow() + 1][t.getColumn()]) return true;
        } else if(direction == EAST) {
            if(collisionSubMap[t.getRow()][t.getColumn() + 1]) return true;
        }
        System.out.println(result);
        return result;
    }

}