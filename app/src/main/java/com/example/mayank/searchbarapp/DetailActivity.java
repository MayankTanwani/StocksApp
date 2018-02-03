package com.example.mayank.searchbarapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {

    TextView twCountryName;
    ArrayList<StockValues> data;
    RecyclerView recyclerView;
    StocksAdapter adapter;
    ProgressDialog progressDialog;
    public SQLiteDatabase mDb;
    public StocksDbHelper mStocksDbHelper;
    public static final String INTENT_BUNDLE = "Bundle";
    public static final String INTENT_EXTRA_VALUE = "data-arraylist";
    public static final int LOADER_ID = 23;

    Button showGraph;
    Cursor mDetails;
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
        downloadStockData(code);
    }

    public void downloadStockData(String code)
    {

        Bundle stockCodeBundle = new Bundle();
        stockCodeBundle.putString("stock-code",code);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Void> getStockDetailLoader = loaderManager.getLoader(LOADER_ID);
        if(getStockDetailLoader == null) {
            loaderManager.initLoader(LOADER_ID,stockCodeBundle,this);
        }else {
            loaderManager.restartLoader(LOADER_ID,stockCodeBundle,this);
        }


        //(new DownloadTask()).execute(apiURL);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Void> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Void>(this) {
            @Override
            protected void onStartLoading() {
                if(args == null)
                    return;
                showGraph.setVisibility(View.INVISIBLE);
                progressDialog.setMessage("Updating\n"+name);      //display a progress dialog
                progressDialog.show();
                forceLoad();
            }

            @Override
            public Void loadInBackground() {
                String code = args.getString("stock-code");
                URL apiURL = NetworkUtils.buildURL(DetailActivity.this,code);
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
        };
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void somedata) {
        adapter.swapArray(data);
        showGraph.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

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
        b.putSerializable(INTENT_EXTRA_VALUE,data);
        i.putExtra(INTENT_BUNDLE,b);
        startActivity(i);
    }

}
