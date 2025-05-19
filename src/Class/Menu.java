package Class;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private boolean closeFlag;
    private boolean pauseFlag;
    private List<Option> options;
    private String exitTitle;
    private String title;
    protected TextUI ui;

    public Menu(String title, String exitTitle) {
        this.title = title;
        this.exitTitle = exitTitle;
        options = new ArrayList<>();
        closeFlag = false;
        pauseFlag = false;
        ui = new TextUI();
    }

    public void addOption(Option opt) {
        options.add(opt);
    }

    public void show() {
        int i;
        while (!closeFlag) {
            if (pauseFlag) {
                ui.println("Press ENTER to continue");
                ui.readLine("");
                pauseFlag = false;
            }
            ui.println(title);
            ui.println("------------------------------");
            for (i = 0; i < options.size(); i++) {
                ui.println("(" + (i + 1) + ") " + options.get(i).getTitle());
            }
            ui.println("(" + (i + 1) + ") " + exitTitle);
            ui.println("------------------------------");
            int num = ui.readInt("Enter a number: ");
            num--;
            if (num >= 0 && num < options.size()) {
                options.get(num).run(ui);
                pauseFlag = true;
            } else if (num == options.size()) {
                ui.println("exiting...");
                closeFlag = true;
            } else {
                ui.println("Unknown option");
                pauseFlag = true;
            }
        }
    }
}
