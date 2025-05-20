package Class;
import Enum.PaymentMethod;


public class Customer {
    private String name;
    private final long hashName;
    private PaymentMethod paymentmethod;

    // fnv64a
    public static long hashName(String name) {
        final long FNV_64_offset_basis = 0xcbf29ce484222325L;
        final long FNV_64_prime = 0x100000001b3L;
        long hashName = FNV_64_offset_basis;
        for (char c : name.toCharArray()) {
            hashName ^= c;
            hashName *= FNV_64_prime;
        }
        return hashName;
    }

    public Customer(String name) {
        this.name = name;
        this.hashName = hashName(name);
    }

    public Customer(String name, PaymentMethod paymentmethod) {
        this.name = name;
        this.paymentmethod = paymentmethod;
        this.hashName = hashName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHashName() {
        return hashName;
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
