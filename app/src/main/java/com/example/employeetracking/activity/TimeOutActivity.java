package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.employeetracking.R;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.utils.ClearUserStatus;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SessionManager;
import com.example.employeetracking.utils.StatusSessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class TimeOutActivity extends AppCompatActivity implements PostCallback {

    private MaterialButton btnTimeOut;
    private Loader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        btnTimeOut = findViewById(R.id.btnTimeOut);
        loader = new Loader();

        btnTimeOut.setOnClickListener(this::TimeOutAction);
    }

    private void TimeOutAction(View view) {
        loader.showLoader(this);

        StatusSessionManager.getInstance(this).clearStatus();

        String userId = SessionManager.getInstance(this).getUser().getUserId();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);

            PostTask postTask = new PostTask(this, this, "error message", "time-out");

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
        loader.dismissLoader();
        ClearUserStatus clearUserStatus = new ClearUserStatus();

        clearUserStatus.clearUserStatus(this);

        Messenger.showAlertDialogAndRedirect(this,
                "Success",
                "You have successfully timed out.",
                "Ok",
                HomeActivity.class);
    }

    @Override
    public void onPostError(String errorMessage) {

    }
}