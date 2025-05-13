package Class;
import Enum.PaymentMethod;

import java.util.Date;
import java.util.List;


public class Customer {
private String name;
private PaymentMethod paymentmethod;
private List<Suit> suits;
private int numPeople;
private Date startDate;
private Date endDate;

    public Customer() {

    }

    public Customer(String name, PaymentMethod paymentmethod, List<Suit> suits, int numPeople, Date startDate, Date endDate) {
        this.name = name;
        this.paymentmethod = paymentmethod;
        this.suits = suits;
        this.numPeople = numPeople;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaymentMethod getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(PaymentMethod paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public List<Suit> getSuits() {
        return suits;
    }

    public void setSuits(List<Suit> suits) {
        this.suits = suits;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", paymentmethod=" + paymentmethod +
                ", suits=" + suits +
                ", numPeople=" + numPeople +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
