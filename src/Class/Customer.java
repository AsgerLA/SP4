package Class;
import Enum.PaymentMethod;

import java.util.List;


public class Customer {
    private String name;
    private PaymentMethod paymentmethod;

    public Customer() {

    }

    public Customer(String name, PaymentMethod paymentmethod) {
        this.name = name;
        this.paymentmethod = paymentmethod;
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

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", paymentmethod=" + paymentmethod +
                '}';
    }
}
