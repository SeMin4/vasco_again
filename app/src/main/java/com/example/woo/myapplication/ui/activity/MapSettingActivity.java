package com.example.woo.myapplication.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.ui.view.NaverMapFragment;

public class MapSettingActivity extends AppCompatActivity 
{
    double centerLat;
    double centerLng;

    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    NaverMapFragment naverMapFragment;
    private Button next_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setting);
        next_btn = (Button)findViewById(R.id.next_btn);
        Intent intent = getIntent();
        centerLat = intent.getDoubleExtra("Lat", 0);
        centerLng = intent.getDoubleExtra("Lng",0);

        fm= getSupportFragmentManager();
        naverMapFragment = (NaverMapFragment)fm.findFragmentById(R.id.naverMap_Frag);
        if(naverMapFragment == null){
            naverMapFragment = NaverMapFragment.newInstance();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.naverMap_Setting, naverMapFragment);
            fragmentTransaction.commit();
        }

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);
            }
        });

    }


}
