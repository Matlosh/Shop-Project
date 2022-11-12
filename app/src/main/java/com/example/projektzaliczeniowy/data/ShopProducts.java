package com.example.projektzaliczeniowy.data;

import android.widget.ArrayAdapter;

import com.example.projektzaliczeniowy.models.ShopProduct;

import java.util.ArrayList;

public class ShopProducts {
    public static ArrayList<ShopProduct> getSets() {
        ArrayList<ShopProduct> sets = new ArrayList<>();

        ShopProduct set1 = new ShopProduct(
                ShopProductsConstants.SET1_DESCRIPTION,
                ShopProductsConstants.SET1_PRICE,
                ShopProductsConstants.SET1_IMAGE
        );

        ShopProduct set2 = new ShopProduct(
                ShopProductsConstants.SET2_DESCRIPTION,
                ShopProductsConstants.SET2_PRICE,
                ShopProductsConstants.SET2_IMAGE
        );

        ShopProduct set3 = new ShopProduct(
                ShopProductsConstants.SET3_DESCRIPTION,
                ShopProductsConstants.SET3_PRICE,
                ShopProductsConstants.SET3_IMAGE
        );

        sets.add(set1);
        sets.add(set2);
        sets.add(set3);

        return sets;
    }

    public static ArrayList<ShopProduct> getKeyboards() {
        ArrayList<ShopProduct> keyboards = new ArrayList<>();

        ShopProduct keyboard1 = new ShopProduct(
                ShopProductsConstants.KEYBOARD1_DESCRIPTION,
                ShopProductsConstants.KEYBOARD1_PRICE,
                ShopProductsConstants.KEYBOARD1_IMAGE
        );

        ShopProduct keyboard2 = new ShopProduct(
                ShopProductsConstants.KEYBOARD2_DESCRIPTION,
                ShopProductsConstants.KEYBOARD2_PRICE,
                ShopProductsConstants.KEYBOARD2_IMAGE
        );

        ShopProduct keyboard3 = new ShopProduct(
                ShopProductsConstants.KEYBOARD3_DESCRIPTION,
                ShopProductsConstants.KEYBOARD3_PRICE,
                ShopProductsConstants.KEYBOARD3_IMAGE
        );

        keyboards.add(keyboard1);
        keyboards.add(keyboard2);
        keyboards.add(keyboard3);

        return keyboards;
    }

    public static ArrayList<ShopProduct> getMouses() {
        ArrayList<ShopProduct> mouses = new ArrayList<>();

        ShopProduct mouse1 = new ShopProduct(
                ShopProductsConstants.MOUSE1_DESCRIPTION,
                ShopProductsConstants.MOUSE1_PRICE,
                ShopProductsConstants.MOUSE1_IMAGE
        );

        ShopProduct mouse2 = new ShopProduct(
                ShopProductsConstants.MOUSE2_DESCRIPTION,
                ShopProductsConstants.MOUSE2_PRICE,
                ShopProductsConstants.MOUSE2_IMAGE
        );

        ShopProduct mouse3 = new ShopProduct(
                ShopProductsConstants.MOUSE3_DESCRIPTION,
                ShopProductsConstants.MOUSE3_PRICE,
                ShopProductsConstants.MOUSE3_IMAGE
        );

        mouses.add(mouse1);
        mouses.add(mouse2);
        mouses.add(mouse3);

        return mouses;
    }

    public static ArrayList<ShopProduct> getMonitors() {
        ArrayList<ShopProduct> monitors = new ArrayList<>();

        ShopProduct monitor1 = new ShopProduct(
                ShopProductsConstants.MONITOR1_DESCRIPTION,
                ShopProductsConstants.MONITOR1_PRICE,
                ShopProductsConstants.MONITOR1_IMAGE
        );

        ShopProduct monitor2 = new ShopProduct(
                ShopProductsConstants.MONITOR2_DESCRIPTION,
                ShopProductsConstants.MONITOR2_PRICE,
                ShopProductsConstants.MONITOR2_IMAGE
        );

        ShopProduct monitor3 = new ShopProduct(
                ShopProductsConstants.MONITOR3_DESCRIPTION,
                ShopProductsConstants.MONITOR3_PRICE,
                ShopProductsConstants.MONITOR3_IMAGE
        );

        monitors.add(monitor1);
        monitors.add(monitor2);
        monitors.add(monitor3);

        return monitors;
    }
}
