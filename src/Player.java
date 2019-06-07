import java.util.ArrayList;

class Player extends Entity {
    /**  INSTANCE VARIABLES  **/
    private String name;
    private double hungerFactor;
    private ArrayList<Item> inventory;
    private ArrayList<Integer> inventoryQtys;
    private int stackSize = 6;

    public Player() {
        name = "ppman";
        health = 3; // for number of hearts
        maxHealth = 3;
        hungerFactor = 100;
    }

    public void addItem(Item targetItem) {

    }



}


