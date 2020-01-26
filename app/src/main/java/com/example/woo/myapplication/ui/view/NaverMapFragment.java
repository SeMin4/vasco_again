package com.example.woo.myapplication.ui.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.utils.GridAdapter;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
    MapFragment mapFragment;
    ArrayList tmp;
    public static NaverMapFragment newInstance() {
        NaverMapFragment fragment = new NaverMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tmp = new ArrayList<>(Arrays.asList("Person1", "Person2", "Person3", "Person4", "Person5", "Person6", "Person7", "Person8", "Person9", "Person10", "Person11", "Person12", "Person13", "Person14"));
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
        RecyclerView mapGridRecycler = (RecyclerView)rootView.findViewById(R.id.map_grid_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 8 , LinearLayoutManager.HORIZONTAL, false);
//        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mapGridRecycler.setLayoutManager(gridLayoutManager);
        GridAdapter gridAdapter = new GridAdapter(getActivity().getApplicationContext(), tmp);
        mapGridRecycler.setAdapter(gridAdapter);
        mapGridRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
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
