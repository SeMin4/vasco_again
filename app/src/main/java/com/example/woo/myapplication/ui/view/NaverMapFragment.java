package com.example.woo.myapplication.ui.view;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.GroundOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
    MapFragment mapFragment;
    ArrayList tmp;
    Fragment naverMapfragment;
//    GridLayoutManager gridLayoutManager;
    FragmentTransaction fragmentTransaction;
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
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.navermap, mapFragment).addToBackStack(null).commit();

        }
        mapFragment.getMapAsync(this);
        naverMapfragment = fm.findFragmentById(R.id.navermap);

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ...
//        super.onCreateView(inflater,container,savedInstanceState);
        View rootView =  inflater.inflate(R.layout.fragment_map, container, true);

//        mapGridRecycler = (RecyclerView)rootView.findViewById(R.id.map_grid_recycler);
//        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        mapGridRecycler.setOnTouchListener();
        return rootView;
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.getUiSettings().setZoomControlEnabled(false);
        Marker marker = new Marker();
        RecyclerView mapRecyclerViewNormal = new RecyclerView(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 8 , LinearLayoutManager.HORIZONTAL, false);
        mapRecyclerViewNormal.setLayoutManager(gridLayoutManager);
        GridAdapter gridAdapter = new GridAdapter(getActivity().getApplicationContext(), tmp);
        mapRecyclerViewNormal.setAdapter(gridAdapter);
        mapRecyclerViewNormal.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                getView().onTouchEvent(motionEvent);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });

//        TextView tmp = new TextView(getContext());
//        tmp.setText("hello");
        OverlayImage recyclerOverlay = OverlayImage.fromView(mapRecyclerViewNormal);

        GroundOverlay groundOverlay = new GroundOverlay();
        groundOverlay.setImage(recyclerOverlay);
        groundOverlay.setBounds(new LatLngBounds(new LatLng(34.0714,125.1131), new LatLng(37,128)));
        groundOverlay.setMap(naverMap);
        tmp.clear();
        tmp. add("hello");
        gridAdapter.notifyDataSetChanged();
        groundOverlay.setMap(null);
        groundOverlay.setBounds(new LatLngBounds(new LatLng(34.0714,125.1131), new LatLng(37,128)));
        recyclerOverlay = OverlayImage.fromView(mapRecyclerViewNormal);
        groundOverlay.setImage(recyclerOverlay);
        groundOverlay.setMap(naverMap);

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                tmp.clear();
//                gridAdapter.notifyDataSetChanged();
//            }
//        },2000);

//        marker.setIcon(recyclerOverlay);
//        marker.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//        marker.setForceShowIcon(true);
//        marker.setIconPerspectiveEnabled(true);
//        marker.setVisible(true);



//        naverMap.setCameraPosition(new CameraPosition(new LatLng(centerLat, centerLng), 10));
//        Marker marker = new Marker();
//        marker.setPosition(new LatLng(centerLat, centerLng));
//        marker.setMap(naverMap);
//        marker.setCaptionText("수색중심지점");
//        marker.setIcon(MarkerIcons.BLACK);
//        marker.setIconTintColor(Color.RED);


    }


}
