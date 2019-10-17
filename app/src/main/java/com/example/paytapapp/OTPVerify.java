package com.example.paytapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTPVerify extends AppCompatActivity {

    Button btn_otpverify;
    EditText otp1,otp2,otp3,otp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        btn_otpverify = findViewById(R.id.btn_act_otpverfy);
        otp1=findViewById(R.id.OTP1);
        otp2=findViewById(R.id.OTP2);
        otp3=findViewById(R.id.OTP3);
        otp4=findViewById(R.id.OTP4);

        editTextSwitcher();

        btn_otpverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPVerify.this, "Clicked on Login", Toast.LENGTH_SHORT).show();
              //  Intent intent = new Intent(OTPVerify.this,Dashboard.class);
              //  startActivity(intent);
            }
        });
    }

    private void editTextSwitcher() {

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp1.getText().toString().length()==1)     //size as per your requirement
                {
                    otp2.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(otp2.getText().toString().length()==1)     //size as per your requirement
                {
                    otp3.requestFocus();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp1.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp3.getText().toString().length()==1)     //size as per your requirement
                {
                    otp4.requestFocus();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            int before;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
before=count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp4.getText().toString().length()==1)     //size as per your requirement
                {
                 btn_otpverify.performClick();
                }
                if(before-count==1)     //size as per your requirement
                {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
}
