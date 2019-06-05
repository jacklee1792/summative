/**
 * MapComponent class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class MapComponent {

    //Instance variables
    protected int MC_ID;
    protected int height, width;

    //Static variables
    final static int NULL = -1;
    final static int GRASS = 0;
    final static int SOIL = 1;
    final static int SAND = 2;
    final static int WATER = 3;
    final static int SMALL_TREE = 4;
    final static int SMALL_BUSH = 5;
    final static int ROCKS = 6;
    static BufferedImage[] texture = new BufferedImage[7];

    //Constructors
    public MapComponent() {
        texture[GRASS] = ImageIO.read("0.png");
    }

    public MapComponent (int MapComponentID) {
        MC_ID = MapComponentID;
    }

    //Methods
    public int getMapComponentID() {
        return MC_ID;
    }

    //Tester
    public static void main(String[] args ) {

    }
}