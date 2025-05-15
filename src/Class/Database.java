package Class;

import java.util.Date;
import java.util.List;

public interface Database {
    boolean addSuite(Suite suite);
    List<Suite> getSuites(int minPeople, Date startDate, Date endDate);
    boolean addRoom(int suitID, Room room);
    List<Room> getSuiteRooms(int suiteID);
    List<Suite> getNumSuites(int n);
    boolean addCustomer(Customer customer);
    boolean removeCustomer(String name);
    Customer getCustomer(String name);
    List<Suite> getCustomerSuites(String name);
    boolean close();
}
