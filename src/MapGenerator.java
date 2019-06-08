import java.util.Random;

public class MapGenerator { //I'll add getters and setters on map later

    static Random rand;
    MapComponent[][][] map;
    int h, w;

    public MapGenerator(int seed) {
        rand = new Random(seed);
    }

    public MapComponent[][][] generate(int height, int width) {
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

        //Some ponds
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.003)) map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER); //randomly spawning water
                else { //not randomly spawning water
                    int touching = touching(Map.GROUND_LAYER, r, c, MapComponent.WATER);
                    if (chance(touching / 2.05)) map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                }
            }
        }

        //Smoothen water generation
        for(int i = 0; i < 2; i++) {
            MapComponent[][][] temp = map; //temp used so changes in one cycle don't affect the same cycle
            for (int r = 0; r < h; r++) {
                for (int c = 0; c < w; c++) {
                    //if you are water and touched by >= 3 grass, turn yourself into grass
                    if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.WATER && touching(Map.GROUND_LAYER, r, c, MapComponent.GRASS) >= 3) {
                        temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.GRASS);
                    }
                    //..and the other way around
                    else if(map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.GRASS && touching(Map.GROUND_LAYER, r, c, MapComponent.WATER) >= 3) {
                        temp[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.WATER);
                    }
                }
            }
            map = temp; //temp destroyed on exit
        }

        //Random dirt spots
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (chance(0.1) && map[Map.GROUND_LAYER][r][c].getMapComponentID() == MapComponent.GRASS) {
                    map[Map.GROUND_LAYER][r][c] = new MapComponent(MapComponent.SOIL);
                }
            }
        }

        return map;
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

    private int touching(int layer, int row, int column, int MapComponentID) {
        int count = 0;
        try {
            if (map[layer][row + 1][column].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(map[layer][row - 1][column].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(map[layer][row][column + 1].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        try {
            if(map[layer][row][column - 1].getMapComponentID() == MapComponentID) count++;
        } catch(ArrayIndexOutOfBoundsException ex) {}
        return count;
    }

}
