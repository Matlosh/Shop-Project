package com.example.projektzaliczeniowy.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projektzaliczeniowy.R;
import com.example.projektzaliczeniowy.models.ShopProduct;

import java.util.ArrayList;

public class ShopSpinnerItemAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<ShopProduct> shopProducts;

    public ShopSpinnerItemAdapter(ArrayList<ShopProduct> shopProducts, Activity context) {
        this.shopProducts = shopProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.shop_spinner_item, null, true);

        ShopProduct shopProduct = shopProducts.get(position);

        TextView item_description = customView.findViewById(R.id.shop_spinner_item_description);
        item_description.setText(String.format("%s %dzł", shopProduct.getDescription(), shopProduct.getPrice()));

        ImageView item_image = customView.findViewById(R.id.shop_spinner_item_image);
        item_image.setImageResource(shopProduct.getImage());

        return customView;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return shopProducts.size();
    }

    public ArrayList<ShopProduct> getShopProducts() {
        return shopProducts;
    }
}
