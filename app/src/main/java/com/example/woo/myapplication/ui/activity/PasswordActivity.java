package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.OverlapExamineData;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.MapInfo;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends Activity {

    private MyGlobals.RetrofitExService retrofitExService;
    private EditText password;
    private EditText repassword;
    private Button confirm;
    private MapInfo info;
    private EditText roomTitle;
    private String index = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_password);
        this.setFinishOnTouchOutside(false);

        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        password = (EditText)findViewById(R.id.password);
        repassword = (EditText)findViewById(R.id.repassword);
        confirm = (Button)findViewById(R.id.confirm);
        roomTitle = (EditText)findViewById(R.id.roomTitle);
        info = (MapInfo)getIntent().getSerializableExtra("mapinfo");
        System.out.println("info : "+info.getM_owner());

        roomTitle.setText(info.getM_center_place_string());

        ArrayList<Integer> placeIndex = info.getPlaceIndex();
        for(int i =0;i<64;i++){
            if(placeIndex.get(i) == 1){
                index = index+i+"@";
            }
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = password.getText().toString();
                String p2 = repassword.getText().toString();
                if(p1.equals(p2)){
                    //지도정보 보내기
                    HashMap<String,String> input = new HashMap();
                    input.put("p_id",info.getP_id());
                    input.put("m_password",p1);
                    input.put("m_owner",info.getM_owner());
                    input.put("m_status",info.getM_status());
                    input.put("m_size",info.getM_size());
                    input.put("m_unit_scale",info.getM_unit_scale());
                    input.put("m_rotation",info.getM_rotation());
                    input.put("m_center_place_string",roomTitle.getText().toString());
                    input.put("m_center_point_latitude",info.getM_center_point_latitude());
                    input.put("m_center_point_longitude",info.getM_center_point_longitude());
                    input.put("index",index);
                    retrofitExService.postMapMake(input).enqueue(new Callback<OverlapExamineData>() {
                        @Override
                        public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
                            Log.d("맵 방 만들기","onResponse" );
                            OverlapExamineData data = response.body();
                            if(data.getOverlap_examine().equals("error")){
                                Toast.makeText(getApplicationContext(), "방 만들기 실패입니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "방 만들기 성공입니다..", Toast.LENGTH_SHORT).show();
                                String mid = data.getM_id();
                               // ArrayList<Integer> placeIndex = info.getPlaceIndex();
                               // Log.d("mapActivity","password mid : "+mid);
                                //다음화면으로 이동한다(서비스있는 부분 적어주기),mid도 필요할거 같아서 서버에서 가저왔음
                                Intent intent = new Intent(getApplicationContext(), GPSService.class);
                                intent.putExtra("mid",mid);
                                intent.putExtra("existFlag",0);
                                if(placeIndex!=null)
                                    intent.putExtra("placeIndex",placeIndex);
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                    startService(intent);
                                } else {
                                    startForegroundService(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OverlapExamineData> call, Throwable t) {
                            Log.d("맵 방 만들기","onFailure" );
                            Toast.makeText(getApplicationContext(), "방 만들기 실패입니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
