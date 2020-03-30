package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class InsertDepartmentActivity extends Activity {
    private EditText departmentEditText;
    private Button confirm_btn;
    private Button cancel_btn;
    private Button select_color_btn;
    private String _color_str = null;
    private int _color_int;
    private ArrayList<String> departNameList;
    private ArrayList<String> departColorList;
    private MyGlobals.RetrofitExService retrofitExService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_insert_department);
        Intent intent = getIntent();
        departNameList = (ArrayList<String>)intent.getSerializableExtra("depList");
        departColorList = (ArrayList<String>)intent.getSerializableExtra("colorList");
        departmentEditText = (EditText)findViewById(R.id.editText_departmentName);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        select_color_btn = (Button)findViewById(R.id.selectColor_btn);
        confirm_btn = (Button)findViewById(R.id.button_accept_depart);
        cancel_btn = (Button)findViewById(R.id.button_cancel_depart);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String department = departmentEditText.getText().toString();
                if(department.equals("")){
                    Toast.makeText(getApplicationContext(),"부서를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(_color_str == null){
                    Toast.makeText(getApplicationContext(),"색상을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(departNameList.contains(department)){
                    Toast.makeText(getApplicationContext(),"이미 존재하는 부서입니다. 다른부서를 입력하세요.",Toast.LENGTH_SHORT).show();
                }else if(departColorList.contains(_color_str)){
                    Toast.makeText(getApplicationContext(),"이미 존재하는 색상입니다. 다른색상을 선택하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    retrofitExService.getInsertDepartment(department,_color_str).enqueue(new Callback<OverlapExamineData>() {
                        @Override
                        public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
                            OverlapExamineData data = response.body();
                            if(data.getOverlap_examine().equals("error")) {
                                Toast.makeText(getApplicationContext(), "색상이 겹칩니다. 다른색상을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "부서추가 성공입니다..", Toast.LENGTH_SHORT).show();
                                InsertDepActivity.departmentListAdapter.addItem(department,_color_str);
                                InsertDepActivity.departmentListAdapter.notifyDataSetChanged();
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


        select_color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this,_color_int, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {


            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                _color_int = color;
                _color_str = ""+_color_int;
                Log.d("색상",_color_str);
                select_color_btn.setBackgroundColor(_color_int);
            }
        });
        colorPicker.show();
    }

}
