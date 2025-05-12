package Class;

import Enum.ExtraService;
import Enum.SuitType;

import java.util.List;

public class Suit {
    private  int suitID;
    private  boolean booked;
    private  List<Room> rooms;
    private  double price;
    private  float holidayFactor;
    private  List<ExtraService> extra;
    private  boolean balcony;
    private  SuitType suitType;

    public Suit(int suitID,
                boolean booked,
                List<Room> rooms,
                double price,
                float holidayFactor,
                List<ExtraService> extra,
                boolean balcony,
                SuitType suitType) {

        this.suitID = suitID;
        this.booked = booked;
        this.rooms = rooms;
        this.price = price;
        this.holidayFactor = holidayFactor;
        this.extra = extra;
        this.balcony = balcony;
        this.suitType = suitType;
    }

    public int getSuitID() {
        return suitID;
    }

    public void setSuitID(int suitID) {
        this.suitID = suitID;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public List<Room> getRooms() {
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

    public List<ExtraService> getExtra() {
        return extra;
    }

    public void setExtra(List<ExtraService> extra) {
        this.extra = extra;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }

    public SuitType getSuitType() {
        return suitType;
    }

    public void setSuitType(SuitType suitType) {
        this.suitType = suitType;
    }

    @Override
    public String toString() {
        return "Suit{" +
                "suitID=" + suitID +
                ", booked=" + booked +
                ", rooms=" + rooms +
                ", price=" + price +
                ", holidayFactor=" + holidayFactor +
                ", extra=" + extra +
                ", balcony=" + balcony +
                ", suitType=" + suitType +
                '}';
    }
}
