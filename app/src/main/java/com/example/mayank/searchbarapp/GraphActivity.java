package com.example.mayank.searchbarapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mayank.searchbarapp.Utils.JsonConvertor;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {

    TextView xValue;
    TextView yValue;
    ArrayList<StockValues> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        xValue = findViewById(R.id.x_value);
        yValue = findViewById(R.id.y_value);
        mData = new ArrayList<>();
        Intent i = getIntent();
        Bundle bundle= null;
        if(i.hasExtra(DetailActivity.INTENT_BUNDLE))
        {
            bundle = i.getBundleExtra(DetailActivity.INTENT_BUNDLE);
        }

        if(bundle!=null) {
            Log.v("GraphActivity","Arraylist received ");
            mData = (ArrayList<StockValues>) bundle.getSerializable(DetailActivity.INTENT_EXTRA_VALUE);
        }
//        xValue.setText(mData.get(0).date);
//        yValue.setText(mData.get(0).value);
        if(mData!=null)
            Log.v("GraphViewActivity","Arraysize" + mData.size());
        else
            Log.v("GraphViewActivity","Arraylist empty null");
        makeGraph(mData);
    }


    private void makeGraph(ArrayList<StockValues> mData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        GraphView graph = findViewById(R.id.graph);
        DataPoint[] dataPoints = new DataPoint[mData.size()];
        Date date = null;
        Date minDate = null;
        Date maxDate = null;
        for(int i=0;i<dataPoints.length;i++)
        {
            StockValues values = mData.get(i);
            try {
                date = sdf.parse(values.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(i==0)
                minDate = date;
            if(i==dataPoints.length-1)
                maxDate = date;
            DataPoint dataPoint = new DataPoint(date,Double.valueOf(values.value));
            dataPoints[i] = dataPoint;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length); // only 4 because of the space

//         set manual x bounds to have nice steps
        graph.getViewport().setMinX(minDate.getTime());
        graph.getViewport().setMaxX(maxDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScalable(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }
}