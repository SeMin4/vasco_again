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

import com.example.woo.myapplication.R;

import java.util.ArrayList;

public class DepartmentListClickPopup extends Activity {
    private Button modify_btn;
    private Button delete_btn;



    private ArrayList<String> departNameList;
    private ArrayList<String> departColorList;
    private int selectPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_department_list_click);
        modify_btn = (Button)findViewById(R.id.departmentModifyBtn);
        delete_btn = (Button)findViewById(R.id.departmentDeleteBtn);

        Intent intent = getIntent();
        setDepartNameList((ArrayList<String>)intent.getSerializableExtra("depList"));
        setDepartColorList((ArrayList<String>)intent.getSerializableExtra("colorList"));
        setSelectPosition(intent.getIntExtra("selectPosition", -1));

        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),ManageDepartmentPopUp.class);
                intent1.putExtra("depList", getDepartNameList());
                intent1.putExtra("colorList", getDepartColorList());
                intent1.putExtra("selectPosition", getSelectPosition());
                startActivityForResult(intent1, 0);
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), DeleteDepartConfirmPopUp.class);
                intent1.putExtra("departName",getDepartNameList().get(getSelectPosition()));
                intent1.putExtra("selectPosition", getSelectPosition());
                startActivityForResult(intent1, 1);
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

    public ArrayList<String> getDepartNameList() {
        return departNameList;
    }

    public void setDepartNameList(ArrayList<String> departNameList) {
        this.departNameList = departNameList;
    }

    public ArrayList<String> getDepartColorList() {
        return departColorList;
    }

    public void setDepartColorList(ArrayList<String> departColorList) {
        this.departColorList = departColorList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("activity_result", "onActivityResult");
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                finish();
            }
        }else if(requestCode == 1){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }
}
