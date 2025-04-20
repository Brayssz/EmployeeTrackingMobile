package com.example.employeetracking.api;

public interface PostCallback {
    void onPostSuccess(String responseData);
    void onPostError(String errorMessage);
}
