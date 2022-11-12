package com.example.projektzaliczeniowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektzaliczeniowy.activities.AboutApp;
import com.example.projektzaliczeniowy.activities.LogIn;
import com.example.projektzaliczeniowy.activities.OrdersList;
import com.example.projektzaliczeniowy.activities.UserAccount;
import com.example.projektzaliczeniowy.adapters.ShopSpinnerItemAdapter;
import com.example.projektzaliczeniowy.data.ShopProducts;
import com.example.projektzaliczeniowy.database.ShopContract;
import com.example.projektzaliczeniowy.database.ShopOperations;
import com.example.projektzaliczeniowy.models.Order;
import com.example.projektzaliczeniowy.models.ShopProduct;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "ProjektZaliczeniowy_MainActivity";

    ShopSpinnerItemAdapter setsAdapter, keyboardsAdapter, mousesAdapter, monitorsAdapter;
    Spinner computerSetsSpinner, keyboardsSpinner, mousesSpinner, monitorsSpinner;
    Button orderButton;
    EditText customerNameInput, customerSurnameInput;
    CheckBox keyboardCheckbox, mouseCheckbox, monitorCheckbox;
    TextView totalPriceText;
    ShopProduct computerSetProduct, keyboardProduct, mouseProduct, monitorProduct;

    boolean isFrozen = false;

//    String customerName, customerSurname, computerSet, keyboard, mouse, monitor;
//    int price;
    SharedPreferences sharedPreferences, loggedUserPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        computerSetsSpinner = findViewById(R.id.computer_sets_spinner);
        keyboardsSpinner = findViewById(R.id.keyboards_spinner);
        mousesSpinner = findViewById(R.id.mouses_spinner);
        monitorsSpinner = findViewById(R.id.monitors_spinner);

        keyboardCheckbox = findViewById(R.id.keyboard_checkbox);
        mouseCheckbox = findViewById(R.id.mouse_checkbox);
        monitorCheckbox = findViewById(R.id.monitor_checkbox);

        orderButton = findViewById(R.id.order_button);

        customerNameInput = findViewById(R.id.customer_name_input);
        customerSurnameInput = findViewById(R.id.customer_surname_input);

        totalPriceText = findViewById(R.id.total_price);

        initView();
        initListeners();
        setCurrentProducts();

        sharedPreferences = getSharedPreferences("app_state", Context.MODE_PRIVATE);
        loggedUserPreferences = getSharedPreferences("logged_user", Context.MODE_PRIVATE);

        // Loads default (if any) saved state
        // Computer set must be available in every order/saved state
        // that's why it's availability is being checked
        if(sharedPreferences.getInt("computer_set_pos", -1) >= 0) {
            computerSetsSpinner.setSelection(sharedPreferences.getInt("computer_set_pos", 0));
            keyboardCheckbox.setChecked(sharedPreferences.getBoolean("keyboard_checked", false));
            keyboardsSpinner.setSelection(sharedPreferences.getInt("keyboard_pos", 0));
            mouseCheckbox.setChecked(sharedPreferences.getBoolean("mouse_checked", false));
            mousesSpinner.setSelection(sharedPreferences.getInt("mouse_pos", 0));
            monitorCheckbox.setChecked(sharedPreferences.getBoolean("monitor_checked", false));
            monitorsSpinner.setSelection(sharedPreferences.getInt("monitor_pos", 0));
            customerNameInput.setText(sharedPreferences.getString("customers_name", ""));
            customerSurnameInput.setText(sharedPreferences.getString("customers_surname", ""));
            totalPriceText.setText(Integer.toString(calculateTotalPrice()));
        }
    }

    private void initView() {
        setsAdapter = new ShopSpinnerItemAdapter(ShopProducts.getSets(), this);
        computerSetsSpinner.setAdapter(setsAdapter);

        keyboardsAdapter = new ShopSpinnerItemAdapter(ShopProducts.getKeyboards(), this);
        keyboardsSpinner.setAdapter(keyboardsAdapter);

        mousesAdapter = new ShopSpinnerItemAdapter(ShopProducts.getMouses(), this);
        mousesSpinner.setAdapter(mousesAdapter);

        monitorsAdapter = new ShopSpinnerItemAdapter(ShopProducts.getMonitors(), this);
        monitorsSpinner.setAdapter(monitorsAdapter);
    }

    private void initListeners() {
        computerSetsSpinner.setOnItemSelectedListener(onSpinnerChange());
        keyboardsSpinner.setOnItemSelectedListener(onSpinnerChange());
        mousesSpinner.setOnItemSelectedListener(onSpinnerChange());
        monitorsSpinner.setOnItemSelectedListener(onSpinnerChange());

        keyboardCheckbox.setOnClickListener(onCheckboxClick());
        mouseCheckbox.setOnClickListener(onCheckboxClick());
        monitorCheckbox.setOnClickListener(onCheckboxClick());

        orderButton.setOnClickListener(v -> {
            boolean valid = true;
            Log.v(TAG, "order...");

            String customerName = customerNameInput.getText().toString();
            String customerSurname = customerSurnameInput.getText().toString();
            String computerSet = computerSetProduct.getDescription();
            String keyboard = keyboardCheckbox.isChecked() ? keyboardProduct.getDescription() : null;
            String mouse = mouseCheckbox.isChecked() ? mouseProduct.getDescription() : null;
            String monitor = monitorCheckbox.isChecked() ? monitorProduct.getDescription() : null;

            // Validates customer's personal data
            if(customerName.isEmpty()) {
                customerNameInput.setError(getResources().getString(R.string.customer_name_error_empty));
                valid = false;
            }
            if(customerSurname.isEmpty()) {
                customerSurnameInput.setError(getResources().getString(R.string.customer_surname_error_empty));
                valid = false;
            }
            if(!valid) return;

            int price = calculateTotalPrice();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String orderDate = formatter.format(date);

            // Inserts order into the database
            Order order = new Order(customerName, customerSurname, computerSet, keyboard,
                    mouse, monitor, price, orderDate, getApplicationContext());

            int result = ShopOperations.insertToDatabase(getApplicationContext(), order);
            if(result == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.order_ordered_positive),
                        Toast.LENGTH_SHORT).show();
                clearForm();

                String orderMessage = String.format("Zamówienie na %s %s zostało złożone dnia %s. Koszt zamówienia wynosi %dzł. \nElementy zamówienia:",
                        customerName, customerSurname, orderDate, price);

                String[] orderElements = {computerSet, keyboard, mouse, monitor};
                for(String orderElement: orderElements) {
                    if(orderElement != null) orderMessage += "\n-" + orderElement;
                }

                showSmsEmailAlertDialog(orderMessage);

                // Clears SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
            }

            if(result != 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.order_ordered_negative),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sets list of current chosen products (in order to get their description, price, etc. later)
    private void setCurrentProducts() {
        computerSetProduct = setsAdapter.getShopProducts().get((int) computerSetsSpinner.getSelectedItemPosition());
        keyboardProduct = keyboardsAdapter.getShopProducts().get((int) keyboardsSpinner.getSelectedItemPosition());
        mouseProduct = mousesAdapter.getShopProducts().get((int) mousesSpinner.getSelectedItemPosition());
        monitorProduct = monitorsAdapter.getShopProducts().get((int) monitorsSpinner.getSelectedItemPosition());
    }

    private int calculateTotalPrice() {
        int price = computerSetProduct.getPrice();
        price += keyboardCheckbox.isChecked() ? keyboardProduct.getPrice() : 0;
        price += mouseCheckbox.isChecked() ? mouseProduct.getPrice() : 0;
        price += monitorCheckbox.isChecked() ? monitorProduct.getPrice() : 0;

        return price;
    }

    private void clearForm() {
        customerNameInput.setText("");
        customerSurnameInput.setText("");

        computerSetsSpinner.setSelection(0);
        keyboardsSpinner.setSelection(0);
        mousesSpinner.setSelection(0);
        monitorsSpinner.setSelection(0);

        keyboardCheckbox.setChecked(false);
        mouseCheckbox.setChecked(false);
        monitorCheckbox.setChecked(false);

        setCurrentProducts();
        totalPriceText.setText(calculateTotalPrice() + " zł");
    }

    // Shows AlertDialog with send sms/send email possibility
    private void showSmsEmailAlertDialog(String orderMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View alertdialog_view = inflater.inflate(R.layout.send_sms_email_alertdialog, null);

        EditText phoneNumberInput = alertdialog_view.findViewById(R.id.sms_phone_number);
        EditText emailAddressInput = alertdialog_view.findViewById(R.id.email_address);

        Button sendSMS = alertdialog_view.findViewById(R.id.send_sms_button);
        Button sendEmail = alertdialog_view.findViewById(R.id.send_email_button);

        sendSMS.setOnClickListener(v -> {
            if(phoneNumberInput.length() > 5) {
                String phoneNumber = phoneNumberInput.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", orderMessage);
                startActivity(intent);
            } else {
                phoneNumberInput.setError(getResources().getString(R.string.alertdialog_phone_number_error));
            }
        });

        sendEmail.setOnClickListener(v -> {
            if(emailAddressInput.length() > 5) {
                String emailAddress = emailAddressInput.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_message_subject));
                intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
                startActivity(intent);
            } else {
                emailAddressInput.setError(getResources().getString(R.string.alertdialog_email_address_error));
            }
        });

        builder.setView(alertdialog_view)
                .setNegativeButton(R.string.finish_button, (dialogInterface, i) -> {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.thank_you_message),
                            Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private AdapterView.OnItemSelectedListener onSpinnerChange() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrentProducts();
                totalPriceText.setText(calculateTotalPrice() + " zł");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    private View.OnClickListener onCheckboxClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentProducts();
                totalPriceText.setText(calculateTotalPrice() + " zł");
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(loggedUserPreferences.getInt("user_id", -1) < 0)
            inflater.inflate(R.menu.menu, menu);
        else
            inflater.inflate(R.menu.menu_user_logged_in, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_orders_list:
                startActivity(new Intent(this, OrdersList.class));
                break;

            case R.id.menu_login:
                startActivity(new Intent(this, LogIn.class));
                isFrozen = true;
                break;

            case R.id.menu_user_account:
                startActivity(new Intent(this, UserAccount.class));
                isFrozen = true;
                break;

            case R.id.menu_about:
                startActivity(new Intent(this, AboutApp.class));
                break;

            case R.id.menu_share:
                String currentOrderInfo = "";
                currentOrderInfo += computerSetProduct.getDescription() + "\n";
                currentOrderInfo += keyboardCheckbox.isChecked() ? keyboardProduct.getDescription() + "\n" : "";
                currentOrderInfo += mouseCheckbox.isChecked() ? mouseProduct.getDescription() + "\n" : "";
                currentOrderInfo += monitorCheckbox.isChecked() ? monitorProduct.getDescription() + "\n" : "";
                currentOrderInfo += String.valueOf(calculateTotalPrice()) + "zł";

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, currentOrderInfo);
                shareIntent.setType("text/plain");

                Intent sendIntent = Intent.createChooser(shareIntent, null);
                startActivity(sendIntent);
                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        Log.v(TAG, "onSaveInstanceState");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("computer_set_pos", computerSetsSpinner.getSelectedItemPosition());
        editor.putBoolean("keyboard_checked", keyboardCheckbox.isChecked());
        editor.putInt("keyboard_pos", keyboardsSpinner.getSelectedItemPosition());
        editor.putBoolean("mouse_checked", mouseCheckbox.isChecked());
        editor.putInt("mouse_pos", mousesSpinner.getSelectedItemPosition());
        editor.putBoolean("monitor_checked", monitorCheckbox.isChecked());
        editor.putInt("monitor_pos", monitorsSpinner.getSelectedItemPosition());
        editor.putString("customers_name", customerNameInput.getText().toString());
        editor.putString("customers_surname", customerSurnameInput.getText().toString());
        editor.apply();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recreates if user has logged out
        if(isFrozen) {
            isFrozen = false;
            recreate();
        }
    }
}