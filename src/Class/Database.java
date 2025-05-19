package Class;

import java.util.Date;
import java.util.List;

public interface Database {
    boolean close();

    // print table "name" contents
    void printTable(String name);

    boolean addSuite(Suite suite);
    boolean addRoom(Suite suite, Room room);
    List<Room> getSuiteRooms(Suite suite);

    boolean addCustomer(Customer customer);
    Customer getCustomer(String name);
    boolean removeCustomer(Customer customer);

    // Get all available suites from startDate to endDate for at least numPeople
    List<Suite> getAvailSuites(int numPeople, Date startDate, Date endDate);
    boolean bookSuite(Suite suite, Customer customer, int numPeople, Date startDate, Date endDate);
    boolean freeSuite(int bookingID);
    List<Suite> getCustomerSuites(Customer customer);
    List<Bookings> getBookings(Customer customer);
}
