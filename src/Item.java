import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Math;

class Item {

    private int I_ID;

    // UPDATE THIS WHENEVER YOU ADD A NEW ITEM
    final static int numItems = 12;

    final static int DAMAGELAYER = 0;
    final static int RANGELAYER = 1;
    final static int SELFHARMLAYER = 2;
    final static int HUNGERLAYER = 3;
    final static int USAGELAYER = 4; // 0 if it can be used infinitely, 1 if single use

    final static int[][] statsSheet = // now a nibber on the floor talking to the athletes
            {
                    {1, 2, 0, 0, 0}, // NULL
                    {5, 3, 0, 0, 1}, // STICK
                    {10, 3, 0, 0, 0}, // SWORD
                    {30, 1, 0, 0, 0}, // KNIFE
                    {3, 7, 0, 0, 0}, // SLINGSHOT
                    {20, 6, 0, 0, 0}, // BOWANDARROW
                    {20, 5, 0, 0, 1}, // ROCK
                    {0, 8, 0, 0, 0}, // STRING
                    {0, 1, 0, 0, 0}, // FEATHER
                    {0, 0, 0, 10, 1}, // APPLE
                    {0, 0, 1, 5, 0}, // BERRY
                    {0, 0, (int)(Math.random() * 3), 25, 1} // MEAT
            };

    final static int NULL = 0;
    final static int STICK = 1;
    final static int SWORD = 2;
    final static int KNIFE = 3;
    final static int SLINGSHOT = 4;
    final static int BOWANDARROW = 5;
    final static int ROCK = 6;
    final static int STRING = 7;
    final static int FEATHER = 8;

    final static int APPLE = 9;
    final static int BERRY = 10;
    final static int MEAT = 11;


    public Item(int ItemID) {
        I_ID = ItemID;
    }

    public int getItemID() {
        return I_ID;
    }

    static BufferedImage[] texture = new BufferedImage[numItems];

    //Methods
    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < numItems; i++) {
            texture[i] = ImageIO.read(new File("./images/items/_ITM" + i + ".png"));
        }
    }

    public int getDamage() {return statsSheet[I_ID][DAMAGELAYER]; }

    public int getRange() {return statsSheet[I_ID][RANGELAYER]; }

    public int getSelfHarm() {return statsSheet[I_ID][SELFHARMLAYER]; }

    public int getHunger() {return statsSheet[I_ID][HUNGERLAYER]; }

    public int getUsage() {return statsSheet[I_ID][USAGELAYER]; }
}
