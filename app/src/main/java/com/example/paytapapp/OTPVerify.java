package com.example.paytapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OTPVerify extends AppCompatActivity {

    Button btn_otpverify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        btn_otpverify = findViewById(R.id.btn_act_otpverfy);


        btn_otpverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPVerify.this,Dashboard.class);
                startActivity(intent);
            }
        });
    }
}
