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

class World extends JFrame implements MouseListener, KeyListener {

    private Map map, subMap;
    private Mission[] missions;
    private Player player;
    private int tileSize = 30;

    public World() {
        //Set up the window
        setTitle("Binecraft");
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //Graphics
    @Override
    public void paint(Graphics g) { //I am still figuring this out, leave it alone for now
        int x = 0, y = 0;
        for(int h = Map.GROUND_LAYER; h <= Map.ABOVE_GROUND_LAYER; h++) {
            for(int y = 0; y < subMap.getRows(); r++) {
                for(MapComponent mc : row) {
                    g.drawImage(MapComponent.texture[mc.getMapComponentID()], x, y, tileSize, tileSize, null);
                    x += tileSize; //shift target x over by one tile
                }
                x = 0; //reset the target x value
                y += tileSize; //shift target y over by one tile
            }
            x = 0;
            y = 0;
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