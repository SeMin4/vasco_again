package com.example.woo.myapplication.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    ArrayList tmp;
    Context context;
    public GridAdapter(Context context, ArrayList tmp){
        this.context = context;
        this.tmp = tmp;
    }
    @Override
    public  MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.gridTextView.setText(tmp.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This " + position + "click", Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        // set the data in items
//
//        // implement setOnClickListener event on item view.
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // open another activity on item click
//
//            }
//        });
//    }
//



    @Override
    public int getItemCount(){
        return tmp.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView gridTextView;
        public MyViewHolder(View itemView){
            super(itemView);
            gridTextView = (TextView) itemView.findViewById(R.id.gridText);
        }
    }
}
