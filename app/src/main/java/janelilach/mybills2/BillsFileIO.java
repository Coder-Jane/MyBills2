package janelilach.mybills2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


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
        if (currentBills == null) {
            currentBills = new ArrayList<Bill>();
        }

        SharedPreferences prefs = context.getSharedPreferences("billsData", Context.MODE_PRIVATE);
//        context.getSharedPreferences("billsData", 0).edit().clear().commit(); // for deleting shared prefs, if necessary dev-side
//        Log.v("BillIO", "removed shared prefs cache");

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

    public ArrayList<Bill> deleteBill(Bill b) {
        currentBills.remove(b);

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

