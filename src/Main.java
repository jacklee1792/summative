import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private MainPage mp = new MainPage();
    private OptionsPage op = new OptionsPage();

    // Constants
    final int frameWidth = 720;
    final int frameHeight = 720;

    public Main(){
        // Window settings
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Island Invasion");
        setLocationRelativeTo(null);
        setSize(frameWidth, frameHeight);

        // Adding components
        add(mp, BorderLayout.CENTER);
        add(op, BorderLayout.CENTER);
        mp.setVisible(true);
        op.setVisible(false);
    }

    // Methods
    private void options(){

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

    // Main and menu JPanels
    class OptionsPage extends JPanel implements MouseListener {
        // Instance variables

        // Mouse Events
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

    class MainPage extends JPanel implements MouseListener{
        // Instance variables
        private EnhancedButton playGame, options;
        private BufferedImage background;

        // Constructors
        public MainPage(){
            // Initializing components
            playGame = new EnhancedButton(10, 10, 50, 50, "PLAY");
            options = new EnhancedButton(50, 70, 50, 50, "OPTIONS");
            try {
                background = ImageIO.read(new File("./images/menu/_BKG0.png"));
            } catch(IOException e) { System.out.println("Background not loaded"); }

            // Adding components
            add(playGame);
            add(options);
            playGame.setBounds(playGame.getX(), playGame.getY(), playGame.getWidth(), playGame.getHeight());
            options.setBounds(options.getX(), options.getY(), options.getWidth(), options.getHeight());
        }

        // Graphical methods
        @Override
        public void paint(Graphics g){
            g.drawImage(background, 0, 0, null);

        }

        // Event Methods
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getX() >= playGame.getX() && e.getX() <= playGame.getX() + playGame.getWidth() && e.getY() >= playGame.getY() && e.getY() <= playGame.getY() + playGame.getHeight()){
                map = new Map(aspectRatio, movementChoice, dropKey);
                this.setVisible(false);
            }
            if(e.getX() >= options.getX() && e.getX() <= options.getX() + options.getWidth() && e.getY() >= options.getY() && e.getY() <= options.getY() + options.getHeight()){
                this.setVisible(false);
                op.setVisible(true);
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
}

class EnhancedButton extends JPanel{        //
    // Instance variables
    private int x, y, width, height;
    private String message;
    private boolean selected;
    private Font font = new Font("Comic Sans MS", Font.BOLD, 24);
    private BufferedImage unpressed, pressed;

    // Constructors
    public EnhancedButton(int x, int y, int width, int height, String message){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;

        // Setting up the image
        try{
            unpressed = ImageIO.read(new File("./images/menu/_BTN0.png"));
            pressed = ImageIO.read(new File("./images/menu/_BTN1.png"));
        } catch(IOException e){ System.out.println("Button images not loaded."); }
    }

    // Methods
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isSelected() { return selected; }

    // Graphics method
    @Override
    public void paint(Graphics g){
        try{
            if(selected)
                g.drawImage(pressed, 0, 0, width, height, null);
            else
                g.drawImage(unpressed, 0, 0, width, height, null);
        } catch(NullPointerException e) {}

        g.drawString(message, 0, 0);
    }
}
