package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.OverlapExamineData;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.MapInfo;
import com.example.woo.myapplication.data.PlaceIndex;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnterMapPWActivity extends Activity {
    private EditText password;
    private int mapInfo_index;
    private Retrofit retrofit;
    private MyGlobals.RetrofitExService retrofitExService;
    //private String mapId;
    private MapInfo mapInfo;
    private Button confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_enter_pw);

        Intent intent = getIntent();
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        mapInfo_index = intent.getIntExtra("mapInfoIndex", -1);
        //mapId = intent.getStringExtra("mapId");
        mapInfo = (MapInfo)intent.getSerializableExtra("mapInfo");
        password = (EditText) findViewById(R.id.EditText_password);
        Log.d("Enter", "map index: " + mapInfo_index);


    }

    public void mOnCancel(View v){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void mOnAccept(View v){
        String strPassword1 = password.getText().toString();
        retrofit=MyGlobals.getInstance().getRetrofit();
        retrofitExService=MyGlobals.getInstance().getRetrofitExService();
        HashMap<String,String> input = new HashMap<>();
        input.put("mapId", mapInfo.getM_id());
        input.put("password", strPassword1);
        retrofitExService.postMapAttendance(input).enqueue(new Callback<OverlapExamineData>() {
            @Override
            public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
                OverlapExamineData data = response.body();
                if(data.getOverlap_examine().equals("yes")){
                    //방에입장한다.
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    ArrayList<Integer> placeIndex = new ArrayList<>();
                    for(int i =0;i<64;i++)
                        placeIndex.add(0);
                    retrofitExService.getPlaceIndex(mapInfo.getM_id()).enqueue(new Callback<ArrayList<PlaceIndex>>() {
                        @Override
                        public void onResponse(Call<ArrayList<PlaceIndex>> call, Response<ArrayList<PlaceIndex>> response) {
                            ArrayList<PlaceIndex> list = response.body();
                            Log.d("index","list : "+list);
                            if(list == null || list.size() == 0){

                            }else{
                                for(int i=0;i<list.size();i++){
                                    int index = Integer.parseInt(list.get(i).getMd_index());
                                    placeIndex.set(index,1);
                                }
                            }
                            Log.d("index","placeIndex : "+placeIndex);
                            String findLat = mapInfo.getM_find_latitude();
                            String findLng = mapInfo.getM_find_longitude();
                            String centerLat = mapInfo.getM_center_point_latitude();
                            String centerLng = mapInfo.getM_center_point_longitude();
                            String mapRadius = mapInfo.getM_size();
                            intent.putExtra("mid",mapInfo.getM_id());
                            intent.putExtra("existFlag",1);
                            intent.putExtra("centerLat",centerLat);
                            intent.putExtra("centerLng",centerLng);
                            intent.putExtra("mapRadius",mapRadius);
                            intent.putExtra("findLat",findLat);
                            intent.putExtra("findLng",findLng);
                            intent.putExtra("placeIndex",placeIndex);
                          //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<ArrayList<PlaceIndex>> call, Throwable t) {

                        }
                    });


//                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                        startService(intent);
//                    } else {
//                        startForegroundService(intent);
//                    }
                    //Toast.makeText(getApplicationContext(), "방에 입장하셨습니다.", Toast.LENGTH_LONG).show();

                   // intent.putExtra("mapInfoIndex", mapInfo_index); //이건 무엇?
                   // setResult(RESULT_OK, intent);
                    finish();
                }
                else if(data.getOverlap_examine().equals("wrong")){
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하세요.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "에러입니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OverlapExamineData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "에러입니다.", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
