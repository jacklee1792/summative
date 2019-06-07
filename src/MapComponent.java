/**
 * MapComponent class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class MapComponent {

    //Instance variables
    protected int MC_ID;
    protected int height, width;

    //Static variables
    final static int NULL = 0;
    final static int GRASS = 1;
    final static int SOIL = 2;
    final static int SAND = 3;
    final static int WATER = 4;
    final static int SMALL_TREE = 5;
    final static int SMALL_BUSH = 6;
    final static int ROCKS = 7;
    final static int STICK = 8;
    final static int APPLE = 9;
    final static int BUG = 10;

    static BufferedImage[] texture = new BufferedImage[8];

    //Constructors
    public MapComponent() {
    }

    public MapComponent (int MapComponentID) {
        MC_ID = MapComponentID;
    }

    //Methods
    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < 8; i++) {
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream(i + ".png"));
        }
    }

    public int getMapComponentID() {
        return MC_ID;
    }

}