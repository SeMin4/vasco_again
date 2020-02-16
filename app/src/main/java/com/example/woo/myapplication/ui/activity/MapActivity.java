package com.example.woo.myapplication.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.LocationInfo;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private LocationManager manager;
    private LocationListener gpsLocationListener;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private ArrayList<LocationInfo> locationList;
    private double prevLong;
    private double prevLat;
    private int flag;
    double distance;
    private PhotoView photoView;
    TextView text1;
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        photoView = (PhotoView) findViewById(R.id.photo_view);
        distance = 2.5;
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationList = new ArrayList<>();
        flag = 0;
        text1=(TextView)findViewById(R.id.textView);
        text2=(TextView)findViewById(R.id.textView2);
        photoView.setImageResource(R.drawable.test);


        // GPS 프로바이더 사용 가능 여부
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Mapaaa","gps");
        }

        // 네트워크 프로바이더 사용 가능 여부
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d("Mapaaa","net");
        }


        Log.d("Mapaaa","oncreate");
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            Log.d("Mapaaa","onGRANT");

        }


            Location lastKnownLocation =
                    manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                Log.d("Mapaaa","gps");
                Log.d("Mapaaa",String.valueOf(lastKnownLocation)); // 위치정보 출력
            } else {
                lastKnownLocation =
                        manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    Log.d("Mapaaa","net");
                    Log.d("Mapaaa",String.valueOf(lastKnownLocation)); // 위치정보 출력
                }
            }

            //Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           /* double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            System.out.println("가장최근 : " + latitude + " " + longitude + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
*/



        if (isGPSEnabled || isNetworkEnabled) {
            gpsLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) { //위치 바뀔 때 마다
                    String provider = location.getProvider();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Log.d("Mapaaa","location");
                    text1.setText("위도"+latitude);
                    text2.setText("경도"+longitude);
                    Log.d("위치정보", "위치정보 : "
                            + provider + " 위도 : " + latitude+ " 경도 : " + longitude + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    if(flag == 0){
                        prevLong = longitude;
                        prevLat = latitude;
                        //통신(서보로 정보 보내기)
                        flag = 1;

                    }else if(flag == 1){
                        if(Math.sqrt( (prevLong-longitude)*(prevLong-longitude) + (prevLat-latitude)*(prevLat-latitude) ) <= distance){
                            Log.d("gps정보","prevLong : "+prevLong+" prevLat : "+prevLat+" curLong : "+longitude+ " curLat : "+latitude);
                            prevLong = longitude;
                            prevLat = latitude;
                            distance = 2.5;
                        }else{//gps 신호가 튄경우
                            Log.d("gps정보","튄 경우 :prevLong : "+prevLong+" prevLat : "+prevLat+" curLong : "+longitude+ " curLat : "+latitude);
                            distance += 2.5;
                        }

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            ArrayList<String> providerList =(ArrayList<String>)manager.getProviders(false);
            for(String name : providerList){
                manager.requestLocationUpdates(name,0,0,gpsLocationListener);
            }
        }else{
            Toast.makeText(getApplicationContext(),"Gps를 켜주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //권할설정 관련 버튼 클릭 후 실행
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"권한설정이 되었습니다..",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"이 앱을 실행하려면 위치 접근 권한이 필요합니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


//    class FilteringThread extends Thread{
//        public void run(){
//            while (true){
//                if(locationList.size()>=10){
//                    ArrayList<LocationInfo> infoList = new ArrayList<>();
//                    for(int i=0;i<10;i++){
//                        infoList.add(locationList.get(i));
//                        locationList.remove(i);
//                    }
//                    String finalCal = locationCal(infoList);
//                }
//            }
//        }
//    }
}
