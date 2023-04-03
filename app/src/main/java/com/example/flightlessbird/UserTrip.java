package com.example.flightlessbird;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class UserTrip implements Parcelable{

    String tripName;
    Integer stationCount;
    Integer tripDuration;
    ArrayList<TripRoute> tripRoutes;


    public UserTrip(JSONObject userTripJSON) throws JSONException{

        //extract string values from JSON
        this.tripName = userTripJSON.getString("name");
        this.stationCount = userTripJSON.getInt("numCities");
        this.tripDuration = userTripJSON.getInt("tripDuration");

        //get array of trip routes in JSON format
        JSONArray tripRoutesJSON = userTripJSON.getJSONArray("routes");

        //read trip routes from JSON array into arrayList
        this.tripRoutes = new ArrayList<TripRoute>();
        TripRoute tripRoute;
        for (Integer i = 0; i < tripRoutesJSON.length(); i++) {

            tripRoute = new TripRoute(tripRoutesJSON.getJSONObject(i));
            this.tripRoutes.add(tripRoute);
        }
    }

    public UserTrip(String tripName, Integer stationCount, Integer tripDuration, ArrayList<TripRoute> tripRoutes) {
        this.tripName = tripName;
        this.stationCount = stationCount;
        this.tripDuration = tripDuration;
        this.tripRoutes = tripRoutes;
    }


    public String getTripName() {
        return tripName;
    }

    public Integer getStationCount() {
        return stationCount;
    }

    public Integer getTripDuration() {
        return tripDuration;
    }

    public ArrayList<TripRoute> getTripRoutes() {
        return tripRoutes;
    }

    public  String getStationNames() {

        //TODO: potential unhandled 0 length

        String stationsList = "";

        //TODO list last destination station
        TripRoute tripRoute;
        for (int i = 0; i < tripRoutes.size(); i++) {
            if (i != tripRoutes.size() - 1) {

                //add station name and comma
                tripRoute = tripRoutes.get(i);
                stationsList += tripRoute.originStationName + ", ";
            } else {

                //add last origin station and comma
                tripRoute = tripRoutes.get(i);
                stationsList += tripRoute.originStationName + ", ";

                //add last destination station
                stationsList += tripRoute.destinationStationName;
            }
        }

        return stationsList;
    }

    ////////////////////////////////////////////////////////////////
    // Parcelable Implementation
    ////////////////////////////////////////////////////////////////

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tripName);
        if (stationCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(stationCount);
        }
        if (tripDuration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tripDuration);
        }

        //TODO remove this
        /*
        tripRoutesSize = tripRoutes.size();
        dest.writeInt(tripRoutesSize);

        for (TripRoute tripRoute: tripRoutes) {
            dest.writeString(tripRoute.arrivalTime);
            dest.writeString(tripRoute.departureDate);
            dest.writeString(tripRoute.departureTime);
            dest.writeString(tripRoute.destinationStationCode);
            dest.writeString(tripRoute.destinationStationName);
            dest.writeString(tripRoute.originStationCode);
            dest.writeString(tripRoute.originStationName);
        }*/

        dest.writeTypedList(tripRoutes);
    }

    protected UserTrip(Parcel in) {
        tripName = in.readString();
        if (in.readByte() == 0) {
            stationCount = null;
        } else {
            stationCount = in.readInt();
        }
        if (in.readByte() == 0) {
            tripDuration = null;
        } else {
            tripDuration = in.readInt();
        }

        String arrivalTime;
        String departureDate;
        String departureTime;
        String destinationStationCode;
        String destinationStationName;
        String originStationCode;
        String originStationName;

        //TODO remove this
        /*
        tripRoutesSize = in.readInt();
        for (int i = 0; i < tripRoutesSize; i++) {

            arrivalTime = in.readString();
            departureDate = in.readString();
            departureTime = in.readString();
            destinationStationCode = in.readString();
            destinationStationName = in.readString();
            originStationCode = in.readString();
            originStationName = in.readString();

            passedTripRoutes.add(new TripRoute(
                arrivalTime,
                departureDate,
                departureTime,
                destinationStationCode,
                destinationStationName,
                originStationCode,
                originStationName
            ));
        }*/

        ArrayList<TripRoute> passedTripRoutes = new ArrayList<>();
        in.readTypedList(passedTripRoutes, TripRoute.CREATOR);

        tripRoutes = passedTripRoutes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserTrip> CREATOR = new Creator<UserTrip>() {
        @Override
        public UserTrip createFromParcel(Parcel in) {
            return new UserTrip(in);
        }

        @Override
        public UserTrip[] newArray(int size) {
            return new UserTrip[size];
        }
    };
}
