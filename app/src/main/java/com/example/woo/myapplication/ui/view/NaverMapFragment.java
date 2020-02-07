package com.example.woo.myapplication.ui.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woo.myapplication.R;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
//    private MapView mapView;
    MapFragment mapFragment;
    TextView tmp;

    public static NaverMapFragment newInstance() {
        NaverMapFragment fragment = new NaverMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
//        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.navermap, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ...
//        super.onCreateView(inflater,container,savedInstanceState);
        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        TextView tmp = (TextView)rootView.findViewById(R.id.tmp);
//        tmp.bringToFront();
        return rootView;
    }




    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.getUiSettings().setZoomControlEnabled(false);

//        naverMap.setCameraPosition(new CameraPosition(new LatLng(centerLat, centerLng), 10));
//        Marker marker = new Marker();
//        marker.setPosition(new LatLng(centerLat, centerLng));
//        marker.setMap(naverMap);
//        marker.setCaptionText("수색중심지점");
//        marker.setIcon(MarkerIcons.BLACK);
//        marker.setIconTintColor(Color.RED);
    }
}
