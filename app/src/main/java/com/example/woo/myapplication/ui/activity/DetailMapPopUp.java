package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.ui.view.FindMapFragment;
import com.example.woo.myapplication.data.Not_Complete_Data;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolygonOverlay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
    import java.util.Arrays;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DetailMapPopUp extends Activity implements OnMapReadyCallback {
    private MapView mapView;
    private double centerLat;
    private double centerLng;
    private LatLng centerLatLng;
    private Socket mSocket;
    private NaverMap naverMap;
    private Marker marker = null;
    private ArrayList<Not_Complete_Data> markerData;
    private ArrayList<LatLng> markerLatLng;
    private int REQUEST_CODE = 1234;
    Button completedBtn;
    Button notfoundedBtn;
    LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSocket = FindMapFragment.mSocket;
        mSocket.on("findPeople",getComplete);
        mSocket.on("specialThing",getNotComplete);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.pop_up_detail_map);

        notfoundedBtn = (Button)findViewById(R.id.notFoundedBtn);
        completedBtn = (Button)findViewById(R.id.completedBtn);
        markerLatLng = new ArrayList<>();
        Intent intent = getIntent();
        centerLat = intent.getDoubleExtra("Lat", -1);
        centerLng = intent.getDoubleExtra("Lng", -1);
        markerData = (ArrayList<Not_Complete_Data>) intent.getSerializableExtra("MarkerData");
        if(markerData != null){
            for(int i = 0; i < markerData.size(); ++i){
                LatLng tmp = new LatLng(Double.parseDouble(markerData.get(i).getUl_latitude()), Double.parseDouble(markerData.get(i).getUl_longitude()));
                markerLatLng.add(tmp);
            }
        }

        centerLatLng = new LatLng(centerLat, centerLng);
        mapView = findViewById(R.id.detail_naver_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

//        completedBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //sendComplete
//            }
//        });
    }

    Emitter.Listener getComplete = new Emitter.Listener() { //다른사람이 올린 수색완료 받아오기
        @Override
        public void call(Object... args) {
            try{
                Log.d("수색완료","detailmap");
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){
                    if(marker!=null){
                        Marker prevMarker = marker;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prevMarker.setMap(null);
                            }
                        });
                    }
                    Log.d("수색완료","if문안");
                    //수색 완료 marker 찍기
                    Double lat = (Double) data.get("latitude");
                    Double lng = (Double)data.get("longitude");
                    MapActivity.findLat = ""+lat;
                    MapActivity.findLng = ""+lng;
                    marker = new Marker();
                    marker.setPosition(new LatLng(lat,lng));
                    marker.setIconTintColor(Color.GREEN);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("수색완료","marker");
                            Log.d("수색완료",""+naverMap);
                            marker.setMap(naverMap);
                        }
                    });
                }else{

                }
            }catch (JSONException e){

            }
        }
    };

    Emitter.Listener getNotComplete = new Emitter.Listener() { //다른사람이 올린 수색불가 특이사항 받아오기
        @Override
        public void call(Object... args) {
            try{
                if(InsertDetailsPopUp.insertDetailsPopUp != null)
                    InsertDetailsPopUp.insertDetailsPopUp.finish();
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){
                    Double lat = (Double)data.get("latitude");
                    Double lng = (Double)data.get("longitude");
                    //수색 불가 marker생성
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(lat,lng));
                    marker.setIconTintColor(Color.RED);
                    marker.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            Intent intent = new Intent(getApplicationContext(),SpecialInfoPopup.class );
                            intent.putExtra("mid", MapActivity.mid);
                            intent.putExtra("lat", lat.toString());
                            intent.putExtra("lng", lng.toString());
                            startActivity(intent);
                            return false;
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            marker.setMap(naverMap);
                        }
                    });
                }
            }catch(JSONException e){

            }

        }
    };

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
        this.naverMap = naverMap;
        if(MapActivity.findLat!=null && MapActivity.findLng!= null) {
            marker = new Marker();
            marker.setPosition(new LatLng(Double.parseDouble(MapActivity.findLat), Double.parseDouble(MapActivity.findLng)));
            marker.setMap(naverMap);
        }
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
        //지도에 해당하는 마커 다 그리기
        if(markerLatLng != null && markerLatLng.size() != 0 ){
            for(int i = 0; i < markerLatLng.size(); ++i){
                Marker marker = new Marker();
                marker.setPosition(markerLatLng.get(i));
                marker.setIconTintColor(Color.RED);
                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        Intent intent = new Intent(getApplicationContext(),SpecialInfoPopup.class );
                        intent.putExtra("mid", MapActivity.mid);
                        intent.putExtra("lat", "" + marker.getPosition().latitude);
                        intent.putExtra("lng", "" + marker.getPosition().longitude);
                        startActivity(intent);
                        return false;
                    }
                });
                marker.setMap(naverMap);
            }
        }

        naverMap.setOnMapLongClickListener((pointF, latLng) -> {
            InfoWindow infoWindow = new InfoWindow();
            infoWindow.setAlpha(0.9f);
            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                @NonNull
                @Override
                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                    return "이 곳을 등록하려면 수색완료 혹은 수색불가를 클릭하세요";
                }
            });
            position = latLng;

            infoWindow.setPosition(latLng);
            infoWindow.open(naverMap);
//            windowHashMap.put(infoWindow.hashCode(), infoWindow);

            completedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(latLng != null){
                        sendComplete(latLng.latitude,latLng.longitude);
                    }
                }
            });

            notfoundedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(latLng != null){
                        Intent intent = new Intent(getApplicationContext(),InsertDetailsPopUp.class);
                        Log.d("click","lat : "+latLng.latitude);
                        Log.d("click","lng : "+latLng.longitude);
                        intent.putExtra("lat",latLng.latitude);
                        intent.putExtra("lng",latLng.longitude);
                        infoWindow.close();
                        startActivity(intent);
                        intent.putExtra("markerId", infoWindow.hashCode());
                        //intent.putExtra("latitude", latLng.latitude);
                        //intent.putExtra("longitude", latLng.longitude);
                        startActivityForResult(intent,REQUEST_CODE);
                    }

                }
            });
/*                infoWindow.setOnClickListener(overlay -> {
                Intent intent = new Intent(getApplicationContext(),InsertDetailsPopUp.class);
                Log.d("click","lat : "+latLng.latitude);
                Log.d("click","lng : "+latLng.longitude);
                intent.putExtra("lat",latLng.latitude);
                intent.putExtra("lng",latLng.longitude);
                infoWindow.close();
                startActivity(intent);


                intent.putExtra("markerId", infoWindow.hashCode());
                intent.putExtra("latitude", latLng.latitude);
                intent.putExtra("longitude", latLng.longitude);
                startActivityForResult(intent,REQUEST_CODE);
                infoWindow.close();
//                intent.putExtra("mapId", mapId);
//                intent.putExtra("index",index);
//                startActivityForResult(intent, RECORD_REGISTER);

                return true;
            });
*/
        });

    }


    public void sendComplete(Double lat,Double lng){ //수색완료 보내기
        try{
            JSONObject data = new JSONObject();
            data.put("m_find_latitude",lat);
            data.put("m_find_longitude",lng);
            mSocket.emit("findPeople",data);
            finish();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public List<LatLng> getFourCornerLatLng(LatLng standardLatLng){
        return Arrays.asList(
                standardLatLng,
                standardLatLng.offset(20,0),
                standardLatLng.offset(20,20),
                standardLatLng.offset(0,20)
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE){


            }


    }

}
