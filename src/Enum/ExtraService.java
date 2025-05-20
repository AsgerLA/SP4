package Enum;


public enum ExtraService {
    Spa,
    Fitness,
    Breakfast,
    Casino;

    public double getPrice() {
        return prices[this.ordinal()];
    }

    public static final double[] prices = new double[] {
       300,
       200,
       150,
       75
    };
}