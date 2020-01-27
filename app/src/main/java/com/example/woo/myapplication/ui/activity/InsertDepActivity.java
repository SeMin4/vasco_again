package com.example.woo.myapplication.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.woo.myapplication.R;

import yuku.ambilwarna.AmbilWarnaDialog;

public class InsertDepActivity extends AppCompatActivity {

    int tColor;
    Button color_btn;
    Button save_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_dep);

        color_btn= findViewById(R.id.colorpicker);
        save_btn=findViewById(R.id.save_btn);
        color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, tColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tColor = color;
                color_btn.setBackgroundColor(tColor);
            }
        });
        colorPicker.show();
    }
}
