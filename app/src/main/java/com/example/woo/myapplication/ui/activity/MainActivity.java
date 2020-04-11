package com.example.woo.myapplication.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.MyRoomItem;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.Mperson;
import com.example.woo.myapplication.ui.view.MpersonItemView;
import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {
    LoginActivity _LoginActivity = (LoginActivity)LoginActivity._LoginActivity;
    InsertMpersonActivity _InsertMpersons = (InsertMpersonActivity)InsertMpersonActivity._InsertMpersons;
    protected Button logout_btn;
    protected Button myPage_btn;
    protected FloatingActionButton fab_btn,fab_sub1,fab_sub2;
    protected Animation fab_open, fab_close;
    protected boolean isFabOpen =false;

    public static Activity _MainActivity;
    ListView listView;
    MpersonAdapter adapter;
    Retrofit retrofit;
    MyGlobals.RetrofitExService retroService;
    String districtName;
    EditText search;

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (MyGlobals.getInstance().getUser().getU_department() == null){
            Intent intent = new Intent(getApplicationContext(), SelectDepartmentPopUp.class);
            startActivity(intent);
        }
        _MainActivity = MainActivity.this;
        if(_InsertMpersons != null){
            _InsertMpersons.finish();
        }

        _LoginActivity.finish();
        logout_btn = (Button) findViewById(R.id.logout_btn);
        myPage_btn = (Button) findViewById(R.id.my_page_btn);

        fab_btn = (FloatingActionButton) findViewById(R.id.fab);
        fab_sub1=(FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2=(FloatingActionButton) findViewById(R.id.fab_sub2);

        mContext = getApplicationContext();
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listView);
        search = (EditText)findViewById(R.id.search);

        adapter = new MpersonAdapter();
        /*if(MyGlobals.getInstance().getUser().getU_email().equals("admin") == false){
            fab_btn.hide();
        }*/
        if( (MyGlobals.getInstance().getRetrofit() == null) || (MyGlobals.getInstance().getRetrofitExService() ==null) ) {
            retrofit = new Retrofit.Builder().baseUrl(MyGlobals.RetrofitExService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            retroService = retrofit.create(MyGlobals.RetrofitExService.class);
            MyGlobals.getInstance().setRetrofit(retrofit);
            MyGlobals.getInstance().setRetrofitExService(retroService);
        }else{
            retrofit = MyGlobals.getInstance().getRetrofit();
            retroService = MyGlobals.getInstance().getRetrofitExService();
        }

        retroService.getData().enqueue(new Callback<ArrayList<Mperson>>() {
            @Override
            public void onResponse(Call<ArrayList<Mperson>> call, Response<ArrayList<Mperson>> response) {
                Log.d("리스트뷰","onResponse");
                ArrayList<Mperson> persons = response.body();
                if(persons == null){
                    Toast.makeText(getApplicationContext(),"실종자 리스트를 띄울 수 없습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<persons.size();i++){
                    Log.d("리스트뷰",persons.get(i).getP_name());
                    adapter.addItem(persons.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<Mperson>> call, Throwable t) {
                System.out.println("onFailure 호출됨@@@@@@@@@");
                Toast.makeText(getApplicationContext(),"리스트 오류",Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(adapter);


        //리스트뷰를 누르면 다른 액티비티로 넘어간다(실종자 상세 정보)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), MpersonDetailActivity.class);
                Object putitem = adapter.getItem(position);
                intent.putExtra("selecteditem", (Serializable) putitem);
                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                districtName = parent.getItemAtPosition(position).toString();
                //((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                //입력값을 변수에 저장한다.

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
            }
        });
        fab_sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
                Intent intent = new Intent(getApplicationContext(), InsertMpersonActivity.class);
                startActivity(intent);

            }
        });
        fab_sub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
                Intent intent = new Intent(getApplicationContext(), ManageDepartmentActivity.class);
                startActivity(intent);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String filterText = editable.toString();
                if(filterText.length() > 0){
                    listView.setFilterText(filterText);
                }else{
                    listView.clearTextFilter();
                }
                ((MpersonAdapter)listView.getAdapter()).getFilter().filter(filterText) ;
            }
        });

    }

    public void onClickMyPage(View view){
        //지도정보 받아오기
        retroService.getMypageMapData(MyGlobals.getInstance().getUser().getU_id()).enqueue(new Callback<ArrayList<MyRoomItem>>() {
            @Override
            public void onResponse(Call<ArrayList<MyRoomItem>> call, Response<ArrayList<MyRoomItem>> response) {
                System.out.println("onResponse 호출됨@@@@@@@@@@@@@@@@");
                ArrayList<MyRoomItem> maplist = response.body();
                System.out.println("size :" +maplist.size());
                if(maplist == null){
                    Toast.makeText(getApplicationContext(), "지도를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    System.out.println("지도를 받아오지 못했습니다");
                }else{
                    Toast.makeText(getApplicationContext(), "지도를 받아왔습니다..", Toast.LENGTH_SHORT).show();
                    System.out.println("지도를 받아왔습니다.");
                }
                MyGlobals.getInstance().setMaplist(maplist);
                Intent intent1 = new Intent(getApplicationContext(),MyPageActivity.class);
                startActivity(intent1);
            }

            @Override
            public void onFailure(Call<ArrayList<MyRoomItem>> call, Throwable t) {
                System.out.println("onFailure 호출됨@@@@@@@@@@@@@@@@@");
                Toast.makeText(getApplicationContext(),"맵호출 실패",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(),MyPageActivity.class);
                startActivity(intent1);
            }
        });

    }

    public void onClickLogout(View view){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        MyGlobals.getInstance().setUser(null);
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor auto_editor = auto.edit();
        auto_editor.clear();
        auto_editor.commit();
        startActivity(intent);
        finish();
    }

    protected void toggleFab() {
        if (isFabOpen) {
            //fab_btn.setImageResource(R.drawable.ic_add);
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            isFabOpen = false;
        } else {

            //fab_btn.setImageResource(R.drawable.ic_close);
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            isFabOpen = true;

        }
    }



    class MpersonAdapter extends BaseAdapter implements Filterable {  //adapter 정의

       ArrayList<Mperson> items = new ArrayList<Mperson>();
       // ArrayList<Mperson> items = recv_list;
        ArrayList<Mperson> filteredItemList = items;
        Filter listFilter;

        @Override
        public int getCount() {
            return filteredItemList.size();
        }

        public void addItem(Mperson item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return filteredItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            MpersonItemView view = new MpersonItemView(getApplicationContext());
            Mperson item = filteredItemList.get(position);
            view.setName(item.getP_name());
            view.setPlace(item.getP_place_string());
            view.setTimee(item.getP_time());
            view.setImage("http://13.125.174.158:9000/mperson_picture/",item.getP_photo());
            if(item.getP_photo() != null){
                Log.w("photoName: ", item.getP_photo());
            }

            //view.setImage(item.getP_photo());
            view.setAge(item.birthToAge());
            return view;
        }

        @Override
        public Filter getFilter() {
            if(listFilter == null){
                listFilter = new ListFilter();
            }
            return listFilter;
        }

        private class ListFilter extends Filter{
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
               FilterResults results = new FilterResults();

               if(charSequence == null || charSequence.length() == 0){
                   results.values = items;
                   results.count = items.size();
               }else{
                   ArrayList<Mperson> itemList = new ArrayList<Mperson>();

                   for(Mperson item : items){
                       if(item.getP_name().toUpperCase().contains(charSequence.toString().toUpperCase()))
                           itemList.add(item);
                   }
                   results.values = itemList;
                   results.count = itemList.size();
               }
               return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItemList = (ArrayList<Mperson>)filterResults.values;

                if(filterResults.count > 0)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();
            }
        }

    }
}



