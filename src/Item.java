import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.lang.Math;

class Item {

    private int I_ID;
    private int stackSize;

    // UPDATE THIS WHENEVER YOU ADD A NEW ITEM
    final static int numItems = 19;

    final static int DAMAGE_LAYER = 0;
    final static int RANGE_LAYER = 1;
    final static int SELF_HARM_LAYER = 2;
    final static int HUNGER_LAYER = 3;
    final static int USAGE_LAYER = 4; // 0 if it can be used infinitely, 1 if single use
    final static int STACK_MAX_LAYER = 5;

    final static int[][] statsSheet = // now a nibber on the floor talking to the athletes
            {
                    {1, 2, 0, 0, 0, 1}, // NULL
                    {10, 3, 0, 0, 1, 10}, // STICK
                    {1000, 8, 0, 0, 0, 1}, // SWORD
                    {34, 1, 0, 0, 0, 1}, // KNIFE
                    {18, 7, 0, 0, 0, 1}, // SLINGSHOT
                    {40, 6, 0, 0, 0, 1}, // BOW_AND_ARROW
                    {20, 5, 0, 0, 1, 10}, // ROCK
                    {0, 2, 0, 0, 0, 10}, // STRING
                    {0, 1, 0, 0, 0, 10}, // FEATHER
                    {0, 0, 0, 10, 1, 10}, // APPLE
                    {0, 1, 5, 5, 1, 10}, // BERRY
                    {0, 1, 10, 25, 1, 10}, // MEAT
                    {0, 0, 0, 15, 1, 1}, // COOKED_FRUIT
                    {0, 0, 0, 40, 1, 1}, // COOKED_MEAT
                    {0, 2, 0, 0, 1, 1}, // FIRE
                    {1, 8, 0, 0, 0, 1}, // FISHING_ROD
                    {0, 1, 5, 20, 1, 10}, // FISH
                    {0, 0, 0, 30, 1, 1}, // COOKED_FISH
                    {0, 8, 0, 1, 0, 1} // MAGIC_WAND
            };

    final static int NULL = 0;
    final static int STICK = 1;
    final static int SWORD = 2; // debug item
    final static int KNIFE = 3;
    final static int SLINGSHOT = 4;
    final static int BOWANDARROW = 5;
    final static int ROCK = 6;
    final static int STRING = 7;
    final static int FEATHER = 8;

    final static int APPLE = 9; // debug item
    final static int BERRY = 10;
    final static int MEAT = 11;
    final static int COOKED_FRUIT = 12;
    final static int COOKED_MEAT = 13;

    final static int FIRE = 14;

    final static int FISHING_ROD = 15;
    final static int FISH = 16;
    final static int COOKED_FISH = 17;

    final static int MAGIC_WAND = 18; // secret item

    // Normal constructor
    public Item(int ItemID) {
        I_ID = ItemID;
        stackSize = 1;
    }

    // Copy constructor
    public Item(Item old) {
        I_ID = old.I_ID;
        stackSize = old.stackSize;
    }

    // for GUI
    static BufferedImage[] texture = new BufferedImage[numItems];

    //Methods
    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < numItems; i++) {
            texture[i] = ImageIO.read(new File("./images/items/_ITM" + i + ".png"));
        }
    }

    public int getItemID() {
        return I_ID;
    }

    public int getDamage() {return statsSheet[I_ID][DAMAGE_LAYER]; }

    public int getRange() {return statsSheet[I_ID][RANGE_LAYER]; }

    public int getSelfHarm() {return statsSheet[I_ID][SELF_HARM_LAYER]; }

    public int getHunger() {return statsSheet[I_ID][HUNGER_LAYER]; }

    public int getUsage() {return statsSheet[I_ID][USAGE_LAYER]; }

    public int getStackSize() {
        return stackSize;
    }

    public void increaseStackSize(int size) { stackSize += size;}

    public int getStackMax() {return statsSheet[I_ID][STACK_MAX_LAYER];}
}
