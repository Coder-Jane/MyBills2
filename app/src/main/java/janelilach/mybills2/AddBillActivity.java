package janelilach.mybills2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddBillActivity extends AppCompatActivity {

    private boolean paid;
    private boolean recurring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bill);
    }

    public void onAddBillConfirmClick(View view) {
        Log.v("addBill", "entered add bill confirm click");
        String color;
        Date dueDate;

        String name = ((EditText) findViewById(R.id.add_bill_name)).getText().toString();

        Double amount = Double.parseDouble(((EditText) findViewById(R.id.add_bill_amount)).getText().toString());

        Spinner spinner = (Spinner) findViewById(R.id.spinner_types);

        String type = spinner.getSelectedItem().toString();

        Date paidDate = null;
        Date today = new Date();

        String date = ((EditText) findViewById(R.id.add_bill_date)).getText().toString();
        Log.v("date",date);
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        try {
            dueDate = df.parse(date);
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(dueDate);
            Log.v("due date cal", dueDate.toString());
//            cal.get(Calendar.DAY_OF_MONTH);

            // may need to fix the logic of setting paid dates here
            if (paid) {
                color = "green";
                Log.v("bill color",color);
                paidDate = new Date();
                if (paidDate.after(dueDate)) { // case 1: editing bill status to paid
//                    String todayString = today.toString();
//                    SimpleDateFormat fromStandard = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

                    String paidDateString = df.format(today);
                    paidDate = df.parse(paidDateString);
//                    Calendar cal2 = Calendar.getInstance();
//                    cal2.setTime(paidDate);
//                    cal2.get(Calendar.DAY_OF_MONTH);
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

            Log.v("bill color 2",color);

            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("bill", new Bill(name, amount, dueDate, paidDate, type, recurring, color));
            intent.putExtra("bill", b);
            setResult(RESULT_OK, intent);
            finish();
        } catch (java.text.ParseException e) {
            String stackTrace = e.getStackTrace().toString();
            Log.v("Date creation error", e.toString());
        }

    }


    public void onAddBillCancelClick(View view) {
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.add_bill_status_paid:
                if (checked)  paid = true;
                break;
            case R.id.add_bill_status_unpaid:
                if (checked) paid = false;
                break;
            case R.id.add_bill_freq_once:
                if (checked) recurring = false;
                break;
            case R.id.add_bill_freq_monthly:
                if (checked) recurring = true;
                break;
        }
    }
}
