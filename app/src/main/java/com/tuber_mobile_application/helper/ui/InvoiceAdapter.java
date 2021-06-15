package com.tuber_mobile_application.helper.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuber_mobile_application.R;

import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>
{
    ArrayList<InvoiceItem> invoiceItems;
    public static class  InvoiceViewHolder extends RecyclerView.ViewHolder
    {
        //public ImageView imageView;
        public TextView txtAmount;
        public TextView txtDate;
        public TextView txtDescription;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAmount = itemView.findViewById(R.id.invoice_amount);
            txtDate = itemView.findViewById(R.id.invoice_date);
            txtDescription = itemView.findViewById(R.id.invoice_desc);
        }
    }

    public InvoiceAdapter(ArrayList<InvoiceItem> invoiceItems){
        this.invoiceItems = invoiceItems;
    }

    @NonNull
    @Override
    public InvoiceAdapter.InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item,parent,false);
        InvoiceAdapter.InvoiceViewHolder invoiceViewHolder = new InvoiceAdapter.InvoiceViewHolder(v);
        return invoiceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.InvoiceViewHolder holder, int position)
    {
        InvoiceItem currentItem = invoiceItems.get(position);

        holder.txtAmount.setText(currentItem.getAmount());
        holder.txtDate.setText(currentItem.getDate());
        holder.txtDescription.setText(currentItem.getDescription());

    }

    @Override
    public int getItemCount() {
        return invoiceItems.size();
    }
}
