package com.example.projektzaliczeniowy.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.projektzaliczeniowy.models.Order;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ShopOperations {
    private static final String TAG = "ProjektZaliczeniowy_ShopOperations";

    // Inserts to the database and:
    //  - returns 0 if inserted correctly
    //  - returns 1 if for some reason data couldn't be inserted
    public static int insertToDatabase(Context context, Order order) {
        try {
            ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Orders.TABLE_NAME);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ShopContract.Orders.COLUMN_NAME_CUSTOMER_NAME, order.customerName);
            values.put(ShopContract.Orders.COLUMN_NAME_CUSTOMER_SURNAME, order.customerSurname);
            values.put(ShopContract.Orders.COLUMN_NAME_COMPUTER_SET, order.computerSet);
            values.put(ShopContract.Orders.COLUMN_NAME_KEYBOARD, order.keyboard);
            values.put(ShopContract.Orders.COLUMN_NAME_MOUSE, order.mouse);
            values.put(ShopContract.Orders.COLUMN_NAME_MONITOR, order.monitor);
            values.put(ShopContract.Orders.COLUMN_NAME_PRICE, order.price);
            values.put(ShopContract.Orders.COLUMN_NAME_ORDER_DATE, order.orderDate);

            db.insert(ShopContract.Orders.TABLE_NAME, null, values);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public static ArrayList<Order> readAllOrdersFromDatabase(Context context) {
        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Orders.TABLE_NAME);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<Order> orders = new ArrayList<>();

        String[] columns = {
                ShopContract.Orders.COLUMN_NAME_CUSTOMER_NAME,
                ShopContract.Orders.COLUMN_NAME_CUSTOMER_SURNAME,
                ShopContract.Orders.COLUMN_NAME_COMPUTER_SET,
                ShopContract.Orders.COLUMN_NAME_KEYBOARD,
                ShopContract.Orders.COLUMN_NAME_MOUSE,
                ShopContract.Orders.COLUMN_NAME_MONITOR,
                ShopContract.Orders.COLUMN_NAME_PRICE,
                ShopContract.Orders.COLUMN_NAME_ORDER_DATE
        };

        Cursor cursor = db.query(
                ShopContract.Orders.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            String customerName = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_CUSTOMER_NAME));
            String customerSurname = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_CUSTOMER_SURNAME));
            String computerSet = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_COMPUTER_SET));
            String keyboard = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_KEYBOARD));
            String mouse = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_MOUSE));
            String monitor = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_MONITOR));
            String price = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_PRICE));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Orders.COLUMN_NAME_ORDER_DATE));

            Order order = new Order(customerName, customerSurname, computerSet, keyboard, mouse, monitor,
                    Integer.parseInt(price), orderDate, context);

            orders.add(order);
        }
        cursor.close();

        return orders;
    }

    // Returns:
    //  - true - if user was successfully registered
    //  - false - if user wasn't registered
    public static boolean registerUser(Context context, String username, String password) {
        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Users.TABLE_NAME);

        // Checks if account with username like this already exists
        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();

        String[] projection = {
                ShopContract.Users.COLUMN_NAME_USERNAME
        };

        String selection = ShopContract.Users.COLUMN_NAME_USERNAME + "= ?";
        String[] selectionArgs = {username};

        Cursor cursor = dbReadable.query(
                ShopContract.Users.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // if cursor isn't empty it means that user with that username already exists
        if(cursor.getCount() > 0) return false;
        cursor.close();

        dbReadable.close();

        // Creates a new account
        String hashedPassword = "";
        try {
            hashedPassword = generateHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }

        SQLiteDatabase dbWriteable = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ShopContract.Users.COLUMN_NAME_USERNAME, username);
        values.put(ShopContract.Users.COLUMN_NAME_PASSWORD, hashedPassword);

        dbWriteable.insert(ShopContract.Users.TABLE_NAME, null, values);
        dbWriteable.close();

        return true;
    }

    // Returns:
    //  - user ID (>= 0) - if user was logged in successfully
    //  - -1 - if user wasn't logged in
    public static int loginUser(Context context, String username, String password) {
        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Users.TABLE_NAME);
        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();

        String[] projection = {
                ShopContract.Users._ID,
                ShopContract.Users.COLUMN_NAME_USERNAME,
                ShopContract.Users.COLUMN_NAME_PASSWORD
        };

        String selection = ShopContract.Users.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = dbReadable.query(
                ShopContract.Users.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.getCount() < 1) return -1;

        cursor.moveToNext();
        String hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Users.COLUMN_NAME_PASSWORD));
        int userID = cursor.getInt(cursor.getColumnIndexOrThrow(ShopContract.Users._ID));
        dbReadable.close();

        try {
             if(!validatePassword(password, hashedPassword)) return -1;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return -1;
        }

        return userID;
    }

    // Publishes an image in the database
    // Returns:
    //  - true - if image was successfully published
    //  - false - if image wasn't published
    public static boolean publishImage(Context context, String imageString, int userID) {
        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Images.TABLE_NAME);
        SQLiteDatabase dbWriteable = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ShopContract.Images.COLUMN_NAME_USER_ID, userID);
        values.put(ShopContract.Images.COLUMN_NAME_IMAGE, imageString);

        long rowID = dbWriteable.insert(ShopContract.Images.TABLE_NAME, null, values);
        dbWriteable.close();

        return rowID != -1;
    }

    @SuppressLint("Range")
    public static ArrayList<String> getTableRowsNumber(Context context, String TABLE_NAME, int userID) {
        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, TABLE_NAME);
        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();

        ArrayList<String> ids = new ArrayList<>();

        String[] projection = {
                ShopContract.Images._ID
        };

        String selection = ShopContract.Images.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = dbReadable.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

//        int rowsNum = cursor.getCount();
        while(cursor.moveToNext()) {
            ids.add(cursor.getString(cursor.getColumnIndex(ShopContract.Images._ID)));
        }
        cursor.close();
        dbReadable.close();

        return ids;
    }

//    public static long getStringMaxValue(Context context, String TABLE_NAME, String COLUMN_NAME)

    @SuppressLint("Range")
    public static ArrayList<String> getImages(Context context, int userID) {
//        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Images.TABLE_NAME);
//        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();
//
//        ArrayList<String> images = new ArrayList<>();
//        ArrayList<String> ids = getTableRowsNumber(context, ShopContract.Images.TABLE_NAME, userID);
//
//        for(int i = 0; i < ids.size(); i++) {
//            Cursor cursor1 = dbReadable.rawQuery(
//                    String.format("SELECT length(%s) FROM %s WHERE %s = %s",
//                            ShopContract.Images.COLUMN_NAME_IMAGE,
//                            ShopContract.Images.TABLE_NAME,
//                            ShopContract.Images._ID,
//                            ids.get(i)),
//                    null);
//
//            Log.v(TAG, "Cursor count: " + cursor1.getCount());
//
//            long stringLength = 0;
//            while(cursor1.moveToNext()) {
////                images.add(cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Images.COLUMN_NAME_IMAGE)));
//                stringLength = Long.parseLong(cursor1.getString(cursor1.getColumnIndex(cursor1.getColumnName(0))));
////                Log.v(TAG, "Fetched data: " + cursor1.getString(cursor1.getColumnIndex(cursor1.getColumnName(0))));
//            }
//            cursor1.close();
//            Log.v(TAG, "stringLength:" + String.valueOf(stringLength));
//
//            long maxAllocationSize = 10000;
//            String image = "";
//            for(long j = 0; j < stringLength; j += maxAllocationSize) {
//                Cursor dataCursor = dbReadable.rawQuery(
//                        String.format("SELECT substr(%s, %s, %s) FROM %s WHERE %s = %s",
//                                ShopContract.Images.COLUMN_NAME_IMAGE,
//                                j,
//                                j + maxAllocationSize,
//                                ShopContract.Images.TABLE_NAME,
//                                ShopContract.Images._ID,
//                                ids.get(i)),
//                        null);
//
//                String imageTemp = "";
//                while(dataCursor.moveToNext()) {
////                    Log.v(TAG, dataCursor.getString(dataCursor.getColumnIndex(dataCursor.getColumnName(0))));
//                    imageTemp = dataCursor.getString(dataCursor.getColumnIndex(dataCursor.getColumnName(0)));
//                }
//                dataCursor.close();
//
//                image += imageTemp;
//            }
//        }
//
//        dbReadable.close();
//
//        return images;

        ShopContract.ShopDbHelper dbHelper = new ShopContract.ShopDbHelper(context, ShopContract.Images.TABLE_NAME);
        SQLiteDatabase dbReadable = dbHelper.getReadableDatabase();

        String[] projection = {
                ShopContract.Images.COLUMN_NAME_IMAGE
        };

        String selection = ShopContract.Images.COLUMN_NAME_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = dbReadable.query(
                ShopContract.Images.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ArrayList<String> images = new ArrayList<>();
        while(cursor.moveToNext()) {
            images.add(cursor.getString(cursor.getColumnIndexOrThrow(ShopContract.Images.COLUMN_NAME_IMAGE)));
        }
        cursor.close();

        dbReadable.close();

        return images;
    }

    // Source for below
    // https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/

    // Generates salt for hashed password
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] saltBytes = new byte[16];
        secureRandom.nextBytes(saltBytes);
        return saltBytes;
    }

    // Generates password's hash
    private static String generateHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    // Validates password and returns yes if passwords are the same, else false
    private static boolean validatePassword(String originalPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(),
                salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
            diff |= hash[i] ^ testHash[i];

        return diff == 0;
    }

    private static String toHex(byte[] array) {
        BigInteger big = new BigInteger(1, array);
        String hex = big.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) return String.format("%0" + paddingLength + "d", 0) + hex;
        else return hex;
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);

        return bytes;
    }
}
