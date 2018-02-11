package com.example.mayank.searchbarapp.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayank.searchbarapp.Database.DatabaseContract;
import com.example.mayank.searchbarapp.Database.StocksDbHelper;
import com.example.mayank.searchbarapp.DetailActivity;
import com.example.mayank.searchbarapp.GraphActivity;
import com.example.mayank.searchbarapp.MainActivity;
import com.example.mayank.searchbarapp.R;
import com.example.mayank.searchbarapp.StockValues;
import com.example.mayank.searchbarapp.StocksAdapter;
import com.example.mayank.searchbarapp.Utils.JsonConvertor;
import com.example.mayank.searchbarapp.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import static android.content.Intent.getIntent;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends android.support.v4.app.Fragment {

    TextView twCountryName;
    static ArrayList<StockValues> data;
    RecyclerView recyclerView;
    StocksAdapter adapter;
    ProgressDialog progressDialog;
    public SQLiteDatabase mDb;
    public StocksDbHelper mStocksDbHelper;
    public static final String INTENT_BUNDLE = "Bundle";
    public static final String INTENT_EXTRA_VALUE = "data-arraylist";

    public static String NAME_KEY = null;

    Button showGraph;
    Cursor mDetails;
    String name;


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details,container,false);
        mStocksDbHelper = new StocksDbHelper(getActivity());
        mDb = mStocksDbHelper.getWritableDatabase();
        twCountryName = view.findViewById(R.id.countryName);
        showGraph = view.findViewById(R.id.showGraph);

        name = NAME_KEY;
        twCountryName.setText(name);
        mDetails = getDetails(name);

        showGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayGraph();
            }
        });


        data = new ArrayList<>();
        recyclerView = view.findViewById(R.id.stocks_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressDialog = new ProgressDialog(getActivity());      //creating a progress dialog
        adapter = new StocksAdapter(data);
        recyclerView.setAdapter(adapter);
        String code = null;
        if(mDetails != null)
        {
            mDetails.moveToFirst();
            code = mDetails.getString(mDetails.getColumnIndex(DatabaseContract.DatabaseEntry.STOCK_CODE));
        }

        URL apiURL = NetworkUtils.buildURL(getActivity(),code);
        (new DownloadTask()).execute(apiURL);

        return view;
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    public class DownloadTask extends AsyncTask<URL,Void,Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.swapArray(data);
            showGraph.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showGraph.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(URL... urls) {
            if(urls == null)
                return null;
            URL apiURL = urls[0];
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(apiURL);
                data = JsonConvertor.convertDataFromJson(getActivity(),response);
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

    public void DisplayGraph() {
        Intent i = new Intent(getActivity(),GraphActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(INTENT_EXTRA_VALUE,data);
        i.putExtra(INTENT_BUNDLE,b);
        startActivity(i);
    }
}
