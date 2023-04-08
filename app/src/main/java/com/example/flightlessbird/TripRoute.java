package com.example.flightlessbird;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TripRoute implements Parcelable {

    String arrivalTime;
    String departureDate;
    String departureTime;
    String destinationStationCode;
    String destinationStationName;
    String operatorName;
    String originStationCode;
    String originStationName;

    public TripRoute(JSONObject tripRouteJSON) throws JSONException {
        this.arrivalTime = tripRouteJSON.getString("arrivalTime");
        this.departureDate = tripRouteJSON.getString("departureDate");
        this.departureTime = tripRouteJSON.getString("departureTime");
        this.destinationStationCode = tripRouteJSON.getString("destinationStationCode");
        this.destinationStationName = tripRouteJSON.getString("destinationStationName");
        this.operatorName = tripRouteJSON.getString("operatorName");
        this.originStationCode = tripRouteJSON.getString("originStationCode");
        this.originStationName = tripRouteJSON.getString("originStationName");
    }

    @Override
    public String toString() {
        return "Route{" +
                "originStationCode='" + originStationCode + '\'' +
                ", originStationName='" + originStationName + '\'' +
                ", destinationStationCode='" + destinationStationCode + '\'' +
                ", destinationStationName='" + destinationStationName + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", operatorName='" + operatorName + '\'' +
                '}';
    }

    TripRoute() {

    }

    public String getArrivalTime() {
        if (arrivalTime.startsWith("A")) {
            return  arrivalTime;
        } else {
            return "Arrives at " + arrivalTime;
        }

    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        if (departureTime.startsWith("D")) {
            return departureTime;
        } else {
            return "Departs at " + departureTime;
        }
    }

    public String getDestinationStationCode() {
        return destinationStationCode;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public String getOriginStationCode() {
        return originStationCode;
    }

    public String getOriginStationName() {
        return originStationName;
    }

    public void setOriginStationCode(String originStationCode) {
        this.originStationCode = originStationCode;
    }

    public void setOriginStationName(String originStationName) {
        this.originStationName = originStationName;
    }

    public void setDestinationStationCode(String destinationStationCode) {
        this.destinationStationCode = destinationStationCode;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    ////////////////////////////////////////////////////////////////
    // Parcelable Implementation
    ////////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(arrivalTime);
        dest.writeString(departureDate);
        dest.writeString(departureTime);
        dest.writeString(destinationStationCode);
        dest.writeString(destinationStationName);
        dest.writeString(operatorName);
        dest.writeString(originStationCode);
        dest.writeString(originStationName);
    }

    protected TripRoute(Parcel in) {
        arrivalTime = in.readString();
        departureDate = in.readString();
        departureTime = in.readString();
        destinationStationCode = in.readString();
        destinationStationName = in.readString();
        operatorName = in.readString();
        originStationCode = in.readString();
        originStationName = in.readString();
    }

    public static final Creator<TripRoute> CREATOR = new Creator<TripRoute>() {
        @Override
        public TripRoute createFromParcel(Parcel in) {
            return new TripRoute(in);
        }

        @Override
        public TripRoute[] newArray(int size) {
            return new TripRoute[size];
        }
    };

}
