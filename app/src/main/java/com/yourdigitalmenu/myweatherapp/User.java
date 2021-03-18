package com.yourdigitalmenu.myweatherapp;

import android.util.Log;

import org.json.JSONObject;

public class User {
    private String userId;

    public User(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public static User userHelper(String json){
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            user = new User(data.optString("user_id"));
        } catch (Exception e) {
            Log.e("User", e.toString());
        }
        return user;
    }
}