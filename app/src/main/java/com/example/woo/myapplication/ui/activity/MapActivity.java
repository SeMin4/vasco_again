package com.example.woo.myapplication.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.OverlapExamineData;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.LatLngData;
import com.example.woo.myapplication.data.Not_Complete_Data;
import com.example.woo.myapplication.ui.view.FindMapFragment;
import com.naver.maps.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

//    private LocationManager manager;
//    private LocationListener gpsLocationListener;
//    private boolean isGPSEnabled;
//    private boolean isNetworkEnabled;
//    private ArrayList<LocationInfo> locationList;
//    private double prevLong;
//    private double prevLat;
//    private int flag;
//    double distance;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    FindMapFragment findMapFragment;
    public static String mid;
    private ArrayList<Integer> placeIndex;
    public Socket mSocket;
    private int existFlag = -1;
    private String findLat = null;
    private String findLng = null;
    private MyGlobals.RetrofitExService retrofitExService;
    //View view_heatmap_info;

    public MapActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.view_heatmap_info, null);

        LinearLayout.LayoutParams paramll = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        addContentView(ll, paramll);

//        try {
//            Log.d("emiiter","들아엄1");
//            mSocket = IO.socket("http://13.125.174.158:9001");
//            if(mSocket == null){
//                Log.d("emiiter","msocket  null");
//            }else {
//                mSocket.connect();
//                Log.d("emiiter","msocket  connect");
//            }
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } //웹소켓 생성

        this.mSocket = FindMapFragment.mSocket;



//        //view_heatmap_info=
//        try {
//            Log.d("emiiter","들아엄1");
//            mSocket = IO.socket("http://13.125.174.158:9001");
//            if(mSocket == null){
//                Log.d("emiiter","msocket  null");
//            }else {
//                mSocket.connect();
//                Log.d("emiiter","msocket  connect");
//            }
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } //웹소켓 생성


        Intent intent = getIntent();
        if (intent != null) {
            mid = intent.getStringExtra("mid");
            placeIndex = (ArrayList<Integer>) intent.getSerializableExtra("placeIndex");
            existFlag = intent.getIntExtra("existFlag",-1);
            Log.d("emiiter","existFlag : "+existFlag);
            //  Log.d("mapActivity","mid : "+mid);
            //  Log.d("mapActivity","placeIndex : "+placeIndex);
        }
        if(existFlag == 1){ //기존방에서 접근
            mid = intent.getStringExtra("mid");
            String cLat = intent.getStringExtra("centerLat");
            String cLng = intent.getStringExtra("centerLng");
            String fLat = intent.getStringExtra("findLat");
            String fLng = intent.getStringExtra("findLng");
            String mRadius = intent.getStringExtra("mapRadius");
            placeIndex = (ArrayList<Integer>)intent.getSerializableExtra("placeIndex");
            if(fLat != null)
                findLat = fLat;
            if(fLng != null)
                findLng = fLng;

            FindMapFragment.centerLatLng = new LatLng(Double.parseDouble(cLat),Double.parseDouble(cLng));
            FindMapFragment.map_radius = Integer.parseInt(mRadius);
            fm = getSupportFragmentManager();
            findMapFragment = (FindMapFragment) fm.findFragmentById(R.id.naverMap_findMap);
            if (findMapFragment == null) {
                System.out.println("placeIndex 설정");
                findMapFragment = FindMapFragment.newInstance();
                Log.d("mapActivity", "placeIndex 설정");
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.naverMap_findMap_layout, findMapFragment);
                fragmentTransaction.commit();
            }
            findMapFragment.setPlaceIndex(placeIndex);
            //findMapFragment.setmSocket(mSocket);

        }else if(existFlag == 0) { //방만들기
            fm = getSupportFragmentManager();
            findMapFragment = (FindMapFragment) fm.findFragmentById(R.id.naverMap_findMap);
            if (findMapFragment == null) {
                System.out.println("placeIndex 설정");
                findMapFragment = FindMapFragment.newInstance();
                Log.d("mapActivity", "placeIndex 설정");
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.naverMap_findMap_layout, findMapFragment);
                fragmentTransaction.commit();
            }
            findMapFragment.setPlaceIndex(placeIndex);
            findMapFragment.setmSocket(mSocket);

        }

        if(findLng != null && findLat != null){ // 수색완료 지점
            //marker 생성 하기
        }
//        getTotalNotComplete();

        retrofitExService.getLatLng(mid).enqueue(new Callback<ArrayList<LatLngData>>() {
            @Override
            public void onResponse(Call<ArrayList<LatLngData>> call, Response<ArrayList<LatLngData>> response) {
                ArrayList<LatLngData> list = response.body();
                Log.d("latLng","onResponse");
                Log.d("latLng","list : "+list);
                if(list == null || list.size() == 0){

                }else{
                    findMapFragment.drawPathsAlreadySaved(list);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LatLngData>> call, Throwable t) {
                Log.d("latLng","onFailure");
                Log.d("latLng","error : "+t);
            }
        });

        mSocket.on("makeRoom",makeRoom);
        try{
            JSONObject data = new JSONObject();
            data.put("uid",MyGlobals.getInstance().getUser().getU_id());
            data.put("mid",mid);
            data.put("color",MyGlobals.getInstance().getUser().getColor());
            mSocket.emit("makeRoom",data);
        }catch (JSONException e) {
            e.printStackTrace();
        }

//        photoView = (PhotoView) findViewById(R.id.photo_view);
//        distance = 2.5;
//        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        locationList = new ArrayList<>();
//        flag = 0;
//        text1 = (TextView) findViewById(R.id.textView);
//        text2 = (TextView) findViewById(R.id.textView2);
//        photoView.setImageResource(R.drawable.test);




//        else if(existFlag == 1){//기존의 방입장
//            Log.d("emiiter","방입장");
//            mSocket.on("attendRoom",attendRoom);
//            try{
//                JSONObject data = new JSONObject();
//                data.put("uid",MyGlobals.getInstance().getUser().getU_id());
//                data.put("mid",mid);
//                mSocket.emit("attendRoom",data);
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }


//        // GPS 프로바이더 사용 가능 여부
//        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Log.d("Mapaaa", "gps");
//        }
//
//        // 네트워크 프로바이더 사용 가능 여부
//        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            Log.d("Mapaaa", "net");
//        }
//
//
//        Log.d("Mapaaa", "oncreate");
//=======
//        photoView = (PhotoView) findViewById(R.id.photo_view);
//        distance = 10;
//        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        locationList = new ArrayList<>();
//        flag = 0;

//        photoView.setImageResource(R.drawable.test);
//
//
//        // GPS 프로바이더 사용 가능 여부
//        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Log.d("Mapaaa","gps");
//        }
//
//        // 네트워크 프로바이더 사용 가능 여부
//        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            Log.d("Mapaaa","net");
//        }
//
//
//        Log.d("Mapaaa","oncreate");
//>>>>>>> gps_new2
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            Log.d("Mapaaa", "onGRANT");

        }


//<<<<<<< HEAD
//        Location lastKnownLocation =
//                manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (lastKnownLocation != null) {
//            Log.d("Mapaaa", "gps");
//            Log.d("Mapaaa", String.valueOf(lastKnownLocation)); // 위치정보 출력
//        } else {
//            lastKnownLocation =
//                    manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (lastKnownLocation != null) {
//                Log.d("Mapaaa", "net");
//                Log.d("Mapaaa", String.valueOf(lastKnownLocation)); // 위치정보 출력
//            }
//        }
//=======
//            Location lastKnownLocation =
//                    manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnownLocation != null) {
//                Log.d("Mapaaa","gps");
//                Log.d("Mapaaa",String.valueOf(lastKnownLocation)); // 위치정보 출력
//            } else {
//                lastKnownLocation =
//                        manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                if (lastKnownLocation != null) {
//                    Log.d("Mapaaa","net");
//                    Log.d("Mapaaa",String.valueOf(lastKnownLocation)); // 위치정보 출력
//                }
//            }
//>>>>>>> gps_new2

        //Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           /* double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            System.out.println("가장최근 : " + latitude + " " + longitude + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
*/


//<<<<<<< HEAD
//        if (isGPSEnabled || isNetworkEnabled) {
//            gpsLocationListener = new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) { //위치 바뀔 때 마다
//                    String provider = location.getProvider();
//                    double longitude = location.getLongitude();
//                    double latitude = location.getLatitude();
//                    Log.d("Mapaaa", "location");
//                    text1.setText("위도" + latitude);
//                    text2.setText("경도" + longitude);
//                    Log.d("위치정보", "위치정보 : "
//                            + provider + " 위도 : " + latitude + " 경도 : " + longitude + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                    if (flag == 0) {
//                        prevLong = longitude;
//                        prevLat = latitude;
//                        //통신(서보로 정보 보내기)
//                        flag = 1;
//
//                    } else if (flag == 1) {
//                        if (Math.sqrt((prevLong - longitude) * (prevLong - longitude) + (prevLat - latitude) * (prevLat - latitude)) <= distance) {
//                            Log.d("gps정보", "prevLong : " + prevLong + " prevLat : " + prevLat + " curLong : " + longitude + " curLat : " + latitude);
//                            prevLong = longitude;
//                            prevLat = latitude;
//                            distance = 2.5;
//                        } else {//gps 신호가 튄경우
//                            Log.d("gps정보", "튄 경우 :prevLong : " + prevLong + " prevLat : " + prevLat + " curLong : " + longitude + " curLat : " + latitude);
//                            distance += 2.5;
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            };
//            ArrayList<String> providerList = (ArrayList<String>) manager.getProviders(false);
//            for (String name : providerList) {
//                manager.requestLocationUpdates(name, 0, 0, gpsLocationListener);
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "Gps를 켜주세요.", Toast.LENGTH_SHORT).show();
//        }
//=======

//        if (isGPSEnabled || isNetworkEnabled) {
//            gpsLocationListener = new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) { //위치 바뀔 때 마다
//                    String provider = location.getProvider();
//                    double longitude = location.getLongitude();
//                    double latitude = location.getLatitude();
//                    Log.d("Mapaaa","location");
//
//                    Log.d("위치정보", "위치정보 : "
//                            + provider + " 위도 : " + latitude+ " 경도 : " + longitude + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                    if(flag == 0){
//                        prevLong = longitude;
//                        prevLat = latitude;
//                        //통신(서보로 정보 보내기)
//                        flag = 1;
//
//                    }else if(flag == 1){
//                        if(Math.sqrt( (prevLong-longitude)*(prevLong-longitude) + (prevLat-latitude)*(prevLat-latitude) ) <= distance){
//
//                                Log.d("gps정보", "prevLong : " + prevLong + " prevLat : " + prevLat + " curLong : " + longitude + " curLat : " + latitude);
//                                prevLong = longitude;
//                                prevLat = latitude;
//                                distance = 10;
//
//                                text1.setText("위도"+latitude);
//                                text2.setText("경도"+longitude);
//
//                        }else{//gps 신호가 튄경우
//                            Log.d("gps정보","튄 경우 :prevLong : "+prevLong+" prevLat : "+prevLat+" curLong : "+longitude+ " curLat : "+latitude);
//                            distance += 10;
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            };
//            ArrayList<String> providerList =(ArrayList<String>)manager.getProviders(false);
//            for(String name : providerList){
//                manager.requestLocationUpdates(name,1000,0,gpsLocationListener);
//            }
//        }else{
//            Toast.makeText(getApplicationContext(),"Gps를 켜주세요.",Toast.LENGTH_SHORT).show();
//        }
//>>>>>>> gps_new2
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //권할설정 관련 버튼 클릭 후 실행
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "권한설정이 되었습니다..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.close();
    }


    //수색 완료 불가 관련 코드

    public void sendComplete(String lat,String lng){ //수색완료 보내기
        try{
            JSONObject data = new JSONObject();
            data.put("m_id",mid);
            data.put("m_find_latitude",lat);
            data.put("m_find_longitude",lng);
            mSocket.emit("findPeople",data);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

//    public void sendImage(File file){//수색불가 보내기
//        mSocket.on("specialThing",getNotComplete);
//        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"),file);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("upload",file.getName(),fileReqBody);
//        RequestBody mapId =  RequestBody.create(MediaType.parse("text/plain"),mid);
//        retrofitExService.postNotComplete(mapId,part).enqueue(new Callback<OverlapExamineData>() {
//            @Override
//            public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
//                OverlapExamineData data = response.body();
//                if(data.getOverlap_examine().equals("success")){
//                    try{
//                        JSONObject latLng = new JSONObject();
//                        latLng.put("ul_longitude",);
//                        latLng.put("ul_latitude",);
//                        latLng.put("ul_desc",);
//                        latLng.put("ul_file",file.getName());
//                        mSocket.emit("specialThing",latLng);
//                    }catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(),"이미지 보내기 실패",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OverlapExamineData> call, Throwable t) {
//                Toast.makeText(getApplicationContext(),"이미지 보내기 실패",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }



    Emitter.Listener getNotComplete = new Emitter.Listener() { //다른사람이 올린 수색불가 특이사항 받아오기
        @Override
        public void call(Object... args) {
            try{
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){
                    String lat = (String)data.get("latitude");
                    String lng = (String)data.get("longitude");
                    //marker생성
                }
            }catch(JSONException e){

            }

        }
    };

    Emitter.Listener getComplete = new Emitter.Listener() { //다른사람이 올린 수색완료 받아오기
        @Override
        public void call(Object... args) {
            try{
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){

                }else{

                }
            }catch (JSONException e){

            }
        }
    };

    public void getNotCompleteDetail(String lat,String lng){ //마커클릭시 세부사항 받아오기
        retrofitExService.getNotCompleteDetail(mid,lat,lng).enqueue(new Callback<Not_Complete_Data>() {
            @Override
            public void onResponse(Call<Not_Complete_Data> call, Response<Not_Complete_Data> response) {
                Not_Complete_Data data = response.body();
                //여기서 넣기
            }

            @Override
            public void onFailure(Call<Not_Complete_Data> call, Throwable t) {

            }
        });

    }



    //수색 완료 불가 관련 코드


    public Socket getMsocket(){
        return this.mSocket;
    }


    //여기서부터 서버로 부터 socket에서 데이터를 받아 이벤트 처리

    private Emitter.Listener makeRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONObject receivedData = (JSONObject)args[0];
                String check = (String)receivedData.get("check");
                if(check.equals("success")){
                    Log.d("emiiter","연결완료");
                    runOnUiThread(new Runnable() {
                        @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"연결 이 완료 이후 방입장 완료",Toast.LENGTH_SHORT).show();
                    }
                    });



                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"방입장 실패. 위치정보가 기록이 안됩니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (JSONException e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"방입장 실패. 위치정보가 기록이 안됩니다.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };



//    private Emitter.Listener attendRoom = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            try{
//                JSONObject receivedData = (JSONObject)args[0];
//                String check = (String)receivedData.get("check");
//                if(check.equals("success")){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),"연결 이 완료 이후 방참여 완료",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }else{
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),"방참여 실패. 위치정보가 기록이 안됩니다.",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }catch (JSONException e){
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),"방참여 실패. 위치정보가 기록이 안됩니다.",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//    };
//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            try {
//                Log.d("emiiter","들아엄2");
//                JSONObject receivedData = (JSONObject) args[0];
//                String check = (String) receivedData.get("check");
//                if (check.equals("success")) {
//                    Toast.makeText(getApplicationContext(),"방 입장을 성공하였습니다.",Toast.LENGTH_SHORT).show();
//                } else if (check.equals("error")) {
//                    Toast.makeText(getApplicationContext(),"방 입장을 실패입니다. 현재 위치정보가 찍혀지지 않습니다",Toast.LENGTH_SHORT).show();
//                }
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//
//        }
//    };


}
