package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINDEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences=MainActivity.this.getSharedPreferences(OTPVerify.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        boolean isLoggedin=sharedPreferences.getBoolean(OTPVerify.IS_LOGGED_IN,false);
        Log.d(TAG, "onCreate: "+ (isLoggedin ?"true":"false"));
        new Handler().postDelayed(() -> {

                    if (!isLoggedin) {
                        Intent intent = new Intent(MainActivity.this, OTPVerification.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                        MainActivity.this.startActivity(intent);
                        finish();
                    }else
                    {
                        Intent intent = new Intent(MainActivity.this, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userid",sharedPreferences.getString(OTPVerify.USERID,"NO_MOBILENUMBER_IN_SHAREDPREFERENCE"));
                        MainActivity.this.startActivity(intent);
                        finish();
                    }


        },100);

    }
}
