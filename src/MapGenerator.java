import java.util.Random;

public class MapGenerator { //I'll add getters and setters on map later

    static Random rand;
    MapComponent[][][] map;
    Tile spawnTile, rockTile;
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
                if (chance(0.001)) {
                    map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER); //randomly spawning water
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                }
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
                        if (chance(touching / 10.0)){
                            temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                            temp[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                        }
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
                        temp[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.NULL);
                    }
                    else if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.GRASS &&
                            countTouching(map, Map.GROUND_LAYER, r, c, MapComponent.WATER) >= 3 &&
                            chance(0.8)) {
                        temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                        temp[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.WATER);
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

        // Miscellaneous
        while(true) {
            int row = spawnTile.getRow() + rand.nextInt(11) - 5; // up to 5 away
            int column = spawnTile.getColumn() + rand.nextInt(11) - 5;
            if(map[Map.GROUND_LAYER][row][column].getMapComponentID() != MapComponent.WATER && //if ground not water and what is above it empty
                    map[Map.ITEM_LAYER][row][column].getMapComponentID() == MapComponent.NULL) {
                map[Map.ITEM_LAYER][row][column] = new MapComponent(MapComponent.WISE_ROCK);
                rockTile = new Tile(row, column);

                /*
                int chestR = row + (int)(Math.random() * 4 - 3);
                int chestC = column + (int)(Math.random() * 4 - 3);

                if(map[Map.ITEM_LAYER][chestR][chestC].getMapComponentID() != MapComponent.WISE_ROCK)
                    map[Map.ITEM_LAYER][chestR][chestC] = new MapComponent(MapComponent.CHEST); // add chest object
                //map[Map.ITEM_LAYER][row-2][column-2] = new MapComponent(MapComponent.CAMPFIRE); // add chest object
                System.out.println(row + " " + column);
                 */
                break; //exit the loop
            }
        }

        //Monsters
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                if(chance(0.003) && // 1/33 chance of spawning
                        map[Map.GROUND_LAYER][r][c].getWalkable() && //Spawn on walkable land
                        map[Map.ITEM_LAYER][r][c].getWalkable() && //Do not spawn inside an item
                        (Math.abs(r - spawnTile.getRow()) > 20 || Math.abs(c - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.MONSTER, 100, 5, 1); //You're a monster bro
                    Map.totalMonsters++;
                }
            }
        }

        // Boss Monster
        map[Map.ITEM_LAYER][8][8] = new MapComponent(MapComponent.BOSS_MONSTER, 1000, 14, 2);
        map[Map.ITEM_LAYER][height - 8][8] = new MapComponent(MapComponent.BOSS_MONSTER2, 5000, 30, 1);
        map[Map.ITEM_LAYER][height - 8][width - 8] = new MapComponent(MapComponent.BOSS_MONSTER3, 60, 30, 3);
        map[Map.ITEM_LAYER][8][width - 8] = new MapComponent(MapComponent.BOSS_MONSTER4, 200, 1, 5);


        // Rabbits
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                if(chance(0.003) && // 1/33 chance of spawning
                        map[Map.GROUND_LAYER][r][c].getWalkable() && //Spawn on walkable land
                        map[Map.ITEM_LAYER][r][c].getWalkable() && //Do not spawn inside an item
                        (Math.abs(r - spawnTile.getRow()) > 20 || Math.abs(c - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.RABBIT, 50, 0, 0);
                }
            }
        }

        // Birds
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                if(chance(0.003) && // 1/33 chance of spawning
                        map[Map.GROUND_LAYER][r][c].getWalkable() && //Spawn on walkable land
                        map[Map.ITEM_LAYER][r][c].getWalkable() && //Do not spawn inside an item
                        (Math.abs(r - spawnTile.getRow()) > 20 || Math.abs(c - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.BIRD, 50, 0, 0);
                }
            }
        }

        // Radio parts
//        boolean placedAntenna = false;
//        boolean placedTransmitter = false;
//        boolean placedCircuitBoard = false;
//        for(int r = 4; r < height - 4; r++) { // so it doesn't spawn somewhere unreachable
//            for(int c = 4; c < width - 4; c++) {
//                boolean notAllPlaced = !(placedAntenna && placedTransmitter && placedCircuitBoard);
//                while (notAllPlaced) {
//
//                    if (chance(0.01) && // 1/1000 chance of spawning
//                            map[Map.GROUND_LAYER][height - r][c].getWalkable() && //Spawn on walkable land
//                            map[Map.ITEM_LAYER][height - r][c].getWalkable() && //Do not spawn inside an item
//                            (Math.abs((height - r) - spawnTile.getRow()) > 20 || Math.abs(c - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
//
//                        if (!placedAntenna) {
//                            map[Map.ITEM_LAYER][height - r][c] = new MapComponent(MapComponent.ANTENNA, 30, 0, 0);
//                            placedAntenna = true;
//                            System.out.println("antenna: " + (height - r) + ", " + c);
//                        }
//
//                    }
//                    if (chance(0.01) && // 1/1000 chance of spawning
//                            map[Map.GROUND_LAYER][r][c].getWalkable() && //Spawn on walkable land
//                            map[Map.ITEM_LAYER][r][c].getWalkable() && //Do not spawn inside an item
//                            (Math.abs(r - spawnTile.getRow()) > 20 || Math.abs(c - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
//
//                        if (!placedAntenna) {
//                            map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.TRANSMITTER, 30, 0, 0);
//                            placedAntenna = true;
//                            System.out.println("transmitter: " + r + ", " + c);
//                        }
//
//                    }
//                    if (chance(0.01) && // 1/1000 chance of spawning
//                            map[Map.GROUND_LAYER][r][width - c].getWalkable() && //Spawn on walkable land
//                            map[Map.ITEM_LAYER][r][width - c].getWalkable() && //Do not spawn inside an item
//                            (Math.abs(r - spawnTile.getRow()) > 20 || Math.abs((height - c) - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
//
//                        if (!placedAntenna) {
//                            map[Map.ITEM_LAYER][r][width - c] = new MapComponent(MapComponent.CIRCUIT_BOARD, 30, 0, 0);
//                            placedAntenna = true;
//                            System.out.println("circuit board: " + r + ", " + (width - c));
//                        }
//
//                    }
//                    notAllPlaced = !(placedAntenna && placedTransmitter && placedCircuitBoard);
//                }
//            }
//        }
//
//


        boolean placedAntenna = false;
        boolean placedTransmitter = false;
        boolean placedCircuitBoard = false;

        int antennaR = (int) (Math.random() * (height - 20) + 4);
        int antennaC = (int) (Math.random() * (width - 20) + 4);
        int transmitterR = (int) (Math.random() * (height - 20) + 4);
        int transmitterC = (int) (Math.random() * (width - 20) + 4);
        int circuitboardR = (int) (Math.random() * (height - 20) + 4);
        int circuitboardC = (int) (Math.random() * (width - 20) + 4);

        // antenna
        if (map[Map.GROUND_LAYER][antennaR][width - antennaC].getWalkable() && //Spawn on walkable land
                map[Map.ITEM_LAYER][antennaR][width - antennaC].getWalkable() && //Do not spawn inside an item
                (Math.abs(antennaR - spawnTile.getRow()) > 20 || Math.abs((height - antennaC) - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
            map[Map.ITEM_LAYER][height - antennaR][antennaC] = new MapComponent(MapComponent.ANTENNA, 30, 0, 0);
            placedAntenna = true;
            System.out.println("antenna: " + (height - antennaR) + ", " + antennaC);
        }

        // transmitter
        if (map[Map.GROUND_LAYER][transmitterR][width - transmitterC].getWalkable() && //Spawn on walkable land
                map[Map.ITEM_LAYER][transmitterR][width - transmitterC].getWalkable() && //Do not spawn inside an item
                (Math.abs(transmitterR - spawnTile.getRow()) > 20 || Math.abs((height - transmitterC) - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
            map[Map.ITEM_LAYER][height - transmitterR][transmitterC] = new MapComponent(MapComponent.TRANSMITTER, 30, 0, 0);
            placedTransmitter = true;
            System.out.println("transmitter: " + (height - transmitterR) + ", " + transmitterC);
        }

        // circuit board
        if (map[Map.GROUND_LAYER][circuitboardR][width - circuitboardC].getWalkable() && //Spawn on walkable land
                map[Map.ITEM_LAYER][circuitboardR][width - circuitboardC].getWalkable() && //Do not spawn inside an item
                (Math.abs(circuitboardR - spawnTile.getRow()) > 20 || Math.abs((height - circuitboardC) - spawnTile.getColumn()) > 20)) { //At least one coordinate has to be >20 blocks away
            map[Map.ITEM_LAYER][height - circuitboardR][circuitboardC] = new MapComponent(MapComponent.CIRCUIT_BOARD, 30, 0, 0);
            placedCircuitBoard = true;
            System.out.println("circuit board: " + (height - circuitboardR) + ", " + circuitboardC);
        }

        // just in case the above thing didn't work
        if (!placedAntenna)
            map[Map.ITEM_LAYER][spawnTile.getRow() - 10][spawnTile.getColumn() - 10] = new MapComponent(MapComponent.ANTENNA, 30, 0, 0);
        if (!placedTransmitter)
            map[Map.ITEM_LAYER][spawnTile.getRow() - 11][spawnTile.getColumn() - 11] = new MapComponent(MapComponent.TRANSMITTER, 30, 0, 0);
        if (!placedCircuitBoard)
            map[Map.ITEM_LAYER][spawnTile.getRow() - 12][spawnTile.getColumn() - 12] = new MapComponent(MapComponent.CIRCUIT_BOARD, 30, 0, 0);

        // String
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                if(chance(0.01) && // 1/100 chance of spawning
                        map[Map.GROUND_LAYER][r][c].getWalkable() && //Spawn on walkable land
                        map[Map.ITEM_LAYER][r][c].getWalkable() && //Do not spawn inside an item
                        (Math.abs(r - spawnTile.getRow()) > 10 || Math.abs(c - spawnTile.getColumn()) > 10)) { //At least one coordinate has to be >10 blocks away
                    map[Map.ITEM_LAYER][r][c] = new MapComponent(MapComponent.STRING_GROUNDED, 30, 0, 0);
                }
            }
        }


    }

    private boolean chance(double probability) {
        if(rand.nextInt(10000) < probability * 10000) return true; //Limited accuracy but it works for this purpose
        return false;
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

    public Tile getRockTile() {
        return rockTile;
    }

}
