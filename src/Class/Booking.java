package Class;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Enum.*;

public class Booking {
    private Scanner sc;
    private Database db;

    public Booking(Database db) {
        sc = new Scanner(System.in);
        this.db = db;
    }

    public void startSession() {
        int num = 0;
        boolean pause = false;
        boolean quitFlag = false;
        while (!quitFlag) {
            if (pause) {
                System.out.println("Press ENTER to continue");
                sc.nextLine();
                pause = false;
            }
            System.out.println("Main menu");
            System.out.println("------------------------------");
            System.out.println("(1) Book");
            System.out.println("(2) Quit");
            System.out.println("------------------------------");
            System.out.print("Enter a number: ");
            try {
                num = Integer.decode(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("*** Invalid number");
                continue;
            }
            switch (num) {
                case 1: {
                    pause = true;
                    Customer customer;
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    customer = db.getCustomer(name);
                    if (customer == null) {
                        customer = new Customer();
                        customer.setName(name);
                    }
                    System.out.print("Number of guest: ");
                    int numPeople;
                    try {
                        numPeople = Integer.decode(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("*** Invalid number");
                        break;
                    }
                    try {
                        System.out.print("Start date: ");
                        LocalDate startDate = LocalDate.parse(sc.nextLine());
                        customer.setStartDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        System.out.print("End date: ");
                        LocalDate endDate = LocalDate.parse(sc.nextLine());
                        if (endDate.isBefore(startDate))
                            throw new Exception();
                        customer.setEndDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    } catch (Exception e) {
                        System.out.println("*** Invalid date");
                        break;
                    }
                    List<Suite> availSuites = db.getSuites(numPeople,
                            customer.getStartDate(), customer.getEndDate());
                    if (availSuites == null || availSuites.isEmpty()) {
                        System.out.println("No suites available");
                        break;
                    }
                    System.out.println("Available suites:");
                    int i;
                    for (i = 0; i < availSuites.size(); i++) {
                        Suite suite = availSuites.get(i);
                        System.out.println("Suite: " + suite.getSuitID());
                        System.out.println("Suite type: " + suite.getSuitType().toString().toLowerCase());
                        System.out.println("rooms: " + suite.getRooms());
                        System.out.println("price: " + suite.getPrice());
                        System.out.println();
                    }
                    System.out.print("Select suites (e.g. \"1 2\"): ");
                    String[] values = sc.nextLine().split(" ");
                    List<Suite> bookedSuites = new java.util.ArrayList<>();
                    for (String s : values) {
                        try {
                            i = Integer.decode(s)-1;
                        } catch (NumberFormatException e) {
                            System.out.println("*** Invalid number");
                            break;
                        }
                        if (i < 0 || i >= availSuites.size()) {
                            System.out.println("*** Invalid suite: "+(i+1));
                            continue;
                        }
                        bookedSuites.add(availSuites.get(i));
                    }
                    System.out.println("Suites Booked:");
                    double totalPrice = 0;
                    for (Suite suite : bookedSuites) {
                        System.out.println("Suite: " + suite.getSuitID());
                        System.out.println("Suite type: " + suite.getSuitType().toString().toLowerCase());
                        System.out.println("rooms: " + suite.getRooms());
                        System.out.println("price: " + suite.getPrice());
                        System.out.println();
                        totalPrice += suite.getPrice();
                    }
                    System.out.println("Total price: "+totalPrice);
                    System.out.print("Extra service (y/n): ");
                    if (sc.nextLine().equals("y")) {
                        for (i = 0; i < ExtraService.values().length; i++) {
                            System.out.println((i+1)+": "+ExtraService.values()[i].toString()+" (price:"+ExtraService.prices[i]+")");
                        }
                        values = sc.nextLine().split(" ");
                        List<ExtraService> extra = new java.util.ArrayList<>();
                        for (String s : values) {
                            try {
                                i = Integer.decode(s)-1;
                            } catch (NumberFormatException e) {
                                System.out.println("*** Invalid number");
                                break;
                            }
                            if (i < 0 || i >= ExtraService.values().length) {
                                System.out.println("*** Invalid choice: "+(i+1));
                                continue;
                            }
                            extra.add(ExtraService.values()[i]);
                            totalPrice += ExtraService.prices[i];
                        }
                        System.out.println(extra);
                        System.out.println("Total price: "+totalPrice);
                    }
                    customer.setSuits(bookedSuites);
                    System.out.println("Choose payment method:");
                    System.out.println("1. Online");
                    System.out.println("2. In person");
                    String input = sc.nextLine();
                    if (Integer.parseInt(input) == 1) {
                        customer.setPaymentmethod(PaymentMethod.Online);
                    }
                    else if(Integer.parseInt(input) == 2) {
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
                    break;
                }
                case 2:
                    System.out.println("exiting...");
                    quitFlag = true;
                    break;
                default:
                    System.out.println("*** Unknown option");
                    break;
            }
        }
    }
}
