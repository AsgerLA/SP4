package Class;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Enum.PaymentMethod;

public class OptionBook extends Option {
    public OptionBook() {
        super("Book");
    }

    public void run(Scanner sc) {
        boolean newCustomer = false;
        Customer customer = null;
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        customer = Hotel.db.getCustomer(name);
        if (customer == null) {
            newCustomer = true;
            customer = new Customer();
            customer.setName(name);
        }

        System.out.print("Number of guest: ");
        int numPeople;
        try {
            numPeople = Integer.decode(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("*** Invalid number");
            return;
        }

        Date startDate, endDate;
        int numDays;
        try {
            System.out.print("Start date (yyyy-mm-dd): ");
            LocalDate lstartDate = LocalDate.parse(sc.nextLine());
            System.out.print("End date (yyyy-mm-dd): ");
            LocalDate lendDate = LocalDate.parse(sc.nextLine());
            if (lendDate.isBefore(lstartDate))
                throw new Exception();
            startDate = Date.from(lstartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            endDate = Date.from(lendDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            numDays = (int)Math.abs(ChronoUnit.DAYS.between(lstartDate, lendDate)) + 1;
        } catch (Exception e) {
            System.out.println("*** Invalid date");
            return;
        }

        List<Suite> availSuites = Hotel.db.getAvailSuites(numPeople, startDate, endDate);
        if (availSuites == null || availSuites.isEmpty()) {
            System.out.println("No suites available");
            return;
        }
        System.out.println("Available suites:");
        int i;
        for (i = 0; i < availSuites.size(); i++) {
            Suite suite = availSuites.get(i);
            System.out.println((i+1)+" {");
            System.out.println("  Suite: " + suite.getSuiteID());
            System.out.println("  Suite type: " + suite.getSuitType().toString().toLowerCase());
            System.out.println("  rooms: " + suite.getRooms());
            System.out.println("  price: " + suite.getPrice());
            System.out.println("}");
        }
        System.out.print("Select suites (e.g. \"1 2\"): ");
        String[] values = sc.nextLine().split(" ");
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
            Hotel.db.bookSuite(availSuites.get(i), customer, numPeople, startDate, endDate);
        }
        double totalPrice = 0;
        List<Suite> bookedSuites = Hotel.db.getCustomerSuites(customer);
        if (bookedSuites == null)
            return;
        System.out.println("Suites Booked for "+numDays+" days:");
        for (Suite suite : bookedSuites) {
            System.out.println("Suite: " + suite.getSuiteID());
            System.out.println("  Suite type: " + suite.getSuitType().toString().toLowerCase());
            System.out.println("  rooms: " + suite.getRooms());
            System.out.println("  price: " + suite.getPrice());
            System.out.println();
            totalPrice += suite.getPrice()*numDays;
        }
        System.out.println("Total price: "+totalPrice);
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
        if (newCustomer && !bookedSuites.isEmpty())
            Hotel.db.addCustomer(customer);
    }
}
