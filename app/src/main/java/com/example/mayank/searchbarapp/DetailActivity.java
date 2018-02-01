package com.example.mayank.searchbarapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mayank.searchbarapp.Database.DatabaseContract;
import com.example.mayank.searchbarapp.Database.StocksDbHelper;
import com.example.mayank.searchbarapp.Utils.JsonConvertor;
import com.example.mayank.searchbarapp.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView twCountryName;
    ArrayList<StockValues> data;
    RecyclerView recyclerView;
    StocksAdapter adapter;
    ProgressDialog progressDialog;
    public SQLiteDatabase mDb;
    public StocksDbHelper mStocksDbHelper;
    public static final String INTENT_BUNDLE = "Bundle";
    Button showGraph;
    Cursor mDetails;
    public static final String INTENT_EXTRA_VALUE = "data-arraylist";
    String name =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mStocksDbHelper = new StocksDbHelper(this);
        mDb = mStocksDbHelper.getWritableDatabase();
        twCountryName = (TextView)findViewById(R.id.countryName);
        Intent i = getIntent();
        showGraph = findViewById(R.id.showGraph);
        if(i.hasExtra(MainActivity.intentKey))
        {
            name = i.getStringExtra(MainActivity.intentKey);
            twCountryName.setText(name);
            mDetails = getDetails(name);
        }
        data = new ArrayList<>();
        recyclerView = findViewById(R.id.stocks_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);      //creating a progress dialog
        adapter = new StocksAdapter(data);
        recyclerView.setAdapter(adapter);
        String code = null;
        if(mDetails != null)
        {
            mDetails.moveToFirst();
            code = mDetails.getString(mDetails.getColumnIndex(DatabaseContract.DatabaseEntry.STOCK_CODE));
        }
        URL apiURL = NetworkUtils.buildURL(this,code);
        (new DownloadTask()).execute(apiURL);
    }

    public class DownloadTask extends AsyncTask<URL,Void,Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.swapArray(data);
            showGraph.setVisibility(View.VISIBLE);
            progressDialog.dismiss();                   //removal of the progress dialog
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showGraph.setVisibility(View.INVISIBLE);
            progressDialog.setMessage("Updating\n"+name);      //display a progress dialog
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(URL... urls) {
           if(urls == null)
               return null;
           URL apiURL = urls[0];
            String response = null;
           try {
               response = NetworkUtils.getResponseFromHttpUrl(apiURL);
               data = JsonConvertor.convertDataFromJson(DetailActivity.this,response);
           }catch (Exception e)
           {
               e.printStackTrace();
           }
            return null;
        }
    }
    public Cursor getDetails(String name)
    {
        String selection = DatabaseContract.DatabaseEntry.STOCK_NAME + " = '" + name + "' ";
        Log.d("Detail Activity" , selection);
        String[] selectionArgs =new String[]{name};
        Cursor cursor = mDb.query(DatabaseContract.DatabaseEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null);
        Log.v("Detail Activity" , cursor.getColumnCount() + " count : " + cursor.getCount() + " ");
        return cursor;
    }

    public void ShowGraph(View view) {
        Intent i = new Intent(DetailActivity.this,GraphActivity.class);
        Log.d("Detail Activity","Array size : " + data.size());
        Bundle b = new Bundle();
        ArrayList<StockValues> allData = new ArrayList<>();
        int size = data.size();
        for(int j=0;j<(2*size/3);j++)
        {
            allData.add(data.get(j));
        }
        b.putSerializable(INTENT_EXTRA_VALUE,allData);
        i.putExtra(INTENT_BUNDLE,b);
        startActivity(i);
    }

}
