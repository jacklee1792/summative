import java.awt.*;
import java.io.File;

class Map {
    //Instance variables
    private MapComponent [][][] map;
    private int rows, cols, squareSize;             // sizing parameters
    public int currentRow, currentCol;              // location of top left corner

    //Static variables
    final static int GROUND_LAYER = 0;
    final static int ABOVE_GROUND_LAYER = 1;

    /**  CONSTRUCTORS  **/
    public Map (File loadFile) {

    }

    /**  GRAPHICAL COMPONENTS **/
    public void paint(Graphics g) {

    }
}