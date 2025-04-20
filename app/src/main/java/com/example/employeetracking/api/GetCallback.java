package com.example.employeetracking.api;

import java.util.List;

public interface GetCallback<T> {
    void onGetSuccess(List<T> data);
    void onGetError(String errorMessage);
}
