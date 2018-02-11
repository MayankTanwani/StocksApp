package com.example.mayank.searchbarapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mayank.searchbarapp.Database.DatabaseContract;
import com.example.mayank.searchbarapp.Database.StocksDbHelper;
import com.example.mayank.searchbarapp.Fragments.DetailsFragment;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ListItemClickListener, SearchView.OnQueryTextListener {

    private static final int TOTAL_DATA_ROWS = 4566 ;
    public ArrayList<String> data;
    public SQLiteDatabase mDb;
    public StocksDbHelper mStocksDbHelper;
    public RecyclerAdapter adapter;
    public RecyclerView recyclerView;
    public Cursor mCursor;
    SearchView searchView;
    ProgressDialog progressDialog;

    public Menu mMenu;
    public FloatingActionButton mFAB;
    public static String intentKey = "stock-name";
    public static final String COUNTRY_DATA = "country.txt";
    public static final String BSE_DATA = "BSE-datasets-codes.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStocksDbHelper = new StocksDbHelper(this);
        mDb = mStocksDbHelper.getWritableDatabase();
        mFAB = findViewById(R.id.fab);
        recyclerView = (RecyclerView)findViewById(R.id.recyler_view);
        progressDialog = new ProgressDialog(this);
        data = new ArrayList<>();
        new storeDatabase().execute();

        adapter = new RecyclerAdapter(mCursor,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.performIdentifierAction(R.id.search,0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        mMenu = menu;
        searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText.toLowerCase(Locale.getDefault());
        filter(text,mCursor);
        return true;
    }

    public class storeDatabase extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading Data...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor count = mDb.query(DatabaseContract.DatabaseEntry.FTS_TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            if(count.getCount()<TOTAL_DATA_ROWS || count == null)
                loadFast();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCursor = getAllStocks();
            adapter.swapCursor(mCursor);
            progressDialog.dismiss();
        }
    }

    public void loadData()
    {
        mDb.execSQL("delete from " + DatabaseContract.DatabaseEntry.TABLE_NAME);
        BufferedReader reader = null;
        mDb.beginTransaction();
        try{
            reader = new BufferedReader(new InputStreamReader(getAssets().open(BSE_DATA)));
            String mLine;
            int i = 0;
            while((mLine = reader.readLine()) != null)
            {
                if(i==0) {
                    i++;
                    continue;
                }
                i++;

                String[] codeNamepair = mLine.split(",");
                String code = codeNamepair[0];
                String name = codeNamepair[1];
                addToDatabase(code,name);
                data.add(mLine);
            }
            mDb.setTransactionSuccessful();
            mDb.endTransaction();
            Log.d("MainActivity",i + " Values loaded");
        }catch (IOException e)
        {
            e.printStackTrace();
        }finally {
            if(reader!=null)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rebuildFTS();
            }
        }
    }
    public void loadFast(){
        mDb.execSQL("delete from " + DatabaseContract.DatabaseEntry.TABLE_NAME);
        mDb.beginTransaction();
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        try{
            parser.beginParsing(getAssets().open(BSE_DATA));
        }catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        String[] row;
        row = parser.parseNext();
        while((row = parser.parseNext()) != null)
        {
            addToDatabase(row[0],row[1]);
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        rebuildFTS();
    }

    public void filter(String query,Cursor mCursor)
    {
        if(!(query.equals(" ") || query.length() ==0))
        {
            String selection = DatabaseContract.DatabaseEntry.STOCK_NAME + " MATCH ?";
            String[] selectionArgs = new String[]{query + "*"};
            Cursor cursor =  mDb.query(DatabaseContract.DatabaseEntry.FTS_TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            adapter.swapCursor(cursor);
        }
        else
        {
            adapter.swapCursor(mCursor);
        }
        adapter.notifyDataSetChanged();
    }

    public void rebuildFTS()
    {
        String delete = "delete from " + DatabaseContract.DatabaseEntry.FTS_TABLE_NAME;
        mDb.execSQL(delete);
        String rebuild = "INSERT INTO " + DatabaseContract.DatabaseEntry.FTS_TABLE_NAME
                + "( docid, " +  DatabaseContract.DatabaseEntry.STOCK_NAME
                + " ) SELECT " + DatabaseContract.DatabaseEntry._ID
                + "  , " + DatabaseContract.DatabaseEntry.STOCK_NAME
                + " FROM " +  DatabaseContract.DatabaseEntry.TABLE_NAME + " ;";
        mDb.execSQL(rebuild);
    }

    public void addToDatabase(String code,String name)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DatabaseEntry.STOCK_CODE,code);
        cv.put(DatabaseContract.DatabaseEntry.STOCK_NAME,name);
        mDb.insert(DatabaseContract.DatabaseEntry.TABLE_NAME,null,cv);
    }

    @Override
    public void onListItemClickListener(String name) {
//        Intent i = new Intent(MainActivity.this,DetailActivity.class);
//        i.putExtra(intentKey,name);
//        startActivity(i);

        startActivity(new Intent(MainActivity.this, StockDetailsActivity.class));
        DetailsFragment.NAME_KEY = name;

    }

    public Cursor getAllStocks()
    {
        Cursor cursor =mDb.query(DatabaseContract.DatabaseEntry.FTS_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }
}
