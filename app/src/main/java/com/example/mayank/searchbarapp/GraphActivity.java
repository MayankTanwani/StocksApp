package com.example.mayank.searchbarapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayank.searchbarapp.Utils.JsonConvertor;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {

    TextView xValue;
    TextView yValue;
    ArrayList<StockValues> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
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

        if(mData!=null)
            Log.v("GraphViewActivity","Arraysize" + mData.size());
        else
            Log.v("GraphViewActivity","Arraylist empty null");
        Collections.reverse(mData);
        //mData = new ArrayList<>(mData.subList(0,10));
//        makeGraph(mData);
        makeGraph();
    }


    public void makeGraph()
    {
        LineChart lineChart = (LineChart)findViewById(R.id.chart);
        ArrayList<Entry> chartData = new ArrayList<>();
//        chartData.add(new Entry(1,10));
//        chartData.add(new Entry(2,20));
//        chartData.add(new Entry(3,30));

        for(int i=0;i<mData.size();i++)
        {
            chartData.add(new Entry(i,Float.valueOf(mData.get(i).value)));
        }

        LineDataSet lineDataSet = new LineDataSet(chartData,"Labels");
        lineDataSet.setColor(R.color.colorPrimary);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.setTouchEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            Toast toast = null;
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(toast != null)
                    toast.cancel();
                toast =Toast.makeText(GraphActivity.this, "Date : " +
                        mData.get((int)e.getX()).date + " Price : " + e.getY(), Toast.LENGTH_SHORT);
                toast.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.RED);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mData.get((int)value).date;
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        //leftAxis.setDrawGridLines(false);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        lineChart.invalidate();
    }
}