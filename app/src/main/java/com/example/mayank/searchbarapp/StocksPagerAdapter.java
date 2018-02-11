package com.example.mayank.searchbarapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mayank.searchbarapp.Fragments.DetailsFragment;
import com.example.mayank.searchbarapp.Fragments.PredictionFragment;

/**
 * Created by namankhanna on 2/11/18.
 */

public class StocksPagerAdapter extends FragmentPagerAdapter {

    int fragCount;

    public StocksPagerAdapter(FragmentManager fm,int fragCount) {
        super(fm);
        this.fragCount = fragCount;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        switch (position) {
            case 0:     fragment = DetailsFragment.newInstance();
                        break;
            case 1:     fragment = PredictionFragment.newInstance();
                        break;
            default:    fragment = null;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return fragCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:     title = "Details";
                        break;
            case 1:     title = "Prediction";
                        break;
            default:    title = null;
        }
        return title;
    }
}
