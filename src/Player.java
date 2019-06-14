import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

class Player extends Entity {
    /**
     * INSTANCE VARIABLES
     **/
    private String name;
    private double hungerFactor;
    private ArrayList<Item> inventory;
    private int inventoryCap = 5;
    private int selectedIndex;
    private int stackSize = 6;
    private int walkState;
    private long lastMovement = 0;

    final static int STILL = 0;
    final static int WALK_L = 1;
    final static int WALK_R = 2;

    public Player() {
        name = "ppman";
        health = 6; // for number of hearts
        maxHealth = 6;
        hungerFactor = 100;
        orientation = NORTH;
        walkState = 0;
        movementSpeed = 3.5;

        inventory = new ArrayList<>();
    }

    static BufferedImage[] texture = new BufferedImage[3];

    public BufferedImage getTexture() {
        return texture[walkState];
    } // current texture

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
        texture = new BufferedImage[3];
        for (int i = 1; i <= 3; i++) {
            texture[i - 1] = ImageIO.read(Player.class.getResourceAsStream("_PLYR" + i + ".png"));
        }
    }

    /*
    TODO
    -Better interaction logic -> interacting with both ground layer and item layer within player
     */
    public void interact(MapComponent m) {
        if (m.getMapComponentID() == MapComponent.SMALL_TREE && inventory.size() < inventoryCap) {
            inventory.add(new Item(Item.STICK));
            System.out.println("Interaction with small tree detected");
        } else if (m.getMapComponentID() == MapComponent.SMALL_BUSH && inventory.size() < inventoryCap) {
            System.out.println("Interaction with small bush detected");
        } else if (m.getMapComponentID() == MapComponent.ROCKS && inventory.size() < inventoryCap) {
            System.out.println("Interaction with rocks detected");
        }
        for (Item i : inventory) {
            System.out.print(i.getItemID());
        }
        System.out.println();
    }

    public int getWalkState() { return walkState;}

    public void setWalkState(int state) {
        walkState = state;
    }

    public void walkAnimation() { //Walking animation
        if(walkState == STILL) walkState ++; //If you are still, move out left foot
        else if (walkState == WALK_L) walkState++; //If you have left foot out, change to right foot
        else if(walkState == WALK_R) walkState--; //If you have right foot out, change to left foot
    }

    public void setLastMovement(long time) {
        lastMovement = time;
    }

    public long getLastMovement() {
        return lastMovement;
    }

    public void dropItem(){
        inventory.remove(selectedIndex);
    }


}


