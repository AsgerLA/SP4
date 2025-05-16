package Class;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private boolean closeFlag;
    private List<Option> options;
    private String exitTitle;
    private String title;
    protected Scanner sc;

    public Menu(String title, String exitTitle) {
        this.title = title;
        this.exitTitle = exitTitle;
        options = new ArrayList<>();
        closeFlag = false;
        sc = new Scanner(System.in);
    }

    public void addOption(Option opt) {
        options.add(opt);
    }

    public void show() {
        int i;
        while (!closeFlag) {
            System.out.println(title);
            System.out.println("------------------------------");
            for (i = 0; i < options.size(); i++) {
                System.out.println("(" + (i + 1) + ") " + options.get(i).getTitle());
            }
            System.out.println("(" + (i + 1) + ") " + exitTitle);
            System.out.println("------------------------------");
            System.out.print("Enter a number: ");
            int num;
            try {
                num = Integer.decode(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("*** Invalid number");
                continue;
            }
            num--;
            if (num >= 0 && num < options.size()) {
                options.get(num).run(this);
                System.out.println("Press ENTER to continue");
                sc.nextLine();
            } else if (num == options.size()) {
                System.out.println("exiting...");
                closeFlag = true;
            } else {
                System.out.println("Unknown option");
            }
        }
    }
}
