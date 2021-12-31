package com.example.testMgmtAndroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.example.testMgmtAndroid.activity.MainActivity;
import com.example.testMgmtAndroid.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    Context context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

//        context = this;
//        preferences = getSharedPreferences("user_data", MODE_PRIVATE);
//        JSONObject data = new JSONObject();
//        try {
//            data.put("userType", preferences.getString("loggedInUserType", ""));
//            data.put("userName", preferences.getString("loggedInUserName", ""));
//            data.put("password", preferences.getString("loggedInPassword", ""));
//            data.put("clientid", preferences.getString("loggedInEmployeId", ""));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (data.optString("userName").isEmpty()) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 2000);
//        }
//        else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 2000);
//        }
//
//        Log.e("MyData"," loggedInUserName ==== " + data);
//
//
//
//    }
    }
}
