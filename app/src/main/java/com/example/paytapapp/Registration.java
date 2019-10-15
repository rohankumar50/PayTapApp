package com.example.paytapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Registration extends AppCompatActivity {


    Button btn_act_registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btn_act_registration = findViewById(R.id.btn_act_registration);

        btn_act_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this,Dashboard.class);
                startActivity(intent);
            }
        });

    }
}
