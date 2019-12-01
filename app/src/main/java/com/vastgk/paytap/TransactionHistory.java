package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vastgk.paytap.OTPVerify.PREFERENCE_FILE_KEY;
import static com.vastgk.paytap.OTPVerify.USERID;

public class TransactionHistory extends AppCompatActivity {
    private static final String TAG = "DEBUGTRANSACTIONS";
    RecyclerView transactionsRecyclerView;
ArrayList<TransactionsModel> transactionlist=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        transactionsRecyclerView=findViewById(R.id.transaction_history_recyclerView);
        fetchrecentTransactions();
    }

    private void fetchrecentTransactions() {
        Toast.makeText(this, "Fetching Transactions", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Loading Transactions", Toast.LENGTH_SHORT).show();
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
        TransactionsAdapter adapter=new TransactionsAdapter(list, list.size());
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       transactionsRecyclerView.setHasFixedSize(true);
        transactionsRecyclerView.setAdapter(adapter);
    }
}
