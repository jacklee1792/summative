import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

class Player extends Entity {
    /**  INSTANCE VARIABLES  **/
    private String name;
    private double hungerFactor;
    private boolean walkCycle; //flip-flop cycle
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

    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < 8; i++) {
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream("PLYR" + i + ".png"));
        }
    }

    public void addItem(Item targetItem) {

    }



}


