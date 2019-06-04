/**
 * Map class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import java.awt.*;
import java.io.File;

class Map {
    //Instance variables
    private MapComponent [][][] map;
    private int rows, cols, squareSize;
    public int currentRow, currentCol;

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