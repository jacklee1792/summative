

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
        catch(ArrayIndexOutOfBoundsException e){
            title = tmp[0];
            text = "";
        }
    }

    // Methods
    public String getText(){ return text; }
    public String getTitle() { return title; }
    public boolean complete() { return isComplete; }
    public void setCompleteness(boolean completeness) { isComplete = completeness; }
}