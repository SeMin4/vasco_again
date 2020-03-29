package com.example.woo.myapplication.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.MyRoomItem;
import com.example.woo.myapplication.MyRoomListAdapter;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.Color;
import com.example.woo.myapplication.data.DepartmentData;
import com.example.woo.myapplication.data.User;
import com.example.woo.myapplication.ui.addListenerOnTextChange;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageActivity extends AppCompatActivity implements MyRoomListAdapter.ListBtnClickListener {
    protected LinearLayout MyPageLayout;
    protected EditText change_password;
    protected EditText change_check_password;
    protected Spinner change_department;
    protected Button change_confirm_btn;
    protected TextView user_id;
    protected TextView user_name;
    protected ListView my_room_list_view;
    static MyRoomListAdapter roomListAdapter;
    protected Button change_password_btn,change_department_btn;
    private Retrofit retrofit;
    private MyGlobals.RetrofitExService retrofitExService;
    ArrayList<String> depList;
    ArrayAdapter<String> depArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        MyPageLayout = (LinearLayout) findViewById(R.id.my_page_layout);
        change_password = (EditText) findViewById(R.id.change_password);
        change_check_password = (EditText) findViewById(R.id.change_check_password);
        change_department=(Spinner)findViewById(R.id.change_department);
        //change_department = (EditText) findViewById(R.id.change_department);
        change_confirm_btn = (Button) findViewById(R.id.change_confirm_btn);
        user_id = (TextView)findViewById(R.id.user_id);
        user_name = (TextView)findViewById(R.id.user_name);
        my_room_list_view = (ListView)findViewById(R.id.my_room_list);

        change_password.addTextChangedListener(new addListenerOnTextChange(this, change_password, R.drawable.ic_lock_outline_selector, R.drawable.ic_lock_outline_burgundy));
        change_check_password.addTextChangedListener(new addListenerOnTextChange(this, change_check_password, R.drawable.ic_lock_outline_selector, R.drawable.ic_lock_outline_burgundy));

        roomListAdapter = new MyRoomListAdapter(this);
        my_room_list_view.setAdapter(roomListAdapter);
        ArrayList<MyRoomItem> mlist = MyGlobals.getInstance().getMaplist();
        if(mlist!=null) {
            for (int i = 0; i < mlist.size(); i++) {
                MyRoomItem item = mlist.get(i);
                roomListAdapter.addItem(item.getM_id(), item.getP_name(), item.getM_place_string());
            }
        }

        change_password_btn = (Button)findViewById(R.id.change_pasaword_btn);
        change_department_btn = (Button)findViewById(R.id.change_department_btn);
        retrofit = MyGlobals.getInstance().getRetrofit();
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();

        user_id.setText(MyGlobals.getInstance().getUser().getU_email());
        user_name.setText(MyGlobals.getInstance().getUser().getU_name());
      //  change_department.setHint(MyGlobals.getInstance().getUser().getU_department());

        depList = new ArrayList<>(); //부서데이터받아오기
        retrofitExService.getDepartmentData().enqueue(new Callback<ArrayList<DepartmentData>>() {
            @Override
            public void onResponse(Call<ArrayList<DepartmentData>> call, Response<ArrayList<DepartmentData>> response) {
                Log.d("부서","부서 onResponse");
                ArrayList<DepartmentData> data = response.body();
                for(int i =0;i<data.size();i++)
                {
                    depList.add(data.get(i).getDepartment());
                }
                depArrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        depList);
                change_department.setAdapter(depArrayAdapter);

                for(int i = 0;i<depList.size();i++){
                    if(depList.get(i).equals(MyGlobals.getInstance().getUser().getU_department())){
                        change_department.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DepartmentData>> call, Throwable t) {
                Log.d("부서","부서 onFailure");
            }
        });


        change_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),depList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        MyPageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(change_password.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(change_check_password.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(change_department.getWindowToken(), 0);

            }
        });
        change_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                else {
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
            }
        });
        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //비밀번호 번경
                if(change_password.getText().toString().equals(change_check_password.getText().toString())){
                    HashMap<String,String> input = new HashMap<>();
                    input.put("u_id",MyGlobals.getInstance().getUser().getU_id());
                    input.put("password",change_password.getText().toString());

                    retrofitExService.postChangePassword(input).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            System.out.println("onResponse 호출@@@@@@@@@@@@@@");
                            User user = response.body();
                            if(user.getCheck().equals("yes")){
                                Toast.makeText(getApplicationContext(),"비밀번호 변경 되었습니다",Toast.LENGTH_SHORT).show();
                            }else if(user.getCheck().equals("no")){
                                Toast.makeText(getApplicationContext(),"비밀번호 변경 에러입니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            System.out.println("onFailure 호출@@@@@@@@@@@@@@");
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"비밀번호를 체크해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_department_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //department 변경
                String department = change_department.getSelectedItem().toString();
                Log.d("부서변경",department);
                if( !(change_department.getSelectedItem().toString().equals(""))){
                    retrofitExService.getChangeDepartment(MyGlobals.getInstance().getUser().getU_id(), department).enqueue(new Callback<Color>() {
                        @Override
                        public void onResponse(Call<Color> call, Response<Color> response) {
                            Log.d("부서변경","onResponse");
                            Color color = response.body();
                            if(color.getCheck().equals("yes")) {
                                MyGlobals.getInstance().getUser().setU_department(department);
                                MyGlobals.getInstance().getUser().setColor(color.getColor());
                                Toast.makeText(getApplicationContext(),"부서변경 성공입니다.",Toast.LENGTH_SHORT).show();
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


        //roomList 클릭 이벤트 핸들러 || 혹시 몰라서 적어놓음
        my_room_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                MyRoomItem item = (MyRoomItem) parent.getItemAtPosition(position) ;

                String titlePerson = item.getP_name() ;
                String locationMap = item.getM_place_string() ;

            }
        }) ;
    }

    @Override
    public  void onListBtnClick(int position){
        Intent intent = new Intent (getApplicationContext(), RoomDeleteActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("mapId",roomListAdapter.myRoomList.get(position).getM_id());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
