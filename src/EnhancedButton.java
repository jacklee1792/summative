import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class EnhancedButton extends JPanel  {
    // Instance variables
    private BufferedImage img;
    private String message;
    private Font textFont = new Font("Comic Sans MS", Font.BOLD, 24);

    // Constructors
    public EnhancedButton(File loadFile){
        try {
            img = ImageIO.read(loadFile);
        }
        catch(IOException e) { }
    }

    public EnhancedButton(String message){
        this.message = message;
    }

    // Methods
    public void setFont(Font f){
        textFont = f;
    }

    // Graphical methods
    @Override
    public void paintComponent(Graphics g){

    }
}
