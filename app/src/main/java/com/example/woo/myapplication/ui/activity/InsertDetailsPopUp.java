package com.example.woo.myapplication.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.ui.view.FindMapFragment;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.socket.client.Socket;

public class InsertDetailsPopUp extends Activity {
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    Button completedBtn;
    private Socket mSocket;
    private String mid;
    private double lat;
    private double lng;
    String mCurrentPhotoPath;

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.pop_up_insert_detail);
        this.mSocket = FindMapFragment.mSocket;
        this.mid = MapActivity.mid;
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",-1);
        lng = intent.getDoubleExtra("lng",-1);
        tedPermission();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("aaa","not");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
            Log.d("aaa","good");
        }

        cameraBtn = (ImageButton)findViewById(R.id.fromCameraBtn);
        galleryBtn = (ImageButton)findViewById(R.id.fromGalleryBtn);
        completedBtn = (Button)findViewById(R.id.completedBtn);


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //수색완료 정보 보내기
                sendComplete(lat,lng);
            }
        });
    }

    public void sendComplete(Double lat,Double lng){ //수색완료 보내기
        try{
            JSONObject data = new JSONObject();
            data.put("m_find_latitude",lat);
            data.put("m_find_longitude",lng);
            mSocket.emit("findPeople",data);
            finish();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //    public void sendImage(File file){//수색불가 보내기
//        mSocket.on("specialThing",getNotComplete);
//        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"),file);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("upload",file.getName(),fileReqBody);
//        RequestBody mapId =  RequestBody.create(MediaType.parse("text/plain"),mid);
//        retrofitExService.postNotComplete(mapId,part).enqueue(new Callback<OverlapExamineData>() {
//            @Override
//            public void onResponse(Call<OverlapExamineData> call, Response<OverlapExamineData> response) {
//                OverlapExamineData data = response.body();
//                if(data.getOverlap_examine().equals("success")){
//                    try{
//                        JSONObject latLng = new JSONObject();
//                        latLng.put("ul_longitude",);
//                        latLng.put("ul_latitude",);
//                        latLng.put("ul_desc",);
//                        latLng.put("ul_file",file.getName());
//                        mSocket.emit("specialThing",latLng);
//                    }catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(),"이미지 보내기 실패",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OverlapExamineData> call, Throwable t) {
//                Toast.makeText(getApplicationContext(),"이미지 보내기 실패",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }



    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("aaa",String.valueOf(resultCode));
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.d("aaa", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        Log.d("aaa",String.valueOf(requestCode));
        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();

        }else if (requestCode == PICK_FROM_CAMERA) {
            Log.d("aaa","bbbbb");

            setImage();

        }
    }

    private void setImage() {

            Log.d("aaa", "Call");
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
            Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();


    }

    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            Log.d("aaa","camera");
            Uri photoUri = Uri.fromFile(tempFile);
            Log.d("aaa","camera2");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            Log.d("aaa","camera3");
            startActivityForResult(intent, PICK_FROM_CAMERA);

        }
    }

    private File createImageFile() throws IOException {

        Log.d("aaa","fi");
        //String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "vasco_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        // 이미지가 저장될 폴더 이름
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/vasco/");
        if (!storageDir.exists()){
            storageDir.mkdirs();
            Log.d("aaa","nonono");
        }
        Log.d("aaa","fil");
        // 빈 파일 생성
        File image = new File(storageDir,imageFileName);
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.d("aaa","file");

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}
