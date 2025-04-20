package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.employeetracking.R;
import com.example.employeetracking.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ImageView loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loader = findViewById(R.id.loader);

        if(SessionManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, ActivityChooseTravel.class));
            finish();
            return;
        }

        Glide.with(this).load(R.drawable.loader).into(loader);

        new Thread(() -> {
            try {
                Thread.sleep(2000);

                runOnUiThread(() -> {
                    startActivity(new Intent(MainActivity.this, ActivityLogin.class));
                    finish();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}