package com.example.woo.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.DepartmentData;
import com.example.woo.myapplication.utils.DepartmentListAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class InsertDepActivity extends AppCompatActivity implements DepartmentListAdapter.ListBtnClickListener{

    private String tColor = null;
    private int tColor2;
    protected ListView departListView;
    private MyGlobals.RetrofitExService retrofitExService;
    private ArrayList<String> depList;
    private ArrayList<String> color;
    private ArrayList<DepartmentData> departList ;
    static DepartmentListAdapter departmentListAdapter;
    private Button addDepart_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_dep);
        departListView = (ListView) findViewById(R.id.department_list);
        departmentListAdapter = new DepartmentListAdapter(this);
        departListView.setAdapter(departmentListAdapter);

        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        addDepart_btn = (Button) findViewById(R.id.addDepartmentBtn);
        depList = new ArrayList<>();
        color = new ArrayList<>();
        addDepart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InsertDepartmentActivity.class);
                intent.putExtra("depList", depList);
                intent.putExtra("colorList", color);
                startActivity(intent);
            }
        });


        retrofitExService.getDepartmentData().enqueue(new Callback<ArrayList<DepartmentData>>() { //기존의 부서와 색상정보 받아오기
            @Override
            public void onResponse(Call<ArrayList<DepartmentData>> call, Response<ArrayList<DepartmentData>> response) {
                Log.d("부서","부서 onResponse");
                departList = response.body();
                for(int i =0;i<departList.size();i++) {
                    DepartmentData data = departList.get(i);
                    departmentListAdapter.addItem(data.getDepartment(), data.getColor());
                    depList.add(data.getDepartment());
                    color.add(data.getColor());
//                    departmentListAdapter.addItem()
                }
                departmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<DepartmentData>> call, Throwable t) {
                Log.d("부서","부서 onFailure");
            }
        });
        departListView.setLongClickable(true);
        departListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "click postion : " + position + ",   " + id , Toast.LENGTH_LONG).show();
                Log.d("long click", "click1");
            }
        });
        departListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "long click postion : " + position + ",   " + id , Toast.LENGTH_LONG).show();
                Log.d("long click", "click2");
                return true;
            }
        });
//
//        color_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openColorPicker();
//            }
//        });


    }

    public void openColorPicker(int colorValue) {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this,colorValue, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {


            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tColor2 = color;
                tColor = ""+tColor2;
                Log.d("색상",tColor);
//                color_btn.setBackgroundColor(tColor2);
            }
        });
        colorPicker.show();
    }

    @Override
    public  void onDepartListBtnClick(int position){
        Toast.makeText(getApplicationContext(), "listclick :" + position,Toast.LENGTH_LONG).show();
//        Intent intent = new Intent (getApplicationContext(), RoomDeleteActivity.class);
//        intent.putExtra("position", position);
//        intent.putExtra("mapId",roomListAdapter.myRoomList.get(position).getM_id());
//        startActivity(intent);
    }

}
