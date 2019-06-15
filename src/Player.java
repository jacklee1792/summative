import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.lang.Math;

class Player {
    /**
     * INSTANCE VARIABLES
     **/
    private String name;

    private int hunger, maxHunger;
    private int health, maxHealth;
    public Item[] inventory;
    private int inventoryCap = 5;
    private int selectedIndex;
    private int movementSpeed;
    private int attackDamage;
    private int range;
    private int walkState;
    private long lastMovement = 0;

    final static int STILL = 0;
    final static int WALK_L = 1;
    final static int WALK_R = 2;

    public Player() {
        name = "ppman";
        health = 100; // for number of hearts
        maxHealth = 100;
        hunger = 100;
        maxHunger = 100;
        walkState = 0;
        movementSpeed = 5;
        attackDamage = 5;
        range = 2;

        inventory = new Item[5];
    }

    static BufferedImage[] texture = new BufferedImage[3];

    public void monsterAttack (MapComponent[][][] subMap, Tile playerTile) {
        int ptRow = playerTile.getRow(), ptColumn = playerTile.getColumn();
        int damageDealt = 0;

        for(int r = 0; r < subMap[0].length; r++) {
            for(int c = 0; c < subMap[0][0].length; c++) {
                if(subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.MONSTER){
                    if (Math.abs(ptRow - r) <= subMap[Map.ITEM_LAYER][r][c].getAttackRange() && Math.abs(ptColumn - c) <= subMap[Map.ITEM_LAYER][r][c].getAttackRange()) {
                        damageDealt += (subMap[Map.ITEM_LAYER][r][c].getAttackDamage());
                    }
                }
            }
        }
        addHealth(-1 *damageDealt);
    }

    public BufferedImage getTexture() {
        return texture[walkState];
    } // current texture

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void updateSelectedIndex(int index) {
        selectedIndex = index;
        try {
            attackDamage = inventory[selectedIndex].getDamage();
            range = inventory[selectedIndex].getRange();
        } catch (NullPointerException ex) {
            attackDamage = 5;
            range = 3;
        }
    }

    public Item[] getInventory() {
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
            if (inventory[selectedIndex].getHunger() > 0) {
                addHunger(inventory[selectedIndex].getHunger());
                addHealth(-1 * (inventory[selectedIndex].getSelfHarm()));
                if (inventory[selectedIndex].getUsage() == 1)
                    dropItem();
            }
        }
        catch (NullPointerException jennifer) {}

        if (m.getMapComponentID() == MapComponent.MONSTER) {
            m.addHealth(-1 * attackDamage);
            System.out.println("You attacked that nibber for " + attackDamage);
            System.out.println("That nibber has " + m.getHealth() + " health left");

            if (inventory[selectedIndex].getUsage() == 1)
                dropItem();
        } else if(m.getMapComponentID() == MapComponent.SMALL_TREE) {
            addItem(new Item(Item.STICK));
        } else if(m.getMapComponentID() == MapComponent.ROCKS) {
            addItem(new Item(Item.ROCK));
        } else if(m.getMapComponentID() == MapComponent.CHEST) {
            addItem(new Item(Item.BOWANDARROW));
        } else if(m.getMapComponentID() == MapComponent.SMALL_BUSH) {
            addItem(new Item(Item.BERRY));
        }

        try {
            attackDamage = inventory[selectedIndex].getDamage();
            range = inventory[selectedIndex].getRange();
        } catch (NullPointerException ex) {
            attackDamage = 5;
            range = 3;
        }

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

    public void addItem(Item i) {
        Item selectedItem;
        try {
            selectedItem = new Item(inventory[selectedIndex]);
        } catch (NullPointerException ex) {
            inventory[selectedIndex] = i;
            return;
        }

        if(selectedItem.getItemID() == i.getItemID() && i.getStackSize() <= selectedItem.getStackMax() - selectedItem.getStackSize()) {
            inventory[selectedIndex].increaseStackSize(i.getStackSize());
        }
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

    public void addHealth(int toadd) {
        health += toadd;
        if (health < 0)
            health = 0;
        if (health > maxHealth)
            health = maxHealth;
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
            addHealth(-1);
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public int getRange() {
        return range;
    }


    public void dropItem(){
        inventory[selectedIndex].increaseStackSize(-1);
        if(inventory[selectedIndex].getStackSize() == 0) inventory[selectedIndex] = null;
    }

}


