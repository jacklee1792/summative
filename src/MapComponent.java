/**
 * MapComponent class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
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
    final static int PLANE = 8;
    final static int FILLED_NULL = 9;
    final static int WISE_ROCK = 10;

    static BufferedImage[] texture = new BufferedImage[11];

    //Constructors
    public MapComponent() {
    }

    public MapComponent (int MapComponentID) {
        MC_ID = MapComponentID;
    }

    //Methods
    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i <= 10; i++) {
            texture[i] = ImageIO.read(MapComponent.class.getResourceAsStream("_TEX" + i + ".png"));
        }
    }

    public int getMapComponentID() {
        return MC_ID;
    }

    public Dimension getComponentSize() {
        Dimension d = new Dimension(0, 0);
        if(MC_ID == NULL) { d = new Dimension(1, 1); }
        else if(MC_ID == GRASS) { d = new Dimension(1, 1); }
        else if(MC_ID == SOIL) { d = new Dimension(1, 1); }
        else if(MC_ID == SAND) { d = new Dimension(1, 1); }
        else if(MC_ID == WATER) { d = new Dimension(1, 1); }
        else if(MC_ID == SMALL_TREE) { d = new Dimension(1, 1); }
        else if(MC_ID == SMALL_BUSH) { d = new Dimension(1, 1); }
        else if(MC_ID == ROCKS) { d = new Dimension(1, 1); }
        else if(MC_ID == PLANE) { d = new Dimension(4, 2); }
        else if(MC_ID == WISE_ROCK) { d = new Dimension(1, 1); }
        return d;
    }

}