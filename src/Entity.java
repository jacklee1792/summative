public class Entity
{
    // Static variables
    final static int NORTH = 0;
    final static int EAST = 1;
    final static int SOUTH = 2;
    final static int WEST = 3;


    // Instance variables
    private String name;
    private int health;
    private int orientation;
    private int maxHealth;
    private int attackDamage;
    private int movementSpeed;

    public String getName() {
        return name;
    }

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

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public void setAttackDamage(int ad) {
        attackDamage = ad;
    }

    public void addHealth(int toadd) {
        health += toadd;
    }

    public void damageOther(Entity other)
    {
        other.addHealth((-1) * attackDamage);
    }

}