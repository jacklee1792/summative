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
    private int inventoryCap = 5;
    private int selectedIndex;
    private int stackSize = 6;

    public Player() {
        name = "ppman";
        health = 6; // for number of hearts
        maxHealth = 6;
        hungerFactor = 100;
        orientation = NORTH;
        //walkCycle = 1;

        inventory = new ArrayList<>();
    }

    static BufferedImage[] texture = new BufferedImage[8];

    public BufferedImage getTexture() {
        return texture[0];
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public static void importTextures() throws IOException { //import textures
        texture = new BufferedImage[8];
        for(int i = 1; i <= 8; i++) {
            texture[i - 1] = ImageIO.read(Player.class.getResourceAsStream("_PLYR" + i + ".png"));
        }
    }

    /*
    TODO
    -Better interaction logic -> interacting with both ground layer and item layer within player
     */
    public void interact(MapComponent m) {
        if(m.getMapComponentID() == MapComponent.SMALL_TREE && inventory.size() < inventoryCap) {
            inventory.add(new Item(Item.STICK));
            System.out.println("Interaction with small tree detected");
        } else if(m.getMapComponentID() == MapComponent.SMALL_BUSH && inventory.size() < inventoryCap) {
            System.out.println("Interaction with small bush detected");
        }
        for(Item i : inventory) {
            System.out.print(i.getItemID());
        }
        System.out.println();
    }

}


