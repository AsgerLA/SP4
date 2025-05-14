package Class;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import Enum.PaymentMethod;

public class Booking {
    private List<Suit> suitsList;
    private Customer customer;
    private Scanner sc = new Scanner(System.in);
    private Database db;

    public Booking(Database db, Customer customer) {
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


                    List<Suit> freeSuits = db.getCustomerSuites(null);
                    for (Suit suit : freeSuits) {
                        System.out.println("Suite: " + suit.getSuitID());
                        System.out.println("Suite type: " + suit.getSuitType().toString().toLowerCase());
                        System.out.println("rooms: " + suit.getRooms());
                        System.out.println("price: " + suit.getPrice());
                        System.out.println();
                    }
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
                    customer.setNumPeople(Integer.
                            parseInt(input));

                    System.out.println("name:");
                    input = sc.nextLine();
                    customer.setName(input);

                    System.out.println("number of suits:");
                    input = sc.nextLine();
                    List<Suit> suits = db.getNumSuites(Integer.parseInt(input));
                    customer.setSuits(suits);
                    System.out.println("Choose payment method:");
                    System.out.println("1. Online");
                    System.out.println("2. In person");
                    input = sc.nextLine();
                    if (Integer.parseInt(input) == 1){
                        customer.setPaymentmethod(PaymentMethod.Online);
                    }
                    else if(Integer.parseInt(input) == 2){
                        System.out.println("Will it be:");
                        System.out.println("1. Card");
                        System.out.println("2. Cash");
                        input = sc.nextLine();

                        if(Integer.parseInt(input) == 1){
                            customer.setPaymentmethod(PaymentMethod.Physical_card);

                        } else if (Integer.parseInt(input) == 2){
                            customer.setPaymentmethod(PaymentMethod.Physical_cash);

                        }
                    }
                    db.addCustomer(customer);

                    //System.out.println("Booking-funktion kommer snart.");
                    break;
                case "3":
                    System.out.println("Farvel!");

                    List<Suit> customerSuits = db.getCustomerSuites(customer);
                    for (Suit suit : customerSuits)
                        System.out.println(suit);

                    return;
                default:
                    System.out.println("Skriv et gyldigt tal.");
                    break;
            }
        }
    }
}
