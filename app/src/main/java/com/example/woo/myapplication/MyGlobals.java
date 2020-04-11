package com.example.woo.myapplication;

import com.example.woo.myapplication.data.Color;
import com.example.woo.myapplication.data.CompleteData;
import com.example.woo.myapplication.data.DepartmentData;
import com.example.woo.myapplication.data.DetailData;
import com.example.woo.myapplication.data.LatLngData;
import com.example.woo.myapplication.data.MapDetail;
import com.example.woo.myapplication.data.MapInfo;
import com.example.woo.myapplication.data.Mperson;
import com.example.woo.myapplication.data.NotCompleteList;
import com.example.woo.myapplication.data.Not_Complete_Data;
import com.example.woo.myapplication.data.PlaceIndex;
import com.example.woo.myapplication.data.User;
import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class MyGlobals {
    private User user = null; //user 정보 저장
    private ArrayList<MyRoomItem> maplist = null;
    private Retrofit retrofit=null;
    private RetrofitExService retrofitExService=null;
    private static MyGlobals instance = null;

    public ArrayList<MyRoomItem> getMaplist() {
        return maplist;
    }

    public void setMaplist(ArrayList<MyRoomItem> maplist) {
        this.maplist = maplist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public RetrofitExService getRetrofitExService() {
        return retrofitExService;
    }

    public void setRetrofitExService(RetrofitExService retrofitExService) {
        this.retrofitExService = retrofitExService;
    }

    public static synchronized MyGlobals getInstance() {
        if(instance == null)
            instance = new MyGlobals();

        return instance;
    }

    public static void setInstance(MyGlobals instance) {
        MyGlobals.instance = instance;
    }

    public interface RetrofitExService{ //interface 선언
        public static final String URL = "http://13.125.174.158:9001/"; //서버 주소와 포트번호


        @GET("/get/latLng")
        Call<ArrayList<LatLngData>> getLatLng(@Query("mid") String mid);

        @GET("/get/placeIndex?")
        Call<ArrayList<PlaceIndex>> getPlaceIndex(@Query("mid") String mid);

        @GET("/get/department")
        Call<ArrayList<DepartmentData>> getDepartmentData();

        @GET("/modify/department?") //부서 정보 수정
        Call<DepartmentData> modifyDepartmentData(@Query("origin_department") String origin_depart, @Query("new_department") String new_depart, @Query("color") String color);

        @GET("/delete/department?") //부서 정보 삭제
        Call<DepartmentData> deleteDepartmentData(@Query("department") String department);

        @GET("/mperson")
        Call<ArrayList<Mperson>> getData();

        @GET("/complete/data?") // 완료 지점 찾기
        Call<CompleteData> getCompleteData(@Query("m_id") String m_id);

        @GET("/detail/unableLocate?") //특정 인덱스 부분에 해당하는 수색불가정보
        Call<Not_Complete_Data> getNotCompleteDetail(@Query("m_id") String m_id,@Query("ul_latitude") String lat,@Query("ul_longitude") String lng);

        @GET("/tracking/list?")
        Call<ArrayList<DetailData>> getTrackingList(@Query("m_id") String m_id, @Query("index") String index);

        @GET("/whole/unableLocate?") //기존지도에서 정보얻어오기 지도전체에 대한 수색불가정보
        Call<ArrayList<Not_Complete_Data>> getNotCompleteData(@Query("m_id") String mid);

        @GET("/get/detail/data?") //기존지도에서 트래킹 정보얻어오기
        Call<ArrayList<DetailData>> getMapDetailData(@Query("mid") String mid);


        @FormUrlEncoded
        @POST("/examine")
        Call<OverlapExamineData> postData(@FieldMap HashMap<String,String> param);

        @FormUrlEncoded
        @POST("/login/process")
        Call<User> postLogin(@FieldMap HashMap<String,String> param);

        @FormUrlEncoded
        @POST("/admin/process")
        Call<OverlapExamineData> postAdmin(@FieldMap HashMap<String,String> param);

        @FormUrlEncoded
        @POST("/change/password")
        Call<User> postChangePassword(@FieldMap HashMap<String,String> param);

        @GET("/change/department?") //URL
        Call<Color> getChangeDepartment(@Query("u_id") String u_id, @Query("u_department") String u_department);

        @FormUrlEncoded
        @POST("/delete/room")
        Call<OverlapExamineData> postDeleteRoom(@FieldMap HashMap<String,String> param);

        @GET("mypage/maplist?")
        Call<ArrayList<MyRoomItem>> getMypageMapData(@Query("u_id") String m_id);

        @GET("person/maplist?")
        Call<ArrayList<MapInfo>> getPersonMapData(@Query("p_id") String p_id);

        @FormUrlEncoded
        @POST("/map/make")
        Call<OverlapExamineData> postMapMake(@FieldMap HashMap<String,String> param);


        @FormUrlEncoded
        @POST("/map/attendance")
        Call<OverlapExamineData> postMapAttendance(@FieldMap HashMap<String,String> param);

        @GET("/mapdetail?")
        Call<ArrayList<MapDetail>> getMapDetail(@Query("m_id") String m_id);


        @Multipart
        @POST("/not_complete/image")
        Call<OverlapExamineData> postNotComplete(@Part("mid")RequestBody mid,@Part MultipartBody.Part file);

        @GET("/not_complete/?")
        Call<OverlapExamineData> getNotComplete(@Query("mid") String m_id,@Query("desc") String desc,@Query("lat") String lat,@Query("lng") String lng);

        @Multipart
        @POST("/insert/mperson")
        Call<OverlapExamineData> postInsertMperson(@Part MultipartBody.Part file,@Part("p_name")RequestBody name,@Part("p_age")RequestBody age,
        @Part("p_time")RequestBody date,@Part("p_place_string")RequestBody place,@Part("p_place_latitude")RequestBody latitude,@Part("p_place_longitude")RequestBody longitude,
                                                   @Part("p_place_description")RequestBody description);

        @GET("/insert/department")
        Call<OverlapExamineData> getInsertDepartment(@Query("department") String dep,@Query("color") String color);



    }

}
