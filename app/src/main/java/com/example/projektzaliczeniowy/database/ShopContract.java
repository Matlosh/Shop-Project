package com.example.projektzaliczeniowy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class ShopContract {
    private static final String TAG = "ProjektZaliczeniowy_ShopContract";

    private static final String SQL_CREATE_ORDERS =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER," +
                    "%s TEXT)",
                    Orders.TABLE_NAME, Orders._ID, Orders.COLUMN_NAME_CUSTOMER_NAME,
                    Orders.COLUMN_NAME_CUSTOMER_SURNAME, Orders.COLUMN_NAME_COMPUTER_SET,
                    Orders.COLUMN_NAME_KEYBOARD, Orders.COLUMN_NAME_MOUSE, Orders.COLUMN_NAME_MONITOR,
                    Orders.COLUMN_NAME_PRICE, Orders.COLUMN_NAME_ORDER_DATE);

    private static final String SQL_DELETE_ORDERS =
            String.format("DROP TABLE IF EXISTS %s", Orders.TABLE_NAME);

    private static final String SQL_CREATE_USERS =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT, %s TEXT)",
                    Users.TABLE_NAME, Users._ID, Users.COLUMN_NAME_USERNAME, Users.COLUMN_NAME_PASSWORD);

    private static final String SQL_DELETE_USERS =
            String.format("DROP TABLE IF EXISTS %s", Users.TABLE_NAME);

    private static final String SQL_CREATE_IMAGES =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s INTEGER, %s TEXT)",
                    Images.TABLE_NAME, Images._ID, Images.COLUMN_NAME_USER_ID, Images.COLUMN_NAME_IMAGE);

    private static final String SQL_DELETE_IMAGES =
            String.format("DROP TABLE IF EXISTS %s", Images.TABLE_NAME);

    private ShopContract() {}

    // Orders
    public static class Orders implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_CUSTOMER_NAME = "customer_name";
        public static final String COLUMN_NAME_CUSTOMER_SURNAME = "customer_surname";
        public static final String COLUMN_NAME_COMPUTER_SET = "computer_set";
        public static final String COLUMN_NAME_KEYBOARD = "keyboard";
        public static final String COLUMN_NAME_MOUSE = "mouse";
        public static final String COLUMN_NAME_MONITOR = "monitor";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_ORDER_DATE = "order_date";
    }

    // Users
    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    // Images
    public static class Images implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_IMAGE = "image";
    }

    public static class ShopDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 8;
        public static final String DATABASE_NAME = "Shop.db";
        private final String TABLE_NAME;

        public ShopDbHelper(Context context, String TABLE_NAME) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.TABLE_NAME = TABLE_NAME;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, TABLE_NAME);
//            switch(TABLE_NAME) {
//                case Orders.TABLE_NAME:
                    db.execSQL(SQL_CREATE_ORDERS);
//                    break;

//                case Users.TABLE_NAME:
                    db.execSQL(SQL_CREATE_USERS);
//                    break;

//                case Images.TABLE_NAME:
                    db.execSQL(SQL_CREATE_IMAGES);
//                    break;
//            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            switch(TABLE_NAME) {
//                case Orders.TABLE_NAME:
                    db.execSQL(SQL_DELETE_ORDERS);
//                    break;

//                case Users.TABLE_NAME:
                    db.execSQL(SQL_DELETE_USERS);
//                    break;

//                case Images.TABLE_NAME:
                    db.execSQL(SQL_DELETE_IMAGES);
//                    break;
//            }
            onCreate(db);
        }
    }
}
