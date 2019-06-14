import java.util.ArrayList;

class Monster extends Entity
{

    static ArrayList<Monster> monsterList = new ArrayList<Monster>();

    private Tile location;

    public static ArrayList<Tile> updateMonster(MapComponent subMap, ArrayList<Tile> list) {
        for(int i = 0; i < list.size(); i++) {
            list.set(i, new Tile(list.get(i).getRow(), list.get(i).getColumn() + 1)); //Just for testing
        }
        return list;
    }

    public void setLocation(Tile spawnlocus){location = spawnlocus; }
    public Tile getLocation() {return location;}

    public void chasePlayer() {

    }



}