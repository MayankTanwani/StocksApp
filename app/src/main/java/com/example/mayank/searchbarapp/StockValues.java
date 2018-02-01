package com.example.mayank.searchbarapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mayank on 1/28/18.
 */

public class StockValues implements Serializable {
    String date;
    String value;
    public StockValues(String date,String value)
    {
        this.date = date;
        this.value = value;
    }

}
