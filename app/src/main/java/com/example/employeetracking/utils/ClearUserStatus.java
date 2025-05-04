package com.example.employeetracking.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;

import org.json.JSONObject;

public class ClearUserStatus implements PostCallback {
    private Loader loader = new Loader();
    public void clearUserStatus(Context context) {
        String userId = SessionManager.getInstance(context).getUser().getUserId();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);

            PostTask postTask = new PostTask(context, this, "error message", "clear-status");

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

    }

    @Override
    public void onPostError(String errorMessage) {

    }
}
