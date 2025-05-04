package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

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

public class TimeInActivity extends AppCompatActivity implements PostCallback {

    private MaterialButton btnTimeIn;
    private Loader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_in);

        loader = new Loader();

        btnTimeIn = findViewById(R.id.btnTimeIn);

        btnTimeIn.setOnClickListener(this::TimeInAction);
    }
    private void TimeInAction(View view) {
        loader.showLoader(this);

        StatusSessionManager.getInstance(this).saveStatus("AtWork");

        String userId = SessionManager.getInstance(this).getUser().getUserId();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);

            PostTask postTask = new PostTask(this, this, "error message", "time-in");

            postTask.execute(jsonObject);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (postTask.getStatus() == AsyncTask.Status.RUNNING) {
                    postTask.cancel(true);
                    loader.dismissLoader();
                    Messenger.showAlertDialog(this,
                            "Timeout",
                            "Request took too long. Please check your connection and try again.",
                            "Ok").show();
                }
            }, 30000);

        } catch (Exception e) {
            loader.dismissLoader();
            e.printStackTrace();
            Messenger.showAlertDialog(this,
                    "Error",
                    "Failed to prepare login request.",
                    "Ok").show();
        }
    }

    @Override
    public void onPostSuccess(String responseData) {

        StatusSessionManager.getInstance(this).saveStatus("TimeIn");

        loader.dismissLoader();

        new SetUserStatus(this).setUserStatus("atwork");

        Intent serviceIntent = new Intent(this, LiveLocationService.class);
        startService(serviceIntent);

        Messenger.showAlertDialogAndRedirect(this,
                "Success",
                "You have successfully timed in.",
                "Ok",
                TimeOutActivity.class);
    }

    @Override
    public void onPostError(String errorMessage) {

    }
}