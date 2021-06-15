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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
{
    private ArrayList<NotificationItem> notificationItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public static class  NotificationViewHolder extends RecyclerView.ViewHolder
    {
        //public ImageView imageView;
        public TextView txtTitle;
        public TextView txtBody;
        public TextView txtTime;

        public NotificationViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            //imageView = itemView.findViewById(R.id.img_card_module_icon);
            txtTitle = itemView.findViewById(R.id.notification_title);
            txtBody = itemView.findViewById(R.id.notification_body);
            txtTime = itemView.findViewById(R.id.notification_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    public NotificationAdapter(ArrayList<NotificationItem> notificationItems){
        this.notificationItems = notificationItems;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item,parent,false);
        NotificationViewHolder moduleViewHolder = new NotificationViewHolder(v,onItemClickListener);
        return moduleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        NotificationItem currentItem = notificationItems.get(position);
        holder.txtTitle.setText(currentItem.getNotificationTitle());
        holder.txtBody.setText(currentItem.getNotificationBody());
        holder.txtTime.setText(currentItem.getNotificationTime());

    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }
}
