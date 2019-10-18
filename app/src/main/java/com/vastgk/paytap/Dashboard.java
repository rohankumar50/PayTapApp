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
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.vastgk.paytap.OTPVerify.IS_LOGGED_IN;
import static com.vastgk.paytap.OTPVerify.PREFERENCE_FILE_KEY;
import static com.vastgk.paytap.OTPVerify.USERID;

public class Dashboard extends AppCompatActivity {
    RequestQueue queue;
    EditText amountet;
    CircularImageView profileImgView;
    TextView balancetxtview,nametv;
    BottomNavigationView bottomNavigationView;
    public static int NFC_Request_code=7;
    private RecyclerView recentTransactions;
    private  String mMobileNumber;
    ArrayList<TransactionsModel> transactionlist=new ArrayList<>();
    private String TAG="DEBUGDASHBOARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
amountet=findViewById(R.id.dashborad_amount_pay);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileImgView = findViewById(R.id.ProfileImgView);
        profileImgView.setOnClickListener((v) -> {
            logout();

        });
        mMobileNumber=getSharedPreferences(PREFERENCE_FILE_KEY,MODE_PRIVATE).getString(USERID,"null");
        balancetxtview=findViewById(R.id.dashboard_balance);
        recentTransactions=findViewById(R.id.dashboard_recyclerView);
        nametv=findViewById(R.id.dashboard_name);

        ((TextView)findViewById(R.id.dashboard_showTransactionHistory)).setOnClickListener(v->{
            Intent intent=new Intent(Dashboard.this,TransactionHistory.class);
            startActivity(intent);
        });
        ((Button)findViewById(R.id.dashboard_btn_pay)).setOnClickListener(v -> {


            if (amountet.getText().toString().isEmpty())
            {
                amountet.setError("Enter amount ");
                return;
            }//TODO add Vendor ID and amount to pay
            Intent intent=new Intent(Dashboard.this,NfcRead.class);
            intent.putExtra("paydirect",true);
            intent.putExtra("amount",amountet.getText().toString());
            intent.putExtra("vendorid","ven007");
            amountet.setText("");

            startActivity(intent);

        });

    queue=Volley.newRequestQueue(this);



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

                    case R.id.bottom_navigation_Dashboard_bank:
                        startActivity(new Intent(Dashboard.this,AddMoney.class));
                        break;
                }
                return true;
            }
        });

    }

    private void loadBalance() {

        String url=String.format("http://api.nixbymedia.com/paytap/users_single.php?username=%s",mMobileNumber);
        Log.d("DEBUGDASHBOARD", "loadBalance:url"+url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,url,response -> {
            try {
                JSONObject jo=new JSONObject(response);
                if (jo.has("user"))
                {
                    JSONArray ja=jo.getJSONArray("user");
                    JSONObject jo1=ja.getJSONObject(0);
                    nametv.setText(jo1.getString("name"));
                    Log.d("DEBUGDASHBOARD", "loadBalance: "+jo1.toString());
                    balancetxtview.setText(jo1.getString("amount"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }




        },error -> {
            Toast.makeText(this, "Unable to Fetch Balance", Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
loadBalance();
fetchrecentTransactions();
    }

    private void fetchrecentTransactions() {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="http://api.nixbymedia.com/paytap/transactions_all.php?username=";
        SharedPreferences sharedPreferences=getSharedPreferences(PREFERENCE_FILE_KEY,MODE_PRIVATE);
        String mobile=sharedPreferences.getString(USERID,"null@TranHistory");
        StringRequest request=new StringRequest(Request.Method.GET,url+mobile, response->{
            try {
                transactionlist.clear();
                JSONObject jsonObject=new JSONObject(response);
                Log.d(TAG, "fetchrecentTransactions: "+jsonObject.toString());
                if (jsonObject.has("error")){
                    if (jsonObject.getJSONArray("error").getJSONObject(0).getString("status").equals("404"))
                    {
                        Toast.makeText(this, "No Transaction Exists", Toast.LENGTH_SHORT).show();
                        TransactionsModel td=new TransactionsModel("No transaction Exits For this user ","","","","","");
                        transactionlist.add(td);
                        setRecentTransactions(transactionlist);

                    }}
                Log.d(TAG, "fetchrecentTransactions: Loading Transactions now");
                JSONArray transactions=jsonObject.getJSONArray("transactions");
                Log.d(TAG, "Jsonarray: "+transactions.length());
                int limit=transactions.length();
                for (int i=0;i<limit;i++)
                {
                    JSONObject jo=transactions.getJSONObject(i);
                    Log.d(TAG, "Inside Transactions: "+jo.toString());
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
        int l=list.size()>2?2:list.size();


        TransactionsAdapter adapter=new TransactionsAdapter(list, l);
        recentTransactions.setLayoutManager(new LinearLayoutManager(this));
        recentTransactions.setAdapter(adapter);
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
