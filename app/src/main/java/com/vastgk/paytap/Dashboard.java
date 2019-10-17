package com.vastgk.paytap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.vastgk.paytap.OTPVerify.IS_LOGGED_IN;
import static com.vastgk.paytap.OTPVerify.PREFERENCE_FILE_KEY;
import static com.vastgk.paytap.OTPVerify.USERID;

public class Dashboard extends AppCompatActivity {
    CircularImageView profileImgView;
    TextView tv_fullTransaction;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SaveUserState();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImgView = findViewById(R.id.ProfileImgView);
        profileImgView.setOnClickListener((v) -> {
            logout();

        });

        // --------------------- Bottom Nanigation ---------------------------

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_Dashboard_nfc:
                        Toast.makeText(Dashboard.this, "NfC", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_navigation_Dashboard_scan:
                        Toast.makeText(Dashboard.this, "Scan", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_navigation_Dashboard_bank:
                        Toast.makeText(Dashboard.this, "Bank", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

    private void SaveUserState() {
        SharedPreferences sharedPreferences=getSharedPreferences( PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN,true);
        editor.putString(USERID,getIntent().getStringExtra("mobilenumber"));
        editor.commit();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(USERID, "null");
        editor.commit();
        Intent intent = new Intent(Dashboard.this, OTPVerification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }


}
