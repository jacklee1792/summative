
class Mission {
    // Instance variables
    private String title, text;
    private boolean isComplete;

    // Constructors
    public Mission (String input) throws ArrayIndexOutOfBoundsException {
        String[] tmp = input.split("\n");
        title = tmp[0];
        text = tmp[1];
    }

    // Methods
    public String getText(){ return text; }
    public String getTitle() { return title; }
    public boolean complete() { return isComplete; }
    public void setCompleteness(boolean completeness) { isComplete = completeness; }
}