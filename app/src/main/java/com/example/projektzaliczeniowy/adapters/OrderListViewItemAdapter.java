package com.example.projektzaliczeniowy.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projektzaliczeniowy.R;
import com.example.projektzaliczeniowy.models.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderListViewItemAdapter extends BaseAdapter {
    Activity context;
    ArrayList<Order> orders;

    public OrderListViewItemAdapter(Activity context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View customView = inflater.inflate(R.layout.order_listview_item, null, true);

        Order currentOrder = orders.get(position);
        LinkedHashMap<String, String> allPropertiesValues = currentOrder.getAllProperties();

        LinearLayout viewLayout = customView.findViewById(R.id.order_listview_entry_layout);

//        StringBuilder orderEntry = new StringBuilder();
        for(LinkedHashMap.Entry<String, String> entry: allPropertiesValues.entrySet()) {
//            orderEntry.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));

            // Creates entry element
            View entryView = context.getLayoutInflater().inflate(R.layout.element_listview_item_entry_line, null, true);

            TextView entryHeader = entryView.findViewById(R.id.element_listview_item_entry_header);
            entryHeader.setText(entry.getKey() + ":");

            TextView entryText = entryView.findViewById(R.id.element_listview_item_entry_text);
            entryText.setText(entry.getValue());

            viewLayout.addView(entryView);
        }

        TextView orderListViewEntryHeader = customView.findViewById(R.id.order_listview_entry_header);
        orderListViewEntryHeader.setText(String.format("%s%d", context.getResources().getString(R.string.order_header), position + 1));

//        TextView orderListViewEntry = customView.findViewById(R.id.order_listview_entry);
//        orderListViewEntry.setText(orderEntry.toString());

        return customView;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
