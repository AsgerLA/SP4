package Class;

import Enum.ExtraService;
import Enum.SuiteType;

import java.util.List;

public class Suite {
    private  int suiteID;
    private  List<Room> rooms;
    private  double price;
    private  float holidayFactor;
    private SuiteType suiteType;

    public Suite(int suiteID,
                 List<Room> rooms,
                 double price,
                 float holidayFactor,
                 SuiteType suiteType) {

        this.suiteID = suiteID;
        this.rooms = rooms;
        this.price = price;
        this.holidayFactor = holidayFactor;
        this.suiteType = suiteType;
    }

    public int getSuiteID() {
        return suiteID;
    }

    public void setSuiteID(int suiteID) {
        this.suiteID = suiteID;
    }

    public List<Room> getRooms() {
        if (rooms == null)
            rooms = Hotel.db.getSuiteRooms(this);
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getHolidayFactor() {
        return holidayFactor;
    }

    public void setHolidayFactor(float holidayFactor) {
        this.holidayFactor = holidayFactor;
    }

    public SuiteType getSuitType() {
        return suiteType;
    }

    public void setSuitType(SuiteType suiteType) {
        this.suiteType = suiteType;
    }

    @Override
    public String toString() {
        return "Suit{" +
                "suitID=" + suiteID +
                ", rooms=" + rooms +
                ", price=" + price +
                ", holidayFactor=" + holidayFactor +
                ", suitType=" + suiteType +
                '}';
    }
}
