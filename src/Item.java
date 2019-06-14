import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Item {

    private int I_ID;

    final static int numItems = 3;
    final static int NULL = 0;
    final static int STICK = 1;
    final static int SWORD = 2;

    final static int NULL_DAMAGE = 0;
    final static int STICK_DAMAGE = 1;
    final static int SWORD_DAMAGE = 4;

    private int damage;

    public Item(int ItemID) {
        I_ID = ItemID;
        if (I_ID == NULL)
            damage = NULL_DAMAGE;
        else if (I_ID == STICK)
            damage = STICK_DAMAGE;
        else if (I_ID == SWORD)
            damage = SWORD_DAMAGE;
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

}
