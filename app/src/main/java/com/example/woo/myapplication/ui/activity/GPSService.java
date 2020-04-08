package com.example.woo.myapplication.ui.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.woo.myapplication.MyGlobals;
import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.MapInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GPSService extends Service {
    static  final String TAG="service";
   // Notification noti;
   public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;
    private ArrayList<Integer> placeIndex;
    private String mid;
    private MyGlobals.RetrofitExService retrofitExService;
    private int existFlag = -1;
    public GPSService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.d(TAG,"oncreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationSomethings();
       /* Intent notificationIntent = new Intent(this, ExampleActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if(Build.VERSION.SDK_INT >= 26) {
            Notification notification =
                    new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                            .setContentTitle("aa")
                            .setContentText("bb")
                            .setSmallIcon(R.drawable.boy)
                            .setContentIntent(pendingIntent)
                            .setTicker("ccc")
                            .build();
            if (intent == null) {
                return Service.START_STICKY;
            }
        }
*/
/*        if(Build.VERSION.SDK_INT >= 26) {
            Intent notificationIntent = new Intent(this, MapActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(this, "1001")
                            .setContentTitle("aa")
                            .setContentText("bb")
                            .setSmallIcon(R.drawable.boy)
                            .setContentIntent(pendingIntent)
                            .setTicker("ccc")
                            .build();

            startForeground(1234, notification);

        }*/


        Intent gpsIntent = new Intent(this, MapActivity.class);
        //여기서 넘기기전에 mid랑 placeindex정보 넘겨야한다..
        existFlag = intent.getIntExtra("existFlag",-1);
       // Log.d("mapActivity","gpsservice mid : "+mid);

        if(existFlag == 0){//방을 만드는 경우
            placeIndex = (ArrayList<Integer>) intent.getSerializableExtra("placeIndex");
            gpsIntent.putExtra("mid",intent.getStringExtra("mid"));
            gpsIntent.putExtra("placeIndex",placeIndex);
            gpsIntent.putExtra("existFlag",existFlag);
            gpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gpsIntent);
        }else if(existFlag == 1){//기존방 입장
            //mid를 통해서 map_radius랑 map_center가져오기
            MapInfo mapInfo = (MapInfo)intent.getSerializableExtra("mapInfo");
            String mid = mapInfo.getM_id();
            //일단 나중에 placeIndex 관련 데이터도 받아서 보내기
            String findLat = mapInfo.getM_find_latitude();
            String findLng = mapInfo.getM_find_longitude();
            String centerLat = mapInfo.getM_center_point_latitude();
            String centerLng = mapInfo.getM_center_point_longitude();
            String mapRadius = mapInfo.getM_size();
            gpsIntent.putExtra("mid",mid);
            gpsIntent.putExtra("existFlag",existFlag);
            gpsIntent.putExtra("centerLat",centerLat);
            gpsIntent.putExtra("centerLng",centerLng);
            gpsIntent.putExtra("mapRadius",mapRadius);
            gpsIntent.putExtra("findLat",findLat);
            gpsIntent.putExtra("findLng",findLng);
            gpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gpsIntent);
        }
        return super.onStartCommand(intent, flags, startId);
       // Toast myToast = Toast.makeText(this.getApplicationContext(),"app 종료", Toast.LENGTH_LONG);
     //   stopForeground(true);
    }


    public void NotificationSomethings() {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MapActivity.class);
        notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.boy)) //BitMap 이미지 요구
                .setContentTitle("바스코로드")
                .setContentText("구역 1 수색중")

                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
               // .setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티피케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

//        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
        startForeground(1234, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        stopSelf();


        Toast myToast = Toast.makeText(this.getApplicationContext(),"app 종료", Toast.LENGTH_LONG);
       // stopForeground(
        Log.d("service","ondestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        //stopLocationUpdates()
        stopForeground(true);
        stopSelf();

        super.onTaskRemoved(rootIntent);
    }
}
