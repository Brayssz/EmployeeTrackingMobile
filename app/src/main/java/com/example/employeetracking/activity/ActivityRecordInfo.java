package com.example.employeetracking.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import android.Manifest;
import com.example.employeetracking.R;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SetUserStatus;
import com.example.employeetracking.utils.StatusSessionManager;
import com.google.android.material.button.MaterialButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;


public class ActivityRecordInfo extends AppCompatActivity implements PostCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton btnRecordInfo;
    private String coordinates;

    private Loader loader;

    private TextInputEditText remarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_info);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        loader = new Loader();

        remarks = findViewById(R.id.remarks);

        btnRecordInfo = findViewById(R.id.submitRemarks);

        btnRecordInfo.setOnClickListener(this::recordTravelInfo);
    }

    private void recordTravelInfo(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getCurrentLocation(); // call your location method directly
        }

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        coordinates = lat + "," + lng;
                        saveTravelInfo();
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveTravelInfo() {

        SharedPreferences preferences = getSharedPreferences("Pref", MODE_PRIVATE);

        int selectedEventId = preferences.getInt("selected_event_id", 0);

        String travelRemarks = remarks.getText().toString();

        if(travelRemarks.isEmpty()){
            Messenger.showAlertDialog(this,
                    "Error",
                    "Please enter remarks.",
                    "Ok").show();
            return;
        }


        loader.showLoader(this);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("travel_user_id", selectedEventId);
            jsonObject.put("coordinates", coordinates);
            jsonObject.put("remarks", travelRemarks);

            PostTask postTask = new PostTask(this, this, "error message", "record-travel");

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


        Toast.makeText(this, "Travel info saved: " + coordinates + " " + travelRemarks + " " + selectedEventId, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    @Override
    public void onPostSuccess(String responseData) {
        loader.dismissLoader();

        StatusSessionManager.getInstance(this).saveStatus("OnTravel");

        new SetUserStatus(this).setUserStatus("ontravel");

        Activity activity = new ActivityLogin();

        Messenger.showAlertDialogAndRedirect(
                this,
                "Success",
                "Travel info saved successfully.",
                "Ok",
                ActivityLogin.class // Pass the target activity class
        );
    }

    @Override
    public void onPostError(String errorMessage) {

    }
}