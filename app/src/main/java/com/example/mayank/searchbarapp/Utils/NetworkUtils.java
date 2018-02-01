package com.example.mayank.searchbarapp.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mayank on 1/27/18.
 */

public class NetworkUtils {
    public static final String BASE_URL = "https://stock-predict.herokuapp.com/api?tags=NSE/BEL";
    public static final String Custom_BASE_URL = "https://stock-predict.herokuapp.com/api";
    public static final String TAGS_QUERY = "tags";
    public static URL buildURL(Context context,String stockCode)
    {
        Uri stockDataUri = Uri.parse(Custom_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_QUERY,stockCode)
                .build();
        try
        {
            URL stockDataURL = new URL(stockDataUri.toString());
            Log.d("NetworkUtils",stockDataURL.toString());
            return stockDataURL;
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
