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
        walkCycle = 1;
    }

    static BufferedImage[] texture = new BufferedImage[8];

    public BufferedImage getTexture() {
        return texture[walkCycle * orientation];
    }

    public static void importTextures() throws IOException { //import textures
        texture = new BufferedImage[8];
        for(int i = 1; i <= 8; i++) {
            texture[i - 1] = ImageIO.read(Player.class.getResourceAsStream("_PLYR" + i + ".png"));
        }
    }

}


