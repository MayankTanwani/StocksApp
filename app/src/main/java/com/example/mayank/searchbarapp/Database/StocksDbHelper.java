package com.example.mayank.searchbarapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mayank.searchbarapp.Database.DatabaseContract;

/**
 * Created by mayank on 1/26/18.
 */

public class StocksDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "stocks.db";
    public static final int DATABASE_VERSION = 1;
    public StocksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + DatabaseContract.DatabaseEntry.TABLE_NAME + "( "+
            DatabaseContract.DatabaseEntry._ID + "  INTEGER PRIMARY KEY ," +
                DatabaseContract.DatabaseEntry.STOCK_CODE + " TEXT NOT NULL ,"+
                DatabaseContract.DatabaseEntry.STOCK_NAME + " TEXT NOT NULL" +
                " );";
        String createFTS = "CREATE VIRTUAL TABLE " + DatabaseContract.DatabaseEntry.FTS_TABLE_NAME
                + " USING FTS3( " +
                DatabaseContract.DatabaseEntry.STOCK_NAME + " );";
        db.execSQL(create);
        db.execSQL(createFTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.FTS_TABLE_NAME);
        onCreate(db);
    }

}
