/**
 * MapComponent class
 * In-depth documentation at:
 *  https://docs.google.com/document/d/1Hu6XnzeBDa0TvPOOIS6JWaDXK1A8l10doPGio8VgK84/edit?usp=sharing
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

class MapComponent {

    //Instance variables
    protected int MC_ID;
    protected int height, width;

    //Static variables and their properties

    static int numOfComponents = 25; //Number of currently implemented components
    static BufferedImage[] texture = new BufferedImage[numOfComponents];
    static Dimension[] size = new Dimension[numOfComponents];
    static boolean[] walkable = new boolean[numOfComponents];
    private boolean expended = false;

    final static int NULL = 0;
    final static int GRASS = 1;
    final static int SOIL = 2;
    final static int SAND = 3;
    final static int WATER = 4;
    final static int SMALL_TREE = 5;
    final static int SMALL_BUSH = 6;
    final static int ROCKS = 7;
    final static int PLANE = 8;
    final static int FILLED_NULL = 9;
    final static int WISE_ROCK = 10;
    final static int MONSTER = 11;
    final static int CHEST = 12;
    final static int RABBIT = 13;
    final static int BIRD = 14;
    final static int CAMPFIRE = 15;
    final static int STRING_GROUNDED = 16;
    final static int DEAD_RABBIT = 17;
    final static int DEAD_BIRD = 18;                      // dead bird
    final static int ANTENNA = 19;
    final static int TRANSMITTER = 20;
    final static int CIRCUIT_BOARD = 21;
    final static int BOSS_MONSTER = 22;
    final static int BOSS_REMAINS = 23;
    final static int SMALL_BUSH_EXPENDED = 24;

    private int health, maxHealth, attackDamage, attackRange;

    //Constructors
    public MapComponent() {

    }
    public MapComponent(int MapComponentID, int health) {
        MC_ID = MapComponentID;
        this.health = health;
        maxHealth = health;
        attackDamage = 1;
        attackRange = 1;
    }

    public MapComponent (int MapComponentID, int health, int damage, int range) {
        MC_ID = MapComponentID;
        this.health = health;
        maxHealth = health;
        attackDamage = damage;
        attackRange = range;
    }

    public MapComponent (int MapComponentID) {
        MC_ID = MapComponentID;
        health = 9999;
        maxHealth = 9999;
        attackDamage = 1;
        attackRange = 1;
    }

    public MapComponent (MapComponent old) {
        MC_ID = old.MC_ID;
        health = old.health;
        maxHealth = old.maxHealth;
        attackDamage = old.attackDamage;
        attackRange = old.attackRange;
        expended = old.expended;
    }

    //Methods
    public boolean isExpended() { return expended; }
    public void expend() {
        expended = true;
        if (MC_ID == SMALL_BUSH)
            MC_ID = SMALL_BUSH_EXPENDED;
        if (MC_ID == ROCKS)
            MC_ID = NULL;
    }

    public static void importTextures() throws IOException { //import textures
        for(int i = 0; i < numOfComponents; i++) {
            texture[i] = ImageIO.read(new File("./images/textures/_TEX" + i + ".png"));
        }
    }

    public int getMapComponentID() {
        return MC_ID;
    }

    //getWalkable can be applied to be items in ground and item layer
    public boolean getWalkable() {
        boolean b = true;
        if(MC_ID == WATER) b = false;
        else if (MC_ID == SMALL_TREE) b = false;
        else if (MC_ID == SMALL_BUSH) b = false;
        else if (MC_ID == ROCKS) b = false;
        else if (MC_ID == PLANE) b = false;
        else if (MC_ID == FILLED_NULL) b = false;
        else if (MC_ID == WISE_ROCK) b = false;
        else if (MC_ID == MONSTER) b = false;
        else if (MC_ID == CHEST) b = false;
        else if (MC_ID == RABBIT) b = false;
        else if (MC_ID == BIRD) b = false;
        else if (MC_ID == CAMPFIRE) b = true;
        else if (MC_ID == STRING_GROUNDED) b = false;
        else if (MC_ID == DEAD_RABBIT) b = true;
        else if (MC_ID == DEAD_BIRD) b = true;
        else if (MC_ID == ANTENNA) b = false;
        else if (MC_ID == TRANSMITTER) b = false;
        else if (MC_ID == CIRCUIT_BOARD) b = false;
        else if (MC_ID == BOSS_MONSTER) b = false;
        else if (MC_ID == BOSS_REMAINS) b = false;
        return b;
    }

    public Dimension getComponentSize() {
        Dimension d = new Dimension(0, 0);
        if(MC_ID == NULL) { d = new Dimension(1, 1); }
        else if(MC_ID == GRASS) { d = new Dimension(1, 1); }
        else if(MC_ID == SOIL) { d = new Dimension(1, 1); }
        else if(MC_ID == SAND) { d = new Dimension(1, 1); }
        else if(MC_ID == WATER) { d = new Dimension(1, 1); }
        else if(MC_ID == SMALL_TREE) { d = new Dimension(1, 1); }
        else if(MC_ID == SMALL_BUSH) { d = new Dimension(1, 1); }
        else if(MC_ID == ROCKS) { d = new Dimension(1, 1); }
        else if(MC_ID == PLANE) { d = new Dimension(4, 2); }
        else if(MC_ID == FILLED_NULL) { d = new Dimension(4, 2); }
        else if(MC_ID == WISE_ROCK) { d = new Dimension(1, 1); }
        else if(MC_ID == MONSTER) { d = new Dimension(1, 1); }
        else if(MC_ID == CHEST) { d = new Dimension(1, 1); }
        else if(MC_ID == RABBIT) { d = new Dimension(1, 1); }
        else if(MC_ID == BIRD) {d = new Dimension(1, 1); }
        else if (MC_ID == CAMPFIRE) {d = new Dimension(1, 1); }
        else if (MC_ID == STRING_GROUNDED) {d = new Dimension(1, 1); }
        else if (MC_ID == DEAD_RABBIT) {d = new Dimension(1, 1); }
        else if (MC_ID == DEAD_BIRD) {d = new Dimension(1, 1); }
        else if (MC_ID == ANTENNA) {d = new Dimension(1, 1); }
        else if (MC_ID == TRANSMITTER) {d = new Dimension(1, 1); }
        else if (MC_ID == CIRCUIT_BOARD) {d = new Dimension(1, 1); }
        else if (MC_ID == BOSS_MONSTER) {d = new Dimension(1, 1); }
        else if (MC_ID == BOSS_REMAINS) {d = new Dimension(1, 1); }
        else if (MC_ID == SMALL_BUSH_EXPENDED) {d = new Dimension(1, 1); }
        return d;
    }

    public boolean isEntity() {
        boolean b = false;
        if(MC_ID == MONSTER || MC_ID == RABBIT || MC_ID == BIRD || MC_ID == BOSS_MONSTER) b = true;
        return b;
    }

    public void addHealth(int toAdd) {
        health += toAdd;
        if (health > maxHealth)
            health = maxHealth;
    }
    public int getHealth() {return health; }
    public int getMaxHealth() {return maxHealth; }

    public int getAttackDamage() {return attackDamage; }

    public int getAttackRange() {return attackRange; }

    public void turnNull() {MC_ID = NULL; }

    public void turnCampfire() {MC_ID = CAMPFIRE; }

    public boolean isFire() {
        boolean isCampfire = false;
        try {
            if (MC_ID == CAMPFIRE)
                isCampfire = true;
        }
        catch (NullPointerException ex) {}
        return isCampfire;
    }

//    public void setExpended (boolean b) {
//        expended = b;
//        if ()
//    }

}