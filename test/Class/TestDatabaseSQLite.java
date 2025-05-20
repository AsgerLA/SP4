package Class;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import Enum.*;

import static org.junit.jupiter.api.Assertions.*;

class TestDatabaseSQLite {
    Database db = null;
    List<Suite> suiteList;
    List<Room> roomList;

    @BeforeEach
    public void start() {
        try {
            db = new DatabaseSQLite("jdbc:sqlite::memory:");
        } catch (IOException e) {
            fail("new DatabaseSQLite() failed");
        }

        roomList = new java.util.ArrayList<>();
        roomList.add(new Room(2));
        roomList.add(new Room(3));
        suiteList = new java.util.ArrayList<>();
        suiteList.add(new Suite(1, roomList, 200, 1.5f, SuiteType.Standard));
        suiteList.add(new Suite(2, null, 200, 1.2f, SuiteType.Standard));
        suiteList.add(new Suite(3, null, 500, 1.5f, SuiteType.Luxury));
        boolean res;
        for (Suite suite : suiteList) {
            res = db.addSuite(suite);
            assertTrue(res);
            if (!suite.getRooms().isEmpty())
                continue;
            res = db.addRoom(suite, new Room(2));
            assertTrue(res);
        }
    }

    @AfterEach
    public void end() {
        boolean res = db.close();
        assertTrue(res);
    }

    @Test
    public void testBookSuite() {
        boolean res;
        Customer customer = new Customer();
        customer.setName("test");
        customer.setPaymentmethod(PaymentMethod.Online);

        // register customer
        res = db.addCustomer(customer);
        assertTrue(res);

        // customer requirements
        int numPeople = 2;
        Date startDate = new Date(0);
        Date endDate = new Date(1000);

        // get available suites
        List<Suite> availSuites;
        availSuites = db.getAvailSuites(numPeople, startDate, endDate);
        assertEquals(3, availSuites.size());

        Suite suite = suiteList.getFirst();

        // book suite for customer
        res = db.bookSuite(suite, customer, numPeople, startDate, endDate);
        assertTrue(res);

        // get booked suite
        List<Suite> suites = db.getCustomerSuites(customer);
        assertEquals(1, suites.size());
        assertEquals(1, suites.getFirst().getSuiteID());
        assertEquals(suite.getPrice(), suites.getFirst().getPrice());
        assertEquals(SuiteType.Standard, suites.getFirst().getSuitType());
        assertEquals(roomList.size(), db.getSuiteRooms(suite).size());

        List<Bookings> bookings = db.getBookings(customer);
        assertEquals(1, bookings.size());

        // check booked suite is not available
        availSuites = db.getAvailSuites(numPeople, startDate, endDate);
        assertEquals(2, availSuites.size());
        assertNotEquals(1, availSuites.getFirst().getSuiteID());

    }

    @Test
    public void testFreeSuite() {
        boolean res;
        Customer customer = new Customer();
        customer.setName("test");
        customer.setPaymentmethod(PaymentMethod.Online);

        // register customer
        res = db.addCustomer(customer);
        assertTrue(res);

        Suite suite = suiteList.getFirst();

        // book suite
        res = db.bookSuite(suite, customer, 2, new Date(0), new Date(1000));
        assertTrue(res);

        // free suite
        res = db.freeSuite(new Bookings(1, 0, 0, null, null, 0.0));
        assertTrue(res);
        List<Suite> suites = db.getCustomerSuites(customer);
        assertTrue(suites.isEmpty());
        suites = db.getAvailSuites(2, new Date(0), new Date(1000));
        assertEquals(3, suites.size());
    }

    @Test
    public void testCheckout() {
        boolean res;
        Customer customer = new Customer();
        customer.setName("test");
        customer.setPaymentmethod(PaymentMethod.Online);

        // register customer
        res = db.addCustomer(customer);
        assertTrue(res);

        Suite suite = suiteList.getFirst();

        // book suite
        res = db.bookSuite(suite, customer, 2, new Date(0), new Date(1000));
        assertTrue(res);

        customer = db.getCustomer("test");
        assertNotNull(customer);

        // remove and free booked suites
        res = db.removeCustomer(customer);
        assertTrue(res);

        customer = db.getCustomer("test");
        assertTrue(customer == null);

        customer = new Customer();
        customer.setName("test");
        List<Suite> suites = db.getCustomerSuites(customer);
        assertTrue(suites.isEmpty());
        suites = db.getAvailSuites(2, new Date(0), new Date(1000));
        assertEquals(3, suites.size());
    }

}