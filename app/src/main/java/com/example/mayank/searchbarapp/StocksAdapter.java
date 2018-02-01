package com.example.mayank.searchbarapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mayank on 1/28/18.
 */

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> {

    ArrayList<StockValues> mData;

    public StocksAdapter(ArrayList<StockValues> data)
    {
        this.mData = data;
    }

    @Override
    public StocksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.stocks_item,parent,false);
        return new StocksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StocksViewHolder holder, int position) {
        StockValues stockValues = mData.get(position);
        holder.tvDate.setText(stockValues.date);
        holder.tvValue.setText(stockValues.value);
    }

    @Override
    public int getItemCount() {
        if(mData == null)
            return 0;
        else
            return mData.size();
    }

    public void swapArray(ArrayList<StockValues> newData)
    {
        mData = newData;
        notifyDataSetChanged();
    }
    public class StocksViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvValue;
        public StocksViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}
