package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.employeetracking.R;
import com.example.employeetracking.utils.ClearUserStatus;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.StatusSessionManager;
import com.google.android.material.button.MaterialButton;

public class EndTravelActivity extends AppCompatActivity {

    private MaterialButton btnEndTravel;
    private Loader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new Loader();
        setContentView(R.layout.activity_end_travel);

        btnEndTravel = findViewById(R.id.btnEndTravel);

        btnEndTravel.setOnClickListener(this::endTravelAction);
    }

    private void endTravelAction(View view) {
        loader.showLoader(this);

        StatusSessionManager.getInstance(this).clearStatus();

        ClearUserStatus clearUserStatus = new ClearUserStatus();
        clearUserStatus.clearUserStatus(this);

        Messenger.showAlertDialogAndRedirect(this,
                "Success",
                "You have successfully ended your travel.",
                "Ok",
                HomeActivity.class);
    }
}