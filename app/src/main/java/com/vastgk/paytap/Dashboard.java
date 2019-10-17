package com.vastgk.paytap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {
    CircleImageView profileImgView;
    TextView tv_fullTransaction;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImgView=findViewById(R.id.ProfileImgView);
        profileImgView.setOnClickListener((v )->{


        });

        // --------------------- Bottom Nanigation ---------------------------

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
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

    public static class Registration extends AppCompatActivity {


        Button btn_act_registration;
        EditText mobileNumberEditText, Name_editTxt;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);
            mobileNumberEditText = findViewById(R.id.OTPVERIFY_mobileEditTxt);
            Name_editTxt = findViewById(R.id.OTPVERIFY_ediText_Name);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            mobileNumberEditText.setText(getIntent().getStringExtra("mobilenumber").toString());
            btn_act_registration = findViewById(R.id.btn_act_registration);
            Name_editTxt.requestFocus();
            btn_act_registration.setOnClickListener((view) -> {
                if (Name_editTxt.getText().toString().isEmpty()) {
                    Name_editTxt.setError("Enter The Name First");
                    return;

                }
                String url = "http://api.nixbymedia.com/paytap/users_add.php?username=" + mobileNumberEditText.getText() + "&name=" + Name_editTxt.getText();
                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                    try {
                        JSONObject jo = new JSONObject(response);
                        Toast.makeText(this, "Registering The user Now " + response, Toast.LENGTH_SHORT).show();
                        //user is added Successfully and now goto OTP verify Class
                        Intent intent = new Intent(Registration.this, OTPVerify.class);
                        intent.putExtra("mobilenumber", getIntent().getStringExtra("mobilenumber"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("REGSITRATOIN", "onCreate: " + e.getLocalizedMessage());
                    }


                }, error -> {

                    Log.e("REGISTRATION", "onCreate:" + error.getLocalizedMessage());
                });

                requestQueue.add(request);


            });

        }
    }
}
