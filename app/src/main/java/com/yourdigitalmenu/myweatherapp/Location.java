package com.yourdigitalmenu.myweatherapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.yourdigitalmenu.myweatherapp.NetworkHelper.isResponseValid;

public class Location {
    private final static String TAG = Location.class.getSimpleName();

    private String zipCode = "";
    private String temp = "";
    private String feels_like = "";
    private String name = "";
    private String description = "";
    private String temp_min = "";
    private String temp_max = "";
    private String time = "";

    public Location(String zipCode, String temp, String feels_like, String name, String description, String temp_min, String temp_max, String time){
        this.zipCode = zipCode;
        this.temp=temp;
        this.feels_like=feels_like;
        this.name=name;
        this.description=description;
        this.temp_min=temp_min;
        this.temp_max=temp_max;
        this.time=time;

    }
    public static Location locationHelper(String json, String zipcode, String date){
        final String temp = "temp";
        final String feels_like = "feels_like";
        final String temp_min = "temp_min";
        final String temp_max = "temp_max";
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (!isResponseValid(jsonObject)) {
                return null;
            }
            JSONArray weatherJasonArray = jsonObject.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherJasonArray.getJSONObject(0);
            JSONObject mainJsonObject = jsonObject.getJSONObject("main");
            return new Location(zipcode,
                    mainJsonObject.optString(temp),
                    mainJsonObject.optString(feels_like),
                    jsonObject.optString("name"),
                    weatherJsonObject.optString("description"),
                    mainJsonObject.optString(temp_min),
                    mainJsonObject.optString(temp_max),
                    date);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getTemp() {
        return temp;
    }

    public String getFeels_like() {
        return feels_like;
    }
    public String getDescription() {
        return description;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }
    public String getTime() {
        return time;
    }

}
