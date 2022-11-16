package com.example.cinema.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.ActivitySplashBinding;
import com.example.cinema.prefs.DataStoreManager;
import com.example.cinema.util.StringUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 2000);
    }

    private void goToActivity() {
        if (DataStoreManager.getUser() != null && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            GlobalFuntion.startActivity(this, MainActivity.class);
        } else {
            GlobalFuntion.startActivity(this, SignInActivity.class);
        }
        finish();
    }
}