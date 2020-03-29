package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    private String mapId;
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
        mapId = intent.getStringExtra("mapId");
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
        input.put("mapId", mapId);
        input.put("password", strPassword1);
        retrofitExService.postMapAttendance(input).enqueue(new Callback<OverlapExamineData>() {
            @Override
            public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
                OverlapExamineData data = response.body();
                if(data.getOverlap_examine().equals("yes")){
                    //방에입장한다.
                    Toast.makeText(getApplicationContext(), "방에 입장하셨습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("mapInfoIndex", mapInfo_index);
                    setResult(RESULT_OK, intent);
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
