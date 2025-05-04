package com.example.employeetracking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.employeetracking.activity.EndLeaveActivity;
import com.example.employeetracking.activity.EndTravelActivity;
import com.example.employeetracking.activity.TimeInActivity;
import com.example.employeetracking.activity.TimeOutActivity;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;

import org.json.JSONObject;

public class SetUserStatus implements PostCallback {

    private Loader loader = new Loader();
    private Context context;

    public SetUserStatus(Context context) {
        this.context = context;
    }

    public void setUserStatus(String status) {
        String userId = SessionManager.getInstance(context).getUser().getUserId();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", status);
            jsonObject.put("user_id", userId);

            PostTask postTask = new PostTask(context, this, "error message", "set-status");

            postTask.execute(jsonObject);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (postTask.getStatus() == AsyncTask.Status.RUNNING) {
                    postTask.cancel(true);
                    loader.dismissLoader();
                    Messenger.showAlertDialog(context,
                            "Timeout",
                            "Request took too long. Please check your connection and try again.",
                            "Ok").show();
                }
            }, 30000);

        } catch (Exception e) {
            loader.dismissLoader();
            e.printStackTrace();
            Messenger.showAlertDialog(context,
                    "Error",
                    "Failed to prepare login request.",
                    "Ok").show();
        }
    }

    @Override
    public void onPostSuccess(String responseData) {
        loader.dismissLoader();
        String status = StatusSessionManager.getInstance(context).getStatus();


        if(status.equals("AtWork")) {

            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.startActivity(new Intent(context, TimeOutActivity.class));
                activity.finish();
            }

        } else if(status.equals("OnLeave")) {

            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.startActivity(new Intent(context, EndLeaveActivity.class));
                activity.finish();
            }

        } else if(status.equals("OnTravel")) {
            Activity activity = (Activity) context;
            activity.startActivity(new Intent(context, EndTravelActivity.class));
            activity.finish();
        }
    }

    @Override
    public void onPostError(String errorMessage) {

    }
}
