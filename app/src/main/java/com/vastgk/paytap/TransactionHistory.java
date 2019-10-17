package com.vastgk.paytap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
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
        String mobile=sharedPreferences.getString(USERID,"null");
        StringRequest request=new StringRequest(Request.Method.GET,url+mobile, response->{
            try {
                transactionlist.clear();
                JSONObject jsonObject=new JSONObject(response);
                JSONArray transactions=jsonObject.getJSONArray("transactions");
                int limit=transactions.length();
                for (int i=0;i<limit;i++)
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
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       transactionsRecyclerView.setHasFixedSize(true);
        transactionsRecyclerView.setAdapter(adapter);
    }
}
