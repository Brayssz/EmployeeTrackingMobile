package com.example.employeetracking.services;

import static android.app.Service.START_STICKY;
import static androidx.core.app.ServiceCompat.startForeground;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.employeetracking.R;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.concurrent.Executor;

public class LiveLocationService extends Service implements PostCallback {
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isRunning = false;

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());
        startPerSecondTask();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void startPerSecondTask() {
        isRunning = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    // 🔥 Your per-second code here
                    System.out.println("Running every second!");

                    getCurrentLocation();
                    handler.postDelayed(this, 700);
                }
            }
        };
        handler.post(runnable);
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running")
                .setContentText("Executing every second...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // use your icon
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Ensures service restarts if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // 1 second
        locationRequest.setNumUpdates(1);  // Get only one update

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "Please turn on your location", Toast.LENGTH_SHORT).show();
                    return;
                }

                Location location = locationResult.getLastLocation();
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                recordLocation(String.valueOf(lat), String.valueOf(lng));
            }
        }, Looper.getMainLooper());
    }

    private void recordLocation(String latitude, String longitude) {
        String userId = SessionManager.getInstance(getApplicationContext()).getUser().getUserId();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("user_id", userId);

            PostTask postTask = new PostTask(getApplicationContext(), this, "error message", "record-location");

            postTask.execute(jsonObject);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (postTask.getStatus() == AsyncTask.Status.RUNNING) {
                    postTask.cancel(true);
                    Messenger.showAlertDialog(getApplicationContext(),
                            "Timeout",
                            "Request took too long. Please check your connection and try again.",
                            "Ok").show();
                }
            }, 30000);

        } catch (Exception e) {
            e.printStackTrace();
            Messenger.showAlertDialog(getApplicationContext(),
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
