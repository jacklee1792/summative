import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class Main extends JFrame implements KeyListener {
    // Main method
    public static void main(String[] args){

    }

    // Instance variables
    private Map map;

    public Main(){

    }

    // Methods
    private void options(){                 // runs the options for the game

    }

    // Action events
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'n' || e.getKeyChar() == 'N')      // starting a new game
            map = new Map();
        else if(e.getKeyChar() == 's' || e.getKeyChar() == 'S')
            options();
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
