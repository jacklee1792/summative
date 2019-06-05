/**
 * Map class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import java.awt.*;
import java.io.File;

class Map {
    //Instance variables
    private MapComponent[][][] map;
    private int squareSize;
    public int currentRow, currentCol;

    //Static variables
    final static int GROUND_LAYER = 0;
    final static int ABOVE_GROUND_LAYER = 1;

    //Constructors
    public Map (int rows, int columns) {
        map = new MapComponent[rows][columns][2];
    }
    public Map(Map m, Tile t, int height, int width) {
        MapComponent[][][] subMap = new Map;
        for(int h = Map.GROUND_LAYER; h <= Map.ABOVE_GROUND_LAYER; h++) {
            for(int r = 0; r < height; r++) {
                for(int c = 0; c < width; c++) {
                    subMap[r][c][h] = map[t.getRow() + r][t.getColumn() + c][h];
                }
            }
        }
        return subMap
    }
    public Map (File loadFile) {
    }

    /**  GRAPHICAL COMPONENTS **/
    public void paint(Graphics g) {

    }
}