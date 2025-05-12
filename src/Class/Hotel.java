package Class;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Enum.*;
import org.w3c.dom.ls.LSOutput;

public class Hotel {
    public static void main(String[] args) {
        List<Suit> suits = new ArrayList<>();

        // Creating rooms
        Room firstRoom = new Room(3);
        Room secondRoom = new Room(3);
        //Room thirdRoom = new Room(3);

        // Creating an collection of rooms
        List<Room> rooms = new ArrayList<>();

        // Adding rooms to the collection
        rooms.add(firstRoom);
        rooms.add(secondRoom);
        //rooms.add(thirdRoom);
        rooms.add(new Room(3));

        List<ExtraService> extras = new ArrayList<>();
        extras.add(ExtraService.Breakfast);
        Suit suitNrOne = new Suit(1,false, rooms, 5000, 2500,extras,true, SuitType.Standard);
        Suit suitNrTwo = new Suit(1,false, rooms, 5000, 2500,extras,true, SuitType.Standard);
        suits.add(suitNrOne);
        suits.add(suitNrTwo);

        //System.out.println(suits);
        for (Suit s: suits){
            System.out.println(s);
        }

        Customer customer = new Customer("Jack", PaymentMethod.Online,suits,7,new Date(2025, 6, 3),new Date(2025,6,13));
        System.out.println(customer);

        Booking booking = new Booking(suits, customer);
        booking.startSession();
    }

}
