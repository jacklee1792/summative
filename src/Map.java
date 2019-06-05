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

class Map extends JFrame implements MouseListener, KeyListener {

    private MapComponent[][][] map;
    private MapComponent[][][] subMap;
    private int tileSize = 30;

    class DrawArea extends JPanel {
        public DrawArea(int height, int width) {
            setPreferredSize(new Dimension(height, width));
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
        }
    }

    //Test
    public static void main(String[] args) {
        new Map();
    }

    public Map() {
        //Set up the window
        setTitle("Binecraft");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Fill the map with random shitto
        Random rand = new Random();
        map = new MapComponent[2][100][100];
        for(int h = 0; h < 2; h++) {
            for(int r = 0; r < 100; r++) {
                for(int c = 0; c < 100; c++) {
                    if(h == 0) map[h][r][c] = new MapComponent(rand.nextInt(4));
                    else if(h == 1) map[h][r][c] = new MapComponent(rand.nextInt(3) + 4);
                }
            }
        }

        //Testing for subMap
        getSubMap(0, 0, 20, 20);

        //Initialize textures
        try {
            MapComponent.importTextures();
        } catch(IOException e) { System.out.println("Image import error!"); }

        //DrawArea
        DrawArea mapArea = new DrawArea(600, 600);
        add(mapArea);

        //Pack
        pack();
    }

    public void getSubMap(int row, int column, int height, int width) {
        subMap = new MapComponent[2][height][width];
        for(int h = 0; h < 2; h++) {
            for(int r = 0; r < height; r++) {
                for(int c = 0; c < height; c++) {
                    subMap[h][r][c] = map[h][row + r][column + c];
                }
            }
        }

    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
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