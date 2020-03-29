package com.example.woo.myapplication.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.ui.view.NaverMapFragment;

public class MapSettingActivity extends AppCompatActivity 
{
    double centerLat;
    double centerLng;

    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    FrameLayout frameLayout;
    NaverMapFragment naverMapFragment;
    private Button placedelete;
    private Button completedelete;
    private Button next_btn;
    Drawable color;
    int first = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setting);
        placedelete = (Button)findViewById(R.id.placedelete);
        completedelete = (Button)findViewById(R.id.deletecomplete);
        next_btn = (Button)findViewById(R.id.next_btn);
        color = placedelete.getBackground();
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



        frameLayout = (FrameLayout)findViewById(R.id.naverMap_Setting);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("터치됨@@@@@@@@@@@@@@@@@@@@@@@@@@");
                Log.d("터치","터치돰@@@@@@@@");
                return true;
            }
        });

      /*  if(Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("channel1", "1번채널", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("1번채널입니다");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }*/

      placedelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(first == 0){
                  naverMapFragment.gestureFunc();
                  first = 1;
              }
              if(NaverMapFragment.flag == 0 ){
                  NaverMapFragment.flag = 1;
                  placedelete.setBackgroundColor(Color.RED);
              }else{
                  NaverMapFragment.flag = 0;
                  placedelete.setBackground(color);
              }

          }
      });

      completedelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              naverMapFragment.detachView();
              NaverMapFragment.flag = 0;
              first = 0;
              placedelete.setBackground(color);
          }
      });



      next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GPSService.class);
                startService(intent);

 /*               Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);*/
            }
        });

    }


}
