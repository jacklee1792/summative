import java.util.ArrayList;

class Monster extends Entity
{

    static ArrayList<Monster> monsterList = new ArrayList<Monster>();

    private Tile location;

    public static ArrayList<Tile> updateMonster(MapComponent[][][] subMap, Tile subMapTile, Tile playerTile, ArrayList<Tile> list) {
        for(int i = 0; i < list.size(); i++) {
            int row = list.get(i).getRow();
            int column = list.get(i).getColumn();
            Tile subMapPosition = new Tile(list.get(i).getRow() - subMapTile.getRow(), list.get(i).getColumn() - subMapTile.getColumn());
            int smRow = subMapPosition.getRow();
            int smColumn = subMapPosition.getColumn();
            int ptRow = playerTile.getRow();
            int ptColumn = playerTile.getColumn();
            if(subMapPosition.getRow() > playerTile.getRow()) {
                if(subMap[Map.ITEM_LAYER][smRow - 1][smColumn].getWalkable() &&
                        (smRow - 1 != ptRow || smColumn != ptColumn)) list.set(i, new Tile(row - 1, column));
            } else if(subMapPosition.getRow() < playerTile.getRow()) {
                if(subMap[Map.ITEM_LAYER][smRow + 1][smColumn].getWalkable() &&
                        (smRow + 1 != ptRow || smColumn != ptColumn)) list.set(i, new Tile(row + 1, column));
            } else if(subMapPosition.getColumn() > playerTile.getColumn()) {
                if(subMap[Map.ITEM_LAYER][smRow][smColumn - 1].getWalkable() &&
                        (smRow != ptRow || smColumn - 1 != ptColumn)) list.set(i, new Tile(row, column - 1));
            } else if(subMapPosition.getColumn() < playerTile.getColumn() &&
                    (smRow != ptRow || smColumn + 1 != ptColumn)) {
                if(subMap[Map.ITEM_LAYER][smRow][smColumn + 1].getWalkable()) list.set(i, new Tile(row, column + 1));
            }
        }
        return list;
    }

    public void setLocation(Tile spawnlocus){location = spawnlocus; }
    public Tile getLocation() {return location;}

    public void chasePlayer() {

    }



}