package com.example.woo.myapplication.ui.activity;

import android.app.Activity;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.DepartmentData;
import com.example.woo.myapplication.data.Color;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectDepartmentPopUp extends Activity {
    private Spinner select_department;
    private MyGlobals.RetrofitExService retrofitExService;
    private Button select_confirm_btn;
    ArrayList<String> departList;
    ArrayAdapter<String> departAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.pop_up_select_department);
        select_department = (Spinner) findViewById(R.id.select_department_spinner);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        departList = new ArrayList<>();
        select_confirm_btn = (Button)findViewById(R.id.button_select_depart);
        retrofitExService.getDepartmentData().enqueue(new Callback<ArrayList<DepartmentData>>() {
            @Override
            public void onResponse(Call<ArrayList<DepartmentData>> call, Response<ArrayList<DepartmentData>> response) {
                ArrayList<DepartmentData> data = response.body();
                for(int i = 0; i< data.size(); ++i){
                    departList.add(data.get(i).getDepartment());
                }
                departAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        departList);
                select_department.setAdapter(departAdapter);
                select_department.setSelection(0);
            }

            @Override
            public void onFailure(Call<ArrayList<DepartmentData>> call, Throwable t) {
                Log.d("부서","부서 onFailure");
            }
        });

        select_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String department = select_department.getSelectedItem().toString();
                Log.d("부서변경",department);
                if( !(select_department.getSelectedItem().toString().equals(""))){
                    retrofitExService.getChangeDepartment(MyGlobals.getInstance().getUser().getU_id(), department).enqueue(new Callback<Color>() {
                        @Override
                        public void onResponse(Call<Color> call, Response<Color> response) {
                            Log.d("부서변경","onResponse");
                            Color color = response.body();
                            if(color.getCheck().equals("yes")) {
                                MyGlobals.getInstance().getUser().setU_department(department);
                                MyGlobals.getInstance().getUser().setColor(color.getColor());
                                Toast.makeText(getApplicationContext(),"부서변경 성공입니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if(color.getCheck().equals("error"))
                                Toast.makeText(getApplication(),"에러 발생 department 변경 실패",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Color> call, Throwable t) {
                            Log.d("부서변경","onFailure" + t);
                            Toast.makeText(getApplication(),"에러 발생 department 변경 실패",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
    }


}
