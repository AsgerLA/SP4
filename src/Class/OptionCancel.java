package Class;

import java.util.List;

public class OptionCancel extends Option {

    public OptionCancel() {
        super("Cancel");
    }

    public void run(TextUI ui) {

        Customer customer = null;
        String name = ui.readLine("Name: ").trim();
        customer = Hotel.db.getCustomer(name);
        if (customer == null) {
            ui.println("No suites booked");
            return;
        }

        List<Bookings> bookings = Hotel.db.getBookings(customer);
        if (bookings == null || bookings.isEmpty()) {
            ui.println("No suites booked");
            return;
        }
        ui.println("Booked suites:");
        int i;
        for (i = 0; i < bookings.size(); i++) {
            Bookings booking = bookings.get(i);
            ui.println((i+1)+" {");
            ui.println("  Suite    : "+booking.getSuiteID());
            ui.println("  guests   : "+booking.getNumPeople());
            ui.println("  startDate:"+booking.getStartDate());
            ui.println("  endDate  :"+booking.getEndDate());
            ui.println("  price    :"+booking.getPrice());
            ui.println("}");
        }
        String[] values = ui.readLine("Select suites (e.g. \"1 2\"): ").split(" ");
        int cancelCnt = 0;
        for (String s : values) {
            try {
                i = Integer.decode(s)-1;
            } catch (NumberFormatException e) {
                ui.println("*** Invalid number");
                break;
            }
            if (i < 0 || i >= bookings.size()) {
                ui.println("*** Invalid suite: "+(i+1));
                continue;
            }
            Hotel.db.freeSuite(bookings.get(i));
            cancelCnt++;
        }

        if (cancelCnt >= bookings.size())
            Hotel.db.removeCustomer(customer);

    }
}
