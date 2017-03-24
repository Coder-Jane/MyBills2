package janelilach.mybills2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBillActivity extends AppCompatActivity {

    private boolean paid;
    private Intent toSend;
    private Date oldPaidDate;
    private Date dueDate = new Date();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bill);

        // populate spinner
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_type, arrayList1);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);


        // set toolbar text
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle("HOME > ADD BILL");

        // get old bill info, and set fields
        toSend = getIntent();
        Bundle receivedB = toSend.getBundleExtra("bill");

        if (receivedB != null) {
            getSupportActionBar().setTitle("HOME > EDIT BILL");
            Bill oldBill = (Bill) receivedB.getSerializable("bill");

            ((EditText) findViewById(R.id.add_bill_name)).setText(oldBill.name);
            ((EditText) findViewById(R.id.add_bill_amount)).setText(Double.toString(oldBill.amount));

            adapter.setDropDownViewResource(R.layout.spinner_dropdown);

            //ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            spinner.setSelection(adapter.getPosition(oldBill.type));

            dueDate = oldBill.dueDate;


            if (oldBill.paidDate != null) {
                ((RadioButton) findViewById(R.id.add_bill_status_paid)).setChecked(true);
                oldPaidDate = new Date(oldBill.paidDate.getTime());
                paid = true;
            } else {
                ((RadioButton) findViewById(R.id.add_bill_status_unpaid)).setChecked(true);
                oldPaidDate = null;
            }
        } else {
            ((RadioButton) findViewById(R.id.add_bill_status_unpaid)).setChecked(true);
        }

        // init datePicker to dueDate (today if new bill, or old due date if old bill)
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                dueDate = cal.getTime();

            }
        });

    }

    public void onAddBillConfirmClick(View view) {

        Boolean invalid = false;
        String color;

//        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        // parse name
        String name = ((EditText) findViewById(R.id.add_bill_name)).getText().toString();
        if (name.equals("")) {
            invalid = true;
        }

        // parse amount
        String amountString = ((EditText) findViewById(R.id.add_bill_amount)).getText().toString();
        String pattern = "^\\d*\\.\\d{2}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(amountString);
        if (!m.matches()) {
            invalid = true;
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_types);

        String type = spinner.getSelectedItem().toString();



        Date paidDate = null;
        Date today = new Date();

        // may need to fix the logic of setting paid dates here
        if (paid) {
            color = "green";
            Log.v("bill color", color);
            if (oldPaidDate == null) { // case 1: adding new paid bill
                paidDate = today;
            } else { // case 2: editing old paid bill
                paidDate = oldPaidDate;
            }
            if (paidDate.after(dueDate)) { // case 3: editing old unpaid bill status to paid
                paidDate = today;
            }
        } else {
            if (!dueDate.before(today)) {  // case 4: adding unpaid bill due in the future
                color = "yellow";
            } else { // case 5: adding unpaid bill due today or earlier
                color = "red";
            }
        }

        Log.v("bill color 2", color);

        if (!invalid) {
            toSend = new Intent();
            Bundle b = new Bundle();
            Double amount = Double.parseDouble(amountString);
            b.putSerializable("bill", new Bill(name, amount, dueDate, paidDate, type, false, color));
            toSend.putExtra("bill", b);

            setResult(RESULT_OK, toSend);
            finish();
        } else {
            warnInvalidBill();
        }

    }


    public void onAddBillCancelClick(View view) {

        setResult(RESULT_CANCELED, toSend);
        finish();
    }

    public void warnInvalidBill() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);
        builder.setTitle("Invalid Form");
        builder.setMessage("Please fill out all the fields in this form!");
        builder.setPositiveButton("Got it!", null);

        AlertDialog alert2 = builder.create();
        alert2.show();

        Button button0 = alert2.getButton(AlertDialog.BUTTON_POSITIVE);
        button0.setBackground(context.getResources().getDrawable(R.drawable.red_dialog_button));
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button0.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButtonLL.bottomMargin = 10;
        positiveButtonLL.width = 850;
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
