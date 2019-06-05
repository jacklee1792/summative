/**
 * MapComponent class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.*;
import java.awt.image.*;

class MapComponent {

    //Instance variables
    protected int MC_ID;
    protected int height, width;

    //Static variables
    static final int NULL = -1, GRASS = 0, SOIL = 1, SAND = 2, WATER = 3, SMALL_TREE = 4, SMALL_BUSH = 5, ROCKS = 6;
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