package Class;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Enum.*;

public class Hotel {
    static Database db;
    static {
        try {
            //db = new DatabaseSQLite("jdbc:sqlite::memory:");
            db = new DatabaseSQLite("jdbc:sqlite:hotel.db");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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
        Suite suiteNrOne = new Suite(1, null, 5000, 2500,extras, SuiteType.Standard);
        Suite suiteNrTwo = new Suite(2, null, 5000, 2500,extras, SuiteType.Luxury);
        suites.add(suiteNrOne);
        suites.add(suiteNrTwo);

        if (db.getSuiteRooms(suiteNrOne).isEmpty()) {
            db.addSuite(suiteNrOne);
            db.addSuite(suiteNrTwo);
            db.addRoom(suiteNrOne, firstRoom);
            db.addRoom(suiteNrOne, secondRoom);
            db.addRoom(suiteNrTwo, thirdRoom);
        }

        Booking booking = new Booking(db);
        booking.startSession();

        db.printTable("Customers");
        db.printTable("Suites");
        db.printTable("Rooms");
        db.printTable("Bookings");

        db.close();

    }

}
