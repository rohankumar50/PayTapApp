package com.vastgk.paytap;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    ArrayList<TransactionsModel> list;
int limit;
    public TransactionsAdapter(ArrayList<TransactionsModel> list, int i) {
        this.list = list;
        Collections.reverse(this.list);
limit=i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_cardview,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionsModel model=list.get(position);
        holder.id.setText("ID: " +model.getId());
        holder.time.setText(" "+model.getTime());
        if (model.getType().equals("credit")) {
            holder.type.setImageResource(R.drawable.ic_receive);
            holder.amount.setText(" + \u20b9"+ model.getAmount());

            holder.amount.setTextColor(Color.rgb(00,100,0));
        }else
        {            holder.type.setImageResource(R.drawable.ic_sent);
            holder.amount.setText(" - \u20b9"+ model.getAmount());

            holder.amount.setTextColor(Color.rgb(255,0,0));

        }

    }

    @Override
    public int getItemCount() {
        return limit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView amount,time,id;
        ImageView type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.transaction_cardView_paymentAmount);
            time=itemView.findViewById(R.id.transaction_cardView_paymentTime);
            id=itemView.findViewById(R.id.transaction_cardView_paymentID);
            type=itemView.findViewById(R.id.transaction_cardView_paymentType);
        }
    }
}
