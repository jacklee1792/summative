import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

class Player extends Entity {
    /**  INSTANCE VARIABLES  **/
    private String name;
    private double hungerFactor;
    private ArrayList<Item> inventory;
    private ArrayList<Integer> inventoryQtys;
    private int stackSize = 6;

    public Player() {
        name = "ppman";
        health = 3; // for number of hearts
        maxHealth = 3;
        hungerFactor = 100;
        orientation = NORTH;
        walkCycle = true;
    }

    static BufferedImage[] texture = new BufferedImage[8];

    public static void importTextures() throws IOException { //import textures
        texture = new BufferedImage[8];
        for(int i = 0; i < 8; i++) {
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream("_PLYR" + i + ".png"));
        }
    }

}


