package janelilach.mybills2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddBillActivity extends AppCompatActivity {

    private boolean paid;
    private boolean recurring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bill);

        ArrayList<String> arrayList1 = new ArrayList<String>();
        arrayList1.add("Cable");
        arrayList1.add("Credit");
        arrayList1.add("Electricity");
        arrayList1.add("Gas");
        arrayList1.add("Heating");
        arrayList1.add("Internet");
        arrayList1.add("Water");
        arrayList1.add("Other");

        Spinner spinner = (Spinner) findViewById(R.id.spinner_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,R.layout.spinner_item_type,arrayList1);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        Bundle receivedB = i.getBundleExtra("bill");
        if (receivedB != null) {
            Bill oldBill = (Bill) receivedB.getSerializable("bill");

            ((EditText) findViewById(R.id.add_bill_name)).setText(oldBill.name);
            ((EditText) findViewById(R.id.add_bill_amount)).setText(Double.toString(oldBill.amount));

            adapter.setDropDownViewResource(R.layout.spinner_dropdown);

            //ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            spinner.setSelection(adapter.getPosition(oldBill.type));

            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            ((EditText) findViewById(R.id.add_bill_date)).setText(df.format((oldBill.dueDate)));

            if (oldBill.paidDate != null) {
                ((RadioButton) findViewById(R.id.add_bill_status_paid)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.add_bill_status_unpaid)).setChecked(true);
            }
        }


    }

    public void onAddBillConfirmClick(View view) {

        String color;
        Date dueDate;

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");


        String name = ((EditText) findViewById(R.id.add_bill_name)).getText().toString();

        Double amount = Double.parseDouble(((EditText) findViewById(R.id.add_bill_amount)).getText().toString());

        Spinner spinner = (Spinner) findViewById(R.id.spinner_types);

        String type = spinner.getSelectedItem().toString();

        Date paidDate = null;
        Date today = new Date();

        String date = ((EditText) findViewById(R.id.add_bill_date)).getText().toString();
        Log.v("date", date);

        try {
            dueDate = df.parse(date);
            Log.v("due date cal", dueDate.toString());

            // may need to fix the logic of setting paid dates here
            if (paid) {
                color = "green";
//                Log.v("bill color",color);
                paidDate = new Date();
                if (paidDate.after(dueDate)) { // case 1: editing bill status to paid

                    String paidDateString = df.format(today);
                    paidDate = df.parse(paidDateString);
                } else { // case 2: adding new bill that was paid (assume paid same date)
                    paidDate = dueDate;
                }
            } else {
                if (dueDate.after(today)) {  // case 3: adding unpaid bill due in the future
                    color = "yellow";
                } else { // case 4: adding unpaid bill due today or earlier
                    color = "red";
                }
            }

//            Log.v("bill color 2",color);

            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("bill", new Bill(name, amount, dueDate, paidDate, type, recurring, color));
            intent.putExtra("bill", b);

//            if (oldBill != null) {
//                intent.putExtra("isEdit", true);
//            }
            setResult(RESULT_OK, intent);
            finish();
        } catch (java.text.ParseException e) {
            String stackTrace = e.getStackTrace().toString();
            Log.v("Date creation error", stackTrace);
        }

    }


    public void onAddBillCancelClick(View view) {
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.add_bill_status_paid:
                if (checked) paid = true;
                break;
            case R.id.add_bill_status_unpaid:
                if (checked) paid = false;
                break;
        }
    }
}
