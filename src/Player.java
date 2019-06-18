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
    private boolean rad1 = false, rad2 = false, rad3 = false;       // to track radio parts
    public static int monstersKilled = 0, rabbitsKilled = 0, birdsKilled = 0;     // tracking monster killing stats

    final static int FIREDAMAGE = 10;

    final static int STILL = 0;
    final static int WALK_L = 1;
    final static int WALK_R = 2;

    public Player(String playerName) {
        name = playerName;
        health = 100; // for number of hearts
        maxHealth = 100;
        hunger = 100;
        maxHunger = 100;
        walkState = 0;
        movementSpeed = 8;
        attackDamage = 5;
        range = 2;

        inventory = new Item[inventoryCap];
//        for(int i = 0; i < inventoryCap; i++){          // initializing the inventory
//            inventory[i] = new Item(Item.NULL);
//        }
    }

    static BufferedImage[] texture = new BufferedImage[3];

    public void monsterAttack (MapComponent[][][] subMap, Tile playerTile) {
        int ptRow = playerTile.getRow(), ptColumn = playerTile.getColumn();
        int damageDealt = 0;

        for(int r = 0; r < subMap[0].length; r++) {
            for(int c = 0; c < subMap[0][0].length; c++) {
                if(subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.MONSTER || subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.BOSS_MONSTER){
                    if (Math.abs(ptRow - r) <= subMap[Map.ITEM_LAYER][r][c].getAttackRange() && Math.abs(ptColumn - c) <= subMap[Map.ITEM_LAYER][r][c].getAttackRange()) {
                        damageDealt += (subMap[Map.ITEM_LAYER][r][c].getAttackDamage());
                    }
                }

                if (subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.BOSS_MONSTER)
                    subMap[Map.ITEM_LAYER][r][c].addHealth(5); // regenerates 1 health per tick

                else if (subMap[Map.ITEM_LAYER][r][c].getMapComponentID()== MapComponent.CAMPFIRE) {
                    if (r == ptRow && c == ptColumn)
                        addHealth(-1 * FIREDAMAGE);
                }
            }
        }
        addHealth(-1 * damageDealt);
    }

    public BufferedImage getTexture() {
        return texture[walkState];
    } // current texture

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getName() { return name; }
    public void setName(String n) { name = n; }

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

    public static void importTextures() throws IOException { //import textures
        texture = new BufferedImage[3];
        for (int i = 1; i <= 3; i++) {
            texture[i - 1] = ImageIO.read(new File("./images/player/_PLYR" + i + ".png"));
        }
    }

    public boolean interact(MapComponent m, Tile t, Tile playerTile, int currentMission) {
        boolean missionComplete = checkMission(currentMission, m);          // checking mission completeness

        try {
            if (inventory[selectedIndex].getHunger() > 0) {
                if (t.getColumn() == playerTile.getColumn() && t.getRow() == playerTile.getRow()) {
                    addHunger(inventory[selectedIndex].getHunger());
                    addHealth(-1 * (inventory[selectedIndex].getSelfHarm()));
                    if (inventory[selectedIndex].getUsage() == 1)
                        dropItem();
                }
                else if (m.getMapComponentID() == MapComponent.CAMPFIRE){
                    if (inventory[selectedIndex].getItemID() == Item.BERRY)
                        inventory[selectedIndex] = new Item(Item.COOKED_FRUIT);
                    else if (inventory[selectedIndex].getItemID() == Item.MEAT)
                        inventory[selectedIndex] = new Item(Item.COOKED_MEAT);
                    else if (inventory[selectedIndex].getItemID() == Item.FISH)
                        inventory[selectedIndex] = new Item(Item.COOKED_FISH);
                }
            }
        }
        catch (NullPointerException ex) {}

        int selectedItemID = 0; // used for magic wand
        try {
            selectedItemID = inventory[selectedIndex].getItemID();
        }
        catch (NullPointerException ex) {}

        if (selectedItemID == Item.MAGIC_WAND) {
            if (m.getMapComponentID() == MapComponent.MONSTER ||
                    m.getMapComponentID() == MapComponent.RABBIT ||
                    m.getMapComponentID() == MapComponent.BIRD ||
                    m.getMapComponentID() == MapComponent.BOSS_MONSTER) {
                int magicDamage = m.getHealth() / 2;

                m.addHealth(-1 * (magicDamage)); // deals half the monster's health, rounded down
                System.out.println("That entity has " + m.getHealth() + " health left");


            } else if (m.getMapComponentID() == MapComponent.NULL ||
                    m.getMapComponentID() == MapComponent.FILLED_NULL ||
                    m.getMapComponentID() == MapComponent.GRASS ||
                    m.getMapComponentID() == MapComponent.SAND ||
                    m.getMapComponentID() == MapComponent.SOIL) {
                if (t.getRow() == playerTile.getRow() && t.getColumn() == playerTile.getColumn()) {
                    addHealth(1);
                    addHunger(-4);
                }
                else
                    m.turnCampfire();
            }
            else if (m.getMapComponentID() == MapComponent.CAMPFIRE)
                m.turnNull();

        }

        if (m.getMapComponentID() == MapComponent.MONSTER ||
                m.getMapComponentID() == MapComponent.RABBIT ||
                m.getMapComponentID() == MapComponent.BIRD ||
                m.getMapComponentID() == MapComponent.BOSS_MONSTER) {

            if(m.getHealth() <= attackDamage){
                if(m.getMapComponentID() == MapComponent.MONSTER || m.getMapComponentID() == MapComponent.BOSS_MONSTER)
                    monstersKilled++;
                else if(m.getMapComponentID() == MapComponent.RABBIT)
                    rabbitsKilled++;
                else if(m.getMapComponentID() == MapComponent.BIRD)
                    birdsKilled++;
            }
            m.addHealth(-1 * attackDamage);
            System.out.println("You attacked for " + attackDamage);
            System.out.println("That entity has " + m.getHealth() + " health left");

            try {
                if (inventory[selectedIndex].getUsage() == 1)
                    dropItem();
            }
            catch (NullPointerException ex) {}
            }

        else if(m.getMapComponentID() == MapComponent.SMALL_TREE && !m.isExpended()) {
            boolean indexFound = false;
            for (int i = 0; i < inventoryCap; i++) {
                try {
                    if (inventory[i].getItemID() == Item.STICK && !indexFound) {
                        selectedIndex = i;
                        addItem(new Item(Item.STICK));
                        m.expend();
                        indexFound = true;
                        break;
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int i = 0; i < inventoryCap; i++) {
                try {
                    if (inventory[i] == null && !indexFound) {
                        selectedIndex = i;
                        addItem(new Item(Item.STICK));
                        m.expend();
                        break;
                    }
                }
                catch (NullPointerException ex) {}
            }


        } else if(m.getMapComponentID() == MapComponent.ROCKS && !m.isExpended())    {
            boolean indexFound = false;
            for (int i = 0; i < inventoryCap; i++) {
                try {
                    if (inventory[i].getItemID() == Item.ROCK && !indexFound) {
                        selectedIndex = i;
                        addItem(new Item(Item.ROCK));
                        m.expend();
                        indexFound = true;
                        break;
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int i = 0; i < inventoryCap; i++) {
                try {
                    if (inventory[i] == null && !indexFound) {
                        selectedIndex = i;
                        addItem(new Item(Item.ROCK));
                        m.expend();
                        break;
                    }
                }
                catch (NullPointerException ex) {}
            }
            } else if(m.getMapComponentID() == MapComponent.CHEST) {
                addItem(new Item(Item.MAGIC_WAND)); // CHANGE THIS TO A KNIFE
            } else if(m.getMapComponentID() == MapComponent.SMALL_BUSH && !m.isExpended()) {

                boolean indexFound = false;
                for (int i = 0; i < inventoryCap; i++) {
                    try {
                        if (inventory[i].getItemID() == Item.BERRY && !indexFound) {
                            selectedIndex = i;
                            addItem(new Item(Item.BERRY));
                            m.expend();
                            indexFound = true;
                            break;
                        }
                    }
                    catch (NullPointerException ex) {}
                }
                for (int i = 0; i < inventoryCap; i++) {
                    try {
                        if (inventory[i] == null && !indexFound) {
                            selectedIndex = i;
                            addItem(new Item(Item.BERRY));
                            m.expend();
                            break;
                        }
                    }
                    catch (NullPointerException ex) {}
                }

            } else if (m.getMapComponentID() == MapComponent.DEAD_RABBIT) {
                for (int i = 0; i < inventoryCap; i++) {
                    if (inventory[i] == null) {
                        selectedIndex = i;
                        addItem(new Item(Item.MEAT));
                        m.turnNull();
                        break;
                    }
                }
            }

        else if(m.getMapComponentID() == MapComponent.DEAD_BIRD) {
            for (int i = 0; i < inventoryCap; i++) {
                if (inventory[i] == null) {
                    selectedIndex = i;
                    addItem(new Item(Item.FEATHER));
                    m.turnNull();
                    break;
                }
            }
        }

        else if(m.getMapComponentID() == MapComponent.STRING_GROUNDED) {
            for (int i = 0; i < inventoryCap; i++) {
                if (inventory[i] == null) {
                    selectedIndex = i;
                    addItem(new Item(Item.STRING));
                    m.turnNull();
                    break;
                }
            }
        }

        else if (m.getWalkable()) {
            try {
                if (inventory[selectedIndex].getItemID() == Item.FIRE) {
                    m.turnCampfire();
                    dropItem();
                }
            }
            catch (NullPointerException neck) {}
        }

        else if (m.getMapComponentID() == MapComponent.WATER) {
            try {
                if (inventory[selectedIndex].getItemID() == Item.FISHING_ROD) {
                    int randy = (int)(Math.random() * 100);
                    if (randy < 20) {// 20% chance

                        boolean indexFound = false;
                        for (int i = 0; i < inventoryCap; i++) {
                            try {
                                if (inventory[i].getItemID() == Item.FISH && !indexFound && inventory[i].getStackSize() < inventory[i].getStackMax()) {
                                    selectedIndex = i;
                                    addItem(new Item(Item.FISH));
                                    m.expend();
                                    indexFound = true;
                                    break;
                                }
                            }
                            catch (NullPointerException ex) {}
                        }
                        for (int i = 0; i < inventoryCap; i++) {
                            try {
                                if (inventory[i] == null && !indexFound) {
                                    selectedIndex = i;
                                    addItem(new Item(Item.FISH));
                                    m.expend();
                                    break;
                                }
                            }
                            catch (NullPointerException ex) {}
                        }
                    }
                    addHunger(-3); // reduces hunger by 4 in total
                }
            }
            catch (NullPointerException wes) {}
        }

        else if (m.getMapComponentID() == MapComponent.ANTENNA)
            rad1 = true;
        else if (m.getMapComponentID() == MapComponent.TRANSMITTER)
            rad2 = true;
        else if (m.getMapComponentID() == MapComponent.CIRCUIT_BOARD)
            rad3 = true;

        else if (m.getMapComponentID() == MapComponent.WISE_ROCK) {

            boolean hasBowAndArrow = false;
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.BOWANDARROW)
                        hasBowAndArrow = true;
                }
                catch (NullPointerException ex) {}
            }
            if (!hasBowAndArrow) //  only crafts campfire if you don't already have one
                craftBowAndArrow();

            boolean hasFishingRod = false;
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.FISHING_ROD)
                        hasFishingRod = true;
                }
                catch (NullPointerException ex) {}
            }
            if (!hasFishingRod) //  only crafts campfire if you don't already have one
                craftFishingRod();

            boolean hasSlingshot = false;
            for (int i = 0; i < inventoryCap; i++) { // checks if you have it
                try {
                    if (inventory[i].getItemID() == Item.SLINGSHOT)
                        hasSlingshot = true;
                }
                catch (NullPointerException ex) {}
            }
            if (!hasSlingshot) // only crafts if you don't have it
                craftSlingshot();

            boolean hasCampfire = false;
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.FIRE)
                        hasCampfire = true;
                }
                catch (NullPointerException ex) {}
            }
            if (!hasCampfire) //  only crafts campfire if you don't already have one
                craftCampfire();

        }

        else if (m.getMapComponentID() == MapComponent.BOSS_REMAINS) {
            for (int i = 0; i < inventoryCap; i++) {
                if (inventory[i] == null) {
                    selectedIndex = i;
                    addItem(new Item(Item.MAGIC_WAND));
                    m.turnNull();
                    break;
                }
            }
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

        return missionComplete;
    }

    private boolean checkMission(int currentMission, MapComponent m){
        if(currentMission == 0 || currentMission == 5 || currentMission == 6 || currentMission == 9 || currentMission == 14){
            return true;
        }
        else if(currentMission == 1){
            return m.getMapComponentID() == MapComponent.WISE_ROCK;
        }
        else if(currentMission == 2){
            return m.getMapComponentID() == MapComponent.PLANE;
        }
        else if(currentMission == 3){
            return m.getMapComponentID() == MapComponent.WATER;
        }
        else if(currentMission == 4){
            return m.getMapComponentID() == MapComponent.SMALL_BUSH;
        }
        else if(currentMission == 7){
            if(m.getMapComponentID() == MapComponent.WISE_ROCK){
                boolean campFireMade = craftCampfire();
                addItem(new Item(Item.FIRE));
                return campFireMade;
            }
        }
        else if(currentMission == 8){
            if(m.getMapComponentID() == MapComponent.CAMPFIRE && inventory[selectedIndex].getItemID() == Item.BERRY){
                inventory[selectedIndex] = new Item(Item.COOKED_FRUIT, inventory[selectedIndex].getStackSize());
                return true;
            }
        }
        else if(currentMission == 10){
            return m.getMapComponentID() == MapComponent.STRING_GROUNDED;
        }
        else if(currentMission == 11){
            if(m.getMapComponentID() == MapComponent.WISE_ROCK){
                boolean slingshotCrafted = craftSlingshot();
                return slingshotCrafted;
            }
        }
        else if(currentMission == 12){
            return m.getMapComponentID() == MapComponent.DEAD_RABBIT;
        }
        else if(currentMission == 13){
            if(m.getMapComponentID() == MapComponent.CAMPFIRE && inventory[selectedIndex].getItemID() == Item.MEAT){
                inventory[selectedIndex] = new Item(Item.COOKED_MEAT, inventory[selectedIndex].getStackSize());
                return true;
            }
        }
        else if(currentMission == 15){
            if(m.getMapComponentID() == MapComponent.ANTENNA){
                rad1 = true;
                return true;
            }
        }
        else if(currentMission == 16){
            if(m.getMapComponentID() == MapComponent.TRANSMITTER){
                rad2 = true;
                return true;
            }
        }
        else if(currentMission == 17){
            if(m.getMapComponentID() == MapComponent.CIRCUIT_BOARD){
                rad3 = true;
                return true;
            }
        }
        else if(currentMission == 18){
            return monstersKilled >= 5;
        }
        else if(currentMission == 19){           // winning
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e) {}
            Map.winGame();
            return true;
        }
        return false;
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

//    public void addItem(Item i) {
//        Item selectedItem;
//        int tempindex = selectedIndex;
//
//        try {
//            selectedItem = new Item(inventory[selectedIndex]);
//        } catch (NullPointerException ex) {
//            inventory[selectedIndex] = i;
//            return;
//        }
//
//        for (int k = 0; k < inventoryCap; k++) {
//            if (inventory[k].getItemID() == i.getItemID() && i.getStackSize() <= selectedItem.getStackMax() - selectedItem.getStackSize())
//                tempindex = k;
//        }
//
//        if(selectedItem.getItemID() == i.getItemID() && i.getStackSize() <= selectedItem.getStackMax() - selectedItem.getStackSize()) {
//            inventory[tempindex].increaseStackSize(i.getStackSize());
//        }
//    }



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

    public void addHealth(int toAdd) {
        health += toAdd;
        if (health < 0)
            health = 0;
        if (health > maxHealth)
            health = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void addHunger(int toAdd) {
        hunger += toAdd;
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

    private boolean craftSlingshot() {
        int numSticks = 0;
        int numString = 0;

        for (int i = 0; i < inventoryCap; i++) {
            try {
                if (inventory[i].getItemID() == Item.STICK)
                    numSticks += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.STRING)
                    numString += inventory[i].getStackSize();
            }
            catch (NullPointerException ex) {}
        }

        if (numSticks >= 2 && numString >= 1) {
            // to remove two sticks
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            // to remove one string
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STRING) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            inventory[selectedIndex] = new Item(Item.SLINGSHOT);
            return true;
        }
        return false;
    }

    private boolean craftCampfire() {
        int numSticks = 0;
        int numRocks = 0;

        for (int i = 0; i < inventoryCap; i++) {
            try {
                if (inventory[i].getItemID() == Item.STICK)
                    numSticks += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.ROCK)
                    numRocks += inventory[i].getStackSize();
            }
            catch (NullPointerException ex) {}
        }

        if (numSticks >= 10 && numRocks >= 2) {
            // to remove ten sticks

            for (int k = 0; k < 10; k++) { // removes ten of them
                for (int j = 0; j < inventoryCap; j++) {
                    try {
                        if (inventory[j].getItemID() == Item.STICK) {
                            selectedIndex = j;
                            dropItem();
                        }
                    } catch (NullPointerException ex) {
                    }
                }
            }
            // to remove two rocks
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.ROCK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.ROCK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }

            inventory[selectedIndex] = new Item(Item.FIRE);
            return true;
        }
        return false;
    }

    private boolean craftBowAndArrow() {
        int numSticks = 0;
        int numString = 0;
        int numFeathers = 0;

        for (int i = 0; i < inventoryCap; i++) {
            try {
                if (inventory[i].getItemID() == Item.STICK)
                    numSticks += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.STRING)
                    numString += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.FEATHER)
                    numFeathers += inventory[i].getStackSize();
            }
            catch (NullPointerException ex) {}
        }

        if (numSticks >= 2 && numString >= 1 && numFeathers >= 3) {
            // to remove three sticks
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            // to remove one string
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STRING) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }

            // to remove three feathers
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.FEATHER) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.FEATHER) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.FEATHER) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            inventory[selectedIndex] = new Item(Item.BOWANDARROW);
            return true;
        }
        return false;
    }

    private boolean craftFishingRod() {
        int numSticks = 0;
        int numString = 0;
        int numMeat = 0;

        for (int i = 0; i < inventoryCap; i++) {
            try {
                if (inventory[i].getItemID() == Item.STICK)
                    numSticks += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.STRING)
                    numString += inventory[i].getStackSize();
                if (inventory[i].getItemID() == Item.MEAT)
                    numMeat += inventory[i].getStackSize();
            }
            catch (NullPointerException ex) {}
        }

        if (numSticks >= 4 && numString >= 2 && numMeat >= 1) {
            // to remove four sticks
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STICK) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }

            // to remove two string
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STRING) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.STRING) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }

            // to remove one meat
            for (int j = 0; j < inventoryCap; j++) {
                try {
                    if (inventory[j].getItemID() == Item.MEAT) {
                        selectedIndex = j;
                        dropItem();
                    }
                }
                catch (NullPointerException ex) {}
            }

            inventory[selectedIndex] = new Item(Item.FISHING_ROD);
            return true;
        }
        return false;
    }

    public void dropItem(){
        try {
            inventory[selectedIndex].increaseStackSize(-1);
            if (inventory[selectedIndex].getStackSize() == 0) inventory[selectedIndex] = null;
        }
        catch(NullPointerException ex) {}
    }

}


