package com.example.woo.myapplication.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.woo.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug","클릭리스너");
            }
        });
    }
//



    @Override
    public int getItemCount(){
        return tmp.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View itemView){
            super(itemView);
        }
    }
}
