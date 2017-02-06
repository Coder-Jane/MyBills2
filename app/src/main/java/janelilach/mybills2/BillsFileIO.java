package janelilach.mybills2;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jane on 2/6/17.
 */
public class BillsFileIO {
    protected ArrayList<Bill> currentBills;
    protected Context context;

    public BillsFileIO(Context context){
        this.context = context;
        if (null == currentBills) {
            currentBills = new ArrayList<Bill>();
        }

        SharedPreferences prefs = context.getSharedPreferences("billsData", Context.MODE_PRIVATE);

        try {
            currentBills = (ArrayList<Bill>) ObjectSerializer.deserialize(prefs.getString("BILLS", ObjectSerializer.serialize(new ArrayList<Bill>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Bill> getBills() {
        return currentBills;
    }

    public ArrayList<Bill> addBill(Bill b) {
        if (null == currentBills) {
            currentBills = new ArrayList<Bill>();
        }
        currentBills.add(b);

        SharedPreferences prefs = context.getSharedPreferences("billsData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("BILLS", ObjectSerializer.serialize(currentBills));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
        return currentBills;
    }

    public ArrayList<Bill> deleteBill(String name) {
        for(int i = 0; i < currentBills.size(); i++) {
            if(currentBills.get(i).name.equals(name)) {
                currentBills.remove(i);
            }
        }

        SharedPreferences prefs = context.getSharedPreferences("billsData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("BILLS", ObjectSerializer.serialize(currentBills));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
        return currentBills;
    }

}

