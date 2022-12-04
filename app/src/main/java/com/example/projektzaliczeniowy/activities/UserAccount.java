package com.example.projektzaliczeniowy.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektzaliczeniowy.MainActivity;
import com.example.projektzaliczeniowy.R;
import com.example.projektzaliczeniowy.adapters.UserImagesListViewItemAdapter;
import com.example.projektzaliczeniowy.database.ShopContract;
import com.example.projektzaliczeniowy.database.ShopOperations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UserAccount extends AppCompatActivity {
    private static final String TAG = "ProjektZaliczeniowy_UserAccount";

    String userUsername;
    int userID;

    TextView welcomeMessage;
    Button publishImageButton, logoutButton;
    ListView userImages;

    ActivityResultLauncher publishImageLauncher;
    String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        SharedPreferences sharedPreferences = getSharedPreferences("logged_user", Context.MODE_PRIVATE);
        userUsername = sharedPreferences.getString("user_username", "");
        userID = sharedPreferences.getInt("user_id", -1);

        if(userID < 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.cookies_error), Toast.LENGTH_SHORT).show();
            finish();
        }

        welcomeMessage = findViewById(R.id.welcome_message);
        welcomeMessage.setText(String.format("%s\n%s!", getResources().getString(R.string.welcome_message), userUsername));

        publishImageButton = findViewById(R.id.publish_image_button);
        publishImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            publishImageLauncher.launch(intent);
        });

        logoutButton = findViewById(R.id.log_out_button);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.log_out_confirmation), Toast.LENGTH_SHORT).show();
            finish();
        });

        userImages = findViewById(R.id.user_images);

        publishImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getData().getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_image_fail),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } catch (NullPointerException e) {
                            return;
                        }

//                        Formatter.formatFileSize(getApplicationContext(), bitmap.getByteCount())
                        Log.v(TAG, "ByteCount: " + String.valueOf(bitmap.getWidth() * bitmap.getHeight()));

                        // Converts bitmap to String
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        imageString = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                        Log.v(TAG, "String length: " + imageString.length());

                        if(ShopOperations.publishImage(getApplicationContext(), imageString, userID)) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_image_successful),
                                    Toast.LENGTH_SHORT).show();
                            recreate();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_image_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Loads all images user has published
        ArrayList<String> userImagesStrings = ShopOperations.getImages(getApplicationContext(), userID);
        UserImagesListViewItemAdapter adapter = new UserImagesListViewItemAdapter(this, userImagesStrings);
        userImages.setAdapter(adapter);
    }
}