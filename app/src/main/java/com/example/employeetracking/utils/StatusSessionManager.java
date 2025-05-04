package com.example.employeetracking.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class StatusSessionManager {
    private static final String PREF_NAME = "StatusSession";
    private static final String KEY_STATUS = "status";

    private static StatusSessionManager instance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private StatusSessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized StatusSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new StatusSessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // Save status
    public void saveStatus(String status) {
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    // Get saved status
    public String getStatus() {
        return pref.getString(KEY_STATUS, null);
    }

    public void clearStatus() {
        editor.remove(KEY_STATUS);
        editor.apply();
    }
}
