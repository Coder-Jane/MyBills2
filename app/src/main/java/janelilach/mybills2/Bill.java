package janelilach.mybills2;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jane on 1/30/17.
 */
public class Bill implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;


    protected String name;
    protected double amount;
    protected Date dueDate;
    protected Date paidDate;

    public Bill(String name, double amount, Date dueDate) {
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public void payBillInFull() {
        paidDate = new Date();
    }

    @Override
    public String toString() {
        return this.name + ": " + this.amount + " on " + this.dueDate.toString();
    }
}
