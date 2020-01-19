package com.example.woo.myapplication.ui.activity;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woo.myapplication.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;

public class MapConfigActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText searchText;
    NaverMap naverMap;
    String searchLocation;
    Button searchBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_config);
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.naverMap_Config, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        searchText = (EditText)findViewById(R.id.searchText);
        searchBtn = (Button)findViewById(R.id.searchButton);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // 지도 기본 셋팅
        naverMap.getUiSettings().setZoomControlEnabled(false);
        //
        final Geocoder geocoder = new Geocoder(this);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation = searchText.getText().toString();
                List<Address> list = null;
                try{
                    list = geocoder.getFromLocationName(
                            searchLocation,10
                    );
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(list != null){
                    if(list.size() == 0){
                        Toast.makeText(getApplicationContext(), "구글지도에 제공되지 않는 위치입니다.", Toast.LENGTH_LONG).show();
                    }else{
                        double searchLat, searchLong;
                        searchLat = list.get(0).getLatitude();
                        searchLong = list.get(0).getLongitude();
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(searchLat, searchLong)).animate(CameraAnimation.Fly,2000);
                        naverMap.moveCamera(cameraUpdate);
                    }
                }
            }
        });
    }
}
