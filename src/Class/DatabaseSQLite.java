package Class;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS Customers (hashName INTEGER PRIMARY KEY UNIQUE, name TEXT, paymentMethod INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Bookings (bookingID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, hashName INTEGER REFERENCES Customers (hashName), suiteID INTEGER REFERENCES Suites (suiteID), numPeople INTEGER, startDate INTEGER, endDate INTEGER, price DOUBLE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Suites (suiteID INTEGER PRIMARY KEY UNIQUE, price DOUBLE, holidayFactor FLOAT, suiteType INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Rooms (suiteID INTEGER REFERENCES Suites (suiteID), maxPeople INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS ExtraServices (serviceID INTEGER, bookingID INTEGER REFERENCES Bookings (BookingID))");

        } catch (SQLException e) {
            throw new IOException("DatabaseSQLite() "+e.getMessage());
        }
    }

    public boolean close() {
        try {
            conn.close();
        } catch (SQLException e) {
            Log.error("close() "+e.getMessage());
            return false;
        }
        return true;
    }

    public void printTable(String name) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+name);
            ResultSetMetaData rsmd = rs.getMetaData();

            int cnt = rsmd.getColumnCount();
            Log.debug(name);
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1 ; i <= cnt; i++)
                    sb.append(rs.getString(i) + " | ");
                Log.debug(sb.toString());
            }

        } catch (SQLException e) {
            Log.error("printTable() "+e.getMessage());
        }
    }

    public boolean addSuite(Suite suite) {
        String sql = "INSERT INTO Suites VALUES("+suite.getSuiteID()+", "+suite.getPrice()+", "+suite.getHolidayFactor()+", "+suite.getSuitType().ordinal()+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            List<Room> rooms = suite.getRooms();
            if (rooms != null) {
                for (Room room : rooms)
                    this.addRoom(suite, room);
            }
        } catch (SQLException e) {
            Log.error("addSuit() "+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addRoom(Suite suite, Room room) {
        String sql = "INSERT INTO Rooms VALUES("+suite.getSuiteID()+", "+room.getMaxPeople()+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            Log.error("addRoom() "+e.getMessage());
            return false;
        }
        return true;
    }

    public List<Room> getSuiteRooms(Suite suite) {
        String sql = "SELECT * FROM Rooms WHERE suiteID = "+suite.getSuiteID();
        List<Room> rooms = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Room room = new Room(rs.getInt("maxPeople"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            Log.error("getSuiteRooms() "+e.getMessage());
            return null;
        }
        return rooms;
    }

    public boolean addCustomer(Customer customer) {

        long hashName = hashName(customer.getName());

        String sql = "INSERT INTO Customers VALUES (?, ?, ?)";
        try {
            Statement stmt = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setLong(i++, hashName);
            ps.setString(i++, customer.getName());
            ps.setInt(i, customer.getPaymentmethod().ordinal());
            ps.executeUpdate();

        } catch (SQLException e) {
            Log.error("addCustomer() "+e.getMessage());
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
                customer = new Customer();
                customer.setName(rs.getString("name"));
                customer.setPaymentmethod(PaymentMethod.values()[rs.getInt("paymentMethod")]);
            }
        } catch (SQLException e) {
            Log.error("getCustomer() "+e.getMessage());
            return null;
        }
        return customer;
    }

    public boolean removeCustomer(Customer customer) {
        long hashName = hashName(customer.getName());
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM Customers WHERE hashName = "+hashName);
            stmt.execute("DELETE FROM Bookings WHERE hashName = "+hashName);
        } catch (SQLException e) {
            Log.error("removeCustomer() "+e.getMessage());
            return false;
        }
        return true;
    }

    public List<Suite> getAvailSuites(int numPeople, Date startDate, Date endDate) {

        long startDateEpoch = startDate.toInstant().getEpochSecond();
        long endDateEpoch = endDate.toInstant().getEpochSecond();
        if (endDateEpoch < startDateEpoch) {
            Log.error("getAvailSuites(): endDate < startDate");
            return null;
        }
        String sql = new StringBuilder().append("""
        SELECT Suites.suiteID, Suites.price, Suites.holidayFactor, Suites.suiteType,
                SUM(Rooms.maxPeople) AS maxPeopleSum
        FROM Suites
        LEFT JOIN Rooms ON Suites.suiteID=Rooms.suiteID
        LEFT JOIN Bookings ON Suites.suiteID=Bookings.suiteID
        WHERE Bookings.bookingID is null OR NOT (
        """)
                .append(startDateEpoch)
                .append("""
        <= Bookings.endDate AND
        """)
                .append(endDateEpoch)
                .append("""
        >= Bookings.startDate)
        GROUP BY Suites.suiteID HAVING maxPeopleSum >=
        """).append(numPeople).toString();
        List<Suite> suites = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suite suite = new Suite(rs.getInt("suiteID"), null, rs.getDouble("price"), rs.getFloat("holidayFactor"), SuiteType.values()[rs.getInt("suiteType")]);
                suites.add(suite);
            }
        } catch (SQLException e) {
            Log.error("getAvailSuites() " + e.getMessage());
            return null;
        }

        return suites;
    }

    public boolean bookSuite(Suite suite, Customer customer, int numPeople, Date startDate, Date endDate) {
        long hashName = 0;
        long startDateEpoch = startDate.toInstant().getEpochSecond();
        long endDateEpoch = endDate.toInstant().getEpochSecond();
        if (endDateEpoch < startDateEpoch) {
            Log.error("bookSuite(): endDate < startDate");
            return false;
        }
        int numDays = (int)Math.abs(ChronoUnit.DAYS.between(
                LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault()),
                LocalDate.ofInstant(endDate.toInstant(), ZoneId.systemDefault()))) + 1;
        hashName = hashName(customer.getName());
        String sql = "INSERT INTO Bookings VALUES (null, "+hashName+", "+suite.getSuiteID()+", "+numPeople+", "+startDateEpoch+", "+endDateEpoch+", "+(suite.getPrice()*numDays)+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            Log.error("bookSuite() " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean freeSuite(Bookings booking) {
        String sql = "DELETE FROM Bookings WHERE bookingID="+booking.getBookingID();
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            Log.error("freeSuite() " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean bookExtraService(ExtraService extra, Bookings booking) {
        long startDateEpoch = booking.getStartDate().toInstant().getEpochSecond();
        long endDateEpoch = booking.getEndDate().toInstant().getEpochSecond();
        if (endDateEpoch < startDateEpoch) {
            Log.error("bookExtraService(): endDate < startDate");
            return false;
        }
        int numDays = (int)Math.abs(ChronoUnit.DAYS.between(
                LocalDate.ofInstant(booking.getStartDate().toInstant(), ZoneId.systemDefault()),
                LocalDate.ofInstant(booking.getEndDate().toInstant(), ZoneId.systemDefault()))) + 1;
        String sql = "INSERT INTO ExtraServices VALUES("+extra.ordinal()+", "+booking.getBookingID()+")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.execute("UPDATE Bookings SET price = price +"+(extra.getPrice()*numDays));
        } catch (SQLException e) {
            Log.error("bookExtraService() " + e.getMessage());
            return false;
        }
        return true;
    }

    public List<Suite> getCustomerSuites(Customer customer) {
        long hashName = 0;
        hashName = hashName(customer.getName());
        String sql = "SELECT * FROM Suites JOIN Bookings ON Suites.suiteID=Bookings.suiteID WHERE Bookings.hashName = "+hashName;
        List<Suite> suites = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Suite suite = new Suite(rs.getInt("suiteID"), null, rs.getDouble("price"), rs.getFloat("holidayFactor"), SuiteType.values()[rs.getInt("suiteType")]);
                suites.add(suite);
            }
        } catch (SQLException e) {
            Log.error("getCustomerSuites() " + e.getMessage());
            return null;
        }
        return suites;
    }

    public List<Bookings> getBookings(Customer customer) {
        long hashName = 0;
        hashName = hashName(customer.getName());
        String sql = "SELECT * FROM Bookings WHERE hashName = "+hashName;
        List<Bookings> bookings = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Bookings booking = new Bookings(rs.getInt("bookingID"), rs.getInt("suiteID"), rs.getInt("numPeople"), new Date(rs.getLong("startDate")*1000), new Date(rs.getLong("endDate")*1000), rs.getDouble("price"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            Log.error("getBookings() " + e.getMessage());
            return null;
        }
        return bookings;
    }

}
