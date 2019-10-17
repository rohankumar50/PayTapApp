package com.vastgk.paytap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

public  class Registration extends AppCompatActivity {


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