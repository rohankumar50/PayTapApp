package com.vastgk.paytap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.vastgk.paytap.OTPVerify.IS_LOGGED_IN;
import static com.vastgk.paytap.OTPVerify.PREFERENCE_FILE_KEY;
import static com.vastgk.paytap.OTPVerify.USERID;

public class Dashboard extends AppCompatActivity {
    CircularImageView profileImgView;
    TextView tv_fullTransaction;
    BottomNavigationView bottomNavigationView;
    public static int NFC_Request_code=7;
    private RecyclerView recentTransactions;
    ArrayList<TransactionsModel> transactionlist=new ArrayList<>();
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
        recentTransactions=findViewById(R.id.dashboard_recyclerView);


        ((TextView)findViewById(R.id.dashboard_showTransactionHistory)).setOnClickListener(v->{
            Intent intent=new Intent(Dashboard.this,TransactionHistory.class);
            startActivity(intent);
        });

        fetchrecentTransactions();

        // --------------------- Bottom Nanigation ---------------------------

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_Dashboard_nfc:
                        Toast.makeText(Dashboard.this, "NfC", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Dashboard.this,NfcRead.class);
                        startActivityForResult(intent,NFC_Request_code);

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

    private void fetchrecentTransactions() {
        Toast.makeText(this, "Fetching Transactions", Toast.LENGTH_SHORT).show();
        RequestQueue queue=Volley.newRequestQueue(this);
        String url="http://api.nixbymedia.com/paytap/transactions_all.php?username=";
        SharedPreferences sharedPreferences=getSharedPreferences(PREFERENCE_FILE_KEY,MODE_PRIVATE);
        String mobile=sharedPreferences.getString(USERID,"null");
        StringRequest request=new StringRequest(Request.Method.GET,url+mobile,response->{
            try {

                transactionlist.clear();
                JSONObject jsonObject=new JSONObject(response);
                JSONArray transactions=jsonObject.getJSONArray("transactions");
                int limit=transactions.length()>3?3:transactions.length();
                for (int i=0;i<=limit;i++)
                {
                    JSONObject jo=transactions.getJSONObject(i);
                    TransactionsModel transaction=new TransactionsModel(jo.getString("id"),
                            jo.getString("datetime"),jo.getString("amount"),jo.getString("type"),jo.getString("vendorid"),"PayTap"
                            );
                    transactionlist.add(transaction);
                }

                setRecentTransactions(transactionlist);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        },error -> {
            Toast.makeText(this, "Can't Fetch Transactions", Toast.LENGTH_SHORT).show();
            return;

        });
        queue.add(request);



    }

    private void setRecentTransactions(ArrayList<TransactionsModel> list) {
        TransactionsAdapter adapter=new TransactionsAdapter(list);
        recentTransactions.setLayoutManager(new LinearLayoutManager(this));
        recentTransactions.setHasFixedSize(true);
        recentTransactions.setAdapter(adapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==NFC_Request_code)
        {
            if (requestCode==RESULT_OK)
            {
                //TODO Do something
            }
        }
    }
}
