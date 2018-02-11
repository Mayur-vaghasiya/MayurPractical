package com.example.mayurpractical.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.mayurpractical.model.CountryModel;
import com.example.mayurpractical.util.CommonUtility;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "countryManager";
    // Contacts table name
    private static final String TABLE_COUNTRY = "COUNTRY";

    public String databasePath = "";


    // Contacts Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_CODE_ONE = "CODEONE";
    private static final String KEY_CODE_TWO = "CODETWO";
    private static final String KEY_IMG_FLAG = "IMGPATH";

    SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }


    public void open() {
        db = this.getWritableDatabase();
    }

    public void close() {
        db.close();
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_NAME + " TEXT,"
                + KEY_CODE_ONE + " TEXT," + KEY_CODE_TWO + " TEXT," + KEY_IMG_FLAG + " BLOB" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        // Create tables again
        onCreate(db);

    }

    //    public void addCountry(ArrayList<CountryModel.Result> results, int i) {
    public void addCountry(CountryModel.Result result) {
        open();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, result.getName());
        values.put(KEY_CODE_ONE, result.getAlpha2Code());
        values.put(KEY_CODE_TWO, result.getAlpha3Code());
       // values.put(KEY_IMG_FLAG,result.getImgPath());
        values.put(KEY_IMG_FLAG,result.getImage());

        // Inserting Row
        db.insert(TABLE_COUNTRY, null, values);
        close(); // Closing database connection
    }

    public int getCountryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COUNTRY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public ArrayList<CountryModel.Result> getAllcountry() {
        ArrayList<CountryModel.Result> contactList = new ArrayList<CountryModel.Result>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CountryModel.Result result = new CountryModel.Result();
                result.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.setAlpha2Code(cursor.getString(cursor.getColumnIndex(KEY_CODE_ONE)));
                result.setAlpha3Code(cursor.getString(cursor.getColumnIndex(KEY_CODE_TWO)));
                //result.setImgPath(cursor.getString(cursor.getColumnIndex(KEY_IMG_FLAG)));
                result.setImage(cursor.getBlob(cursor.getColumnIndex(KEY_IMG_FLAG)));

                Log.e("log_get", cursor.getString(1));

                contactList.add(result);
            } while (cursor.moveToNext());
        }

        return contactList;
    }


    public int existRecord(CountryModel.Result result) {

        int i = 0;
        open();
        try {

            String sqlstatament = ("SELECT COUNT(NAME) AS SRNO FROM COUNTRY WHERE NAME = '" + result.getName() + "'"
                    + " AND CODEONE = '" + result.getAlpha2Code() + "'" + " AND CODETWO = '" + result.getAlpha3Code() + "'");

            Cursor cursor = db.rawQuery(sqlstatament, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    i = cursor.getInt(0);
                }
            }
            cursor.close();
            close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("CountRow", String.valueOf(i));
        return i;

    }

    public boolean isDbPresent() {

        Log.v("log", databasePath);
        boolean checkFlag = true;
        SQLiteDatabase testDb;

        try {
            testDb = SQLiteDatabase.openDatabase(databasePath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException sqlException) {
            Log.v("log", "DB absent");
            checkFlag = false;
        }
        Log.v("log", "is DB present Exit!!!");
        return checkFlag;
    }

}