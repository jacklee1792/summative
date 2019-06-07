//Use this for testing things

public class Test {
    public static void main(String[] args) {
        //Storing an item in a MapComponent array because i don't know if it actually works
        MapComponent[] yeet = new MapComponent[3];
        yeet[0] = new Item(1);
        System.out.println(yeet[0].getMapComponentID());
        //Hmm can I check if it's an item
        System.out.println(yeet[0].getClass());
    }
}
