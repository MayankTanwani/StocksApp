package com.example.mayank.searchbarapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mayank.searchbarapp.R;

public class StockDetailsActivity extends AppCompatActivity {

    TabLayout tlStockDetails;
    ViewPager vpStockDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        tlStockDetails = findViewById(R.id.tlStockDetails);
        vpStockDetails = findViewById(R.id.vpStockDetails);

        tlStockDetails.setupWithViewPager(vpStockDetails);
        vpStockDetails.setAdapter(
                new StocksPagerAdapter(
                        getSupportFragmentManager(),2
                )
        );
    }
}
