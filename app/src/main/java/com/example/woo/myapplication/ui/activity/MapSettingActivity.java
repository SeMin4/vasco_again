package com.example.woo.myapplication.ui.activity;

import android.app.Fragment;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;

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

import android.widget.Toast;

import android.widget.FrameLayout;


import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;

import com.example.woo.myapplication.ui.view.FindMapFragment;

import com.example.woo.myapplication.data.MapInfo;

import com.example.woo.myapplication.ui.view.NaverMapFragment;

import java.util.ArrayList;
import java.util.List;

public class MapSettingActivity extends AppCompatActivity 
{
    double centerLat;
    double centerLng;
    String pid;

    FragmentManager fm;
    FragmentTransaction fragmentTransaction;

    NaverMapFragment naverMapFragment;
    private Button placedelete;
    private Button placerecovery;
    private Button completedelete;
    private Button next_btn;

    private int placedeleteFlag = 0;
    Drawable color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setting);
        placedelete = (Button)findViewById(R.id.placedelete);
        placerecovery = (Button)findViewById(R.id.placerecovery);
        completedelete = (Button)findViewById(R.id.deletecomplete);
        next_btn = (Button)findViewById(R.id.next_btn);
        color = placedelete.getBackground(); //본래의 색
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
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

      //구역 삭제
      placedelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (placedeleteFlag == 0) {
                  naverMapFragment.gestureFunc(); //리사이클러뷰 올림
                  placedeleteFlag = 1; //1이면 리사이클러뷰 안올리기
                  placedelete.setBackgroundColor(Color.RED);
              }else {
                  placedelete.setBackgroundColor(Color.RED);
                  placerecovery.setBackground(color);
              }
              NaverMapFragment.flag = 1;
          }
      });

      placerecovery.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(placedeleteFlag == 1) {
                  placedelete.setBackground(color);
                  placerecovery.setBackgroundColor(Color.RED);
                  NaverMapFragment.flag = 0;
              }
          }
      });


      completedelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              naverMapFragment.detachView();
              //naverMapFragment.placeIndex = new ArrayList<>(64);
              NaverMapFragment.flag = 0;
              placedeleteFlag = 0;
              placedelete.setBackground(color);
              placerecovery.setBackground(color);
          }
      });



      //navermap에있는 arraylist넘기기
      next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NaverMapFragment.map_radius == 1280){
                    for (int i = 0; i < naverMapFragment.placeIndex.size(); i++) {
                        System.out.println("" + i + " : " + naverMapFragment.placeIndex.get(i));
                    }

//<<<<<<< HEAD
                    FindMapFragment.map_radius = NaverMapFragment.map_radius;
//                Intent intent = new Intent(getApplicationContext(), GPSService.class);
//
//                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    startService(intent);
//                } else {
//                    startForegroundService(intent);
//=======
                    Intent intent = new Intent(getApplicationContext(),PasswordActivity.class);
                    int m_size = naverMapFragment.getMsize();
                    int m_unit_scale = m_size/8;
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    String centerLocation = "";
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(centerLat, centerLng, 10);
                        if(list != null){
                            if(list.size() == 0){
                                Toast.makeText(getApplicationContext(), "구글지도에 제공되지 않는 위치입니다.", Toast.LENGTH_LONG).show();
                            }else{
                                String s1 = list.get(0).getCountryName(); //국가명
                                if(s1 != null || !(s1.trim().equals("null")))
                                    centerLocation += s1+" ";
                                String s2 = list.get(0).getAdminArea(); // 시
                                if(s2 != null || !(s2.trim().equals("null")))
                                    centerLocation += s2+" ";
                                String s3 = list.get(0).getLocality(); // 구 메인
                                if(s3 != null || !(s3.trim().equals("null")))
                                    centerLocation += s3+" ";
                                String s4 = list.get(0).getSubLocality(); //구 서브데이터
                                if(s4 != null || !(s4.trim().equals("null")))
                                    centerLocation += s4+" ";
                                String s5 = list.get(0).getThoroughfare(); // 동
                                if(s5 != null || !(s5.trim().equals("null")))
                                    centerLocation += s5+" ";
                                String s6 = list.get(0).getSubThoroughfare(); // 번지
                                if(s2 != null || !(s6.trim().equals("null")))
                                    centerLocation += s6+" ";
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
//>>>>>>> maplist
                    }

                    MapInfo info = new MapInfo();
                    info.setP_id(pid);
                    info.setM_owner(MyGlobals.getInstance().getUser().getU_id());
                    info.setM_size(""+m_size);
                    info.setM_status("1");
                    info.setM_unit_scale(""+m_unit_scale);
                    info.setM_rotation("0");
                    info.setM_center_place_string(centerLocation);
                    info.setM_center_point_latitude(""+centerLat);
                    info.setM_center_point_longitude(""+centerLng);
                    //수색 구역 설정한 arraylist넘기기
                    info.setPlaceIndex(naverMapFragment.placeIndex);
                    info.setM_find_latitude(null);
                    info.setM_find_longitude(null);
                    intent.putExtra("mapinfo",info);
                    startActivity(intent);

//                Intent intent = new Intent(getApplicationContext(), GPSService.class);
//
//                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    startService(intent);
//                } else {
//                    startForegroundService(intent);
//                }
 /*               Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);*/
                }else{
                    Toast.makeText(getApplicationContext(), "추후에 지원되는 지도 크기 입니다.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
