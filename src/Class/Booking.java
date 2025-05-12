package Class;

import java.util.List;
import java.util.Scanner;

public class Booking {
    private List<Suit> suitsList;
    private Customer customer;
    private Scanner sc = new Scanner(System.in);

    public Booking(List<Suit> suitsList, Customer customer) {
        this.suitsList = suitsList;
        this.customer = customer;
    }

    public void startSession() {
        System.out.println("Velkommen " + customer.getName());

        String input;
        while (true) {
            System.out.println("\nStartMenu:");
            System.out.println("1. Ledige værelser");
            System.out.println("2. Book");
            System.out.println("3. Afslut");

            input = sc.nextLine();

            switch (input) {
                case "1":
                    for (Suit suit : suitsList) {
                        if (!suit.isBooked()) {
                            System.out.println(suit);
                        }
                    }
                    break;
                case "2":
                    System.out.println("Booking-funktion kommer snart.");
                    break;
                case "3":
                    System.out.println("Farvel!");
                    return;
                default:
                    System.out.println("Skriv et gyldigt tal.");
                    break;
            }
        }
    }
}
