import java.util.ArrayList;

class Monster extends Entity
{

    static ArrayList<Monster> monsterList = new ArrayList<Monster>();

    private Tile location;

    public Monster() {
    }

    public static ArrayList<Tile> updateMonster(MapComponent[][][] subMap, Tile subMapTile, Tile playerTile, ArrayList<Tile> list) {
        for(int i = 0; i < list.size(); i++) {
            int row = list.get(i).getRow(), column = list.get(i).getColumn();
            Tile subMapPosition = new Tile(list.get(i).getRow() - subMapTile.getRow(), list.get(i).getColumn() - subMapTile.getColumn());
            int smRow = subMapPosition.getRow(), smColumn = subMapPosition.getColumn();
            int ptRow = playerTile.getRow(), ptColumn = playerTile.getColumn();

            if(subMapPosition.getRow() > playerTile.getRow()) {
                if(subMap[Map.ITEM_LAYER][smRow - 1][smColumn].getWalkable() &&
                        (smRow - 1 != ptRow || smColumn != ptColumn)) list.set(i, new Tile(row - 1, column));
            } if(subMapPosition.getRow() < playerTile.getRow()) {
                if(subMap[Map.ITEM_LAYER][smRow + 1][smColumn].getWalkable() &&
                        (smRow + 1 != ptRow || smColumn != ptColumn)) list.set(i, new Tile(row + 1, column));
            } if(subMapPosition.getColumn() > playerTile.getColumn()) {
                if(subMap[Map.ITEM_LAYER][smRow][smColumn - 1].getWalkable() &&
                        (smRow != ptRow || smColumn - 1 != ptColumn)) list.set(i, new Tile(row, column - 1));
            } if(subMapPosition.getColumn() < playerTile.getColumn() &&
                    (smRow != ptRow || smColumn + 1 != ptColumn)) {
                if(subMap[Map.ITEM_LAYER][smRow][smColumn + 1].getWalkable()) list.set(i, new Tile(row, column + 1));
            }

            subMap[Map.ITEM_LAYER][list.get(i).getRow() - subMapTile.getRow()][list.get(i).getColumn() - subMapTile.getColumn()] = new MapComponent(MapComponent.MONSTER); //Update comparison subMap
        }
        return list;
    }

    public void setLocation(Tile spawnlocus){location = spawnlocus; }
    public Tile getLocation() {return location;}

    public void chasePlayer() {

    }



}