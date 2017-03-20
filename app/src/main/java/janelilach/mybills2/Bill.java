package janelilach.mybills2;

import android.util.Log;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Calendar;
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
    protected String type;
    protected boolean recurring;
    protected String color;

    public Bill(String name, double amount, Date dueDate, Date paidDate, String type, boolean recurring, String color) {
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;


        this.type = type;
        this.recurring = recurring;
        this.color = color;
    }

    public void payBillInFull() {
        paidDate = new Date();
        this.color = "green";
    }

    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.dueDate);
        int dayInt = cal.get(Calendar.DAY_OF_MONTH);
        String day = String.format("%02d", dayInt);
        int monthInt = cal.get(Calendar.MONTH) + 1;
        String month = String.format("%02d", monthInt);
        Log.v("billMonth", month);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        return this.name + " - " + this.type + "\n$" + this.amount + " due on " + month + "/" + day + "/" + year + "\n";
    }
}
