package com.example.woo.myapplication.utils;

import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.woo.myapplication.R;

import java.util.ArrayList;

public class MapAdater extends RecyclerView.Adapter<MapAdater.ViewHolder> {
   // ArrayList<String> items = new ArrayList<>();
   int width;



    public class ViewHolder extends  RecyclerView.ViewHolder{
        protected TextView cell;
        protected LinearLayout linearLayout;
        public ViewHolder(View view){
            super(view);
            cell = (TextView)view.findViewById(R.id.cell);
            linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);
        }
    }

    public MapAdater(int width){
        this.width = width;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.map_cell,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdater.ViewHolder viewHolder, int i) {
        Log.d("색상",""+viewHolder.linearLayout.getBackground());
        //cell 배경색 지정
        viewHolder.linearLayout.setBackgroundColor(Color.argb(90,255,255,255));
        //cell 크기 지정
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams)viewHolder.linearLayout.getLayoutParams();
        params.width = width;
        params.height = width;
        viewHolder.linearLayout.requestLayout();
    }

    @Override//총 그리드 개수는 64개
    public int getItemCount() {
        return 64;
    }
}
