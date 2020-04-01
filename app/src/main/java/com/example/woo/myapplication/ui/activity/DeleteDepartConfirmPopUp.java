package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.DepartmentData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteDepartConfirmPopUp extends Activity {
    private TextView deleteDepartment_tv;
    private Button deleteConfirmBtn;
    private Button deleteCancelBtn;
    private String deleteDepartment_name;
    private MyGlobals.RetrofitExService retrofitExService;
    private int selectPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.pop_up_delete_depart_confirm);
        deleteDepartment_tv = (TextView)findViewById(R.id.delete_departmentName);
        deleteConfirmBtn = (Button)findViewById(R.id.delete_confirm_btn);
        deleteCancelBtn = (Button)findViewById(R.id.delete_cancel_btn);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        Intent intent = getIntent();
        setDeleteDepartment_name(intent.getStringExtra("departName"));
        setSelectPosition(intent.getIntExtra("selectPosition", -1));
        deleteDepartment_tv.setText(getDeleteDepartment_name());
        deleteConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofitExService.deleteDepartmentData(getDeleteDepartment_name()).enqueue(new Callback<DepartmentData>() {
                    @Override
                    public void onResponse(Call<DepartmentData> call, Response<DepartmentData> response) {
                        DepartmentData data = response.body();
                        if(data.getChecked().equals("error")){
                            Toast.makeText(getApplicationContext(), "다음에 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "부서 삭제 성공입니다.", Toast.LENGTH_SHORT).show();
                            ManageDepartmentActivity.departmentListAdapter.departList.remove(selectPosition);
                            ManageDepartmentActivity.departmentListAdapter.notifyDataSetChanged();
                            ManageDepartmentActivity.depList.remove(selectPosition);
                            ManageDepartmentActivity.color.remove(selectPosition);
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
        });
        deleteCancelBtn.setOnClickListener(new View.OnClickListener() {
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
    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
    public String getDeleteDepartment_name() {
        return deleteDepartment_name;
    }

    public void setDeleteDepartment_name(String deleteDepartment_name) {
        this.deleteDepartment_name = deleteDepartment_name;
    }
}
