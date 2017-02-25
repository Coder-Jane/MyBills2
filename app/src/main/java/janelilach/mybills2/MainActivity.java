package janelilach.mybills2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private BillsFileIO billsIO;
    private ArrayList<Bill> bills;

    private ListView listView;
    BillArrayAdapter billAA;

    private String spinnerValue = "All";
//    private CheckBox unpaid;
//    private CheckBox paid;
//    private CheckBox dueSoon;


//    private Button addButton;
//    private Button deleteButton;
//    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_main);

        // set default values of checkboxes to view all
//        unpaid = (CheckBox) findViewById(R.id.filter_unpaid);
//        paid = (CheckBox) findViewById(R.id.filter_paid);
//        dueSoon = (CheckBox) findViewById(R.id.filter_due_soon);
//        unpaid.setChecked(true);
//        paid.setChecked(true);
//        dueSoon.setChecked(true);

        billsIO = new BillsFileIO(getApplicationContext());

        listView = (ListView) findViewById(R.id.listView);
        bills = billsIO.getBills();
        Log.v("list:pre",bills.toString());
        // first populate array, then sort it
        billAA = new BillArrayAdapter(bills, this, this);

        listView.setAdapter(billAA);
        filterHelper();

        // set spinner listener
        Spinner dateSpinner = (Spinner) findViewById(R.id.spinner_dates);
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_months, R.layout.spinner_item);
        dateSpinner.setAdapter(spinnerAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView child = (TextView) adapterView.getChildAt(0);
                child.setTextSize(18);
                spinnerValue = adapterView.getSelectedItem().toString();
                filterHelper();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onFilterClick(View view) {
        filterHelper();
    }

    public void filterHelper() {
        CheckBox unpaid = (CheckBox) findViewById(R.id.filter_unpaid);
        CheckBox paid = (CheckBox) findViewById(R.id.filter_paid);
        CheckBox dueSoon = (CheckBox) findViewById(R.id.filter_due_soon);

//        ArrayList<Bill> filtered = new ArrayList<Bill>(bills);
        ArrayList<Bill> filtered = bills;
        Collections.sort(filtered, Collections.<Bill>reverseOrder(new BillComparator()));

        Iterator<Bill> billIter = filtered.iterator();
        while (billIter.hasNext()) {
            Bill b = billIter.next();
            // filter bill dates
            if (!spinnerValue.equals("All")) {
                Date today = new Date();
                long temp = today.getTime() - b.dueDate.getTime();
                long diff = TimeUnit.DAYS.convert(temp, TimeUnit.MILLISECONDS);
                long limit = Integer.parseInt(spinnerValue);
                if (diff > limit) {
                    billIter.remove();
                }
            }
            // filter bill statuses
           else if (!unpaid.isChecked() && b.color.equals("red")) {
                billIter.remove();
            }
            else if (!paid.isChecked() && b.color.equals("green")) {
                billIter.remove();
            }
            else if (!dueSoon.isChecked() && b.color.equals("yellow")) {
                billIter.remove();
            }
        }
        //bills = filtered;
        //billAA = new BillArrayAdapter(filtered, this, this);
        billAA.notifyDataSetChanged();
        //listView.setAdapter(billAA);
    }

    public void onAddBillClick(View view) {
        Intent i = new Intent(this, AddBillActivity.class);
        startActivityForResult(i, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK){
                Bundle b = data.getBundleExtra("bill");
                Bill newBill = (Bill) b.getSerializable("bill");
                bills = billsIO.addBill(newBill);
                Log.v("activity result","return success?");
                billAA.notifyDataSetChanged();
                //listView.setAdapter(billAA);
            }
        }
    }
}
