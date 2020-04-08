package com.example.woo.myapplication.ui.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.woo.myapplication.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FindMapFragment extends Fragment implements OnMapReadyCallback {

    MapFragment mapFragment;
    FragmentTransaction fragmentTransaction;
    Fragment findMapFragment;
    public static LatLng centerLatLng;
    public static int map_radius;
    public ArrayList<PolygonOverlay> squareOverlay;
    public IdleListener cameraIdleListener;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    private ArrayList<Integer> placeIndex; //수색구역 정보



    public void setPlaceIndex(ArrayList<Integer> placeIndex) {
        this.placeIndex = placeIndex;
    }

    // TODO: Rename and change types and number of parameters
    public static FindMapFragment newInstance() {
        FindMapFragment fragment = new FindMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.findmap, mapFragment).addToBackStack(null).commit();
        }
        squareOverlay = new ArrayList<PolygonOverlay>();
        mapFragment.getMapAsync(this);
        findMapFragment = fm.findFragmentById(R.id.findmap);

        locationSource = new FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_map, container, true);
        return  rootView;
    }




    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap){
        naverMap.getUiSettings().setZoomControlEnabled(false);
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.setCameraPosition(new CameraPosition(centerLatLng, 10));
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();

        // 지도 중심으로 부터 지도의 전체 크기의 절반 만큼 남서쪽 북동쪽 부분으로 바운드를 결정하고 그 부분을 볼 수 있는 부분으로 카메라를 옮김.
        naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2))));

        //네이버맵의 맵 설정을 위성 맵으로 설정
        naverMap.setMapType(NaverMap.MapType.Satellite);

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
        naverMap.addOnLocationChangeListener(location ->
                locationOverlay.setPosition(new LatLng(location.getLatitude(),location.getLongitude()))
        );

        locationOverlay.setVisible(true);
        locationOverlay.setCircleRadius(100);
        //uiSettings.setLocationButtonEnabled(true);

        //AsynTask를 extend 해서 비동기적으로 뒤에 해당하는 격자표 그리기.
        FindMapMakeTask gridMapMakeTask = new FindMapMakeTask(naverMap);
        gridMapMakeTask.execute();
    }

    private class IdleListener implements NaverMap.OnCameraIdleListener{
        private NaverMap naverMap;
        private double zoom;
        private boolean cameraMove;
        private double comparisionZoom;
        private IdleListener(NaverMap naverMap, double zoom, boolean cameraMove){
            this.naverMap = naverMap;
            this.zoom = zoom;
            this.cameraMove = cameraMove;
        }

        @Override
        public void onCameraIdle(){

        }
    }

    private class FindMapMakeTask extends AsyncTask<Void, PolygonOverlay, Void> implements Serializable{
        private NaverMap naverMap;
        public FindMapMakeTask(){
            super();
        }
        public FindMapMakeTask(NaverMap naverMap){
            this.naverMap = naverMap;
        }
        public NaverMap getNaverMap() {
            return naverMap;
        }

        public void setNaverMap(NaverMap naverMap) {
            this.naverMap = naverMap;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(PolygonOverlay... values){
            values[0].setMap(getNaverMap());
            super.onProgressUpdate(values);
        }

        @Override
        protected  void onCancelled(Void aVoid){
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled(){

        }

        @SuppressLint("WrongThread")
        @Override
        protected  Void doInBackground(Void... voids){
            //여기서 placeIndex사용하기 ,알고리즘 생각하기
            // 백그라운드 내에서 해당하는 폴리곤 오버레이 객체를 계속해서 만들어 내는 중.......
            LatLng lineLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            LatLng drawLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            for(int i = 0; i< 64; i++) {
                if (i != 0 && i % 8 == 0) {
                    lineLatLng = lineLatLng.offset(-1 * map_radius / 8, 0);
                    drawLatLng = lineLatLng;
                }
                Log.d("mapActivity", "doingBackground");
                Log.d("mapActivity","placeIndex : "+placeIndex);

                PolygonOverlay polygonOverlay = new PolygonOverlay();
                //getFourCornerLatLng 함수는 가장 남서쪽에 있는 좌표를 기준을 하여 총 사각형을 그릴수 있는 4개의 좌표를 알아내는 함수 (남서쪽, 북서쪽, 북동쪽, 남동쪽) 순서의 리스트 값을 반환한다.
                polygonOverlay.setCoords(getFourCornerLatLng(drawLatLng));
                polygonOverlay.setOutlineColor(Color.WHITE);
                if(placeIndex != null) {
                    if (placeIndex.get(i) == 1) {
                        polygonOverlay.setColor(Color.BLACK);
                    } else {
                        polygonOverlay.setColor(Color.TRANSPARENT);
                    }
                }else{
                    polygonOverlay.setColor(Color.TRANSPARENT);
                }
                polygonOverlay.setOutlineWidth(2);
                polygonOverlay.setTag(i);
                squareOverlay.add(polygonOverlay);
                // 메인스레드로 넘겨 줌. 안드로이드의 특성상 메인 스레드가 아니면 UI를 건드릴수 없기 때문에 객체만 만들고 메인스레드로 넘겨서 메인스레드는 UI를 변경함.
                publishProgress(polygonOverlay);
                // 다음 객체에 해당하는 4개의 점의 좌표를 알기 위해 그전에 polygonOverlay의 남동쪽 좌표 값을 다음 폴리곤 오버레이의 남서쪽 좌표 객제 를 생성하는 부분이다.
                drawLatLng = getFourCornerLatLng(drawLatLng).get(3);

            }
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
