package com.example.woo.myapplication.ui.view;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.utils.GridAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.CameraUpdateParams;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.GroundOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.MarkerIcons;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;


public class NaverMapFragment extends Fragment implements OnMapReadyCallback {
    MapFragment mapFragment;
    ArrayList tmp;
    public static double zoomLevel;
    public static double centerLat;
    public static double centerLng;
    public LatLng centerLatLng;
    public static boolean moving_camera = false;
    Fragment naverMapfragment;
    int level = 1;
    int map_radius = 1280;
    public EventListener eventListener;
    public ArrayList<PolygonOverlay> squareOverlay;
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
        squareOverlay = new ArrayList<PolygonOverlay>();
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
//        naverMap.getUiSettings().setScrollGesturesEnabled(false);
//        naverMap.getUiSettings().setZoomGesturesEnabled(false);
        naverMap.getUiSettings().setTiltGesturesEnabled(false);

        centerLatLng = new LatLng(centerLat,centerLng);


        naverMap.setCameraPosition(new CameraPosition(new LatLng(centerLat, centerLng), 10));
        naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2))));
        naverMap.setMapType(NaverMap.MapType.Satellite);
        Marker marker = new Marker();
        marker.setPosition(new LatLng(centerLat, centerLng));
        marker.setMap(naverMap);
        marker.setCaptionText("수색중심지점");
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.RED);

        LatLng centerLatLng = new LatLng(centerLat,centerLng);
        zoomLevel = naverMap.getCameraPosition().zoom + 0.9;
//        Log.d("ZoomLevel", zoomLevel+ "");
        GridMapMakeTask gridMapMakeTask = new GridMapMakeTask(naverMap);
        gridMapMakeTask.execute();
//        eventListener = new EventListener(naverMap);
//        naverMap.addOnCameraChangeListener(eventListener);
//        naverMap.addOnCameraChangeListener(new EventListener(naverMap));
//        naverMap.addOnCameraChangeListener((reason, animated) -> {
//            if(reason == CameraUpdate.REASON_GESTURE && !moving_camera &&naverMap.getUiSettings().isZoomGesturesEnabled()){
//                if(zoomLevel > naverMap.getCameraPosition().zoom){
//                    naverMap.getUiSettings().setZoomGesturesEnabled(false);
//                    moving_camera = true;
////                    CameraUpdateParams params = new CameraUpdateParams();
////                    params.zoomTo(zoomLevel + 1);
////                    zoomLevel -= 1;
//
//                    CameraUpdate cameraUpdate = CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1,map_radius *-1),centerLatLng.offset(map_radius ,map_radius)))
//                            .finishCallback(()->{
//                                moving_camera = false;
//
//                                for(int i = 0; i<squareOverlay.size(); i++){
//                                    squareOverlay.get(i).setMap(null);
//                                }
////                                squareOverlay.clear();
//
//                                squareOverlay.clear();
//                                zoomLevel = naverMap.getCameraPosition().zoom;
//                                map_radius *= 2;
//                                {
//                                    GridMapMakeTask task = new GridMapMakeTask(naverMap);
//                                    task.execute();
//                                }
//
//                                Log.d("ZoomLevel", zoomLevel+ "");
//                            })
//                            .cancelCallback(()->{
//                                moving_camera = false;
//                                naverMap.getUiSettings().setZoomGesturesEnabled(true);
//                            });
//
//                    naverMap.moveCamera(cameraUpdate.animate(CameraAnimation.Easing,2000));
//
//                    if(map_radius == 5120 * 2){
//                        map_radius = 5120;
//                        naverMap.cancelTransitions();
//                    }
//
//                    Log.d("ZoomLevel", zoomLevel+ "");
//                }
//            }
//        });
        //위도 1도 사이의 거리 : 110 키로미터 위도 1분 사이의 거리 : 1.8km
        //경도 1도 사이의 거리 : cos(위도)* 2 * pi * r (6380) / 360

//      혹시 모르는 리사이클러뷰 냉겨 놓기.
//        Marker marker = new Marker();
//        RecyclerView mapRecyclerViewNormal = new RecyclerView(getContext());
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 8 , LinearLayoutManager.HORIZONTAL, false);
//        mapRecyclerViewNormal.setLayoutManager(gridLayoutManager);
//        GridAdapter gridAdapter = new GridAdapter(getActivity().getApplicationContext(), tmp);
//        mapRecyclerViewNormal.setAdapter(gridAdapter);
//        mapRecyclerViewNormal.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
//                getView().onTouchEvent(motionEvent);
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean b) {
//            }
//        });


//        TextView tmp = new TextView(getContext());
//        tmp.setText("hello");
//        OverlayImage recyclerOverlay = OverlayImage.fromView(mapRecyclerViewNormal);
//
//        GroundOverlay groundOverlay = new GroundOverlay();
//        groundOverlay.setImage(recyclerOverlay);
//        groundOverlay.setBounds(new LatLngBounds(new LatLng(34.0714,125.1131), new LatLng(37,128)));
//        groundOverlay.setMap(naverMap);
//        tmp.clear();
//        tmp. add("hello");
//        gridAdapter.notifyDataSetChanged();
//        groundOverlay.setMap(null);
//        groundOverlay.setBounds(new LatLngBounds(new LatLng(34.0714,125.1131), new LatLng(37,128)));
//        recyclerOverlay = OverlayImage.fromView(mapRecyclerViewNormal);
//        groundOverlay.setImage(recyclerOverlay);
//        groundOverlay.setMap(naverMap);

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
    private class EventListener implements NaverMap.OnCameraChangeListener{
        private NaverMap naverMap;
        public EventListener(){

        };
        public EventListener(NaverMap naverMap){
            this.naverMap = naverMap;
        }

        public NaverMap getNaverMap() {
            return naverMap;
        }

        @Override
        public void onCameraChange(int reason, boolean animated){
//            zoomLevel = naverMap.getCameraPosition().zoom;
            if(reason == CameraUpdate.REASON_GESTURE && !moving_camera && naverMap.getUiSettings().isZoomGesturesEnabled()){

                if(zoomLevel > naverMap.getCameraPosition().zoom+0.05){
                    naverMap.getUiSettings().setZoomGesturesEnabled(false);
                    moving_camera = true;
//                    CameraUpdateParams params = new CameraUpdateParams();
//                    params.zoomTo(zoomLevel + 1);
//                    zoomLevel -= 1;
                    Log.d("MAP_radius", map_radius+ "");
//                    CameraUpdate cameraUpdate = CameraUpdate.finishCallback(()->{
                                moving_camera = false;

                                for(int i = 0; i<squareOverlay.size(); i++){
                                    squareOverlay.get(i).setMap(null);
                                }
                                squareOverlay.clear();

                                {
                                    Log.d("네이버", "그리드 다시 그리기");
                                    GridMapMakeTask task = new GridMapMakeTask(naverMap);
                                    task.execute();
                                }

                                Log.d("ZoomLevel", zoomLevel+ "");
//                            })
//                            .cancelCallback(()->{
////                                naverMap.moveCamera(CameraUpdate.fitBounds((new LatLngBounds(centerLatLng.offset(map_radius * -1,map_radius *-1),centerLatLng.offset(map_radius ,map_radius)))).animate(CameraAnimation.Easing,2000));
//                                moving_camera = false;
//                                naverMap.getUiSettings().setZoomGesturesEnabled(true);
//                            });
//                    naverMap.moveCamera(cameraUpdate);



//                    new Handler().postDelayed(()->{
////                        for(int i = 0; i<squareOverlay.size(); i++){
////                            squareOverlay.get(i).setMap(null);
////                        }
//////                                squareOverlay.clear();
////
////                        squareOverlay.clear();
////                        zoomLevel = naverMap.getCameraPosition().zoom;
////                        map_radius *= 2;
////                        {
////                            Log.d("네이버", "그리드 다시 그리기");
////                            GridMapMakeTask task = new GridMapMakeTask(naverMap);
////                            task.execute();
////                        }
////                    },2000);
//                    new Handler().postDelayed(()->{
//                        moving_camera = false;
//
//                        for(int i = 0; i<squareOverlay.size(); i++){
//                            squareOverlay.get(i).setMap(null);
//                        }
////                                squareOverlay.clear();
//
//                        squareOverlay.clear();
//                        zoomLevel = naverMap.getCameraPosition().zoom;
//                        map_radius *= 2;
//                        {
//                            Log.d("네이버", "그리드 다시 그리기");
//                            GridMapMakeTask task = new GridMapMakeTask(naverMap);
//                            task.execute();
//                        }
//
//                    },2000);
                    if(map_radius == 5120 * 2){
                        map_radius = 5120;
                        naverMap.cancelTransitions();
                    }

                    Log.d("ZoomLevel", zoomLevel+ "");
                }
//                else{
//                    task = null;
//                }
            }
        }
    }


    private class GridMapMakeTask extends AsyncTask<Void, PolygonOverlay, Void> implements Serializable {
        private NaverMap naverMap;

        public GridMapMakeTask() {
            super();
        }
        public GridMapMakeTask(NaverMap naverMap){
            super();
            this.naverMap = naverMap;
//            zoomLevel = naverMap.getCameraPosition().zoom;
        }

        public NaverMap getNaverMap() {
            return naverMap;
        }

        public void setNaverMap(NaverMap naverMap) {
            this.naverMap = naverMap;
        }
        @Override
        protected void onPreExecute() {
//            map_radius *= 2;
            if (eventListener != null)
                naverMap.removeOnCameraChangeListener(eventListener);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            naverMap.getUiSettings().setZoomGesturesEnabled(true);
            eventListener = new EventListener(naverMap);
            naverMap.addOnCameraChangeListener(eventListener);
            Log.d("홍성기 바보야", map_radius + " " + zoomLevel);
            naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1/2,map_radius *-1/2),centerLatLng.offset(map_radius/2 ,map_radius/2)))
                    .animate(CameraAnimation.Easing,2000));
            map_radius *= 2;
            zoomLevel -= 0.9;

//            new Handler().postDelayed(()->{
//                zoomLevel = naverMap.getCameraPosition().zoom;
//                naverMap.getUiSettings().setZoomGesturesEnabled(true);
//                eventListener = new EventListener(naverMap);
//                naverMap.addOnCameraChangeListener(eventListener);
//            },2000);


//            new Handler().postDelayed(()->{

//                eventListener = new EventListener(naverMap);
//                naverMap.addOnCameraChangeListener(eventListener);
//            },2000);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(PolygonOverlay... values) {
//            values[0].setPosition(new LatLng(34.0714,125.1131));
            values[0].setMap(getNaverMap());
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {

        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            centerLatLng = new LatLng(centerLat,centerLng);
            Log.d("네이버",""+map_radius);
            Log.d("네이버",""+naverMap.getUiSettings().isScrollGesturesEnabled());
            LatLng lineLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            LatLng drawLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            for(int i = 0; i< 64; i++){
                if(i != 0 && i % 8 == 0){
                    lineLatLng = lineLatLng.offset(-1*map_radius/8,0);
                    drawLatLng = lineLatLng;
                }
                PolygonOverlay polygonOverlay = new PolygonOverlay();
                polygonOverlay.setCoords(getFourCornerLatLng(drawLatLng));
                polygonOverlay.setOutlineColor(Color.WHITE);
                polygonOverlay.setColor(Color.TRANSPARENT);
                polygonOverlay.setOutlineWidth(2);
                squareOverlay.add(polygonOverlay);
                publishProgress(polygonOverlay);
                drawLatLng = getFourCornerLatLng(drawLatLng).get(3);
            }

//            Marker a = new Marker();
//            a.setPosition(centerLatLng);
////            a.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//            publishProgress(a);
//            Marker b = new Marker();
//            b.setPosition(norEastLatLng);
////            a.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//            publishProgress(b);
//            Marker c = new Marker();
//            c.setPosition(southWestLatLNg);
////            a.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//            publishProgress(c);
//            Marker d = new Marker();
//            d.setPosition(new LatLng(37.55917549459318,126.97819392597788));
////            a.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//            publishProgress(d);
//            Marker e = new Marker();
//            e.setPosition(new LatLng(37.55917549459318,126.97839392597788));
////            a.setPosition(new LatLng(37.55917549459318,126.97829392597788));
//            publishProgress(e);

            return null;
        }


    }

    public List<LatLng> getFourCornerLatLng(LatLng standardLatLng){
        return Arrays.asList(
                standardLatLng,
                standardLatLng.offset(map_radius/8,0),
                standardLatLng.offset(map_radius/8,map_radius/8),
                standardLatLng.offset(0,map_radius/8)
        );

    }


}
