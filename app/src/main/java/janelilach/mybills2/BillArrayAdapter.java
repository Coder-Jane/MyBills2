package janelilach.mybills2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Created by User on 2/19/2017.
 */

/**
 * Filtering setup code borrowed from https://gist.github.com/fjfish/3024308
 */

public class BillArrayAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Bill> listOriginal = null;
    private ArrayList<Bill> listFiltered = null;

    private Context context;
    private Activity activity;
    private BillFilter filter;

    private boolean[] currChecked;
    private String currSpinner;

    public BillArrayAdapter(ArrayList<Bill> list, Context context, Activity activity) {
        this.listOriginal = list;
        this.listFiltered = list;

        this.context = context;
        this.activity = activity;
        filter = new BillFilter();

        currChecked = new boolean[]{true, true, true};
        currSpinner = "All";
    }

    public int getCount() {
        return listFiltered.size();
    }

    public Object getItem(int pos) {
        return listFiltered.get(pos);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Bill thisBill;
        int bgColor = 0;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bill_elem, null);
        }

        // set empty view if null
        if (listFiltered.isEmpty()) {
            Log.v("emptyList", "list is empty");
            return view;
        }

        thisBill = listFiltered.get(position);

        //Handle TextView and display string from your list
        final TextView listItemText = (TextView) view.findViewById(R.id.bill_elem_content);
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
        Button deleteButton = (Button) view.findViewById(R.id.bill_elem_delete);


        parentView.setBackgroundColor(bgColor);
        editButton.setBackgroundColor(bgColor);
        deleteButton.setBackgroundColor(bgColor);

        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.bill_elem_wrapper);
        editButton.setTag(wrapper);
        deleteButton.setTag(wrapper);


        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LinearLayout parent = (LinearLayout) v.getTag();
                ListView clickParent = (ListView) parent.getParent();
                clickParent.performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                LinearLayout parent = (LinearLayout) v.getTag();
                final ListView clickParent = (ListView) parent.getParent();
                final View arg = v;

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);
                builder.setTitle("Deleting Bill");
                builder.setMessage("Are you sure you want to delete this bill?");
                builder.setPositiveButton("Yes, delete this bill", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clickParent.performItemClick(arg, position, 0); // Let the event be handled in onItemClick()
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog alert = builder.create();
                alert.show();

                Button button0 = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button0.setBackground(context.getResources().getDrawable(R.drawable.red_dialog_button));
                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button0.getLayoutParams();
                positiveButtonLL.gravity = Gravity.CENTER;
                positiveButtonLL.bottomMargin = 10;
                positiveButtonLL.width = 800;

                Button button2 = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                button2.setBackground(context.getResources().getDrawable(R.drawable.red_dialog_button));
                LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) button2.getLayoutParams();
                negativeButtonLL.gravity = Gravity.CENTER;
                negativeButtonLL.bottomMargin = 10;
                negativeButtonLL.width = 800;
            }
        });

            return view;
    }


    public Filter getFilter(){
        return filter;
    }

    private class BillFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            final ArrayList<Bill> list = listOriginal;
            int count = list.size();

            Log.v("originalListSize", Integer.toString(count));
            final ArrayList<Bill> nlist = new ArrayList<Bill>(count);

            // check if sent refresh, or spinner/checkbox value
            boolean isSpinner = false;
            String constraintString = constraint.toString();
            if (!constraintString.equals("refresh")) {
                if (constraintString.indexOf(',') ==  -1) {
                    isSpinner = true;
                    currSpinner = constraintString; // copy result to carry over to subsequent filtering
                }

                if (!isSpinner) {
                    StringTokenizer tokenizer = new StringTokenizer(constraint.toString());
                    int iter = 0;
                    currChecked  = new boolean[3]; // copy results to carry over to subsequent filtering
                    while (tokenizer.hasMoreTokens()) {
                        currChecked[iter] = Integer.parseInt(tokenizer.nextToken(",")) == 1 ? true : false;
                        iter++;
                    }
                }
            }

            Bill currentBill;
            for (int i = 0; i < count; i++) {
                currentBill = list.get(i);

                // filter bill dates
                if (isSpinner || constraintString.equals("refresh")) {

                    if (currSpinner.equals("All")) {
                        nlist.add(currentBill);
                    } else {
                        Date today = new Date();
                        long temp = today.getTime() - currentBill.dueDate.getTime();
                        long diff = TimeUnit.DAYS.convert(temp, TimeUnit.MILLISECONDS);
                        long limit = Integer.parseInt(currSpinner);
                        if (diff <= limit) {
                            nlist.add(currentBill);
                        }
                    }
                }

                // filter bill statuses
                if ((!isSpinner || constraintString.equals("refresh")) && !nlist.contains(currentBill)) {
                    if (currentBill.color.equals("red")) {
                        if (currChecked[0]) nlist.add(currentBill);
                    }
                    else if (currentBill.color.equals("green")) {
                        if (currChecked[1]) nlist.add(currentBill);
                    }
                    else if (currentBill.color.equals("yellow")) {
                        if (currChecked[2]) nlist.add(currentBill);
                    }
                }

            }

            // do sorting by color & date
            Collections.sort(nlist, Collections.reverseOrder(new BillComparator()));

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listFiltered = (ArrayList<Bill>) results.values;
            notifyDataSetChanged();
        }
    }

}
