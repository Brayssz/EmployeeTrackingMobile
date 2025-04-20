package com.example.employeetracking.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GetTask<T> extends AsyncTask<Void, String, String> {

    private GetCallback<T> callback;
    private String errorMessage;
    private String apiRequestWithParams;
    private Context context;
    private Class<T> clazz;

    public GetTask(Context context, GetCallback<T> callback, String errorMessage, String apiRequestWithParams, Class<T> clazz) {
        this.context = context;
        this.callback = callback;
        this.apiRequestWithParams = apiRequestWithParams; // Example: "get-user-travels?user_id=1"
        this.errorMessage = errorMessage;
        this.clazz = clazz;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";

        try {
            URL url = new URL(ApiAddress.url + apiRequestWithParams);
            Log.d("GetTask", "URL: " + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                response += line;
            }

            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("GetTask", "Error in doInBackground: " + e.getMessage());
            response = "server_error";
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GetTask", "Response: " + result);

        if (result.equals("server_error")) {
            new AlertDialog.Builder(context)
                    .setTitle("Server Error")
                    .setMessage("An error occurred during request!")
                    .setPositiveButton("Close", (dialogInterface, i) -> {
                        ((Activity) context).finishAffinity();
                        System.exit(0);
                    }).show();
            return;
        }

        if (result != null && !result.equals("[]")) {
            try {
                JSONObject json = new JSONObject(result);

                if (result.contains("success")) {
                    if (json.has("data")) {
                        Gson gson = new Gson();
                        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
                        List<T> dataList = gson.fromJson(json.getString("data"), listType);
                        callback.onGetSuccess(dataList);
                        return;
                    }
                } else if (result.contains("error")) {
                    callback.onGetError(json.getString("message"));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        callback.onGetError(errorMessage);
    }
}
