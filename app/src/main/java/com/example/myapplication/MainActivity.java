package com.example.myapplication;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton ib_toMap=null;
    private ImageButton ib_toInspecting=null;
    private ImageButton ib_toWarnMessage=null;
    private ImageButton ib_toInstall=null;
    Intent intent=null;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean coarseLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean readPhoneState = Boolean.TRUE.equals(result.get(Manifest.permission.READ_PHONE_STATE));
                boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                if (coarseLocation && fineLocation && readPhoneState && writeStorage) {

                } else {
                    Toast.makeText(MainActivity.this, "有权限未通过", Toast.LENGTH_SHORT).show();
                }
            }
    );
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        requestPermissionsLauncher.launch(permissions);
        setContentView(R.layout.activity_main);
        ib_toMap=(ImageButton) findViewById(R.id.ib_toMap);
        ib_toInspecting=findViewById(R.id.ib_toInspecting);
        ib_toInstall=findViewById(R.id.ib_toInstall);
        ib_toWarnMessage=findViewById(R.id.ib_toWarnMessage);
        ib_toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        ib_toInspecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MainActivity.this,Inspecting.class);
                startActivity(intent);
            }
        });
        ib_toWarnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MainActivity.this,WarnMessage.class);
                startActivity(intent);
            }
        });
    }

}