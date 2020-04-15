package com.example.woo.myapplication.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.LatLngData;
import com.example.woo.myapplication.data.Not_Complete_Data;
import com.example.woo.myapplication.ui.activity.DetailMapPopUp;
import com.example.woo.myapplication.ui.activity.MapActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FindMapFragment extends Fragment implements OnMapReadyCallback {

    MapFragment mapFragment;
    FragmentTransaction fragmentTransaction;
    Fragment findMapFragment;
    private NaverMap naverMap;
    public static LatLng centerLatLng;
    public static int map_radius;
    public ArrayList<PolygonOverlay> squareOverlay;
    public ArrayList<PolygonOverlay> squareOverlay2;
    public IdleListener cameraIdleListener;
    View view;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    PathOverlay myPath;
    public ArrayList<PathOverlay> allPathsOverlay = new ArrayList<>(); //실시간 수색
    public ArrayList<PathOverlay> savedPathsOverlay = new ArrayList<>(); //이미 db에 저장된 경로들

    private ArrayList<Integer> placeIndex; //수색구역 정보
    public static Socket mSocket;
    Button reload_btn;
    //zoom in-out button 정보
    Button zoom_in_btn;
    Button zoom_out_btn;
    private LatLng zoomCenterLatLng;
//    View heatmapView;

    private int zoom_level;
    private int[] click_index  = new int[2];
    protected int[] heat_map_rate = new int[64];
    private Marker marker = null;

//    public FindMapFragment(){
//        this.mSocket = MapActivity.mSocket;
//    }

    private MyGlobals.RetrofitExService retrofitExService;

    public Socket getmSocket() {
        return mSocket;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    int mycolor;
    private NaverMap.OnLocationChangeListener onLocationChangeListener;
    double latitude;
    double longitude;
    double prevLat;
    double prevLong;
    double minDistance = 2.5;
    double maxDistance = 10;
    int flag =0;
    int count;
    int tmplat = 0, tmplng = 100;



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
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        try {
            Log.d("emiiter","들아엄1");
            mSocket = IO.socket("http://13.125.174.158:9001");
            if(mSocket == null){
                Log.d("emiiter","msocket  null");
            }else {
                mSocket.connect();
                Log.d("emiiter","msocket  connect");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } //웹소켓 생성


        mSocket.on("drawLatLng",getLatLng); //그림그리기 이벤트
        mSocket.on("heatmap",getHeatmapRate);
        mSocket.on("findPeople",getComplete); //수색완료
        mSocket.on("specialThing",getNotComplete);//수색불가
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
//        heatmapView = (View)getView().findViewById(R.id.view_heatmap_info);
        FrameLayout frameLayout = (FrameLayout)rootView.findViewById(R.id.frame_lay);
        zoom_in_btn = rootView.findViewById(R.id.zoom_in_btn);
        zoom_out_btn = rootView.findViewById(R.id.zoom_out_btn);
        reload_btn = rootView.findViewById(R.id.reload_btn);
        zoom_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getZoom_level() == 0){
                    if(getClick_index(getZoom_level()) == -1){
                        Toast.makeText(getContext(), "확대할 부분을 클릭해주세요", Toast.LENGTH_LONG).show();
                    }
                    else{
                        getNaverMap().moveCamera(CameraUpdate.fitBounds(new LatLngBounds(squareOverlay.get(getClick_index(getZoom_level())).getBounds().getSouthWest(), squareOverlay.get(getClick_index(getZoom_level())).getBounds().getNorthEast())));
                        squareOverlay.get(getClick_index(getZoom_level())).setColor(Color.TRANSPARENT);
                        for(int i = 0; i<squareOverlay.size(); ++i){
                            squareOverlay.get(i).setMap(null);
                        }
//                    getNaverMap().setMinZoom(getNaverMap().getCameraPosition().zoom);
//                    getNaverMap().setMaxZoom(getNaverMap().getCameraPosition().zoom);
                        setZoomCenterLatLng(getNaverMap().getCameraPosition().target);
                        map_radius /= 8;
                        setZoom_level(1);
                        FindMapMakeTask gridMapMakeTask = new FindMapMakeTask(getNaverMap(), getZoomCenterLatLng(), map_radius);
                        gridMapMakeTask.execute();
                    }

                }
                else{
                    Toast.makeText(getContext(),"더 이상 확대 할 수 없습니다.", Toast.LENGTH_LONG).show();

                }

            }
        });
        zoom_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getZoom_level() == 1){
                    setZoom_level(0);
                    setClick_index(-1,0);
                    map_radius *= 8;
                    getNaverMap().moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2))));
                    for(int i = 0; i<squareOverlay.size(); ++i){
                        squareOverlay.get(i).setMap(null);

                    }
                    squareOverlay.clear();
//                getNaverMap().setMinZoom(getNaverMap().getCameraPosition().zoom);
//                getNaverMap().setMaxZoom(getNaverMap().getCameraPosition().zoom);
//                    FindMapMakeTask gridMapMakeTask = new FindMapMakeTask(getNaverMap(), centerLatLng, map_radius);
//                    gridMapMakeTask.execute();
                    mSocket.emit("heatmap");
                }
                else{
                    Toast.makeText(getContext(),"더 이상 축소 할 수 없습니다.", Toast.LENGTH_LONG).show();
                }

            }
        });
        reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getZoom_level() == 0){
                    for(int i = 0; i<squareOverlay.size(); ++i){
                        squareOverlay.get(i).setMap(null);

                    }
                    squareOverlay.clear();
                    mSocket.emit("heatmap");
                }

//                        data.put("mid",mid);
//                        mSocket.emit("makeRoom",data);
            }
        });
        return  rootView;
    }

//    5120 / 8 = 640
//    640 / 8 = 80
//    80 / 8 = 10

//    2560 / 8 = 320
//    320 / 8 = 40
//    40 / 8 = 5

//    1280 / 8 = 160
//    160 / 8 = 20
//    20 / 8 = 2....

    @UiThread
    @Override
        public void onMapReady(@NonNull NaverMap naverMap){
        //처음 줌레벨을 설정하고 줌레벨에 해당하는 클릭 인덱스 값이 없으므로 -1으로 설정
        setZoom_level(0);
        setNaverMap(naverMap);
        if(MapActivity.findLat!=null && MapActivity.findLng!= null){
            marker = new Marker();
            marker.setPosition(new LatLng(Double.parseDouble(MapActivity.findLat),Double.parseDouble(MapActivity.findLng)));
            marker.setMap(naverMap);
        }
        setClick_index(-1,getZoom_level());
        naverMap.getUiSettings().setZoomControlEnabled(false);
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.getUiSettings().setScrollGesturesEnabled(false);
        naverMap.setOnMapDoubleTapListener(new NaverMap.OnMapDoubleTapListener() {
            @Override
            public boolean onMapDoubleTap(@NonNull PointF pointF, @NonNull LatLng latLng) {
                return true;
            }
        });
        naverMap.setCameraPosition(new CameraPosition(centerLatLng, 10));
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        List<LatLng> coords = new ArrayList<>();
        myPath = new PathOverlay();
        mycolor = Integer.parseInt(MyGlobals.getInstance().getUser().getColor());
        count = 0;
        // 지도 중심으로 부터 지도의 전체 크기의 절반 만큼 남서쪽 북동쪽 부분으로 바운드를 결정하고 그 부분을 볼 수 있는 부분으로 카메라를 옮김.
        naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2))));
        naverMap.setExtent(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2)));
//        naverMap.setMinZoom(naverMap.getCameraPosition().zoom);
//        naverMap.setMaxZoom(naverMap.getCameraPosition().zoom);
        //네이버맵의 맵 설정을 위성 맵으로 설정
        naverMap.setMapType(NaverMap.MapType.Satellite);

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        //myPath.setCoords(coords);
        myPath.setColor(mycolor);

        onLocationChangeListener = new NaverMap.OnLocationChangeListener() {

            @Override
            public void onLocationChange(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLngBounds latLngBounds = new LatLngBounds(centerLatLng.offset(1280*-1/2,1280*-1/2),centerLatLng.offset(1280/2,1280/2));
                LatLng latLng = new LatLng(latitude, longitude);

                if (latLngBounds.contains(latLng) == false){
                    return;
                }

                    if (flag == 0) {
                        prevLong = longitude;
                        prevLat = latitude;
                        //통신(서보로 정보 보내기)
                        flag = 1;

                    } else if (flag == 1) {
                        //Log.d("aaa",String.valueOf(latitude));
                        //myPath.setMap(naverMap);
                        Location prev = new Location("prev");
                        prev.setLatitude(prevLat);
                        prev.setLongitude(prevLong);
                        double euclidean = location.distanceTo(prev);
                        //double euclidean =Math.sqrt((prevLong - longitude) * (prevLong - longitude) + (prevLat - latitude) * (prevLat - latitude));
                        Log.d("aaa","euclidean"+String.valueOf(euclidean));
                        if (euclidean >=minDistance && euclidean <= maxDistance) {
                            prevLong = longitude;
                            prevLat = latitude;
                            Log.d("aaa", String.valueOf(latitude));
                            coords.add(new LatLng(latitude, longitude));
                            if(coords.size() >=2){
                                myPath.setCoords(coords);
                                myPath.setMap(naverMap);
                            }
                            try{
                                JSONObject data = new JSONObject();
                                //data.put("Lat", tmplat);
                                data.put("Lat", latitude);
                                //data.put("Lng", tmplng);
                                data.put("Lng", longitude);
                                int tmpidx = getClick_index(0);
                                data.put("idx", tmpidx);


                                mSocket.emit("sendLatLng", data);
//                        data.put("mid",mid);
//                        mSocket.emit("makeRoom",data);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (euclidean> maxDistance){//gps 신호가 튄경우
                            if (count <=5){
                                count++;
                            }
                            else{
                                prevLong = longitude;
                                prevLat = latitude;

                                count =0;
                            }
                        }

                    }


            }
        };

        naverMap.addOnLocationChangeListener(onLocationChangeListener);
        locationOverlay.setVisible(true);
        locationOverlay.setCircleRadius(100);
        //uiSettings.setLocationButtonEnabled(true);
        mSocket.emit("heatmap");

        getTotalNotComplete();

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
        private LatLng centerLatLng;
        private int map_radius;
        public FindMapMakeTask(){
            super();
        }
        public FindMapMakeTask(NaverMap naverMap, LatLng centerLatLng, int map_radius){
            this.naverMap = naverMap;
            this.centerLatLng = centerLatLng;
            this.map_radius = map_radius;
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
            LatLng lineLatLng = this.centerLatLng.offset(this.map_radius/8*3,-1* this.map_radius / 2);
            LatLng drawLatLng = this.centerLatLng.offset(this.map_radius/8*3,-1* this.map_radius / 2);
            for(int i = 0; i< 64; i++) {
                if (i != 0 && i % 8 == 0) {
                    lineLatLng = lineLatLng.offset(-1 * this.map_radius / 8, 0);
                    drawLatLng = lineLatLng;
                }
               // Log.d("mapActivity", "doingBackground");
              //  Log.d("mapActivity","placeIndex : "+placeIndex);

                PolygonOverlay polygonOverlay = new PolygonOverlay();
                //getFourCornerLatLng 함수는 가장 남서쪽에 있는 좌표를 기준을 하여 총 사각형을 그릴수 있는 4개의 좌표를 알아내는 함수 (남서쪽, 북서쪽, 북동쪽, 남동쪽) 순서의 리스트 값을 반환한다.
                polygonOverlay.setCoords(getFourCornerLatLng(drawLatLng));

                polygonOverlay.setOutlineColor(Color.WHITE);
                if(getZoom_level() != 1){
                    if(placeIndex != null) {
                        if (placeIndex.get(i) == 1) {
                            polygonOverlay.setColor(Color.BLACK);
                        } else {
                            polygonOverlay.setColor(Color.TRANSPARENT);
                        }
                    }else{
                        polygonOverlay.setColor(Color.TRANSPARENT);
                    }
                }else{
                    polygonOverlay.setColor(Color.TRANSPARENT);
                }
                polygonOverlay.setOutlineWidth(2);
                polygonOverlay.setTag(i);
                if(getZoom_level() == 0){
                    if(!((placeIndex != null) && (placeIndex.get(i) == 1))){
                        polygonOverlay.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                //원래 색칠되어 있으면 색칠 된 부분을 투명으로 변경
                                if(getClick_index(getZoom_level()) != -1){
                                    showHeatMap(squareOverlay.get(getClick_index(getZoom_level())), rateCalculation(heat_map_rate[getClick_index(0)]));
                                }
                                //다른 인덱스값을 클릭한 인덱스 값으로 지정
                                if(getClick_index(getZoom_level()) != (Integer)overlay.getTag()){
                                    ((PolygonOverlay)overlay).setColor(Color.RED);
                                    setClick_index((Integer)overlay.getTag(),getZoom_level());
                                }
                                else{//원래 색칠되어 있는 부분을 클릭한 경우
                                    setClick_index(-1, getZoom_level());
                                }

                                return false;
                            }
                        });
                    }

                   //heat_map_rate[i]=40;//테스트용 임시 값

                    Log.d("showHeat",String.valueOf(heat_map_rate[i]));
                    showHeatMap(polygonOverlay, rateCalculation(heat_map_rate[i]));
//                    showLines();
                  //  polygonOverlay.setColor(Color.BLUE);

                }
                else{
                    polygonOverlay.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            LatLng tmp = polygonOverlay.getBounds().getCenter();
                            double tmplat = tmp.latitude;
                            double tmplng = tmp.longitude;
                            Intent intent = new Intent(getContext(), DetailMapPopUp.class);
                            intent.putExtra("Lat", tmplat);
                            intent.putExtra("Lng", tmplng);
                            ArrayList<Not_Complete_Data> marker_data = new ArrayList<>();
                            retrofitExService.getNotCompleteData(MapActivity.mid).enqueue(new Callback<ArrayList<Not_Complete_Data>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Not_Complete_Data>> call, Response<ArrayList<Not_Complete_Data>> response) {
                                    ArrayList<Not_Complete_Data> list = response.body();
                                    if(list != null){
                                        for(int i =0;i<list.size();i++){
                                            Not_Complete_Data data = list.get(i);
                                            String lat = data.getUl_latitude();
                                            String lng = data.getUl_longitude();
                                            LatLng tmpLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                            if(polygonOverlay.getBounds().contains(tmpLatLng)){
                                                marker_data.add(data);
                                            }
                                        }

                                        intent.putExtra("MarkerData", marker_data);
//                                        startActivity(intent);

                                    }else{
//                                        Toast.makeText(getContext(),"수색 불가 정보를 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<ArrayList<Not_Complete_Data>> call, Throwable t) {
//                                    Toast.makeText(getContext(),"수색 불가 정보를 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();

                                }
                            });

                            return false;
                        }
                    });
                }


                squareOverlay.add(polygonOverlay);
                // 메인스레드로 넘겨 줌. 안드로이드의 특성상 메인 스레드가 아니면 UI를 건드릴수 없기 때문에 객체만 만들고 메인스레드로 넘겨서 메인스레드는 UI를 변경함.
                publishProgress(polygonOverlay);
                // 다음 객체에 해당하는 4개의 점의 좌표를 알기 위해 그전에 polygonOverlay의 남동쪽 좌표 값을 다음 폴리곤 오버레이의 남서쪽 좌표 객제 를 생성하는 부분이다.
                drawLatLng = getFourCornerLatLng(drawLatLng).get(3);

            }
            Log.d("showLines","showLines");
            Log.d("showLines",""+savedPathsOverlay.size());
            showLines(); // 표시 여부 판단 후 내 경로, 실시간으로 받아온 경로, 이미 저장된 경로 표시
            return null;
        }
    }

    protected void showHeatMap(PolygonOverlay polygonOverlay, double rate){
        //  double rate = 0.3;


        if (rate>=0 && rate < 0.2){
            polygonOverlay.setColor(Color.TRANSPARENT);
        }
        else if (rate>=0.2 && rate < 0.4){
            polygonOverlay.setColor(getResources().getColor(R.color.heatmap_no1));
        }
        else if(rate >=0.4 && rate <0.6){
            polygonOverlay.setColor(getResources().getColor(R.color.heatmap_no2));
        }
        else if(rate >=0.6 && rate <0.8){
            polygonOverlay.setColor(getResources().getColor(R.color.heatmap_no3));
        }else if(rate <0){
            polygonOverlay.setColor(Color.BLACK);
        }
        else if(rate>=0.8 && rate <=1){
            polygonOverlay.setColor(getResources().getColor(R.color.heatmap_no4));
        }
        else{
            polygonOverlay.setColor(Color.argb(0,255,255,153));
        }

    }

    private double rateCalculation(int count){
        double rate;

        Log.d("showHeat",String.valueOf(count));

        rate= (double)count/50.0; //추후에 비율 수정
        Log.d("showHeat"," rate : "+rate);
        return rate;
    }

    private void showLines(){

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int i;
                if (getZoom_level() == 0) {
                    if (allPathsOverlay != null && allPathsOverlay.size() >= 1) {
                        for (i = 0; i < allPathsOverlay.size(); i++) {
                            allPathsOverlay.get(i).setMap(null);
                        }
                    }
                    if (savedPathsOverlay != null && savedPathsOverlay.size() >= 1) {
                        for (i = 0; i < savedPathsOverlay.size(); i++) {
                            savedPathsOverlay.get(i).setMap(null);
                        }
                    }
                                if(myPath != null){
                                    myPath.setMap(null);
                                }
                } else if (getZoom_level() == 1) {
                    Log.d("showLines","zoomlevel1");
                    if (allPathsOverlay != null && allPathsOverlay.size() >= 1) {
                        for (i = 0; i < allPathsOverlay.size(); i++) {
                            allPathsOverlay.get(i).setMap(naverMap);
                        }
                    }
                    if (savedPathsOverlay != null && savedPathsOverlay.size() >= 1) {
                        for (i = 0; i < savedPathsOverlay.size(); i++) {
                            Log.d("showLines",""+savedPathsOverlay.get(i).getCoords()+"@@");
                            savedPathsOverlay.get(i).setMap(naverMap);
                        }
                    }

                }
            }
        });
    }

    public List<LatLng> getFourCornerLatLng(LatLng standardLatLng){
        return Arrays.asList(
                standardLatLng,
                standardLatLng.offset(map_radius/8,0),
                standardLatLng.offset(map_radius/8,map_radius/8),
                standardLatLng.offset(0,map_radius/8)
        );
    }


    //전체 줌레벨 설정
    public int getZoom_level() {
        return zoom_level;
    }

    public void setZoom_level(int zoom_level) {
        this.zoom_level = zoom_level;
    }


    //클릭된 인덱스 설정
    public int getClick_index(int level) {
        return click_index[level];
    }
    //클릭된 인덱스 설정
    public void setClick_index(int click_index, int level) {
        this.click_index[level] = click_index;
    }

    //zoom 했을때 새로운 맵의 center 값
    public LatLng getZoomCenterLatLng() {
        return zoomCenterLatLng;
    }

    public void setZoomCenterLatLng(LatLng zoomCenterLatLng) {
        this.zoomCenterLatLng = zoomCenterLatLng;
    }

    public void setNaverMap(NaverMap naverMap){
        this.naverMap = naverMap;
    }
    public NaverMap getNaverMap(){
        return  this.naverMap;
    }

    //경로 그리기
    Emitter.Listener getLatLng = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                Log.d("getLatLng","호출");
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                String latLng = (String)data.get("latLng"); //위도경도 스트링
                String color = (String)data.get("color");
                if(check.equals("success")){
                    Log.d("aaaa",latLng);
                    drawPaths(tokenizer(latLng), Integer.parseInt(color),allPathsOverlay);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

//    Emitter.Listener
    Emitter.Listener getHeatmapRate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONObject data = (JSONObject)args[0];
                Log.d("data : ", data.toString());
                for(int i = 0; i < 64; ++i){
                    heat_map_rate[i] = (int) data.get("" + i);
                }
                for(int i = 0; i < 64; ++i){
                    Log.d("Heat Map", "" + i + " 번째 : "+ heat_map_rate[i]);
                }
                //AsynTask를 extend 해서 비동기적으로 뒤에 해당하는 격자표 그리기.
                FindMapMakeTask gridMapMakeTask = new FindMapMakeTask(naverMap, centerLatLng, map_radius);
                gridMapMakeTask.execute();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };


    Emitter.Listener getComplete = new Emitter.Listener() { //다른사람이 올린 수색완료 받아오기
        @Override
        public void call(Object... args) {
            try{
                Log.d("수색완료","findmap");
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){
                    if(marker!=null){
                        Marker prevMarker = marker;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prevMarker.setMap(null);
                            }
                        });
                    }

                    Log.d("수색완료","if문안");
                    //수색완료 마커찍기
                     Double lat = (Double)data.get("latitude");
                     Double lng = (Double)data.get("longitude");
                     MapActivity.findLat = ""+lat;
                     MapActivity.findLng = ""+lng;
                     Log.d("수색완료","lat : "+lat + " lng : "+lng);
                     marker = new Marker();
                     marker.setPosition(new LatLng(lat,lng));
                     marker.setIconTintColor(Color.GREEN);
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Log.d("수색완료","marker");
                             Log.d("수색완료",""+getNaverMap());
                             marker.setMap(getNaverMap());
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
                JSONObject data = (JSONObject)args[0];
                String check = (String)data.get("check");
                if(check.equals("success")){
                    Double lat = (Double)data.get("latitude");
                    Double lng = (Double)data.get("longitude");
                    //수색불가 marker생성
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(lat,lng));
                    marker.setIconTintColor(Color.RED);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            marker.setMap(getNaverMap());
                        }
                    });
                }
            }catch(JSONException e){

            }

        }
    };



    private ArrayList tokenizer (String positions){ //위도, 경도 읽어서 list에 저장
        ArrayList<LatLng> arrayList = new ArrayList<>();
        String[] array = positions.split("@");
        StringTokenizer token1;
        LatLng latLng;

        for (int i =0; i< array.length;i++){
            token1 = new StringTokenizer(array[i] , ";");
            latLng = new LatLng(Double.parseDouble(token1.nextToken()),Double.parseDouble(token1.nextToken()));
            Log.d("위치받기",String.valueOf(latLng));
            arrayList.add(latLng);
        }

        return arrayList;
    }

    protected void drawPaths (ArrayList pathList, int pathColor, ArrayList<PathOverlay> overlayList){ //다른 사람들의 경로 표시

                PathOverlay path = new PathOverlay();
                path.setColor(pathColor);
                path.setCoords(pathList);
                overlayList.add(path);

                if(getZoom_level() ==1){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            path.setMap(naverMap);
                        }
                    });
                }
    }

    public void drawPathsAlreadySaved(ArrayList<LatLngData> list){ //디비에서 받은 데이터로 그림그리기

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                for (i = 0; i < list.size(); i++) {
                    String latlng = list.get(i).getLatlng_arr();
                    Log.d("bbbb",latlng);
                    drawPaths(tokenizer(latlng), Integer.parseInt(list.get(i).getColor()),savedPathsOverlay);
                }

            }

        });
    }

    @UiThread
    public void getTotalNotComplete(){ //처음 입장시 전체 수색불가 받아오기
        retrofitExService.getNotCompleteData(MapActivity.mid).enqueue(new Callback<ArrayList<Not_Complete_Data>>() {
            @Override
            public void onResponse(Call<ArrayList<Not_Complete_Data>> call, Response<ArrayList<Not_Complete_Data>> response) {
                ArrayList<Not_Complete_Data> list = response.body();
                if(list != null){
                    for(int i =0;i<list.size();i++){
                        Not_Complete_Data data = list.get(i);
                        String lat = data.getUl_latitude();
                        String lng = data.getUl_longitude();
                        LatLng tmpLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        Marker marker = new Marker();
                        marker.setPosition(tmpLatLng);
                        marker.setIconTintColor(Color.RED);
                        marker.setMap(getNaverMap());
                        //marker 만들기
                    }
                }else{
                    Toast.makeText(getContext(),"수색 불가 정보를 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Not_Complete_Data>> call, Throwable t) {
                Toast.makeText(getContext(),"수색 불가 정보를 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
