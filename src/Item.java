import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

class Item {

    private int I_ID;

    final static int numItems = 3;
    final static int NULL = 0;
    final static int STICK = 1;
    final static int SWORD = 2;

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
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream("_ITM" + i + ".png"));
        }
    }

}
