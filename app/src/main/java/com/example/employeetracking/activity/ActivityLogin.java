package com.example.employeetracking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.employeetracking.R;
import com.example.employeetracking.api.PostCallback;
import com.example.employeetracking.api.PostTask;
import com.example.employeetracking.model.APIResponse;
import com.example.employeetracking.model.User;
import com.example.employeetracking.utils.Loader;
import com.example.employeetracking.utils.Messenger;
import com.example.employeetracking.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ActivityLogin extends AppCompatActivity implements PostCallback {

    private TextInputEditText username, password;
    private Button btnLogin;

    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loader = new Loader();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this::loginAction);
    }

    private void loginAction(View view) {
        loader.showLoader(this);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", username.getText().toString());
            jsonObject.put("password", password.getText().toString());

            PostTask postTask = new PostTask(this, this, "error message", "login");

            postTask.execute(jsonObject);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (postTask.getStatus() == AsyncTask.Status.RUNNING) {
                    postTask.cancel(true);
                    loader.dismissLoader();
                    Messenger.showAlertDialog(this,
                            "Timeout",
                            "Login request took too long. Please check your connection and try again.",
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
        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        User user = gson.fromJson(responseData, type);

        APIResponse<User> apiResponse = gson.fromJson(responseData, new TypeToken<APIResponse<User>>(){}.getType());

        List<User> users = apiResponse.getUser();

        if(apiResponse.getStatus().equals("success")) {
            Intent intent = new Intent(ActivityLogin.this, ActivityChooseTravel.class);

            SessionManager.getInstance(this).createLoginSession(users.get(0));

            startActivity(intent);

            finish();

//            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

        }else if(apiResponse.getStatus().equals("error")){
            Messenger.showAlertDialog(this, "Error", apiResponse.getMessage(), "Yes").show();
        }

        loader.dismissLoader();
    }

    @Override
    public void onPostError(String errorMessage) {
        Messenger.showAlertDialog(this, "Error", errorMessage, "Yes").show();
        loader.dismissLoader();
    }
}