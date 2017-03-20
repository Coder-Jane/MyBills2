package janelilach.mybills2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private BillsFileIO billsIO;
    private ArrayList<Bill> bills;

    private ListView listView;
    private BillArrayAdapter billAA;

    private Bill oldBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_main);

        billsIO = new BillsFileIO(getApplicationContext());

        listView = (ListView) findViewById(R.id.listView);
        bills = billsIO.getBills();

        // first populate array, then sort it
        billAA = new BillArrayAdapter(bills, this, this);
        listView.setAdapter(billAA);
        billAA.getFilter().filter("1,1,1");



        // set spinner listener
        Spinner dateSpinner = (Spinner) findViewById(R.id.spinner_dates);
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_months, R.layout.spinner_item);
        dateSpinner.setAdapter(spinnerAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                TextView child = (TextView) adapterView.getChildAt(0);
//                child.setTextSize(18);
                String spinnerValue = adapterView.getSelectedItem().toString();
                billAA.getFilter().filter(spinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();

                Bill thisBill = (Bill) listView.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putSerializable("bill", thisBill);

                if (viewId == R.id.bill_elem_edit) {
                    Log.v("EditBill",thisBill.toString());
                    // send bill data back
                    Intent i = new Intent(view.getContext(), AddBillActivity.class);
                    i.putExtra("bill", b);
                    startActivityForResult(i, 1);

                } else if (viewId == R.id.bill_elem_delete) {
                    Log.v("DeleteBill",thisBill.toString());
                }
                // need to remove old bill in either case
                oldBill = thisBill;
                bills = billsIO.deleteBill(thisBill);
                billAA.getFilter().filter("refresh");
            }
        });
    }

    public void onFilterClick(View view) {
        CheckBox unpaid = (CheckBox) findViewById(R.id.filter_unpaid);
        CheckBox paid = (CheckBox) findViewById(R.id.filter_paid);
        CheckBox dueSoon = (CheckBox) findViewById(R.id.filter_due_soon);

        String toSend = "";
        if (unpaid.isChecked()) {
            toSend += "1,";
        } else {
            toSend += "0,";
        }
        if (paid.isChecked()) {
            toSend += "1,";
        } else {
            toSend += "0,";
        }
        if (dueSoon.isChecked()) {
            toSend += "1";
        } else {
            toSend += "0";
        }

        Log.v("sendingData", toSend);
        billAA.getFilter().filter(toSend);
    }


    public void onAddBillClick(View view) {
        Intent i = new Intent(this, AddBillActivity.class);
        startActivityForResult(i, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                Bundle b = data.getBundleExtra("bill");
                if (b != null) {
                    Bill newBill = (Bill) b.getSerializable("bill");
                    bills = billsIO.addBill(newBill);
                    billAA.getFilter().filter("refresh");
                }
            }
        }
    }


}
