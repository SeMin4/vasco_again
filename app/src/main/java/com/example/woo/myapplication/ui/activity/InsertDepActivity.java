package com.example.woo.myapplication.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.OverlapExamineData;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.DepartmentData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class InsertDepActivity extends AppCompatActivity {

    private String tColor = null;
    private int tColor2;
    private Button color_btn;
    private Button save_btn;
    private MyGlobals.RetrofitExService retrofitExService;
    private EditText editText;
    private ArrayList<String> depList;
    private ArrayList<String> color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_dep);

        editText = (EditText)findViewById(R.id.editText);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        color_btn= findViewById(R.id.colorpicker);
        save_btn=findViewById(R.id.save_btn);
        depList = new ArrayList<>();
        color = new ArrayList<>();

        retrofitExService.getDepartmentData().enqueue(new Callback<ArrayList<DepartmentData>>() { //기존의 부서와 색상정보 받아오기
            @Override
            public void onResponse(Call<ArrayList<DepartmentData>> call, Response<ArrayList<DepartmentData>> response) {
                Log.d("부서","부서 onResponse");
                ArrayList<DepartmentData> data = response.body();
                for(int i =0;i<data.size();i++) {
                    depList.add(data.get(i).getDepartment());
                    color.add(data.get(i).getColor());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DepartmentData>> call, Throwable t) {
                Log.d("부서","부서 onFailure");
            }
        });
        color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() { //저장
            @Override
            public void onClick(View v) {
                String department = editText.getText().toString();
                if(department.equals("")){
                    Toast.makeText(getApplicationContext(),"부서를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(tColor == null){
                    Toast.makeText(getApplicationContext(),"색상을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(depList.contains(department)){
                    Toast.makeText(getApplicationContext(),"이미 존재하는 부서입니다. 다른부서를 입력하세요.",Toast.LENGTH_SHORT).show();
                }else if(color.contains(tColor)){
                    Toast.makeText(getApplicationContext(),"이미 존재하는 색상입니다. 다른색상을 선택하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    retrofitExService.getInsertDepartment(department,tColor).enqueue(new Callback<OverlapExamineData>() {
                        @Override
                        public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
                            OverlapExamineData data = response.body();
                            if(data.getOverlap_examine().equals("error")) {
                                Toast.makeText(getApplicationContext(), "색상이 겹칩니다. 다른색상을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "부서추가 성공입니다..", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        @Override
                        public void onFailure(Call<OverlapExamineData> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"부서추가 실패입니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this,tColor2, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {


            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tColor2 = color;
                tColor = ""+tColor2;
                Log.d("색상",tColor);
                color_btn.setBackgroundColor(tColor2);
            }
        });
        colorPicker.show();
    }
}
