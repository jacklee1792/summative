import java.awt.image.BufferedImage;

public abstract class Entity extends MapComponent
{
    // Static variables
    final static int NORTH = 0;
    final static int WEST = 1;
    final static int SOUTH = 2;
    final static int EAST = 3;


    // Instance variables
    private String name;
    //protected int walkCycle; //flip-flop cycle
    protected int health;
    protected int orientation;
    protected int maxHealth;
    protected BufferedImage currentTexture;
    private int attackDamage;
    private int attackRange;
    protected double movementSpeed;

    public String getName() {
        return name;
    }

    /* public String setName(String inname) {name = inname; } */

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        if (health < 0)
            return true;
        else
            return false;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int direction) {
        orientation = direction;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    /* Too many textures I don't have time to make right now
     public void walkCycle() {
        if (walkCycle == 1) walkCycle++;
        else walkCycle--;
    }
    */

    public void setAttackRange(int attrange){attackRange = attrange; }

    public int getAttackRange() {return attackRange; }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setAttackDamage(int attackdam) {
        attackDamage = attackdam;
    }

    public void addHealth(int toadd) {
        health += toadd;
    }

    public void damageOther(Entity other)
    {
        other.addHealth((-1) * attackDamage);
    }

}