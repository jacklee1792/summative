import java.util.Random;

public class MapGenerator { //I'll add getters and setters on map later

    static Random rand;
    MapComponent[][][] map;
    Tile spawnTile;
    int h, w;

    public MapGenerator(int seed) {
        rand = new Random(seed);
    }

    public void generate(int height, int width) {
        map = new MapComponent[2][height][width];
        h = height;
        w = width;

        //Fill the thing with empty crap so it doesn't bother you and say huRr huRr NullPointerException
        for (int l = 0; l < 2; l++) {
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) { //C++ hehe
                    map[l][r][c] = new MapComponent(MapComponent.NULL);
                }
            }
        }

        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) { //C++ hehe
                map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.GRASS);
            }
        }

        //Initial lake point generation
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.001)) map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER); //randomly spawning water
            }
        }

        //Lake generation based on initial points
        for(int i = 0; i < 70; i++) {
            //Temp
            MapComponent[][][] temp = new MapComponent[2][h][w];
            copyMap(map, temp); //copy everything from map to temp
            //Check map, write to temp
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    {
                        int touching = countTouching(map, Map.GROUND_LAYER, r, c, MapComponent.WATER);
                        if (chance(touching / 10.0)) temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                    }
                }
            }
            copyMap(temp, map); //copy everything from temp to map
        }

        //Smoothen water generation
        for(int i = 0; i < 10; i++) {
            //Temp
            MapComponent[][][] temp = new MapComponent[2][h][w];
            copyMap(map, temp);
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.WATER &&
                            countTouching(map, Map.GROUND_LAYER, r, c, MapComponent.GRASS) >= 3 &&
                            chance(0.2)) {
                        temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.GRASS);
                    }
                    else if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.GRASS &&
                            countTouching(map, Map.GROUND_LAYER, r, c, MapComponent.WATER) >= 3 &&
                            chance(0.8)) {
                        temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                    }
                }
            }
            copyMap(temp, map);
        }

        //Random dirt spots
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.1) && map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.GRASS) { //if grass and chance(0.1)
                    map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.SOIL);
                }
            }
        }

        //Trees and bushes
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.08) && map[Map.GROUND_LAYER][r][c].getMapComponentID() != MapComponent.WATER) { //if grass and chance(0.1)
                    if(chance(0.5)) map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.SMALL_TREE);
                    else map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.SMALL_BUSH);
                }
            }
        }

        //Rocks
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.01) && map[Map.GROUND_LAYER][r][c].getMapComponentID() != MapComponent.WATER) { //if grass and chance(0.1)
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.ROCKS);
                }
            }
        }

        //Generate spawn tile
        boolean spawnTileFound = false;
        while(!spawnTileFound) {
            spawnTileFound = true; //True until proven otherwise
            int row = rand.nextInt(h / 2) + h / 4; //Somewhere in the middle
            int column = rand.nextInt(w / 2) + w / 4;
            spawnTile = new Tile(row, column);
            for(int r = spawnTile.getRow(); r < spawnTile.getRow() + 4; r++) { //Find a 4x3 area where there is no water on the base layer
                for(int c = spawnTile.getColumn(); c < spawnTile.getColumn() + 3; c++) {
                    if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.WATER) spawnTileFound = false;
                }
            }
        }

        //Plane
        for(int r = spawnTile.getRow() + 1; r < spawnTile.getRow() + 2; r++) { //4 by 2 region of plane
            for(int c = spawnTile.getColumn(); c < spawnTile.getColumn() + 4; c++) {
                map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.FILLED_NULL);
            }
        }
        map[Map.ITEM_LAYER][spawnTile.getRow() + 1][spawnTile.getColumn()] = new MapComponent(MapComponent.PLANE); //top left corner of plane

        //Rock
        while(true) {
            int row = spawnTile.getRow() + rand.nextInt(11) - 5; // up to 5 away
            int column = spawnTile.getColumn() + rand.nextInt(11) - 5;
            if(map[Map.GROUND_LAYER][row][column].getMapComponentID() != MapComponent.WATER && //if ground not water and what is above it empty
                    map[Map.ITEM_LAYER][row][column].getMapComponentID() == MapComponent.NULL) {
                map[Map.ITEM_LAYER][row][column] = new MapComponent(MapComponent.WISE_ROCK);
                System.out.println(row + " " + column);
                break; //exit the loop
            }
        }
    }


    private boolean chance(double probability) {
        if(rand.nextInt(10000) < probability * 10000) return true; //Limited accuracy but it works for this purpose
        else return false;
    }

    //maybe we can use this later???
    private int surrounding(int layer, int row, int column, int MapComponentID, int radius) {
        int count = 0;
        for(int r = row - radius; r <= row + radius; r++) {
            for(int c = column - radius; c <= column + radius; c++) {
                try {
                    if(map[layer][r][c].getMapComponentID() == MapComponentID) count++;
                } catch(ArrayIndexOutOfBoundsException ex) {}
            }
        }
        return count;
    }

    private int countTouching(MapComponent[][][] m, int layer, int row, int column, int MapComponentID) {
        int count = 0;
        try {
            if (m[layer][row + 1][column].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(m[layer][row - 1][column].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(m[layer][row][column + 1].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(m[layer][row][column - 1].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        return count;
    }

    public void copyMap(MapComponent[][][] original, MapComponent[][][] copy) {
        for(int l = 0; l < 2; l++) {
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    copy[l][r][c] = new MapComponent(original[l][r][c].getMapComponentID());
                }
            }
        }
    }

    public MapComponent[][][] getMap() {
        return map;
    }
    public Tile getSpawnTile() {
        return spawnTile;
    }

}
