package com.example.woo.myapplication.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.socket.client.Socket;

public class InsertDetailsPopUp extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_insert_detail);
        this.mSocket = FindMapFragment.mSocket;
        this.mid = MapActivity.mid;
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",-1);
        lng = intent.getDoubleExtra("lng",-1);
        tedPermission();

        cameraBtn = (ImageButton)findViewById(R.id.fromCameraBtn);
        galleryBtn = (ImageButton)findViewById(R.id.fromGalleryBtn);
        completedBtn = (Button)findViewById(R.id.completedBtn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("aaa", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

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

            setImage();

        }
    }

    private void setImage() {

//        ImageView imageView = findViewById(R.id.imageView);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

//        imageView.setImageBitmap(originalBm);

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

            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }
}
