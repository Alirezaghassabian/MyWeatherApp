package com.yourdigitalmenu.myweatherapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkHelper {
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?units=imperial&appid=14e099e4a178fba16675694f67767519&zip=";
    private final static String TAG = NetworkHelper.class.getSimpleName();
    private NetworkHelper() {}

    public static String getAPIUrlByZipCode(String zipcode) {
        return BASE_URL + zipcode;
    }

    public static void getJson(String url,
                               Context context,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        Log.d(TAG, "getJson: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);
        requestQueue.add(stringRequest);
    }

    public static boolean isResponseValid(JSONObject jsonObject) {
        try {
            return jsonObject.getString("cod").equals("200");
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

}
