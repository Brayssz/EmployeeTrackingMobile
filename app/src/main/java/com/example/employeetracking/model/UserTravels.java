package com.example.employeetracking.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class UserTravels implements Parcelable {
    @SerializedName("purpose")
    private String purpose;

    @SerializedName("travel_user_id")
    private String travelUserId;

    public UserTravels() {

    }

    protected UserTravels(Parcel in) {
        purpose = in.readString();
    }

    public static final Creator<UserTravels> CREATOR = new Creator<UserTravels>() {
        @Override
        public UserTravels createFromParcel(Parcel in) {
            return new UserTravels(in);
        }

        @Override
        public UserTravels[] newArray(int size) {
            return new UserTravels[size];
        }
    };

    public String getPurpose() {  // Renamed to getPurpose() for clarity
        return purpose;
    }

    public String getTravelUserId() {  // Renamed to match camel case
        return travelUserId;
    }

    // Setter methods
    public void setTravelUserId(String travelUserId) {  // Renamed to match camel case
        this.travelUserId = travelUserId;
    }

    public void setPurpose(String purpose) {  // Renamed to setPurpose() for clarity
        this.purpose = purpose;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(purpose);
    }

    public ContentValues getContentValues(){
        return null;
    }
    public JSONObject twoJsonObject(){
        return null;
    }
}
