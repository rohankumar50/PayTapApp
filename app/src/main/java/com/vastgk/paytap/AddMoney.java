package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class AddMoney extends AppCompatActivity {
Button loadMoneybtn;
EditText amount;
String username;
String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        loadMoneybtn=findViewById(R.id.addmoney_btn_load);
        amount=findViewById(R.id.addmoney_amount_et);
        username=getSharedPreferences(OTPVerify.PREFERENCE_FILE_KEY,MODE_PRIVATE).getString(OTPVerify.USERID,"null");

        loadMoneybtn.setOnClickListener(v->{
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            if (!amount.getText().toString().isEmpty()&& Integer.parseInt(amount.getText().toString())>0)
            {
                url=String.format("http://api.nixbymedia.com/paytap/transactions_add.php?username=%s&amount=%s&type=%s&vendorid=%s&itemid=%s",
                        username,amount.getText().toString(),"credit","ven007","Load Money"

                        );


                StringRequest request=new StringRequest(Request.Method.GET,url,response -> {
                    try {
                        JSONObject jo=new JSONObject(response);
                        String rs=jo.getString("response");
                        if (rs.equals("Transaction Added"))
                        {
                            MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.payment_unlock);
                            mediaPlayer.start();
                            Toast.makeText(this, "Amount Added Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AddMoney.this,Dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else
                        {
                            Toast.makeText(this, "Failed To load money", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                },error -> {
                    Toast.makeText(this, "Failed To load money", Toast.LENGTH_SHORT).show();
                    finish();



                });
                requestQueue.add(request);

            }else
            {
                amount.setError("Enter the amount First");
            }
        });


    }
}
