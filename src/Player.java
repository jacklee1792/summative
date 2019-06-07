class Player extends Entity {
    /**  INSTANCE VARIABLES  **/
    private String name;
    private double hungerFactor;
    private ArrayList<Item> inventory;

    public Player()
    // default constructor
    {
        name = "ppman";
        health = 3; // for number of hearts
        maxHealth = 3;
        hungerFactor = 100;
    }

    public void addItem(Item itemtoadd)
    {
        inventory.add(itemtoadd);
    }



}


