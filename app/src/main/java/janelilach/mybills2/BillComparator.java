package janelilach.mybills2;

import java.util.Comparator;

/**
 * Created by User on 2/20/2017.
 */

public class BillComparator implements Comparator<Bill> {
    // red > green > yellow

    public int compare(Bill a, Bill b) {
        if (a.color.equals(b.color)) { // if same colors, sort by date
            if (a.dueDate.before(b.dueDate)) {
                return 1;
            } else if (a.dueDate.equals(b.dueDate)) {
                return 0;
            } else {
                return -1;
            }
        } else if (a.color.equals("red")) { // if a red, b non-red
            return 1;
        } else if (b.color.equals("red")) { // if b red, a non-red
            return -1;
        } else if (a.color.equals("green")) { // if a green, b yellow
            return 1;
        } else { // if b green, a yellow
            return -1;
        }
    }
}
