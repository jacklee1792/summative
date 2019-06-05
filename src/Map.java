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
    private int r, c;

    //Static variables
    final static int GROUND_LAYER = 0;
    final static int ABOVE_GROUND_LAYER = 1;

    //Constructors
    public Map(int rows, int columns) {
        map = new MapComponent[rows][columns][2]; //empty map
    }
    public Map(Map m, Tile t, int rows, int columns) {
        map = new MapComponent[rows][columns][2];
        for(int hgt = Map.GROUND_LAYER; hgt <= Map.ABOVE_GROUND_LAYER; hgt++) {
            for(int row = 0; row < rows; row++) {
                for(int col = 0; col < columns; col++) {
                    map[row][col][hgt] = m.map[t.getRow() + r][t.getColumn() + c][h];
                }
            }
        }
        squareSize = m.getSquareSize();
        r = rows;
        c = columns;
    }
    public Map (File loadFile) {
    }

    //Methods
    public int getSquareSize() {
        return squareSize;
    }
    public int getRows() {
        return r;
    }
    public int getColumns() {
        return c;
    }
}