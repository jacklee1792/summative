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
        for (int i = 0; i < inventoryCap; i++)
            inventory.add(new Item(Item.NULL));
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
        System.out.println(damageDealt + "damage was dealt to you ");
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
            attackDamage = inventory.get(selectedIndex).getDamage();
            range = inventory.get(selectedIndex).getRange();
        } catch (ArrayIndexOutOfBoundsException ex) {}
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
                addHealth(-1 * (inventory.get(selectedIndex).getSelfHarm()));
                if (inventory.get(selectedIndex).getUsage() == 1)
                    dropItem();
            }
        }
        catch (IndexOutOfBoundsException jennifer) {}

        int firstNullIndex;

        if (m.getMapComponentID() == MapComponent.SMALL_TREE && inventoryNotFull()) {
            firstNullIndex = findFirstNull();
            inventory.set(firstNullIndex, new Item(Item.STICK));
            System.out.println("Interaction with small tree detected");
        } else if (m.getMapComponentID() == MapComponent.SMALL_BUSH && inventoryNotFull()) {
            firstNullIndex = findFirstNull();
            inventory.set(firstNullIndex, new Item(Item.BERRY));
            System.out.println("Interaction with small bush detected");
        } else if (m.getMapComponentID() == MapComponent.ROCKS && inventoryNotFull()) {
            firstNullIndex = findFirstNull();
            inventory.set(firstNullIndex, new Item(Item.ROCK));
            System.out.println("Interaction with rocks detected");
        } else if (m.getMapComponentID() == MapComponent.CHEST && inventoryNotFull()) {
            firstNullIndex = findFirstNull();
            inventory.set(firstNullIndex, new Item(Item.BOWANDARROW));
            System.out.println("Interaction with chest detected");
        }
//        else if (m.getMapComponentID() == MapComponent.xxx && inventoryNotFull()) {
//            inventory.add(new Item(Item.xxx));
//            System.out.println("Interaction with xxx object detected");
        // rip x
//        }

        else if (m.getMapComponentID() == MapComponent.MONSTER) {
            m.addHealth(-1 * attackDamage);
            System.out.println("You attacked that nibber for " + attackDamage);
            System.out.println("That nibber has " + m.getHealth() + " health left");

            if (inventory.get(selectedIndex).getUsage() == 1)
                dropItem();
        }

//        for (Item i : inventory) {
//            System.out.print(i.getItemID());
//        }
//        System.out.println();

        try {
            attackDamage = inventory.get(selectedIndex).getDamage();
            range = inventory.get(selectedIndex).getRange();
        } catch (ArrayIndexOutOfBoundsException ex) {}

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
        inventory.remove(selectedIndex);
        inventory.add(new Item(Item.NULL));
    }

    public int getNumEmpty() { // finds how many slots are NULL
        int numNull = 0;

        for (int i = 0; i < inventoryCap; i++) {
            if (inventory.get(i).getItemID() == 0)
                numNull++;
        }
        return numNull;
    }

    public boolean inventoryNotFull() {
        if (getNumEmpty() > 0)
            return true;
        else
            return false;
    }

    public int removeFirstNull() { // removes the first occurrence of a NULL Item from inventory
        for (int i = 0; i < inventoryCap; i++){
            if (inventory.get(i).getItemID() == 0) {
                inventory.remove(i);
                return i;
            }
        }
        return -1;
    }

    public int findFirstNull() {
        for (int i = 0; i < inventoryCap; i++){
            if (inventory.get(i).getItemID() == 0) {
                return i;
            }
        }
        return -1;
    }
}


