package com.example.mayank.searchbarapp.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayank.searchbarapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PredictionFragment extends android.support.v4.app.Fragment {


    public PredictionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prediction, container, false);
    }

    public static PredictionFragment newInstance() {
        return new PredictionFragment();
    }

}
