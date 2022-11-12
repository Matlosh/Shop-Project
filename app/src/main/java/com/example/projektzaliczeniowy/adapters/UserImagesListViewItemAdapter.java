package com.example.projektzaliczeniowy.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.projektzaliczeniowy.R;

import java.util.ArrayList;

public class UserImagesListViewItemAdapter extends BaseAdapter {
    Activity context;
    ArrayList<String> stringImages;

    public UserImagesListViewItemAdapter(Activity context, ArrayList<String> stringImages) {
        this.context = context;
        this.stringImages = stringImages;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.user_images_listview_item, null, true);

        String stringImage = stringImages.get(i);
        byte[] imageBytes = Base64.decode(stringImage.getBytes(), Base64.NO_WRAP);
        Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        ImageView userImage = itemView.findViewById(R.id.user_images_image);
        userImage.setImageBitmap(image);

        return itemView;
    }

    @Override
    public int getCount() {
        return stringImages.size();
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
