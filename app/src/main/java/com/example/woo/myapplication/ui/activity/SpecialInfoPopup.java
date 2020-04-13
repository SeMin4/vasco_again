package com.example.woo.myapplication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.Not_Complete_Data;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialInfoPopup extends Activity {
    private String SERVER_HOST_PATH = "http://13.125.174.158:9000/not_complete_picture/";
    private ImageView specialImage;
    private TextView specialDescription;
    private MyGlobals.RetrofitExService retrofitExService;
    private String specialFileName;
    private String specialDescText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_special_info);
        retrofitExService = MyGlobals.getInstance().getRetrofitExService();
        specialImage = (ImageView) findViewById(R.id.specialImage);
        specialDescription = (TextView)findViewById(R.id.specialDescription);

        Intent intent = getIntent();
        String mid = intent.getStringExtra("mid");
        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("lng");
        retrofitExService.getNotCompleteDetail(mid,latitude,longitude).enqueue(new Callback<Not_Complete_Data>() {
            @Override
            public void onResponse(Call<Not_Complete_Data> call, Response<Not_Complete_Data> response) {
                Not_Complete_Data data = response.body();
                specialFileName = data.getUl_file();
                specialDescText = data.getUl_desc();
                specialDescription.setText(specialDescText);
                Picasso.with(getApplicationContext())
                        .load(SERVER_HOST_PATH  + specialFileName)
                        .rotate(0f)
                        .fit()
                        .into(specialImage);
            }

            @Override
            public void onFailure(Call<Not_Complete_Data> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
//        retrofitExService.getNotCompleteDetail(mid,latitude,longitude).enqueue(new Callback<Not_Complete_Data>() {
//            @Override
//            public void onResponse(Call<Not_Complete_Data> call, Response<Not_Complete_Data> response) {
//                Not_Complete_Data data = response.body();
//                specialFileName = data.getUl_file();
//                specialDescText = data.getUl_desc();
//                specialDescription.setText(specialDescText);
//                Picasso.with(getApplicationContext())
//                        .load(SERVER_HOST_PATH  + specialFileName)
//                        .rotate(0f)
//                        .fit()
//                        .into(specialImage);
//            }
//
//            @Override
//            public void onFailure(Call<Not_Complete_Data> call, Throwable t) {
//                Log.d("failureerror", t.getMessage());
//                Log.d("error", "detail notcomplete error");
//            }
//        });

    }
}
