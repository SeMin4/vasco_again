package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ManageDepartmentPopUp extends Activity {
    private EditText departmentEditText;
    private Button confirm_btn;
    private Button cancel_btn;
    private Button select_color_btn;
    private String _color_str = null;
    private int _color_int;
    private ArrayList<String> departNameList;
    private ArrayList<String> departColorList;
    private MyGlobals.RetrofitExService retrofitExService;
    private int selectPosition;
    private TextView manageDepartmentTitle;
    private GradientDrawable drawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_manage_department);
        Intent intent = getIntent();
        departNameList = (ArrayList<String>)intent.getSerializableExtra("depList");
        departColorList = (ArrayList<String>)intent.getSerializableExtra("colorList");
        setSelectPosition(intent.getIntExtra("selectPosition", -1));
        manageDepartmentTitle = (TextView)findViewById(R.id.manageDepartmentTitle);
        departmentEditText = (EditText)findViewById(R.id.editText_departmentName);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        select_color_btn = (Button)findViewById(R.id.selectColor_btn);
        confirm_btn = (Button)findViewById(R.id.button_accept_depart);
        cancel_btn = (Button)findViewById(R.id.button_cancel_depart);
        drawable = (GradientDrawable) getApplicationContext().getDrawable(R.drawable.iv_corners_rounded);

        if(getSelectPosition() != -1){//부서 수정의 경우
            manageDepartmentTitle.setText(getString(R.string.modifyDepartmentTitle));
            confirm_btn.setText(getString(R.string.modify));
            departmentEditText.setText(departNameList.get(getSelectPosition()));
            drawable.setColor(Integer.parseInt(departColorList.get(getSelectPosition())));
            select_color_btn.setBackground(drawable);
            select_color_btn.setClipToOutline(true);
            _color_int = Integer.parseInt(departColorList.get(getSelectPosition()));
            _color_str = departColorList.get(getSelectPosition());
            String original_department = departNameList.get(getSelectPosition());
            String original_color = departColorList.get(getSelectPosition());
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String department = departmentEditText.getText().toString();
                    if(department.equals("")){
                        Toast.makeText(getApplicationContext(),"부서를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }else if(departNameList.contains(department) && !department.equals(original_department)){
                        Toast.makeText(getApplicationContext(),"이미 존재하는 부서입니다. 다른부서를 입력하세요.",Toast.LENGTH_SHORT).show();
                    }else if(departColorList.contains(_color_str) && !_color_str.equals(original_color)){
                        Toast.makeText(getApplicationContext(),"이미 존재하는 색상입니다. 다른색상을 선택하세요.",Toast.LENGTH_SHORT).show();
                    }else {
                        retrofitExService.modifyDepartmentData(original_department, department,_color_str).enqueue(new Callback<DepartmentData>() {
                            @Override
                            public void onResponse(Call<DepartmentData> call, Response<DepartmentData> response) {
                                DepartmentData data = response.body();
                                Log.d("body", "body : " + response.body().toString());
                                if(data.getChecked().equals("error")) {
                                    Toast.makeText(getApplicationContext(), "다음에 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), "부서 수정 성공입니다.", Toast.LENGTH_SHORT).show();
                                    ManageDepartmentActivity.departmentListAdapter.departList.get(selectPosition).setDepartment(department);
                                    ManageDepartmentActivity.departmentListAdapter.departList.get(selectPosition).setColor(_color_str);
                                    ManageDepartmentActivity.departmentListAdapter.notifyDataSetChanged();
                                    ManageDepartmentActivity.depList.set(selectPosition, department);
                                    ManageDepartmentActivity.color.set(selectPosition,_color_str);
                                    Intent intent1 = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                            @Override
                            public void onFailure(Call<DepartmentData> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),"다음에 다시 시도해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });


        }

        else{//부서 추가의 경우

            drawable.setColor(getResources().getColor(R.color.cream));
            select_color_btn.setBackground(drawable);
            select_color_btn.setClipToOutline(true);

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
                                    ManageDepartmentActivity.departmentListAdapter.addItem(department,_color_str);
                                    ManageDepartmentActivity.departmentListAdapter.notifyDataSetChanged();
                                    ManageDepartmentActivity.depList.add(department);
                                    ManageDepartmentActivity.color.add(_color_str);

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
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this,0xff0000ff, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {


            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                _color_int = color; //int
                _color_str = ""+_color_int; //str
                Log.d("색상",_color_str);
                drawable.setColor(_color_int);
                select_color_btn.setBackground(drawable);
                select_color_btn.setClipToOutline(true);
            }
        });
        colorPicker.show();
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

}
