package com.example.projektzaliczeniowy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.projektzaliczeniowy.R;
import com.example.projektzaliczeniowy.database.ShopOperations;

public class LogIn extends AppCompatActivity {
    private static final String TAG = "ProjektZaliczeniowy_LogIn";

    Button loginSubmenuButton, registerSubmenuButton, loginButton, registerButton;
    EditText loginUsername, loginPassword, registerUsername, registerPassword;
    RelativeLayout loginPanel, registerPanel;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loginPanel = findViewById(R.id.login_panel);
        registerPanel = findViewById(R.id.register_panel);
        loginSubmenuButton = findViewById(R.id.login_submenu_button);
        registerSubmenuButton = findViewById(R.id.register_submenu_button);
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        registerUsername = findViewById(R.id.register_username);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);

        loginSubmenuButton.setOnClickListener(v -> {
            showLoginPanel();
        });

        registerSubmenuButton.setOnClickListener(v -> {
            showRegisterPanel();
        });

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());

        sharedPreferences = getSharedPreferences("logged_user", Context.MODE_PRIVATE);
    }

    private void registerUser() {
        String username = registerUsername.getText().toString();
        String password = registerPassword.getText().toString();

        if(validateFields(registerUsername, registerPassword)) {
            if(ShopOperations.registerUser(getApplicationContext(), username, password)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_successful), Toast.LENGTH_SHORT).show();
                clearForm();
                showLoginPanel();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_unsuccessful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser() {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        if(validateFields(loginUsername, loginPassword)) {
            int userID = ShopOperations.loginUser(getApplicationContext(), username, password);
            if(userID >= 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                clearForm();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", userID);
                editor.putString("user_username", username);
                editor.apply();

                finish();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_unsuccessful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns true if all fields are correct, else false
    private boolean validateFields(EditText username, EditText password) {
        boolean isCorrect = true;

        if(username.length() == 0) {
            username.setError(getResources().getString(R.string.empty_username_error));
            isCorrect = false;
        }
        if(password.length() == 0) {
            password.setError(getResources().getString(R.string.empty_password_error));
            isCorrect = false;
        }

        return isCorrect;
    }

    private void clearForm() {
        registerUsername.setText("");
        registerPassword.setText("");
        loginUsername.setText("");
        loginPassword.setText("");
    }

    private void showLoginPanel() {
        registerPanel.setVisibility(View.GONE);
        loginPanel.setVisibility(View.VISIBLE);
    }

    private void showRegisterPanel() {
        loginPanel.setVisibility(View.GONE);
        registerPanel.setVisibility(View.VISIBLE);
    }
}