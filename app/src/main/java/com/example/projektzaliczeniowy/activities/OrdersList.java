package com.example.projektzaliczeniowy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.projektzaliczeniowy.R;
import com.example.projektzaliczeniowy.adapters.OrderListViewItemAdapter;
import com.example.projektzaliczeniowy.database.ShopOperations;
import com.example.projektzaliczeniowy.models.Order;

import java.util.ArrayList;

public class OrdersList extends AppCompatActivity {
    ListView ordersListListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        ordersListListView = findViewById(R.id.orders_list_listview);

        ArrayList<Order> orders = ShopOperations.readAllOrdersFromDatabase(getApplicationContext());
        OrderListViewItemAdapter adapter = new OrderListViewItemAdapter(this, orders);
        ordersListListView.setAdapter(adapter);
    }
}