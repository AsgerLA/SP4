package Class;

import java.util.Scanner;

public class TextUI {
    private Scanner scanner;

    public TextUI() {
        scanner = new Scanner(System.in);
    }


    public void println(String message) {
        System.out.println(message);
    }

    public void print(String message) {
        System.out.print(message);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }


    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }


    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Please write 'yes' or 'no'.");
            }
        }
    }


    public void close() {
        scanner.close();
    }
}
