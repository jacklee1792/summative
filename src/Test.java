//Use this for testing things

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Test t = new Test();
        t.testChance();
    }

    public Test() {
        //Not useful, like you
    }

    //ok dis gay
    public void testChance() {
        int sum = 0;
        double prob = 0;
        int trials = 200000;
        for(int i = 0; i < trials; i++) {
            if(chance(prob)) sum++;
        }
        System.out.println("EXPECTED: " + prob);
        System.out.println("ACTUAL: " + 1.0 * sum / trials);
    }

    private boolean chance(double probability) {
        Random r = new Random();
        if(r.nextInt(1000) < probability * 1000) return true; //Limited accuracy but it works for this purpose
        else return false;
    }

    public void parenting101() {
        //Storing an item in a MapComponent array because i don't know if it actually works
        MapComponent[] yeet = new MapComponent[3];
        yeet[0] = new Item(1);
        System.out.println(yeet[0].getMapComponentID());
        //Hmm can I check if it's an item
        System.out.println(yeet[0].getClass());
    }
}
