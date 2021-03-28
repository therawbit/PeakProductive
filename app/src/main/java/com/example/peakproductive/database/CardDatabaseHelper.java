package com.example.peakproductive.database;


import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;



public class CardDatabaseHelper extends SQLiteOpenHelper {
    public static final String CARD_TABLE = "CARD_TABLE";
    public static final String COLUMN_CARD_ID="CARD_ID";
    public static final String COLUMN_CARD_TITLE = "CARD_TITLE";
    public static final String COLUMN_CARD_CONTENT = "CARD_CONTENT";
    public static final String COLUMN_CARD_TAG = "CARD_TAG";
    private static final String TAG = "Database Class";
    private static final String DB_NAME = "peekproductive.db";
    private static final int VERSION = 1;


    public CardDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    //this is called the first time a database is accessed .
    // It should contain the code to generate a new database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + CARD_TABLE + "(" +
                COLUMN_CARD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CARD_TITLE + ", " +
                COLUMN_CARD_CONTENT + ", " +
                COLUMN_CARD_TAG +
                ")";

        db.execSQL(createTableQuery);

    }


    //this is calle if the database version changes.
    // prevents previous users apps from breaking when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop the old tables
        db.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE);

        //CRETAE TABLE AGAIN
        onCreate(db);

    }



}