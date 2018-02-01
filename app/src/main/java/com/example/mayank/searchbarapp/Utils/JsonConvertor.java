package com.example.mayank.searchbarapp.Utils;

import android.content.Context;

import com.example.mayank.searchbarapp.StockValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mayank on 1/28/18.
 */

public class JsonConvertor {

    public static final String CLOSE_CODE = "Close";
    public static final String DATE_CODE = "Date";
    public static final int TOTAL_VALUES = 4944;

    public static ArrayList<StockValues> convertDataFromJson(Context context, String jsonDataStr) throws JSONException
    {
        JSONObject jsonData = new JSONObject(jsonDataStr);
        JSONObject closeData = jsonData.getJSONObject(CLOSE_CODE);
        JSONObject dateData = jsonData.getJSONObject(DATE_CODE);
        ArrayList<StockValues> values = new ArrayList<>();
        for(int i=0;i<TOTAL_VALUES;i++)
        {
            if(closeData.has("" + i))
            {
                String closingValue = closeData.getString("" + i);
                String date = dateData.getString("" + i);
                values.add(new StockValues(date,closingValue));
            }
            else break;
        }
        return values;
    }
}
