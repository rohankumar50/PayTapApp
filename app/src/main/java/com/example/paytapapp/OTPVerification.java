package com.example.paytapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OTPVerification extends AppCompatActivity {

    Button btn_otpverifiction_login;
    TextView tv_otpverivication_registerhere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        btn_otpverifiction_login = findViewById(R.id.btn_otpverfication_login);
        tv_otpverivication_registerhere = findViewById(R.id.tv_otpverification_registerhere);

        btn_otpverifiction_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPVerification.this,OTPVerify.class);
                startActivity(intent);
            }
        });
        tv_otpverivication_registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPVerification.this,Registration.class);
                startActivity(intent);
            }
        });
    }
}
