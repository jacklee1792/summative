import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.Math;

class Monster extends MapComponent {

    public static MapComponent[][][] updateMonster(MapComponent[][][] subMap, Tile playerTile) {

        int ptRow = playerTile.getRow(), ptColumn = playerTile.getColumn();

        ArrayList<Tile> locations = new ArrayList<>();
        for(int r = 0; r < subMap[0].length; r++) {
            for(int c = 0; c < subMap[0][0].length; c++) {
                if(subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.MONSTER) locations.add(new Tile(r, c));
            }
        }

        for(Tile t : locations) {
            int row = t.getRow(), column = t.getColumn();
            MapComponent currentMonster = new MapComponent(subMap[Map.ITEM_LAYER][row][column]);
            subMap[Map.ITEM_LAYER][row][column] = new MapComponent(MapComponent.NULL);

            if(currentMonster.getHealth() <= 0) {
                //Do nothing to re-spawn it
            } else if (row > ptRow &&
                    subMap[Map.ITEM_LAYER][row - 1][column].getWalkable() &&
                    subMap[Map.GROUND_LAYER][row - 1][column].getWalkable() &&
                    (row - 1 != ptRow || column != ptColumn)) {
                subMap[Map.ITEM_LAYER][row - 1][column] = new MapComponent(currentMonster);
            } else if (row < ptRow &&
                    subMap[Map.ITEM_LAYER][row + 1][column].getWalkable() &&
                    subMap[Map.GROUND_LAYER][row + 1][column].getWalkable() &&
                    (row + 1 != ptRow || column != ptColumn)) {
                subMap[Map.ITEM_LAYER][row + 1][column] = new MapComponent(currentMonster);
            } else if (column > ptColumn &&
                    subMap[Map.ITEM_LAYER][row][column - 1].getWalkable() &&
                    subMap[Map.GROUND_LAYER][row][column - 1].getWalkable() &&
                    (column - 1 != ptColumn || row != ptRow)) {
                subMap[Map.ITEM_LAYER][row][column - 1] = new MapComponent(currentMonster);
            } else if (column < ptColumn &&
                    subMap[Map.ITEM_LAYER][row][column + 1].getWalkable() &&
                    subMap[Map.GROUND_LAYER][row][column + 1].getWalkable() &&
                    (column + 1 != ptColumn || row != ptRow)) {
                subMap[Map.ITEM_LAYER][row][column + 1] = new MapComponent(currentMonster);
            } else subMap[Map.ITEM_LAYER][row][column] = new MapComponent(currentMonster);
        }

        return subMap;
    }


//    public boolean isDead() {
//        if (health <= 0)
//            return true;
//        else
//            return false;
//    }

}