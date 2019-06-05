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

    private Map map;
    private Mission[] missions;
    private Player player;

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

    /**  GRAPHICAL COMPONENTS **/
    public void paint(Graphics g) {

    }
}