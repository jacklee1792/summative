import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

class Player {
    /**
     * INSTANCE VARIABLES
     **/
    private String name;

    private int hunger, maxHunger;
    private int health, maxHealth;
    public ArrayList<Item> inventory;
    private int inventoryCap = 5;
    private int selectedIndex;
    private int movementSpeed;
    private int attackDamage;
    private int range;
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
        hunger = 99;
        maxHunger = 100;
        walkState = 0;
        movementSpeed = 5;
        attackDamage = 5;
        range = 2;

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
            texture[i - 1] = ImageIO.read(new File("./images/player/_PLYR" + i + ".png"));
        }
    }

    public void interact(MapComponent m, Tile t) {

        try {
            if (inventory.get(selectedIndex).getHunger() > 0) {
                addHunger(inventory.get(selectedIndex).getHunger());
                health -= inventory.get(selectedIndex).getSelfHarm();
                if (inventory.get(selectedIndex).getUsage() == 1)
                    dropItem();
            }
        }
        catch (IndexOutOfBoundsException jennifer) {}

        if (m.getMapComponentID() == MapComponent.SMALL_TREE && inventory.size() < inventoryCap) {

            inventory.add(new Item(Item.STICK));
            System.out.println("Interaction with small tree detected");
        } else if (m.getMapComponentID() == MapComponent.SMALL_BUSH && inventory.size() < inventoryCap) {
            inventory.add(new Item(Item.BERRY));
            System.out.println("Interaction with small bush detected");
        } else if (m.getMapComponentID() == MapComponent.ROCKS && inventory.size() < inventoryCap) {
            inventory.add(new Item(Item.ROCK));
            System.out.println("Interaction with rocks detected");
        } else if (m.getMapComponentID() == MapComponent.CHEST && inventory.size() < inventoryCap) {
            inventory.add(new Item(Item.BOWANDARROW));
            System.out.println("Interaction with chest detected");
        }
//        else if (m.getMapComponentID() == MapComponent.xxx && inventory.size() < inventoryCap) {
//            inventory.add(new Item(Item.xxx));
//            System.out.println("Interaction with xxx object detected");
        // rip x
//        }

        else if (m.getMapComponentID() == MapComponent.MONSTER) {
            m.addHealth(-1 * attackDamage);
            System.out.println("You attacked that nibber for " + attackDamage);
            System.out.println("That nibber has " + m.getHealth() + " health left");
        }

//        for (Item i : inventory) {
//            System.out.print(i.getItemID());
//        }
//        System.out.println();
        try {
            attackDamage = inventory.get(selectedIndex).getDamage();
            range = inventory.get(selectedIndex).getRange();
        }
        catch (IndexOutOfBoundsException how) {}

        addHunger(-1);
        checkHunger();
    }
    public int interact(MapComponent m, int currentMission) {
        return checkMission(m, currentMission);
    }
    private int checkMission(MapComponent m, int missionNumber){
        /* TODO
            Implement this
         */
        return 0;
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

    public int getHunger() {
        return hunger;
    }

    public int getMaxHunger() {
        return maxHunger;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void addHunger(int toadd) {
        hunger += toadd;
        if (hunger > maxHunger)
            hunger = maxHunger;
        if (hunger < 0)
            hunger = 0;
    }

    public void checkHunger(){
        if (hunger <= 0)
            health -= 1;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public int getRange() {
        return range;
    }


    public void dropItem(){
        inventory.remove(selectedIndex);
    }


}


