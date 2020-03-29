package com.example.woo.myapplication.ui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.utils.GridAdapter;
import com.example.woo.myapplication.utils.MapAdater;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.CameraUpdateParams;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
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


public class NaverMapFragment extends Fragment  implements OnMapReadyCallback {
    MapFragment mapFragment;
    public static double zoomLevel;
    public static double centerLat;
    public static double centerLng;
    public LatLng centerLatLng;
    Fragment naverFragment;
    public static boolean moving_camera = false;
    Fragment naverMapfragment;
//<<<<<<< HEAD
    public static int map_radius = 1280;
//=======
    MapView view;
//    int map_radius = 1280;
//>>>>>>> gesture_func
    public EventListener eventListener;
    public IdleListener cameraIdleListener;
    public ArrayList<PolygonOverlay> squareOverlay;
    TextView mapSizeTextView;
    FragmentTransaction fragmentTransaction;
    private RecyclerView recyclerView;
    private MapAdater adapter;
    private RelativeLayout relativeLayout;
    private GestureDetector gestureDetector;
    private RecyclerView.OnItemTouchListener onItemTouchListener;
    static public int flag = 0;

    //ArrayList<Integer> placeIndex;//삭제된 장소는 값이 1로 되어있음

    public static NaverMapFragment newInstance() {
        NaverMapFragment fragment = new NaverMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void detachView(){
        relativeLayout.removeView(recyclerView);
    }

    public void gestureFunc(){
        recyclerView = new RecyclerView(getContext());
        GridLayoutManager mGridmanager = new GridLayoutManager(getContext(),8);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        adapter = new MapAdater(dm.widthPixels/8);
        recyclerView.setLayoutManager(mGridmanager);
        recyclerView.setAdapter(adapter);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(recyclerView,params);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.navermap, mapFragment).addToBackStack(null).commit();
        }


//        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.fragment_map,null);
        squareOverlay = new ArrayList<PolygonOverlay>();
        mapFragment.getMapAsync(this);
        naverMapfragment = fm.findFragmentById(R.id.navermap);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_map, container, true);
        view = rootView.findViewById(R.id.navermap);
        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        mapSizeTextView = rootView.findViewById(R.id.mapSizeText);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("터치됨@@@@@@@@@@@@@@@@@@@@@@@@@@");
                Log.d("터치","터치돰@@@@@@@@");
                return true;
            }
        });
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
        //기본적인 네이버맵의 ui 셋팅을 하는 부분 오른쪽에 뜨는 zoomControl 창을 없애고 맵 기울이는 기능을 비활성화 시킴
        naverMap.getUiSettings().setZoomControlEnabled(false);
        naverMap.getUiSettings().setTiltGesturesEnabled(false);
        naverMap.getUiSettings().setScrollGesturesEnabled(false);

//<<<<<<< HEAD
        // 이전의 액티비티로 부터 설정해주었던 centerLat, centerLng 값을 받아서 지도 중심을 설정 // 다음 findMapActivity의 센터값도 설정 해줌.
//=======
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("ondowne.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : "+e.getX());
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                System.out.println("onshowpresse.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : "+e.getX());
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                System.out.println("onsingletapupe.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : "+e.getX());
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println("onscrolle.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : " + e1.getX());

                View view = recyclerView.findChildViewUnder(e1.getX(), e1.getY());
                View view2 = recyclerView.findChildViewUnder(e2.getX(), e2.getY());
                if (flag == 1) {
                    System.out.println("flag 11111111111111111111111111111");
                    if (view != null) {
                        view.setBackgroundColor(Color.RED);
                    }
                    if (view2 != null) {
                        view2.setBackgroundColor(Color.RED);
                    }
                } else if (flag == 0) {
                    System.out.println("flag 000000000000000000000000000000000000");
                    if (view != null) {
                        view.setBackgroundColor(Color.argb(90,255,255,255));
                    }

                    if (view2 != null) {
                        view2.setBackgroundColor(Color.argb(90,255,255,255));                    }
                }
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                System.out.println("onLonge.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : "+e.getX());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                System.out.println("onfillige.getX()@@@@@@@@@@@@@@@@@@@@@@@@@@ : "+e1.getX());
                return true;
            }
        });



        onItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        };



        // 이전의 액티비티로 부터 설정해주었던 centerLat, centerLng 값을 받아서 지도 중심을 설정
//>>>>>>> gesture_func
        centerLatLng = new LatLng(centerLat,centerLng);
        FindMapFragment.centerLatLng = centerLatLng;

        naverMap.setCameraPosition(new CameraPosition(centerLatLng, 10));

        // 지도 중심으로 부터 지도의 전체 크기 1280 m에서 절반 640 미터씩 남서쪽 북동쪽 부분으로 바운드를 결정하고 그 부분을 볼 수 있는 부분으로 카메라를 옮김.
        naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius*-1/2,map_radius*-1/2),centerLatLng.offset(map_radius/2,map_radius/2))));

        //네이버맵의 맵 설정을 위성 맵으로 설정
        naverMap.setMapType(NaverMap.MapType.Satellite);

        //수색 중심지점 마커 생성 하기 (캡션으로 "수색중심지점" 텍스트 설정, 색깔을 빨간색으로 바꾸기)
        Marker marker = new Marker();
        marker.setPosition(centerLatLng);
        marker.setMap(naverMap);
        marker.setCaptionText("수색중심지점");
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.RED);

        zoomLevel = naverMap.getCameraPosition().zoom + 0.9;

        //AsynTask를 extend 해서 비동기적으로 뒤에 해당하는 격자표 그리기.
        GridMapMakeTask gridMapMakeTask = new GridMapMakeTask(naverMap);
        gridMapMakeTask.execute();


        cameraIdleListener = new IdleListener(naverMap, naverMap.getCameraPosition().zoom, false);
        naverMap.addOnCameraIdleListener(cameraIdleListener);

    }

    //이벤트 리스너 클래스 생성 (네이버 지도 내에서 어떠한 이유에서든지 카메라가 변경이 되면 이벤트 리스너 클래스 내에 있는 onCameraChange 메서드를 호출함 ) NaverMap.OnCameraChangeListener 인터페이스를 implements 해서  안에서 구현
    //현재는 사용하지 않는 이벤트 리스너 하지만 추후를 위해서 남겨 놓았음
    private class EventListener implements NaverMap.OnCameraChangeListener{
        //안에서 naverMap에 접근하기 위해서 naverMap 객체를 private 멤버 변수로 받아 옴.
        private NaverMap naverMap;
        public EventListener(){
        };
        //constructor 에서 이 이벤트 리스너에 해당하는 naverMap의 객체를 연결시켜준다.
        public EventListener(NaverMap naverMap){
            this.naverMap = naverMap;
        }
        // private 멤버 변수를 사용하였기 때문에 getMethod 생성 하였음.
        public NaverMap getNaverMap() {
            return naverMap;
        }


        // 가장 중요한 부분!! <어떠한 이유에서든지 네이버 맵의 카메라가 조금이라도 변경이 되면 이 메서드를 호출하게 됨)
        @Override
        public void onCameraChange(int reason, boolean animated){
            if(reason == CameraUpdate.REASON_GESTURE && !moving_camera && naverMap.getUiSettings().isZoomGesturesEnabled() && map_radius < 10240){
                if(zoomLevel > naverMap.getCameraPosition().zoom+0.1){
                    naverMap.getUiSettings().setZoomGesturesEnabled(false);
                    moving_camera = true;
                    Log.d("MAP_radius", map_radius+ "");
                    moving_camera = false;
                    for(int i = 0; i<squareOverlay.size(); i++){
                        squareOverlay.get(i).setMap(null);
                    }
                    squareOverlay.clear();
                    {
                        naverMap.removeOnCameraChangeListener(this);
                        Log.d("네이버", "그리드 다시 그리기");
                        GridMapMakeTask task = new GridMapMakeTask(naverMap);
                        task.execute();
                    }
                    Log.d("ZoomLevel", zoomLevel+ "");
                }
            }
        }
    }

    //이벤트 리스너 클래스 생성 (네이버 지도 내에서 변경이 완료되고 난 뒤에 이벤트 리스너 클래스 내에 있는 onCameraIdle 메서드를 호출함 ) NaverMap.onCameraIdleListener 인터페이스를 implements 해서  안에서 구현
    private class IdleListener implements NaverMap.OnCameraIdleListener{
        private NaverMap naverMap;
        private double zoom;
        private double comparisionZoom;
        private boolean reduction;
        //생성자를 통하여 naverMap 객체와 zoom 레벨, reduciton 맵이 이동중인지 아닌지 등을 파악.
        private IdleListener (NaverMap naverMap, double zoom, boolean reduction){
            this.naverMap = naverMap;
            this.zoom = zoom;
            this.reduction = reduction;
        }

        //가장 중요한 함수로서 카메라의 이동이 완료되는 시점에서 이 함수가 호출 됨.
        @Override
        public void onCameraIdle() {
            Log.d("IdleListener", "IdleLister 동작" + " zoom Level 은 : "+ zoom);
            //현재 줌레벨을 가지고 와서 원래 리스너가 들고 있는 줌레벨과 현재 줌 레벨을 비교해봄.
            comparisionZoom = naverMap.getCameraPosition().zoom;
            Log.d("IdleListener", "IdleLister 동작" + " current zoom Level 은 : "+ comparisionZoom);
            //카메라가 이동중일때 가장 위에 if문에 걸리게 됨.
            if(reduction){
                //이동중임을 없애고. 밑에 거에 걸릴수 있도록 함. 연속되서 호출 될일이 없음. 밑에 있는 else if 구문에서 해당하는 naverMap.moveCamera의 애니메이션에 의해 이동되는 카메라 이동이 모두 완료되고 난 뒤에 다시 이 리스너를 호출하기 때문에 연속적으로 호출 되지 않음.
                reduction = false;
                mapSizeTextView.setText("지도 크기 : " + map_radius + "m");
                zoom = naverMap.getCameraPosition().zoom;
                if(map_radius == 640)
                    naverMap.setMaxZoom(zoom);
                if(map_radius == 5120)
                    naverMap.setMinZoom(zoom);
            }
            // 지도를 축소 했을 당시에 이 else if 구문에 걸리게 되어 동작하게 된다.
            else if(zoom - comparisionZoom > 0.1){
                //우리가 제공하는 지도의 최대 레벨에 도달했는데도 불구하고 맵을 다시 축소시키려고 할때 우리가 제한해 놓은 범위로 다시 돌리는 과정
                if(map_radius == 5120){
                    naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1/2,map_radius *-1/2),centerLatLng.offset(map_radius/2 ,map_radius/2)))
                            .animate(CameraAnimation.Easing,2000));
                    return;
                }
                // 축소시킬 시 맵의 반경을 2배로 확대하고 이동중임을 확인해주면 reduction 변수에 true 값을 지정해줌으로써 다음에 이 리스너가 호출 될 경우 가장 위에 있는 if문에 걸릴수 있도록 설정함.
                map_radius *= 2;
                reduction = true;
                //map 을 우리가 설정한 boundary 에 맞춰서 맵을 이동시킨다. 간격을 2초라는 시간을 두고 최대한 자연스러움을 나타내기 위해서 표현.
                naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1/2,map_radius *-1/2),centerLatLng.offset(map_radius/2 ,map_radius/2)))
                        .animate(CameraAnimation.Easing,2000));
                //원래 이미 있던 폴리곤 오버레이들은 모두 삭제 시키는 반복문
                for(int i = 0; i<squareOverlay.size(); i++){
                    squareOverlay.get(i).setMap(null);
                }
                squareOverlay.clear();
                // 윗 반복문을 통해서 지워진 폴리곤 오버레이 객체들의 다시 AsyncTask 객체를 생성하여 다시 객체를 그림.
                GridMapMakeTask task = new GridMapMakeTask(naverMap);
                task.execute();
            }
            //윗부분과 동일하고 이부분에서는 지도를 확대했을 당시에 관한 부분들을 처리해 주는 곳.
            else if(comparisionZoom - zoom  > 0.1){
                if(map_radius == 640){
                    naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1/2,map_radius *-1/2),centerLatLng.offset(map_radius/2 ,map_radius/2)))
                            .animate(CameraAnimation.Easing,2000));
                    return;
                }
                //지도를 확대했기 때문에 지도의 반경을 1/2 로 축소하여서 다시 폴리곤 오버레이를 그리는 과정을 반복한다.
                map_radius /= 2;
                reduction = true;
                naverMap.moveCamera(CameraUpdate.fitBounds(new LatLngBounds(centerLatLng.offset(map_radius * -1/2,map_radius *-1/2),centerLatLng.offset(map_radius/2 ,map_radius/2)))
                        .animate(CameraAnimation.Easing,2000));
                for(int i = 0; i<squareOverlay.size(); i++){
                    squareOverlay.get(i).setMap(null);
                }
                squareOverlay.clear();
                GridMapMakeTask task = new GridMapMakeTask(naverMap);
                task.execute();
            }else{
                //System.out.println("터치됨@@@@@@@@@@@@@@@");
//                long downTime = SystemClock.uptimeMillis();
//                long eventTime = SystemClock.uptimeMillis() + 100;
//                float x = 0.0f;
//                float y = 0.0f;
//                int meetaState = 0;
//                MotionEvent event = MotionEvent.obtain(downTime,eventTime,MotionEvent.ACTION_UP,x,y,meetaState);
//                detector.onTouchEvent(event);
            }
        }
    }

//<<<<<<< HEAD
    public int getMsize(){
        return map_radius;
    }
//=======

//>>>>>>> gesture_func

    // 지도상에 폴리곤 오버레이를 그려주는 부분
    private class GridMapMakeTask extends AsyncTask<Void, PolygonOverlay, Void> implements Serializable {
        // naverMap 객체를 얻어오기 위해서 private 멤버 변수로 지정
        private NaverMap naverMap;

        public GridMapMakeTask() {
            super();
        }
        //생성자에서 private 멤버 변수를 연결 시켜줌.
        public GridMapMakeTask(NaverMap naverMap){
            super();
            this.naverMap = naverMap;
        }

        //NaverMap 을 위한 GetMethod
        public NaverMap getNaverMap() {
            return naverMap;
        }

        public void setNaverMap(NaverMap naverMap) {
            this.naverMap = naverMap;
        }


        @Override
        protected void onPreExecute() {  // AsyncTask 를 시작하기 전에 발생
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {// AsyncTask 가 전부 다 끝나고 나서 한번 실행 됨.
            super.onPostExecute(aVoid);
        }


        // 이부분이 doInBackground 에서 publishProgress 부분을 통해서 넘어온 부분 넘어온 부분이 parameter의 values의 배열에 담기게 되고 하나 밖에 없을 경우에는 values[0]의 값을 참조 하면 된다. 그래서 그 부분의 values[0].setMap을 통해 폴리곤 오버레이를 맵에 활성화 시켜줌.
        // 그리고 지도의 폴리곤 오버레이에 그것에 해당하는 클릭 리스너를 달아 주는 과정이다. 지도상에서 수색을 진행하지 않을 부분들을 선택해야 하는 경우 이 것을 통해서 넘어 가게 된다.
        @Override
        protected void onProgressUpdate(PolygonOverlay... values) {
            values[0].setMap(getNaverMap());
            values[0].setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    values[0].setColor(getResources().getColor(R.color.noArea));
                    return true;
                }
            });
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

            // 백그라운드 내에서 해당하는 폴리곤 오버레이 객체를 계속해서 만들어 내는 중.......
            LatLng lineLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            LatLng drawLatLng = centerLatLng.offset(map_radius/8*3,-1* map_radius / 2);
            for(int i = 0; i< 64; i++){
                if(i != 0 && i % 8 == 0){
                    lineLatLng = lineLatLng.offset(-1*map_radius/8,0);
                    drawLatLng = lineLatLng;
                }
                PolygonOverlay polygonOverlay = new PolygonOverlay();
                //getFourCornerLatLng 함수는 가장 남서쪽에 있는 좌표를 기준을 하여 총 사각형을 그릴수 있는 4개의 좌표를 알아내는 함수 (남서쪽, 북서쪽, 북동쪽, 남동쪽) 순서의 리스트 값을 반환한다.
                polygonOverlay.setCoords(getFourCornerLatLng(drawLatLng));
                polygonOverlay.setOutlineColor(Color.WHITE);
                polygonOverlay.setColor(Color.TRANSPARENT);
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


    //폴리곤 오버레이를 그리기 위해서 4개의 꼭짓점을 구하는 Method
    public List<LatLng> getFourCornerLatLng(LatLng standardLatLng){
        return Arrays.asList(
                standardLatLng,
                standardLatLng.offset(map_radius/8,0),
                standardLatLng.offset(map_radius/8,map_radius/8),
                standardLatLng.offset(0,map_radius/8)
        );
    }
}
