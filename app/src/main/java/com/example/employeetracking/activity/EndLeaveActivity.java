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

public class EndLeaveActivity extends AppCompatActivity {

    private MaterialButton btnEndLeave;
    private Loader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_leave);

        btnEndLeave = findViewById(R.id.btnEndLeave);
        loader = new Loader();

        btnEndLeave.setOnClickListener(this::endLeaveAction);
    }
    private void endLeaveAction(View view){
        loader.showLoader(this);

        StatusSessionManager.getInstance(this).clearStatus();
        ClearUserStatus clearUserStatus = new ClearUserStatus();
        clearUserStatus.clearUserStatus(this);

        Messenger.showAlertDialogAndRedirect(this,
                "Success",
                "You have successfully ended your leave.",
                "Ok",
                HomeActivity.class);
    }
}