package Class;

public abstract class Option {
    private String title;

    public Option(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void run(Menu menu);
}
