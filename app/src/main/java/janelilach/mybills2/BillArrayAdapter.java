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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
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

    public BillArrayAdapter(ArrayList<Bill> list, Context context, Activity activity) {
        this.listOriginal = list;
        this.listFiltered = list;
        this.context = context;
        this.activity = activity;
        filter = new BillFilter();
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
        Bill thisBill;
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
//        deleteBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                list.remove(position); //or some other task
//                notifyDataSetChanged();
//            }
//        });

        Button editBtn = (Button) view.findViewById(R.id.bill_elem_edit);
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // send bill data back
                Intent i = new Intent(v.getContext(), AddBillActivity.class);
                Bill thisBill = listFiltered.get(position);
                Bundle b = new Bundle();
                b.putSerializable("bill", thisBill);
                i.putExtra("bill", b);
                activity.startActivityForResult(i, 1);
                notifyDataSetChanged();
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

            final ArrayList<Bill> nlist = new ArrayList<Bill>(count);

            // check if sent spinner or checkbox value
            boolean isSpinner = false;
            String constraintString = constraint.toString();
            if (constraintString.indexOf(',') ==  -1) {
                isSpinner = true;
            }

            boolean[] isChecked = new boolean[3];
            if (!isSpinner) {
                StringTokenizer tokenizer = new StringTokenizer(constraint.toString());
                int iter = 0;
                while (tokenizer.hasMoreTokens()) {
                    isChecked[iter] = Integer.parseInt(tokenizer.nextToken(",")) == 1 ? true : false;
                    iter++;
                }
            }

            Bill currentBill;
            for (int i = 0; i < count; i++) {
                currentBill = list.get(i);

                // quick and dirty way to render add/remove instantly
                if (constraintString.equals("add") || constraintString.equals("remove")) {
                    nlist.add(currentBill);
                }
                // filter bill dates
                else if (isSpinner) {
                    if (constraintString.equals("All")) {
                        nlist.add(currentBill);
                    } else {
                        Date today = new Date();
                        long temp = today.getTime() - currentBill.dueDate.getTime();
                        long diff = TimeUnit.DAYS.convert(temp, TimeUnit.MILLISECONDS);
                        long limit = Integer.parseInt(constraintString);
                        if (diff <= limit) {
                            nlist.add(currentBill);
                        }
                    }
                }

                // filter bill statuses
                else {
                    if (currentBill.color.equals("red")) {
                        if (isChecked[0]) nlist.add(currentBill);
                    }
                    else if (currentBill.color.equals("green")) {
                        if (isChecked[1]) nlist.add(currentBill);
                    }
                    else if (currentBill.color.equals("yellow")) {
                        if (isChecked[2]) nlist.add(currentBill);
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
        protected void publishResults(CharSequence constrain, FilterResults results) {
            listFiltered = (ArrayList<Bill>) results.values;
            notifyDataSetChanged();
        }
    }

}
