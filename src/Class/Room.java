package Class;

public class Room {
    private int maxPeople;

    public Room(int maxPeople) {
        this.maxPeople = maxPeople;

    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    @Override
    public String toString() {
        return "Room{" +
                "maxPeople=" + maxPeople +
                '}';
    }
}
