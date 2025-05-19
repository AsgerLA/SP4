package Class;

public class Booking {
    private Database db;

    public Booking(Database db) {
        this.db = db;
    }

    public void startSession() {
        Menu mainmenu = new Menu("Main menu", "Quit");
        mainmenu.addOption(new OptionBook());
        mainmenu.addOption(new OptionCancel());
        mainmenu.addOption(new OptionCheckout());
        mainmenu.show();
    }
}
