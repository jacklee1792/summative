/**
 * Tile class
 *
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1mD2PvWuL1wYKbuQaVSP8YdepfEGyc_JHo2QgtvZMw-s/edit?usp=sharing
 *
 */

class Tile {
    private int r, c;

    public Tile(int row, int column) {
        r = row;
        c = column;
    }
    public Tile(Tile t) {
        r = t.r; //same class, has access to data fields
        c = t.c;
    }

    public int getRow() {
        return r;
    }
    public int getColumn() {
        return c;
    }
}
