package com.example.projektzaliczeniowy.models;

import android.app.Activity;
import android.content.Context;

import com.example.projektzaliczeniowy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Order {
    public String customerName, customerSurname, computerSet, keyboard, mouse, monitor, orderDate;
    public int price;
    Context context;

    public Order(String customerName, String customerSurname, String computerSet, String keyboard,
                 String mouse, String monitor, int price, String orderDate, Context context) {
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.computerSet = computerSet;
        this.keyboard = keyboard;
        this.mouse = mouse;
        this.monitor = monitor;
        this.price = price;
        this.orderDate = orderDate;
        this.context = context;
    }

//    public ArrayList<String> getAllPropertiesValues() {
//        return new ArrayList<>(
//                Arrays.asList(
//                        customerName,
//                        customerSurname,
//                        computerSet,
//                        keyboard,
//                        mouse,
//                        monitor,
//                        String.valueOf(price),
//                        orderDate
//                )
//        );
//    }

    public LinkedHashMap<String, String> getAllProperties() {
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put(context.getResources().getString(R.string.order_customer_name_header), customerName);
        properties.put(context.getResources().getString(R.string.order_customer_surname_header), customerSurname);
        properties.put(context.getResources().getString(R.string.order_computer_set_header), computerSet);
        if(keyboard != null) properties.put(context.getResources().getString(R.string.order_keyboard_header), keyboard);
        if(mouse != null) properties.put(context.getResources().getString(R.string.order_mouse_header), mouse);
        if(monitor != null) properties.put(context.getResources().getString(R.string.order_monitor_header), monitor);
        properties.put(context.getResources().getString(R.string.order_price_header), price + " zł");
        properties.put(context.getResources().getString(R.string.order_order_date_header), orderDate);

        return properties;
    }
}
