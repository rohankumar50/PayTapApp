package com.vastgk.paytap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    ArrayList<TransactionsModel> list;

    public TransactionsAdapter(ArrayList<TransactionsModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_cardview,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionsModel model=list.get(position);
        holder.type.setText(model.getType());
        holder.id.setText(model.getId());
        holder.time.setText(model.getTime());
        holder.amount.setText(model.getAmount());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView amount,time,id,type;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.transaction_cardView_paymentAmount);
            time=itemView.findViewById(R.id.transaction_cardView_paymentTime);
            id=itemView.findViewById(R.id.transaction_cardView_paymentID);
            type=itemView.findViewById(R.id.transaction_cardView_paymentType);
        }
    }
}
