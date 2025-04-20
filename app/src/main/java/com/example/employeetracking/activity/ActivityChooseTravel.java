package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.employeetracking.R;
import com.example.employeetracking.api.GetCallback;
import com.example.employeetracking.api.GetTask;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.model.User;
import com.example.employeetracking.model.UserTravels;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SessionManager;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityChooseTravel extends AppCompatActivity implements GetCallback<UserTravels> {

    private MaterialButton chooseTravelBtn, logoutBtn;
    private AutoCompleteTextView eventDropdown;

    private Loader loader;

    private int selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_travel);

        logoutBtn = findViewById(R.id.logoutBtn);
        loader = new Loader();

        eventDropdown = findViewById(R.id.eventDropdown);

        getUserTravels();
        chooseTravelBtn = findViewById(R.id.chooseTravelBtn);

        chooseTravelBtn.setOnClickListener(this::proceedToTravel);
        logoutBtn.setOnClickListener(this::logoutUserAction);
    }

    private void logoutUserAction(View view) {
        Messenger.showAlertDialog(this,
                "Logout",
                "Are you sure you want to logout?",
                "Yes",
                "No",
                (dialogInterface, i) -> {
                    loader.showLoader(this);

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000); // Simulate processing time

                            runOnUiThread(() -> {
                                loader.dismissLoader();
                                startActivity(new Intent(ActivityChooseTravel.this, ActivityLogin.class));
                                SessionManager.getInstance(getApplicationContext()).logout();
                                finish();
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                },
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }
        ).show();
    }

    private void getUserTravels() {
        User user = SessionManager.getInstance(this).getUser();

        loader.showLoader(this);

        try {

            String apiRequestWithParams = "get-user-travels?user_id=" + user.getUserId();

            GetTask<UserTravels> getTask = new GetTask<>(
                    this,
                    this,
                    "Unable to fetch travel options.",
                    apiRequestWithParams,
                    UserTravels.class
            );

            getTask.execute();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (getTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getTask.cancel(true);
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
                    "Failed to fetch travel list.",
                    "Ok").show();
        }
    }
    private void proceedToTravel(View view) {

        if (selectedEvent == 0) {
            Messenger.showAlertDialog(
                    this,
                    "Error",
                    "Please select a travel assign to you.",
                    "Ok"
            ).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selected_event_id", selectedEvent);
        editor.apply();

//        Toast.makeText(this, "Selected Event: " + selectedEvent, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ActivityRecordInfo.class);

        intent.putExtra("selected_event_id", selectedEvent);
        startActivity(intent);
    }

    @Override
    public void onGetSuccess(List<UserTravels> data) {
        loader.dismissLoader();

        List<String> travelEvents = new ArrayList<>();
        Map<String, String> eventMap = new HashMap<>();

        if (data != null && !data.isEmpty()) {
            for (UserTravels travel : data) {
                String eventName = travel.getPurpose();
                String eventId = travel.getTravelUserId();

                travelEvents.add(eventName);
                eventMap.put(eventName, eventId);
            }
        } else {
            travelEvents.add("No travel schedule assigned to you");
        }

        // ArrayAdapter setup for the dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, travelEvents);

        eventDropdown.setAdapter(adapter);

        eventDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEvent = (String) parent.getItemAtPosition(position);

            String selectedEventId = eventMap.get(selectedEvent);

            if (selectedEventId != null) {
                this.selectedEvent = Integer.parseInt(selectedEventId);
            } else {
                this.selectedEvent = 0;
            }
        });

        // Show the dropdown when touched
        eventDropdown.setOnTouchListener((v, event) -> {
            eventDropdown.showDropDown();
            return false;
        });
    }


    @Override
    public void onGetError(String errorMessage) {

    }
}