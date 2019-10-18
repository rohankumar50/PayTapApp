package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class OTPVerification extends AppCompatActivity {
    private static final String TAG ="OTPDEBUG" ;
    String url="http://api.nixbymedia.com/paytap/users_single.php?username=";
    Button btn_otpverifiction_login;
    EditText mobileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        mobileNumber=findViewById(R.id.editxt_otp);
        btn_otpverifiction_login = findViewById(R.id.btn_otpverfication_login);
        btn_otpverifiction_login.setEnabled(false);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
mobileNumber.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count==10)
        {
            btn_otpverifiction_login.performClick();
        }
        if (count<10)
            btn_otpverifiction_login.setEnabled(false);
        else
            btn_otpverifiction_login.setEnabled(true);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
});
        btn_otpverifiction_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobileNumber.getText().toString().isEmpty() && mobileNumber.getText().toString().length()==10)
                {
                    btn_otpverifiction_login.setEnabled(false);
                    Log.d(TAG, "onClick: "+url);
                    StringRequest request=new StringRequest(Request.Method.GET,url+mobileNumber.getText().toString(),response -> {
                        try {
                            JSONObject jo=new JSONObject(response);
                            Log.d(TAG, "onClick: "+jo.toString());

                            if (jo.has("error"))
                            {
                                Toast.makeText(OTPVerification.this, "User not found", Toast.LENGTH_SHORT).show();
                                    //Register the user
                                    Intent intent=new Intent(OTPVerification.this, Registration.class);
                                    intent.putExtra("mobilenumber",mobileNumber.getText().toString());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                            }else if (jo.has("user"))
                            {
                               // Toast.makeText(OTPVerification.this, ""+jo.getJSONArray("user").toString(), Toast.LENGTH_SHORT).show();
                            //Send Otp to Login
                                Intent intent=new Intent(OTPVerification.this, OTPVerify.class);
                                intent.putExtra("mobilenumber",mobileNumber.getText().toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onClick: "+e.getLocalizedMessage() );
                        }

                    },error -> {
                        Toast.makeText(OTPVerification.this, "Error Fetching request", Toast.LENGTH_SHORT).show();
                btn_otpverifiction_login.setEnabled(true);
                    });
                    requestQueue.add(request);

                }else
                {if (mobileNumber.getText().toString().length()<10)
                    mobileNumber.setError("Enter all the digits of Mobile Number");

                }


            }
        });

    }
}
