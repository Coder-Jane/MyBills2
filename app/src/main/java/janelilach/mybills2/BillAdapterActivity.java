package janelilach.mybills2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by User on 2/28/2017.
 */

public class BillAdapterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();

        if (viewId == R.id.bill_elem_edit) {

        } else if (viewId == R.id.bill_elem_edit) {

        }
    }
}
