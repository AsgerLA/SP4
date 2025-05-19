package Class;

import java.util.Scanner;

public abstract class Option {
    private String title;

    public Option(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void run(Scanner sc);
}
