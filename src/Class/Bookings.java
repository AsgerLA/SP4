package Class;

import java.util.Date;

public class Bookings {
    private int bookingID;
    private int suiteID;
    private int numPeople;
    private Date startDate;
    private Date endDate;
    private double price;

    public Bookings(int bookingID, int suiteID, int numPeople, Date startDate, Date endDate, double price) {
        this.bookingID = bookingID;
        this.suiteID = suiteID;
        this.numPeople = numPeople;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public int getBookingID() {
        return bookingID;
    }

    public int getSuiteID() {
        return suiteID;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getPrice() {
        return price;
    }
}
