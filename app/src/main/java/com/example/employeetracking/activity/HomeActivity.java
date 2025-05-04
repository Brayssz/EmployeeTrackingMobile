package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.employeetracking.R;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.services.LiveLocationService;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SessionManager;
import com.example.employeetracking.utils.SetUserStatus;
import com.example.employeetracking.utils.StatusSessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton btnOnTravel;
    private MaterialButton btnOnLeave;
    private MaterialButton btnAtWork;

    private Loader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkStatus();

        loader = new Loader();

        btnOnTravel = findViewById(R.id.btnOnTravel);
        btnOnLeave = findViewById(R.id.btnOnLeave);
        btnAtWork = findViewById(R.id.btnAtWork);

        btnOnTravel.setOnClickListener(this::SetOnTravel);
        btnOnLeave.setOnClickListener(this::SetOnLeave);
        btnAtWork.setOnClickListener(this::SetAtWork);
    }

    private void SetAtWork(View view) {

        startActivity(new Intent(this, TimeInActivity.class));
    }

    private void checkStatus() {
        String status = StatusSessionManager.getInstance(this).getStatus();

        if(status == null || status.isEmpty()) {
            return;
        }

        if(status.equals("AtWork")) {
            startActivity(new Intent(this, TimeInActivity.class));
            finish();
        } else if(status.equals("OnLeave")) {
            startActivity(new Intent(this, EndLeaveActivity.class));
            finish();
        } else if(status.equals("OnTravel")) {
            startActivity(new Intent(this, ActivityChooseTravel.class));
            finish();
        } else if(status.equals("TimeIn")) {
            Intent serviceIntent = new Intent(this, LiveLocationService.class);
            startService(serviceIntent);
            startActivity(new Intent(this, TimeOutActivity.class));
        }
    }


    private void SetOnLeave(View view) {
        loader.showLoader(this);
        StatusSessionManager.getInstance(this).saveStatus("OnLeave");
        new SetUserStatus(this).setUserStatus("onleave");
    }
    private void SetOnTravel(View view) {

        startActivity(new Intent(this, ActivityChooseTravel.class));
    }

}