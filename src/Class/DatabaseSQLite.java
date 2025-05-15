package Class;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Enum.*;

public class DatabaseSQLite implements Database {
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
            CREATE TABLE IF NOT EXISTS Suites (suiteID INTEGER PRIMARY KEY AUTOINCREMENT, price DOUBLE, holidayFactor FLOAT, type INTEGER, hashName INTEGER);
            """, """
            CREATE TABLE IF NOT EXISTS Rooms (suiteID INTEGER KEY, maxPeople INTEGER);
            """, """
            CREATE TABLE IF NOT EXISTS Customers (hashName INTEGER PRIMARY KEY, name TEXT, paymentMethod INTEGER, numPeople INTEGER, startDate LONG, endDate LONG);
            """};
            Statement stmt = conn.createStatement();
            for (String sql : statements)
                stmt.execute(sql);
        } catch (SQLException e) {
            throw new IOException("DatabaseSQLite() "+e.getMessage());
        }
    }

    public boolean close() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("close() "+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addSuite(Suit suit) {
        String sql = "INSERT INTO Suites VALUES(null, "+suit.getPrice()+", "+suit.getHolidayFactor()+", "+suit.getSuitType().ordinal()+", "+0+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            List<Room> rooms = suit.getRooms();
            if (rooms != null) {
                for (Room room : rooms)
                    this.addRoom(suit.getSuitID(), room);
            }
        } catch (SQLException e) {
            System.err.println("addSuit() "+e.getMessage());
            return false;
        }
        return true;
    }

    public List<Suit> getSuites(int minPeople, Date startDate, Date endDate) {
        System.out.println("TODO: endDate");
        StringBuilder sb = new StringBuilder();
        sb.append("""
        SELECT Suites.suiteID, Suites.price, Suites.holidayFactor, Suites.type,
        COUNT(Rooms.suiteID) roomCnt, SUM(Rooms.maxPeople) AS maxPeopleSum
        FROM Suites
        LEFT JOIN Rooms ON Suites.suiteID=Rooms.suiteID
        LEFT JOIN Customers ON Suites.hashName=Customers.hashName
        WHERE Suites.hashName=0 OR Customers.endDate < """);
        sb.append(startDate.toInstant().getEpochSecond());
        sb.append(" GROUP BY Suites.suiteID HAVING maxPeopleSum >= ");
        sb.append(minPeople);
        String sql = sb.toString();
        List<Suit> suits = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suit suit = new Suit(rs.getInt("suiteID"), false, null, rs.getDouble("price"), rs.getFloat("holidayFactor"), null, false, SuitType.values()[rs.getInt("type")]);
                suit.setRooms(this.getSuiteRooms(suit.getSuitID()));
                suits.add(suit);
            }
        } catch (SQLException e) {
            System.err.println("getSuites() " + e.getMessage());
            return null;
        }
        return suits;
    }

    public List<Suit> getNumSuites(int n) {
        String sql = "SELECT * FROM Suites WHERE hashName = 0 LIMIT "+n;
        List<Suit> suits = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suit suit = new Suit(rs.getInt("suiteID"), false, null, rs.getDouble("price"), rs.getFloat("holidayFactor"), null, false, SuitType.values()[rs.getInt("type")]);
                suits.add(suit);
            }
        } catch (SQLException e) {
            System.err.println("getCustomerSuites() " + e.getMessage());
            return null;
        }
        return suits;
    }


    public List<Suit> getCustomerSuites(String name) {
        long hashName = 0;
        if (name != null)
            hashName = hashName(name);
        String sql = "SELECT * FROM Suites WHERE hashName = "+hashName;
        List<Suit> suits = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suit suit = new Suit(rs.getInt("suiteID"), false, null, rs.getDouble("price"), rs.getFloat("holidayFactor"), null, false, SuitType.values()[rs.getInt("type")]);
                suits.add(suit);
            }
        } catch (SQLException e) {
            System.err.println("getCustomerSuites() " + e.getMessage());
            return null;
        }
        return suits;
    }

    public boolean addRoom(int suiteID, Room room) {
        String sql = "INSERT INTO Rooms VALUES("+suiteID+", "+room.getMaxPeople()+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("addRoom() "+e.getMessage());
            return false;
        }
        return true;
    }

    public List<Room> getRooms() {
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
            System.err.println("getRooms() "+e.getMessage());
            return null;
        }
        return rooms;
    }

    public List<Room> getSuiteRooms(int suiteID) {
        String sql = "SELECT * FROM Rooms WHERE suiteID = "+suiteID;
        List<Room> rooms = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Room room = new Room(rs.getInt("maxPeople"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("getSuiteRooms() "+e.getMessage());
            return null;
        }
        return rooms;
    }

    public boolean addCustomer(Customer customer) {

        long hashName = hashName(customer.getName());
        if (hashName == 0) // reserved to mark suite as free
            return false;
        
        String sql = "INSERT INTO Customers VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Statement stmt = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setLong(i++, hashName);
            ps.setString(i++, customer.getName());
            ps.setInt(i++, customer.getPaymentmethod().ordinal());
            ps.setInt(i++, customer.getNumPeople());
            ps.setLong(i++, customer.getStartDate().toInstant().getEpochSecond());
            ps.setLong(i, customer.getEndDate().toInstant().getEpochSecond());
            ps.executeUpdate();

            for (Suit suit : customer.getSuits()) {
                stmt.execute("UPDATE Suites SET hashName = " + hashName + " WHERE suiteID = " + suit.getSuitID());
            }

        } catch (SQLException e) {
            System.err.println("addCustomer() "+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean removeCustomer(String name) {
        long hashName = hashName(name);
        String sql = "DELETE FROM Customers WHERE hashName = "+hashName;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

            stmt.execute("UPDATE Suites SET hashName = 0 WHERE hashName = " + hashName);
        } catch (SQLException e) {
            System.err.println("removeCustomer() "+e.getMessage());
            return false;
        }
        return true;
    }

    public Customer getCustomer(String name) {
        long hashName = hashName(name);
        String sql = "SELECT * FROM Customers WHERE hashName = "+hashName;
        Customer customer = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                customer = new Customer(rs.getString("name"), PaymentMethod.values()[rs.getInt("paymentMethod")], null, rs.getInt("numPeople"), new Date(rs.getLong("startDate")), new Date(rs.getLong("endDate")));
                //List<Suit> suits = this.getCustomerSuites(customer);
                //customer.setSuits(suits);
            }
        } catch (SQLException e) {
            System.err.println("getCustomer() "+e.getMessage());
            return null;
        }
        return customer;
    }

    public static void main(String[] args) {

        Database db = null;
        try {
            db = new DatabaseSQLite("jdbc:sqlite::memory:");
            //db = new DatabaseSQLite("jdbc:sqlite:test.db");
        } catch (IOException e) {
            System.exit(-1);
        }

        List<Room> rooms = new java.util.ArrayList<>();
        rooms.add(new Room(2));
        db.addSuite(new Suit(1, false, rooms, 1000, 1500, null, false, SuitType.Standard));
        db.addSuite(new Suit(2, false, null, 1200, 1600, null, false, SuitType.Standard));
        db.addSuite(new Suit(3, false, null, 1200, 1600, null, false, SuitType.Luxury));

        //db.addRoom(1, new Room(2));
        db.addRoom(2, new Room(3));
        db.addRoom(3, new Room(2));
        db.addRoom(3, new Room(2));

        /*
        List<Suit> availSuites = db.getCustomerSuites(null);
        for (Suit suit : availSuites) {
            System.out.println(suit);
        }

        availSuites = db.getNumSuites(2);
        Customer customer = new Customer("Jack", PaymentMethod.Online,availSuites,7,new java.util.Date(2025, 6, 3),new Date(2025,6,13));
        db.addCustomer(customer);

        availSuites = db.getNumSuites(2);
        db.addCustomer(new Customer("Ole", PaymentMethod.Online,availSuites,7,new java.util.Date(2025, 6, 3),new Date(2025,6,13)));
        if (!db.getCustomerSuites(null).isEmpty())
            System.err.println("suites available");

        customer = db.getCustomer("Jack");
        List<Suit> bookedSuites = db.getCustomerSuites(customer.getName());
        customer.setSuits(bookedSuites);
        System.out.println(customer);
        for (Suit suit : bookedSuites) {
            System.out.println(suit);
            for (Room room : db.getSuiteRooms(suit.getSuitID()))
                System.out.println(room);
        }
        db.removeCustomer("Ole");
        System.out.println("getSuites");
        List<Suit> suites = db.getSuites(3);
        for (Suit suite : suites)
            System.out.println(suite);
         */
        db.close();
    }


}
