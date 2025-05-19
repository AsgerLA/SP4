package Class;

import java.util.List;
import java.util.Scanner;

public class OptionCancel extends Option {

    public OptionCancel() {
        super("Cancel");
    }

    public void run(Scanner sc) {

        Customer customer = null;
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        customer = Hotel.db.getCustomer(name);
        if (customer == null) {
            System.out.println("No suites booked");
            return;
        }

        List<Bookings> bookings = Hotel.db.getBookings(customer);
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No suites booked");
            return;
        }
        System.out.println("Booked suites:");
        int i;
        for (i = 0; i < bookings.size(); i++) {
            Bookings booking = bookings.get(i);
            System.out.println((i+1)+" {");
            System.out.println("  Suite    : "+booking.getSuiteID());
            System.out.println("  guests   : "+booking.getNumPeople());
            System.out.println("  startDate:"+booking.getStartDate());
            System.out.println("  endDate  :"+booking.getEndDate());
            System.out.println("  price    :"+booking.getPrice());
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
            if (i < 0 || i >= bookings.size()) {
                System.out.println("*** Invalid suite: "+(i+1));
                continue;
            }
            Hotel.db.freeSuite(bookings.get(i).getBookingID());
        }

    }
}
