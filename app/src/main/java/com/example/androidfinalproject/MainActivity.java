package com.example.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidfinalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkAllPermession();
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.audioMenu:
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED
                    ){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                },
                                28
                        );
                    }else{
                        changeFragment(new AudiosFragment());
                        //Toast.makeText(getApplicationContext(), "ana hna", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.videoMenu:
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                    ){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},
                                28
                        );
                    }else{
                        changeFragment(new VideosFragment());
                        //Toast.makeText(getApplicationContext(), "ana hna", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.imageMenu:
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED
                    ){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},
                                28
                        );
                    }else{
                        //changeFragment(new VideosFragment());
                        changeFragment(new ImagesFragment());
                        //Toast.makeText(getApplicationContext(), "ana hna", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            //Toast.makeText(getApplicationContext(), item.getItemId()+"", Toast.LENGTH_LONG).show();

            return true;
        });


    }


    private void checkAllPermession() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION},
                    28
            );
        }else{
            changeFragment(new VideosFragment());
           // Toast.makeText(getApplicationContext(), "ana hna", Toast.LENGTH_LONG).show();
        }
    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fram_layout, fragment);
        fragmentTransaction.commit();
    }
}