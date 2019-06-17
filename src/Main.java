import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

class Main extends JFrame implements KeyListener {
    // Main method
    public static void main(String[] args){
        Main window = new Main();
    }

    // Instance variables
    private Map map;
    private int aspectRatio = Map.ASPECT_16_9;
    private int movementChoice = Map.WASD;
    private char dropKey = 'q';
    private OptionsPage op = new OptionsPage();

    public Main(){

    }

    // Methods
    private void options(){                 // runs the options for the game

    }

    class OptionsPage extends JFrame {

    }

    // Action events
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'n' || e.getKeyChar() == 'N')      // starting a new game
            map = new Map(aspectRatio, movementChoice, dropKey);
        else if(e.getKeyChar() == 's' || e.getKeyChar() == 'S')
            options();
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
