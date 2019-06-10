import java.io.File;

class Mission {
    // Instance variables
    private int currentStage;
    private ArrayList<String> text;
    private boolean ArrayList<Boolean> isComplete;

    // Constructors
    public Mission (File loadFile) throws IOException {
        FileReader fr = new FileReader(loadFile);
        BufferedReader br = new BufferedReader(fr);
        String line = " ";

        while(line != null){
            if(line == null)
                break;
            line = br.readLine();
            text.add(line);
        }
    }

    // Methods
    public String runCurrentStage(){
        return text.get(currentStage);
    }
    public int getStage() { return currentStage; }
    public void setStage(int newStage){
        if(newStage < text.size())
            currentStage = newStage;
    }
}