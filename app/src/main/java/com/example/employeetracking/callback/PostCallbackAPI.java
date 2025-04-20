package com.example.employeetracking.callback;

public interface PostCallbackAPI<T> {
    void onComplete(String message);
    void onError(Throwable error);
}
