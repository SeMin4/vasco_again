package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.MapInfo;
import com.example.woo.myapplication.data.Mperson;
import com.naver.maps.geometry.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MpersonDetailActivity extends Activity implements View.OnClickListener{
    private final String SERVER_HOST_PATH = "http://13.125.174.158:9000/mperson_picture";
    ListView listView;
    public static Activity _mpersonDetailActivity;
    private Button makeRoom;
    static ListAdapter adapter;
    private LatLng missingPoint;
    private ArrayList<MapInfo> maplist;
    Retrofit retrofit = null;
    MyGlobals.RetrofitExService retrofitExService =null;
    Mperson selected;
    public class ListAdapter extends BaseAdapter
        {
            ArrayList<MapInfo> listViewItemList = new ArrayList<MapInfo>();

            public ListAdapter()
            {}
            @Override
            public int getCount() {
                return listViewItemList.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                final int pos = position;
                final Context context = parent.getContext();

                if(convertView==null)
                {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.custom_listview_popup,parent,false);
                }

                // 화면에 표시될 View(Layout이 IT터 위젯에 대한 참조 획득

                TextView place = (TextView) convertView.findViewById(R.id.TextView_Searchingplace) ;
                MapInfo listViewItem = listViewItemList.get(position);
                // 아이템 내 각 위젯에 데이터 반영;
                // place.setText("실종지점 위도 : "+listViewItem.getM_center_point_latitude() + " 실종지점 경도 : "+listViewItem.getM_center_point_longitude());
                //place.setText(listViewItem.getM_place_string());
                place.setText(listViewItem.getM_center_place_string());
                return convertView;
        }
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        // 아이템 데이터 추가

        public void addItem(MapInfo item)
        {
            listViewItemList.add(item);
        }

    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mperson_detail);
        _mpersonDetailActivity = MpersonDetailActivity.this;
        retrofit = MyGlobals.getInstance().getRetrofit();
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        listView = (ListView)findViewById(R.id.listView_popup);
        adapter = new ListAdapter();
        makeRoom = (Button)findViewById(R.id.makeroom);
        listView.setAdapter(adapter);
        selected = (Mperson)getIntent().getSerializableExtra("selecteditem");


        setPerson(); // 사람정보 저장

        //***************************맵정보 가져오기***********
        retrofitExService.getPersonMapData( selected.getP_id()).enqueue(new Callback<ArrayList<MapInfo>>() {
            @Override
            public void onResponse(Call<ArrayList<MapInfo>> call, Response<ArrayList<MapInfo>> response) {
                System.out.println("onResponse@@@@@@@@@@@@");
                maplist = response.body();
                if(maplist == null){
                    Toast.makeText(getApplicationContext(),"지도정보를 받아오지 못했습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("maplist _size : "+maplist.size());
                for(int i =0;i<maplist.size();i++){
                    adapter.addItem(maplist.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<MapInfo>> call, Throwable t) {
                System.out.println("onFailure@@@@@@@@@@@@@@");
                Toast.makeText(getApplicationContext(),"지도 목록 띄우기 실패입니다.",Toast.LENGTH_SHORT).show();
            }
        });


        //리스트뷰를 누르면 해당 지역의 수색 상황을 보여준다,방으로 입장하기 위해서 비밀번호 입력
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), EnterMapPWActivity.class);
                intent.putExtra("mapInfoIndex", position);
                intent.putExtra("mapId", maplist.get(position).getM_id());
                startActivityForResult(intent,0);
            }
        });

        //방생성 이벤트
        makeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapConfigActivity.class);
                intent.putExtra("missing_lat", missingPoint.latitude); //위도
                intent.putExtra("missing_long", missingPoint.longitude); //경도
                intent.putExtra("p_id",(CharSequence)selected.getP_id()); //실종자 아이디
                intent.putExtra("m_place_string", (CharSequence)selected.getP_place_string()); // 실종위치
                intent.putExtra("selecteditem",selected);
                startActivity(intent);
            }
        });
    }

    public void setPerson(){
        ImageView profile = (ImageView)findViewById(R.id.ImageView_popuptitle);
        TextView name = (TextView)findViewById(R.id.TextView_Name);
        TextView time = (TextView)findViewById(R.id.TextView_Time);
        TextView place = (TextView)findViewById(R.id.TextView_Place);
        TextView desc = (TextView)findViewById(R.id.TextView_Characteristic);
        TextView age = (TextView)findViewById(R.id.TextView_Age);
        //실종자 사진 자르기
        GradientDrawable drawable = (GradientDrawable) getApplicationContext().getDrawable(R.drawable.iv_circle);
        //이름 장소 시간 사진 특징 +나이
        name.setText((CharSequence)selected.getP_name());
        time.setText((CharSequence)selected.getP_time());
        place.setText((CharSequence)selected.getP_place_string());
        desc.setText((CharSequence)selected.getP_place_description());
        age.setText((CharSequence)selected.birthToAge()); //실종자 정보


        missingPoint = new LatLng(
                Double.parseDouble(selected.getP_place_latitude()),
                Double.parseDouble(selected.getP_place_longitude())
        );

        //실종자 이미지 자르기
        profile.setBackground(drawable);
        profile.setClipToOutline(true);
        profile.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if(selected.getP_photo() == null){
            profile.setImageResource(R.drawable.boy);
        }
        else{
            float rotation = 0;
            Picasso.with(getApplicationContext())
                    .load(SERVER_HOST_PATH + "/" + selected.getP_photo())
                    .rotate(0f)
                    .into(profile);
        }
    }

    public void mOnClick(View v){
        /*Intent intent = new Intent(this, RegisterNewMapActivity.class);
        intent.putExtra("missing_lat", missingPoint.latitude);
        intent.putExtra("missing_long", missingPoint.longitude);
        intent.putExtra("p_id",(CharSequence)selected.getP_id());
        intent.putExtra("m_place_string", (CharSequence)selected.getP_place_string());
        intent.putExtra("selecteditem",selected);
        startActivityForResult(intent, 1);*/
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("list", "onActivityResult");
//        if(requestCode == 0){
//            /*if(resultCode == RESULT_OK){
//                //Toast.makeText(this, "비밀번호 성립", Toast.LENGTH_LONG).show();
//                int index = data.getIntExtra("mapInfoIndex", -1);
//                Intent intent = new Intent(this, ExistingMapActivity.class);
//                intent.putExtra("mapInfo", maplist.get(index));
//                intent.putExtra("selecteditem",selected);
//                startActivityForResult(intent, 1);
//            }*/
//        }
//    }
}
