import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Item {

    private int I_ID;

    // UPDATE THIS WHENEVER YOU ADD A NEW ITEM
    final static int numItems = 3;

    final static int NULL = 0;
    final static int STICK = 1;
    final static int SWORD = 2;
    final static int KNIFE = 3;
    final static int SLINGSHOT = 4;

    final static int APPLE = 5;
    final static int BERRY = 6;
    final static int MEAT = 7;

    // DAMAGE OF EACH ITEM
    final static int NULL_DAMAGE = 0;
    final static int STICK_DAMAGE = 1;
    final static int SWORD_DAMAGE = 4;
    final static int KNIFE_DAMAGE = 10;
    final static int SLINGSHOT_DAMAGE = 3;

    final static int APPLE_DAMAGE = 0;
    final static int BERRY_DAMAGE = 0;
    final static int MEAT_DAMAGE = 0;

    // RANGE OF EACH ITEM
    final static int NULL_RANGE = 2;
    final static int STICK_RANGE = 3;
    final static int SWORD_RANGE = 3;
    final static int KNIFE_RANGE = 1;
    final static int SLINGSHOT_RANGE = 7;

    final static int APPLE_RANGE = 1;
    final static int BERRY_RANGE = 1;
    final static int MEAT_RANGE = 1;

    private int damage;
    private int range;

    public Item(int ItemID) {
        I_ID = ItemID;
        if (I_ID == NULL) {
            damage = NULL_DAMAGE;
            range = NULL_RANGE;
        } else if (I_ID == STICK) {
            damage = STICK_DAMAGE;
            range = STICK_RANGE;
        } else if (I_ID == SWORD) {
            damage = SWORD_DAMAGE;
            range = SWORD_RANGE;
        } else if (I_ID == KNIFE) {
            damage = KNIFE_DAMAGE;
            range = KNIFE_RANGE;
        } else if (I_ID == SLINGSHOT){
            damage = SLINGSHOT_DAMAGE;
            range = SLINGSHOT_RANGE;
        } else if (I_ID == APPLE){
            damage = APPLE_DAMAGE;
            range = APPLE_RANGE;
        } else if (I_ID == BERRY){
            damage = BERRY_DAMAGE;
            range = BERRY_RANGE;
        } else if (I_ID == MEAT){
            damage = MEAT_DAMAGE;
            range = MEAT_RANGE;
        }
    }

    public int getItemID() {
        return I_ID;
    }

    static BufferedImage[] texture = new BufferedImage[numItems];

    //Methods
    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < numItems; i++) {
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream("_ITM" + i + ".png"));
        }
    }

    public int getDamage() {return damage; }

    public int getRange() {return range; }
}
