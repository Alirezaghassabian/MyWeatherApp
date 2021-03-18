package com.yourdigitalmenu.myweatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button facebookLoginBtn = findViewById(R.id.facebookLoginBtn);
        Button continueWithoutLoginBtn = findViewById(R.id.continueWithoutLoginBtn);
        facebookLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        });
        continueWithoutLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),WeatherInfo.class);
            intent.putExtra(LoginActivity.USER_KEY, (String) null);

            startActivity(intent);
        });
    }
}