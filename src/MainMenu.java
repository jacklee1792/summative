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

    // Instance variables
    private Map m;
    private int aspectRatio = Map.ASPECT_16_9;
    private int movementChoice = Map.WASD;
    private char dropKey = 'q';
    private int seed;

    private BufferedImage backgroundImage;
    private JButton newGame = new JButton(playBtnName);
    private JButton loadGame = new JButton(loadBtnName);
    private JButton quit = new JButton(quitBtnName);
    private JButton randomSeed = new JButton("Random seed");
    private JTextField nameTF = new JTextField(10);
    private JTextField seedTF = new JTextField(10);
    private JPanel buttons = new JPanel();
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
        buttons.add(new JLabel("Name:"));
        buttons.add(nameTF);
        buttons.add(new JLabel("Generation seed:"));
        buttons.add(seedTF);
        buttons.add(randomSeed);
        buttons.add(newGame);
        buttons.add(loadGame);
        buttons.add(quit);

        DrawArea drawArea = new DrawArea(800, 450);
        add(drawArea, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        nameTF.setText("Name");
        seedTF.setText("Seed");

        // Adding action listeners
        newGame.addActionListener(this);
        loadGame.addActionListener(this);
        quit.addActionListener(this);
        randomSeed.addActionListener(this);

        // Window settings
        pack();
        setTitle("Survival Island");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Graphics class
    class DrawArea extends JPanel{

        public DrawArea(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        public void paintComponent(Graphics g){
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

    // Action events
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(playBtnName)){
            try {
                seed = Integer.parseInt(seedTF.getText());
            } catch (Exception ex) {
                seed = (int)(Math.random() * 1000000000);
            }
            m = new Map(aspectRatio, movementChoice, dropKey, nameTF.getText(), seed);
        }
        else if(e.getActionCommand().equals(loadBtnName)){
//            try {
//                //m = new Map(aspectRatio, movementChoice, dropKey, nameTF.getText(), new File("./src/-save_map.txt"));
//                m = new Map(new File("./src/-save_map.txt"));
//            } catch(Exception ex){
//                System.out.println("Bruh moment");
//            }
            m = new Map(new File("./src/-save_map.txt"));
        }
        else if(e.getActionCommand().equals(quitBtnName)){
            setVisible(false);
            System.exit(0);
        }
        else if(e.getActionCommand().equals("Random seed")) {
            seedTF.setText("" + (int)(Math.random() * 1000000000));
        }
    }

    // Main method
    public static void main(String[] args){
        new MainMenu();
    }

}
