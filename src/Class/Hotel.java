package Class;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Enum.*;

public class Hotel {
    public static void main(String[] args) {
        List<Suite> suites = new ArrayList<>();

        // Creating rooms
        Room firstRoom = new Room(3);
        Room secondRoom = new Room(3);
        Room thirdRoom = new Room(3);

        // Creating an collection of rooms
        List<Room> rooms = new ArrayList<>();

        // Adding rooms to the collection
        rooms.add(firstRoom);
        rooms.add(secondRoom);
        //rooms.add(thirdRoom);
        rooms.add(new Room(3));

        List<ExtraService> extras = new ArrayList<>();
        extras.add(ExtraService.Breakfast);
        Suite suiteNrOne = new Suite(1,false, rooms, 5000, 2500,extras,true, SuiteType.Standard);
        Suite suiteNrTwo = new Suite(2,false, rooms, 5000, 2500,extras,true, SuiteType.Luxury);
        suites.add(suiteNrOne);
        suites.add(suiteNrTwo);

        //System.out.println(suits);
        //for (Suit s: suits){
        //    System.out.println(s);
        //}

        //Customer customer = new Customer("Jack", PaymentMethod.Online,suits,7,new// Date(2025, 6, 3),new Date(2025,6,13));
        //System.out.println(customer);
        Database db = null;
        try {
            db = new DatabaseSQLite("jdbc:sqlite::memory:");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        db.addSuite(suiteNrOne);
        db.addSuite(suiteNrTwo);
        db.addRoom(suiteNrOne.getSuitID(), firstRoom);
        db.addRoom(suiteNrOne.getSuitID(), secondRoom);
        db.addRoom(suiteNrTwo.getSuitID(), thirdRoom);

        Booking booking = new Booking(db);
        booking.startSession();
        db.close();

    }

}
