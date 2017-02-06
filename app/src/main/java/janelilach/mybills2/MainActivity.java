package janelilach.mybills2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private BillsFileIO billsIO;
    private ArrayList<Bill> bills;

    private ListView listView;
    private ArrayAdapter arrayAdapter;

    private Button addButton;
    private Button deleteButton;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billsIO = new BillsFileIO(getApplicationContext());
        bills = billsIO.getBills();

        listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<Bill>(this, android.R.layout.simple_list_item_1, bills);
        listView.setAdapter(arrayAdapter);

        addButton = (Button) findViewById(R.id.add);
        deleteButton = (Button) findViewById(R.id.delete);
        text   = (EditText)findViewById(R.id.billName);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bill newBill = new Bill(text.getText().toString(), Math.random()*100, new Date());
                bills = billsIO.addBill(newBill);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bills = billsIO.deleteBill(text.getText().toString());
                arrayAdapter.notifyDataSetChanged();
            }
        });

    }
}
