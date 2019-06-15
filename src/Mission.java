

class Mission {
    // Instance variables
    private String title, text;
    private boolean isComplete;
    static final String LINE_SEPARATOR = " !!! ";


    // Constructors
    public Mission (String input) {
        String[] tmp = input.split(LINE_SEPARATOR);
        try {
            title = tmp[0];
            text = tmp[1];
        }
        catch(Exception e){
            text = tmp[0];
            title = "";
        }
    }

    // Methods
    public String getText(){ return text; }
    public String getTitle() { return title; }
    @Override
    public String toString(){
        return title + LINE_SEPARATOR + text;
    }
    public boolean getCompleteness() { return isComplete; }
    public void setCompleteness(boolean completeness) { isComplete = completeness; }
}