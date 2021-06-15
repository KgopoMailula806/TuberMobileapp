package com.tuber_mobile_application.helper.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuber_mobile_application.R;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>
{
    private OnItemClickListener onItemClickListener;
    ArrayList<RequestItem> requestItems;

    public interface OnItemClickListener{
        void onPathClick(int pos);
    }

    public static class  RequestViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;
        private TextView number;
        private TextView date;
        private TextView startDate;
        private TextView endDate;
        private TextView location;
        private TextView periods;
        private TextView module;
        private TextView reason;
        private ImageView path;

        public RequestViewHolder(@NonNull View itemView, final RequestAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.req_name);
            number = itemView.findViewById(R.id.req_number);
            date = itemView.findViewById(R.id.req_date);
            endDate = itemView.findViewById(R.id.req_end);
            startDate = itemView.findViewById(R.id.req_start);
            location = itemView.findViewById(R.id.req_location);
            periods = itemView.findViewById(R.id.req_periods);
            module = itemView.findViewById(R.id.req_module);
            reason = itemView.findViewById(R.id.reason);
            path = itemView.findViewById(R.id.req_route);

            path.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onPathClick(pos);
                        }
                    }
                }
            });
        }
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }
    public RequestAdapter(ArrayList<RequestItem> requestItems){
        this.requestItems = requestItems;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item,parent,false);
        return new RequestViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.RequestViewHolder holder, int position)
    {
        RequestItem currentItem = requestItems.get(position);

        holder.name.setText(currentItem.getName());
        holder.module.setText(currentItem.getModule());
        holder.location.setText(currentItem.getLocation());
        holder.reason.setText(currentItem.getReason());
        holder.startDate.setText(currentItem.getStartDate());
        holder.endDate.setText(currentItem.getEndDate());
        holder.number.setText(currentItem.getNumber());
        holder.date.setText(currentItem.getDate());
        holder.periods.setText(currentItem.getPeriods());

    }

    @Override
    public int getItemCount() {
        return requestItems.size();
    }
}
