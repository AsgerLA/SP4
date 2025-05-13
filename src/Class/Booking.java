package Class;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Booking {
    private List<Suit> suitsList;
    private Customer customer;
    private Scanner sc = new Scanner(System.in);
    private DatabaseSQLite db;

    public Booking(DatabaseSQLite db, Customer customer) {
        //this.suitsList = suitsList;
        //this.customer = customer;
        this.customer = new Customer();
        this.db = db;
    }

    public void startSession() {

        String input;
        System.out.println("Velkommen");
        while (true) {
            System.out.println("\nStartMenu:");
            System.out.println("1. Ledige værelser");
            System.out.println("2. Book");
            System.out.println("3. Afslut");

            input = sc.nextLine();

            switch (input) {
                case "1":


                    try {
                        List<Suit> freeSuits = db.getCustomerSuits(null);
                        for (Suit suit : freeSuits)
                            System.out.println(suit);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                    //for (Suit suit : suitsList) {
                    //    if (!suit.isBooked()) {
                    //        System.out.println(suit);
                    //    }
                    //}
                    break;
                case "2":

                    System.out.println("start date:");
                    input = sc.nextLine();
                    LocalDate ldate = LocalDate.parse(input);
                    customer.setStartDate(new Date(ldate.getYear(), ldate.getMonthValue(), ldate.getDayOfMonth()));

                    System.out.println("end date:");
                    input = sc.nextLine();
                    ldate = LocalDate.parse(input);
                    customer.setEndDate(new Date(ldate.getYear(), ldate.getMonthValue(), ldate.getDayOfMonth()));
                    System.out.println("number of people:");
                    input = sc.nextLine();
                    customer.setNumPeople(Integer.parseInt(input));

                    System.out.println("name:");
                    input = sc.nextLine();
                    customer.setName(input);

                    System.out.println("number of suits:");
                    input = sc.nextLine();
                    try {
                        List<Suit> suits = db.getNumSuits(Integer.parseInt(input));
                        customer.setSuits(suits);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }

                    try {
                        db.addCustomer(customer);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                    //System.out.println("Booking-funktion kommer snart.");
                    break;
                case "3":
                    System.out.println("Farvel!");

                    try {
                        List<Suit> customerSuits = db.getCustomerSuits(customer);
                        for (Suit suit : customerSuits)
                            System.out.println(suit);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                    return;
                default:
                    System.out.println("Skriv et gyldigt tal.");
                    break;
            }
        }
    }
}
