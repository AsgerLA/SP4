package Class;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Enum.*;

public class DatabaseSQLite {
    private final Connection conn;

    // fnv64a
    private long hashName(String name) {
        final long FNV_64_offset_basis = 0xcbf29ce484222325L;
        final long FNV_64_prime = 0x100000001b3L;
        long hashName = FNV_64_offset_basis;
        for (char c : name.toCharArray()) {
            hashName ^= c;
            hashName *= FNV_64_prime;
        }
        return hashName;
    }
    public DatabaseSQLite(String url) throws IOException {
        try {
            conn = DriverManager.getConnection(url);

            String[] statements = new String[] {"""
            CREATE TABLE IF NOT EXISTS Suits (suitNum INTEGER PRIMARY KEY AUTOINCREMENT, price DOUBLE, holidayFactor FLOAT, type INTEGER, hashName INTEGER);
            """, """
            CREATE TABLE IF NOT EXISTS Rooms (suitNum INTEGER KEY, maxPeople INTEGER);
            """, """
            CREATE TABLE IF NOT EXISTS Customers (hashName INTEGER PRIMARY KEY, name TEXT, numPeople INTEGER, startDate LONG, endDate LONG);
            """};
            Statement stmt = conn.createStatement();
            for (String sql : statements)
                stmt.execute(sql);
        } catch (SQLException e) {
            throw new IOException("DatabaseSQLite() "+e.getMessage());
        }
    }

    public void addSuit(Suit suit) throws IOException {
        String sql = "INSERT INTO Suits VALUES(null, "+suit.getPrice()+", "+suit.getHolidayFactor()+", "+suit.getSuitType().ordinal()+", "+0+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new IOException("addSuit() "+e.getMessage());
        }
    }

    public List<Suit> getCustomerSuits(Customer customer) throws IOException {
        long hashName = hashName(customer.getName());
        String sql = "SELECT * FROM Suits WHERE hashName = "+hashName;
        List<Suit> suits = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suit suit = new Suit(rs.getInt("suitNum"), false, null, rs.getDouble("price"), rs.getFloat("holidayFactor"), null, false, SuitType.Standard);
                suits.add(suit);
            }
        } catch (SQLException e) {
            throw new IOException("getCustomerSuits() " + e.getMessage());
        }
        return suits;
    }

    public void addRoom(int suitNum, Room room) throws IOException {
        String sql = "INSERT INTO Rooms VALUES("+suitNum+", "+room.getMaxPeople()+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new IOException("addRoom() "+e.getMessage());
        }
    }

    public List<Room> getRooms() throws IOException {
        String sql = "SELECT * FROM Rooms";
        List<Room> rooms = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Room room = new Room(rs.getInt("maxPeople"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new IOException("getRooms() "+e.getMessage());
        }
        return rooms;
    }

    public List<Room> getRoomsInSuit(int suitNum) throws IOException {
        String sql = "SELECT * FROM Rooms WHERE suitNum = "+suitNum;
        List<Room> rooms = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Room room = new Room(rs.getInt("maxPeople"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new IOException("getRooms() "+e.getMessage());
        }
        return rooms;
    }

    public boolean addCustomer(Customer customer) throws IOException {

        long hashName = hashName(customer.getName());
        if (hashName == 0) // reserved to free mark suit as free
            return false;
        
        String sql = "INSERT INTO Customers VALUES (?, ?, ?, ?, ?)";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT hashName FROM Customers WHERE hashName = "+hashName);
            if (rs.next())
                return false;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, hashName);
            ps.setString(2, customer.getName());
            ps.setInt(3, customer.getNumPeople());
            ps.setLong(4, customer.getStartDate().toInstant().getEpochSecond());
            ps.setLong(5, customer.getEndDate().toInstant().getEpochSecond());
            ps.executeUpdate();

            for (Suit suit : customer.getSuits()) {
                stmt.execute("UPDATE Suits SET hashName = " + hashName + " WHERE suitNum = " + suit.getSuitID());
            }

        } catch (SQLException e) {
            throw new IOException("addCustomer() "+e.getMessage());
        }
        return true;
    }

    public static void main(String[] args) {
        DatabaseSQLite db;
        try {
            //db = new DatabaseSQLite("jdbc:sqlite:test.db");
            db = new DatabaseSQLite("jdbc:sqlite::memory:");


            // Creating rooms
            Room firstRoom = new Room(3);
            Room secondRoom = new Room(3);
            Room thirdRoom = new Room(3);

            // Creating an collection of rooms
            List<Room> rooms = new ArrayList<>();
            List<Room> rooms2 = new ArrayList<>();

            // Adding rooms to the collection
            rooms.add(firstRoom);
            rooms.add(secondRoom);
            rooms2.add(thirdRoom);

            List<ExtraService> extras = new ArrayList<>();
            extras.add(ExtraService.Breakfast);
            Suit suitNrOne = new Suit(1,false, rooms, 5000, 2500,extras,true, SuitType.Standard);
            Suit suitNrTwo = new Suit(2,false, rooms2, 5000, 2500,extras,true, SuitType.Standard);

            List<Suit> suits = new ArrayList<>();
            suits.add(suitNrOne);
            suits.add(suitNrTwo);


            Customer customer = new Customer("Jack", PaymentMethod.Online,suits,7,new java.util.Date(2025, 6, 3),new Date(2025,6,13));

            db.addSuit(suitNrOne);
            db.addSuit(suitNrTwo);
            db.addRoom(suitNrOne.getSuitID(), firstRoom);
            db.addRoom(suitNrOne.getSuitID(), secondRoom);
            db.addRoom(suitNrTwo.getSuitID(), thirdRoom);
            db.addCustomer(customer);
            List<Suit> customerSuits = db.getCustomerSuits(customer);
            for (Suit suit : customerSuits)
                System.out.println(suit);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
