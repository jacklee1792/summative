import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JFrame implements ActionListener {
    // Constants
    private final String playBtnName = "Play Game", loadBtnName = "Load Game", quitBtnName = "Quit";
    private final int frameWidth = 700, frameHeight = 700;

    // Instance variables
    private Map m;
    private int aspectRatio = Map.ASPECT_16_9;
    private int movementChoice = Map.WASD;
    private char dropKey = 'q';

    private BufferedImage backgroundImage;
    private JButton newGame = new JButton(playBtnName);
    private JButton loadGame = new JButton(loadBtnName);
    private JButton quit = new JButton(quitBtnName);
    private JTextField nameTF = new JTextField(20);
    private JPanel buttons = new JPanel();
    private DrawArea backgroundImg = new DrawArea();
    private JFrame optionFrame = new JFrame();

    // Constructors
    public MainMenu(){
        // Initializing image
        try{
            backgroundImage = ImageIO.read(new File("./images/menu/_BKG0.png"));
        } catch(IOException e) {
            System.out.println("Background image not loaded.");
        }

        // Adding components
        setFont(new Font("Comic Sans MS", Font.BOLD, 18));          // doesn't do anything yet
        buttons.add(new JLabel("Name:"));
        buttons.add(nameTF);
        buttons.add(newGame);
        buttons.add(loadGame);
        buttons.add(quit);

        add(backgroundImg, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        nameTF.setText("Name");

        // Adding action listeners
        newGame.addActionListener(this);
        loadGame.addActionListener(this);
        quit.addActionListener(this);

        // Window settings
        setTitle("Survival Island");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(frameWidth, frameHeight);
    }

    // Graphics class
    class DrawArea extends JPanel{
        @Override
        public void paint(Graphics g){
            g.drawImage(backgroundImage, 0, 0,frameWidth, frameHeight - 50, null);
        }
    }

    // Action events
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(playBtnName)){
            m = new Map(aspectRatio, movementChoice, dropKey, nameTF.getText());
        }
        else if(e.getActionCommand().equals(loadBtnName)){
            try {
                m = new Map(new File(".src/-save_map.txt"));
            } catch(Exception ex){
                System.out.println("Bruh moment");
            }
        }
        else if(e.getActionCommand().equals(quitBtnName)){
            setVisible(false);
            System.exit(0);
        }
    }

    // Main method
    public static void main(String[] args){
        new MainMenu();
    }

}
