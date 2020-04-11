package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.woo.myapplication.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.PolygonOverlay;

import java.util.Arrays;
import java.util.List;

public class DetailMapPopUp extends Activity implements OnMapReadyCallback {
    private MapView mapView;
    private double centerLat;
    private double centerLng;
    private LatLng centerLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.pop_up_detail_map);
        Intent intent = getIntent();
        centerLat = intent.getDoubleExtra("Lat", -1);
        centerLng = intent.getDoubleExtra("Lng", -1);
        centerLatLng = new LatLng(centerLat, centerLng);
        mapView = findViewById(R.id.detail_naver_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.getUiSettings().setZoomControlEnabled(false);      // 지도 줌버튼 비활성화
        naverMap.getUiSettings().setLocationButtonEnabled(false);    // 현위치 버튼 활성화
        naverMap.getUiSettings().setScrollGesturesEnabled(false);   // 스크롤 제스쳐 비활성화
        naverMap.getUiSettings().setTiltGesturesEnabled(false);     // 기울임 제스쳐 비활성화
        naverMap.getUiSettings().setStopGesturesEnabled(false);     // 애니메이션 비활성화
        naverMap.getUiSettings().setRotateGesturesEnabled(false);   // 회전 제스쳐 비활성화
        naverMap.getUiSettings().setZoomGesturesEnabled(false);
        naverMap.setMapType(NaverMap.MapType.valueOf("Satellite"));

        naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(-10, -10),centerLatLng.offset(10, 10)),50));
        PolygonOverlay polygonOverlay = new PolygonOverlay();
        LatLng outlineLatLng = this.centerLatLng.offset(-10,-10);
        polygonOverlay.setCoords(getFourCornerLatLng(outlineLatLng));

        polygonOverlay.setOutlineColor(Color.WHITE);
        polygonOverlay.setColor(Color.TRANSPARENT);
        polygonOverlay.setOutlineWidth(5);
        polygonOverlay.setTag("center");

        polygonOverlay.setMap(naverMap);
        //NaverMap 기본 설정 하고 폴리곤 오버레이 다시 그리기

        naverMap.setOnMapLongClickListener((pointF, latLng) -> {
            InfoWindow infoWindow = new InfoWindow();
            infoWindow.setAlpha(0.9f);
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return "이곳을 등록하려면 말풍선을 클릭하세요";
                }
            });

            infoWindow.setPosition(latLng);
            infoWindow.open(naverMap);
//            windowHashMap.put(infoWindow.hashCode(), infoWindow);
            infoWindow.setOnClickListener(overlay -> {
                infoWindow.close();
//                Intent intent = new Intent(DistrictActivity.this, DistrictRecordActivity.class);
//                intent.putExtra("markerId", infoWindow.hashCode());
//                intent.putExtra("latitude", latLng.latitude);
//                intent.putExtra("longitude", latLng.longitude);
//                intent.putExtra("mapId", mapId);
//                intent.putExtra("index",index);
//                startActivityForResult(intent, RECORD_REGISTER);
                return true;
            });

        });

    }

    public List<LatLng> getFourCornerLatLng(LatLng standardLatLng){
        return Arrays.asList(
                standardLatLng,
                standardLatLng.offset(20,0),
                standardLatLng.offset(20,20),
                standardLatLng.offset(0,20)
        );
    }
}
