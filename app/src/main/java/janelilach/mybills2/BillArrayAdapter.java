package janelilach.mybills2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2/19/2017.
 */

public class BillArrayAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Bill> list;
    private Context context;
    private Activity activity;

    public BillArrayAdapter(ArrayList<Bill> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Bill thisBill;
        int bgColor = 0;
        View view = convertView;
        CheckBox unpaid = (CheckBox) convertView.findViewById(R.id.filter_unpaid);
        CheckBox paid = (CheckBox) convertView.findViewById(R.id.filter_paid);
        CheckBox dueSoon = (CheckBox) convertView.findViewById(R.id.filter_due_soon);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bill_elem, null);
        }

        // set empty view if null
        if (list.isEmpty()) {
            Log.v("emptyList", "list is empty");
            return view;
        }

        thisBill = list.get(position);

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.bill_elem_content);
        listItemText.setText(thisBill.toString());

        // Set bill color
        if (thisBill.color.equals("red")) {
            bgColor = ContextCompat.getColor(context, R.color.red_light);
        } else if (thisBill.color.equals("green")) {
            bgColor = ContextCompat.getColor(context, R.color.green_light);
        } else {
            bgColor = ContextCompat.getColor(context, R.color.yellow_light);
        }
        LinearLayout parentView = (LinearLayout)listItemText.getParent();
        Button editButton = (Button) view.findViewById(R.id.bill_elem_edit);
        parentView.setBackgroundColor(bgColor);
        editButton.setBackgroundColor(bgColor);

        //Handle buttons and edit onClickListeners
//        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button editBtn = (Button) view.findViewById(R.id.bill_elem_edit);

//        deleteBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                list.remove(position); //or some other task
//                notifyDataSetChanged();
//            }
//        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // send bill data back
                Intent i = new Intent(v.getContext(), AddBillActivity.class);
                Bill thisBill = list.get(position);
                Bundle b = new Bundle();
                b.putSerializable("bill", thisBill);
                i.putExtra("bill", b);
                activity.startActivityForResult(i, 1);
                notifyDataSetChanged();
            }
        });

        return view;
    }

}
