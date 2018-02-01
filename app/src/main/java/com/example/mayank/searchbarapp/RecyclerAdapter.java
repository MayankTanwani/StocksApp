package com.example.mayank.searchbarapp;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mayank.searchbarapp.Database.DatabaseContract;

/**
 * Created by mayank on 1/25/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    public Cursor mData;

    public RecyclerAdapter(Cursor data,ListItemClickListener listener)
    {
        mData = data;
        mListItemClickListener = listener;
    }

    public interface ListItemClickListener{
        public void onListItemClickListener(String name);
    }

    public ListItemClickListener mListItemClickListener;

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(android.R.layout.simple_list_item_1,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if(!mData.moveToPosition(position))
            return;
        String code = mData.getString(mData.getColumnIndex(DatabaseContract.DatabaseEntry.STOCK_NAME));
        holder.textView.setText(code);
    }

    @Override
    public int getItemCount() {
        if(mData == null)
            return 0;
        else
            return mData.getCount();
    }
    public void swapCursor(Cursor data)
    {
        mData = data;
        notifyDataSetChanged();
    }
//    public void filter(String text,ArrayList<String> data)
//    {
//        ArrayList<String> newData = new ArrayList<>();
//        text = text.toLowerCase(Locale.getDefault());
//        if(!(text.equals(" ") || text.length() ==0))
//        {
//            for(String country : data)
//            {
//                if(country.toLowerCase(Locale.getDefault()).contains(text))
//                    newData.add(country);
//            }
//        }else
//        {
//            swapArray(data);
//            notifyDataSetChanged();
//            return;
//        }
//        swapArray(newData);
//        notifyDataSetChanged();
//    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String name = textView.getText().toString();
            mListItemClickListener.onListItemClickListener(name);
        }
    }
}
