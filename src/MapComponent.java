import java.awt.image.BufferedImage;

class MapComponent {

    //Instance variables
    protected BufferedImage image;
    protected int row, col;
    protected int MapComponentID;
    protected int height, width;

    //Static variables
    final static int GRASS = 0;
    final static int SOIL = 1;
    final static int SAND = 2;
    final static int WATER = 3;
    final static int SMALL_TREE = 4;
    final static int SMALL_BUSH = 5;
    final static int ROCKS = 6;

    //Constructors

    public MapComponent() {

    }

    public MapComponent (int MC_ID, int r, int c) {
        MapComponentID = MC_ID;
    }

    //Tester
    public static void main(String[] args ) {

    }
}