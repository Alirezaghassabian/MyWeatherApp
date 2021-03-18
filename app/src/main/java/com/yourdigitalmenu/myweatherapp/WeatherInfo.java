package com.yourdigitalmenu.myweatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.time.OffsetDateTime;
import java.util.regex.Pattern;

public class WeatherInfo extends AppCompatActivity {
    private final static String TAG = WeatherInfo.class.getSimpleName();
    private final Pattern mZipcodePattern = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
    private final Gson mGson = new Gson();
    private TextView mTempTextView;
    private TextView mTempFeelTextView;
    private TextView mNameTextView;
    private TextView mDescriptionTextView;
    private TextView mMinTempTextView;
    private TextView mMaxTempTextView;
    private TextView mZipcodeTextView;
    private TextView mDateTextView;
    private SharedPreferences sharedPreferences;
    private EditText mZipCodeEditText;
    private Button mSearchButton;
    private ProgressBar mProgressBar;
    private String mUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        Intent intent = getIntent();
        mUserKey = intent.getStringExtra(LoginActivity.USER_KEY);
        if (mUserKey == null) {
            mUserKey = "GUEST";
        }

        mSearchButton = findViewById(R.id.search_btn);
        mZipCodeEditText = findViewById(R.id.zipcode_edittext);
        mTempTextView = findViewById(R.id.temp_textview);
        mTempFeelTextView = findViewById(R.id.temp_feel_textview);
        mNameTextView = findViewById(R.id.name_textview);
        mDescriptionTextView = findViewById(R.id.description_textview);
        mMinTempTextView = findViewById(R.id.min_temp_textview);
        mMaxTempTextView = findViewById(R.id.high_temp_textview);
        mZipcodeTextView = findViewById(R.id.zipcode_textview);
        mDateTextView = findViewById(R.id.date_textview);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        mSearchButton.setOnClickListener(v -> {
            final String strZipCode = mZipCodeEditText.getText().toString();
            if (!isZipcodeValid(strZipCode)) {
                Toast.makeText(v.getContext(), R.string.enterProperZipcode, Toast.LENGTH_SHORT).show();
                return;
            }
            mSearchButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            NetworkHelper.getJson(
                    NetworkHelper.getAPIUrlByZipCode(strZipCode),
                    getApplicationContext(),
                    response -> {
                        Location location = Location.locationHelper(response, strZipCode, getDate());
                        if (location != null) {
                            saveLocationToCache(location, mUserKey);
                        }
                        updateUI(location);
                    },
                    error -> {
                        Log.e(TAG, error.toString());
                        updateUI(null);
                        Toast.makeText(v.getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    });
        });

        updateUI(loadLocationDataFromCache(mUserKey));
    }

    private void updateUI(Location location) {
        if (location != null) {
            mNameTextView.setText(location.getName());
            mTempTextView.setText(String.format("%s F", location.getTemp()));
            mTempFeelTextView.setText(String.format("Feels like %sF", location.getFeels_like()));
            mDescriptionTextView.setText(location.getDescription());
            mMinTempTextView.setText(String.format("Min is %sF", location.getTemp_min()));
            mMaxTempTextView.setText(String.format("Max is %sF", location.getTemp_max()));
            mZipcodeTextView.setText(location.getZipCode());
            mDateTextView.setText(location.getTime());
        } else {
            mNameTextView.setText("");
            mTempTextView.setText("");
            mTempFeelTextView.setText("");
            mDescriptionTextView.setText("");
            mMinTempTextView.setText("");
            mMaxTempTextView.setText("");
            mZipcodeTextView.setText("");
            mDateTextView.setText("");
        }
        mSearchButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private boolean isZipcodeValid(String zipcode) {
        return mZipcodePattern.matcher(zipcode).matches();
    }


    private Location loadLocationDataFromCache(String userKey) {
        String json = sharedPreferences.getString(userKey, null);
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            Location location = mGson.fromJson(json, Location.class);
            return new Location(location.getZipCode(),
                    location.getTemp(),
                    location.getFeels_like(),
                    location.getName(),
                    location.getDescription(),
                    location.getTemp_min(),
                    location.getTemp_max(),
                    location.getTime());
        } catch (JsonSyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public void saveLocationToCache(Location location, String userKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userKey, mGson.toJson(location));
        editor.apply();
    }

    public String getDate() {
        OffsetDateTime offset = OffsetDateTime.now();
        return (offset.getDayOfWeek() + " " + offset.getDayOfMonth() + ", " + offset.getYear());
    }
}