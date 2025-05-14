package Class;

import java.util.List;

public interface Database {
    boolean addSuite(Suit suit);
    boolean addRoom(int suitID, Room room);
    List<Room> getSuiteRooms(int suiteID);
    List<Suit> getNumSuites(int n);
    boolean addCustomer(Customer customer);
    Customer getCustomer(String name);
    List<Suit> getCustomerSuites(Customer customer);
    boolean close();
}
