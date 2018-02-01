package com.example.mayank.searchbarapp.Database;

import android.provider.BaseColumns;

/**
 * Created by mayank on 1/26/18.
 */

public class DatabaseContract {
    public class DatabaseEntry implements BaseColumns{
        public static final String TABLE_NAME = "stocks";
        public static final String STOCK_CODE = "stockCode";
        public static final String STOCK_NAME = "stockName";
        public static final String FTS_TABLE_NAME = "stocksFTS";
    }
}
