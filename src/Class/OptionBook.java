package Class;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import Enum.PaymentMethod;
import Enum.ExtraService;

public class OptionBook extends Option {
    public OptionBook() {
        super("Book");
    }

    public void run(TextUI ui) {
        boolean newCustomer = false;
        Customer customer = null;
        String name = ui.readLine("Name: ").trim();
        customer = Hotel.db.getCustomer(name);
        if (customer == null) {
            newCustomer = true;
            customer = new Customer(name);
        }

        int numPeople = ui.readInt("Number of guest: ");

        Date startDate, endDate;
        int numDays;
        try {
            LocalDate lstartDate = LocalDate.parse(ui.readLine("Start date (yyyy-mm-dd): "));
            LocalDate lendDate = LocalDate.parse(ui.readLine("End date (yyyy-mm-dd): "));
            if (lendDate.isBefore(lstartDate))
                throw new Exception();
            startDate = Date.from(lstartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            endDate = Date.from(lendDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            numDays = (int)Math.abs(ChronoUnit.DAYS.between(lstartDate, lendDate)) + 1;
        } catch (Exception e) {
            ui.println("*** Invalid date");
            return;
        }

        List<Suite> availSuites = Hotel.db.getAvailSuites(numPeople, startDate, endDate);
        if (availSuites == null || availSuites.isEmpty()) {
            ui.println("No suites available");
            return;
        }
        ui.println("Available suites:");
        int i;
        for (i = 0; i < availSuites.size(); i++) {
            Suite suite = availSuites.get(i);
            ui.println((i+1)+" {");
            ui.println("  Suite: " + suite.getSuiteID());
            ui.println("  Suite type: " + suite.getSuitType().toString().toLowerCase());
            ui.println("  rooms: " + suite.getRooms());
            ui.println("  price: " + suite.getPrice());
            ui.println("}");
        }
        String[] values = ui.readLine("Select suites (e.g. \"1 2\"): ").split(" ");
        for (String s : values) {
            try {
                i = Integer.decode(s)-1;
            } catch (NumberFormatException e) {
                ui.println("*** Invalid number");
                break;
            }
            if (i < 0 || i >= availSuites.size()) {
                ui.println("*** Invalid suite: "+(i+1));
                continue;
            }
            Hotel.db.bookSuite(availSuites.get(i), customer, numPeople, startDate, endDate);
        }
        double totalPrice = 0;
        List<Suite> bookedSuites = Hotel.db.getCustomerSuites(customer);
        List<Bookings> bookings = Hotel.db.getBookings(customer);
        if (bookedSuites == null || bookings == null)
            return;
        if (bookedSuites.size() != bookings.size()) {
            Log.error("bookedSuites.size() != bookings.size()");
            return;
        }
        ui.println("Suites Booked for "+numDays+" days:");
        for (i = 0; i < bookings.size(); i++) {
            Bookings booking = bookings.get(i);
            Suite suite = bookedSuites.get(i);
            if (booking.getSuiteID() != suite.getSuiteID()) {
                Log.error("booking.getSuiteID() != suite.getSuiteID()");
                continue;
            }
            ui.println(booking.getBookingID()+" {");
            ui.println("  Suite: " + suite.getSuiteID());
            ui.println("  Suite type: " + suite.getSuitType().toString().toLowerCase());
            ui.println("  rooms: " + suite.getRooms());
            ui.println("  price: " + booking.getPrice());
            ui.println("}");
            totalPrice += booking.getPrice();
            if (ui.readYesNo("Extra service")) {
                for (ExtraService extra : ExtraService.values()) {
                    ui.println((extra.ordinal()+1)+" {");
                    ui.println("  "+extra);
                    ui.println("  price: "+extra.getPrice());
                    ui.println("}");
                }
                String[] valuesExtra = ui.readLine("Select extras (e.g. \"1 2\"): ").split(" ");
                for (String str : valuesExtra) {
                    try {
                        i = Integer.decode(str)-1;
                    } catch (NumberFormatException e) {
                        ui.println("*** Invalid number");
                        break;
                    }
                    if (i < 0 || i >= ExtraService.values().length) {
                        ui.println("*** Invalid extra: "+(i+1));
                        continue;
                    }
                    Hotel.db.bookExtraService(ExtraService.values()[i], booking);
                    totalPrice += ExtraService.prices[i]*numDays;
                }
            }
        }
        ui.println("Total price: "+totalPrice);
        if (newCustomer) {
            ui.println("1. Online");
            ui.println("2. In person");
            i = ui.readInt("Choose payment method: ");
            if (i == 1) {
                customer.setPaymentmethod(PaymentMethod.Online);
            } else if (i == 2) {
                ui.println("Will it be:");
                ui.println("1. Card");
                ui.println("2. Cash");
                i = ui.readInt("");

                if (i == 1) {
                    customer.setPaymentmethod(PaymentMethod.Physical_card);
                } else if (i == 2) {
                    customer.setPaymentmethod(PaymentMethod.Physical_cash);
                } else {
                    ui.println("*** Invalid payment method");
                    return;
                }
            } else {
                ui.println("*** Invalid payment method");
                return;
            }
        }
        if (newCustomer && !bookedSuites.isEmpty())
            Hotel.db.addCustomer(customer);
    }
}
