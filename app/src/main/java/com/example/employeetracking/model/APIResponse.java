package com.example.employeetracking.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

public class APIResponse<T> implements Parcelable {

    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<T> data;

    public APIResponse() {
    }

    protected APIResponse(Parcel in) {
        status = in.readString();
        message = in.readString();
    }

    public static final Creator<APIResponse> CREATOR = new Creator<APIResponse>() {
        @Override
        public APIResponse createFromParcel(Parcel in) {
            return new APIResponse(in);
        }

        @Override
        public APIResponse[] newArray(int size) {
            return new APIResponse[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getUser() {
        return data;
    }

    public void setUser(List<T> user) {
        this.data = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
    }

    public JSONObject toJsonObject(){
        return null;
    }
}
