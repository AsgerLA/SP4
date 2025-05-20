package Class;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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
        //try {
        //    Log.setOutputStream(new PrintStream("hotel.log"));
        //} catch (FileNotFoundException e) {
        //    Log.error("Log.setOutputStream failed - "+e.getMessage());
        //}
        Log.setLogLevel(Log.LOG_LEVEL_WARN);

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

        Suite suiteNrOne = new Suite(1, null, 200, 1.5f, SuiteType.Standard);
        Suite suiteNrTwo = new Suite(2, null, 500, 2.0f, SuiteType.Luxury);
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
        db.printTable("ExtraServices");

        db.close();

    }

}
