import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

class Monster extends MapComponent {

    public static MapComponent[][][] updateMonster(MapComponent[][][] subMap, Tile playerTile) {

        int ptRow = playerTile.getRow(), ptColumn = playerTile.getColumn();

        ArrayList<Tile> locations = new ArrayList<>();
        for(int r = 0; r < subMap[0].length; r++) {
            for(int c = 0; c < subMap[0][0].length; c++) {
                if(subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.MONSTER ||
                        subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.RABBIT ||
                        subMap[Map.ITEM_LAYER][r][c].getMapComponentID() == MapComponent.BIRD)
                    locations.add(new Tile(r, c));
            }
        }

        for(Tile t : locations) {
            int row = t.getRow(), column = t.getColumn();
            MapComponent currentMonster = subMap[Map.ITEM_LAYER][row][column];
            subMap[Map.ITEM_LAYER][row][column] = new MapComponent(MapComponent.NULL);

            if (currentMonster.getMapComponentID() == MONSTER) {
                int[] walkPriority = {0, 0, 0, 0}; //0 = cannot walk, 1 = can walk, 2 = can walk towards player
                try {
                    if (subMap[Map.ITEM_LAYER][row - 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row - 1][column].getWalkable() && (row - 1 != ptRow || column != ptColumn))
                        walkPriority[Map.NORTH] = 1;
                } catch (Exception ex) {
                }
                try {
                    if (subMap[Map.ITEM_LAYER][row + 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row + 1][column].getWalkable() && (row + 1 != ptRow || column != ptColumn))
                        walkPriority[Map.SOUTH] = 1;
                } catch (Exception ex) {
                }
                try {
                    if (subMap[Map.ITEM_LAYER][row][column - 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column - 1].getWalkable() && (column - 1 != ptColumn || row != ptRow))
                        walkPriority[Map.WEST] = 1;
                } catch (Exception ex) {
                }
                try {
                    if (subMap[Map.ITEM_LAYER][row][column + 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column + 1].getWalkable() && (column + 1 != ptColumn || row != ptRow))
                        walkPriority[Map.EAST] = 1;
                } catch (Exception ex) {
                }
                if (row > ptRow) walkPriority[Map.NORTH] *= 2;
                if (row < ptRow) walkPriority[Map.SOUTH] *= 2;
                if (column > ptColumn) walkPriority[Map.WEST] *= 2;
                if (column < ptColumn) walkPriority[Map.EAST] *= 2;

                int highestPriority = Integer.MIN_VALUE;
                for (int val : walkPriority) {
                    if (val > highestPriority) highestPriority = val;
                }

                ArrayList<Integer> toChoose = new ArrayList<>();
                for (int i = 0; i < walkPriority.length; i++) {
                    if (walkPriority[i] == highestPriority) toChoose.add(i);
                }

                Random r = new Random();
                int choice = toChoose.get(r.nextInt(toChoose.size()));


                if (currentMonster.getHealth() > 0) {
                    if (highestPriority == 0)
                        subMap[Map.ITEM_LAYER][row][column] = currentMonster; //Put it back where it was
                    else if (choice == Map.NORTH) subMap[Map.ITEM_LAYER][row - 1][column] = currentMonster;
                    else if (choice == Map.SOUTH) subMap[Map.ITEM_LAYER][row + 1][column] = currentMonster;
                    else if (choice == Map.WEST) subMap[Map.ITEM_LAYER][row][column - 1] = currentMonster;
                    else if (choice == Map.EAST) subMap[Map.ITEM_LAYER][row][column + 1] = currentMonster;
                }
                else {// if its dead
                    System.out.println("you have lynched " + Player.monstersKilled + " neighbours");
                }
            }

            if (currentMonster.getMapComponentID() == RABBIT) {
                if (currentMonster.getHealth() > 0) {
                    int direction = (int) (Math.random() * 4);
                    boolean hasMoved = false;
                    if (direction == 0) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row][column + 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column + 1].getWalkable() && (column + 1 != ptColumn || row != ptRow)) {
                                subMap[Map.ITEM_LAYER][row][column + 1] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 1) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row][column - 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column - 1].getWalkable() && (column - 1 != ptColumn || row != ptRow)) {
                                subMap[Map.ITEM_LAYER][row][column - 1] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 2) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row + 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row + 1][column].getWalkable() && (column != ptColumn || row + 1 != ptRow)) {
                                subMap[Map.ITEM_LAYER][row + 1][column] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 3) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row - 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row - 1][column].getWalkable() && (column != ptColumn || row - 1 != ptRow)) {
                                subMap[Map.ITEM_LAYER][row - 1][column] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    }

                    if (!hasMoved)
                        subMap[Map.ITEM_LAYER][row][column] = currentMonster;
                }
                else {// if its dead
                    int randy = (int) (Math.random() * 10);
                    if (randy <= 9) // 30% chance of dropping meat
                        subMap[Map.ITEM_LAYER][row][column] = new MapComponent(DEAD_RABBIT);
                    System.out.println("you have killed " + Player.monstersKilled + " monsters");
                }
            }

            if (currentMonster.getMapComponentID() == BIRD && currentMonster.getHealth() > 0) {
                int direction = (int) (Math.random() * 4);
                boolean hasMoved = false;
                if (currentMonster.getHealth() == currentMonster.getMaxHealth()) {
                    if (direction == 0) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row][column + 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column + 1].getWalkable() && (column + 1 != ptColumn || row != ptRow)) {
                                subMap[Map.ITEM_LAYER][row][column + 1] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 1) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row][column - 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column - 1].getWalkable() && (column - 1 != ptColumn || row != ptRow)) {
                                subMap[Map.ITEM_LAYER][row][column - 1] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 2) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row + 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row + 1][column].getWalkable() && (column != ptColumn || row + 1 != ptRow)) {
                                subMap[Map.ITEM_LAYER][row + 1][column] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                    } else if (direction == 3) {
                        try {
                            if (subMap[Map.ITEM_LAYER][row - 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row - 1][column].getWalkable() && (column != ptColumn || row - 1 != ptRow)) {
                                subMap[Map.ITEM_LAYER][row - 1][column] = currentMonster;
                                hasMoved = true;
                            }
                        } catch (Exception ex) {
                        }
                        if (!hasMoved)
                            subMap[Map.ITEM_LAYER][row][column] = currentMonster;
                    }
                }
//                else if (currentMonster.getHealth() < currentMonster.getMaxHealth() && currentMonster.getHealth() > 0) {// if its been hit
//                    int[] walkPriority = {0, 0, 0, 0}; //0 = cannot walk, 1 = can walk, 2 = can walk towards player
//                    try {
//                        if (subMap[Map.ITEM_LAYER][row - 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row - 1][column].getWalkable() && (row - 1 != ptRow || column != ptColumn))
//                            walkPriority[Map.NORTH] = 1;
//                    } catch (Exception ex) {
//                    }
//                    try {
//                        if (subMap[Map.ITEM_LAYER][row + 1][column].getWalkable() && subMap[Map.GROUND_LAYER][row + 1][column].getWalkable() && (row + 1 != ptRow || column != ptColumn))
//                            walkPriority[Map.SOUTH] = 1;
//                    } catch (Exception ex) {
//                    }
//                    try {
//                        if (subMap[Map.ITEM_LAYER][row][column - 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column - 1].getWalkable() && (column - 1 != ptColumn || row != ptRow))
//                            walkPriority[Map.WEST] = 1;
//                    } catch (Exception ex) {
//                    }
//                    try {
//                        if (subMap[Map.ITEM_LAYER][row][column + 1].getWalkable() && subMap[Map.GROUND_LAYER][row][column + 1].getWalkable() && (column + 1 != ptColumn || row != ptRow))
//                            walkPriority[Map.EAST] = 1;
//                    } catch (Exception ex) {
//                    }
//                    if (row > ptRow) walkPriority[Map.NORTH] *= 2;
//                    if (row < ptRow) walkPriority[Map.SOUTH] *= 2;
//                    if (column > ptColumn) walkPriority[Map.WEST] *= 2;
//                    if (column < ptColumn) walkPriority[Map.EAST] *= 2;
//
//                    int highestPriority = Integer.MIN_VALUE;
//                    for (int val : walkPriority) {
//                        if (val > highestPriority) highestPriority = val;
//                    }
//
//                    ArrayList<Integer> toChoose = new ArrayList<>();
//                    for (int i = 0; i < walkPriority.length; i++) {
//                        if (walkPriority[i] == highestPriority) toChoose.add(i);
//                    }
//
//                    Random r = new Random();
//                    int choice = toChoose.get(r.nextInt(toChoose.size()));
//
//
//                    if (currentMonster.getHealth() > 0) {
//                        try {
//                            if (highestPriority == 0) subMap[Map.ITEM_LAYER][row][column] = currentMonster; //Put it back where it was
//                            else if (choice == Map.NORTH) subMap[Map.ITEM_LAYER][row + 1][column] = currentMonster; // reversed from monster
//                            else if (choice == Map.SOUTH) subMap[Map.ITEM_LAYER][row - 1][column] = currentMonster;
//                            else if (choice == Map.WEST) subMap[Map.ITEM_LAYER][row][column + 1] = currentMonster;
//                            else if (choice == Map.EAST) subMap[Map.ITEM_LAYER][row][column - 1] = currentMonster;
//                        }
//                        catch(ArrayIndexOutOfBoundsException maggie) {
//                            subMap[Map.ITEM_LAYER][row][column] = currentMonster;
//                        }
//                    }
//
//                }

                else {// if dead
                    int randy = (int) (Math.random() * 10);
                    if (randy <= 9) // 20% chance of dropping feather
                        subMap[Map.ITEM_LAYER][row][column] = new MapComponent(DEAD_BIRD);
                    System.out.println("you have killed " + Player.monstersKilled + " monsters");
                }


            }

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