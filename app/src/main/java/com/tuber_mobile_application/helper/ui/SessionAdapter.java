package com.tuber_mobile_application.helper.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuber_mobile_application.R;
import com.tuber_mobile_application.helper.HelperMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder>
{
    private ArrayList<SessionItem> sessionItems;
    private OnItemClickListener onItemClickListener;
    private String userType;

    public interface OnItemClickListener{
        void onItemClick(int pos);
        void onPlayClick(int pos);
        void onCancelClick(int pos);
        void onPathClick(int pos);
    }

    public SessionAdapter(ArrayList<SessionItem> sessionItems, String userType){
        this.sessionItems = sessionItems;
        this.userType = userType;
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtModuleName, txtTutorName, txtPeriods, txtVenue, txtTime, txtDate;
        public ImageView imgStart, imgEnd, imgRoute;

        public SessionViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener)
        {
            super(itemView);
            txtDate = itemView.findViewById(R.id.sess_date);
            txtModuleName = itemView.findViewById(R.id.sess_module_name);
            txtPeriods = itemView.findViewById(R.id.sess_number);
            txtTutorName = itemView.findViewById(R.id.sess_tutor_name);
            //txtTime = itemView.findViewById(R.id.sess_time);
            txtVenue = itemView.findViewById(R.id.sess_venue);

            imgStart = itemView.findViewById(R.id.start_session);
            imgEnd = itemView.findViewById(R.id.cancel_session);
            imgRoute = itemView.findViewById(R.id.sess_route);


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

            // click listener for play button
            imgStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onPlayClick(pos);
                        }
                    }
                }
            });

            // click listener for cancel button
            imgEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onCancelClick(pos);
                        }
                    }
                }
            });

            imgRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null)
                    {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onPathClick(pos);
                        }
                    }
                }
            });

        }
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_layout_format,parent,false);
        return new SessionViewHolder(v,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position)
    {
        SessionItem currentSession = sessionItems.get(position);
        holder.txtVenue.setText(currentSession.getVenue());
        //holder.txtTime.setText(currentSession.getTime());
        holder.txtTutorName.setText(currentSession.getTutorName());
        holder.txtPeriods.setText(currentSession.getPeriods());
        holder.txtDate.setText(currentSession.getDate());
        holder.txtModuleName.setText(currentSession.getModuleName());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); // date format in use
            Calendar calendar = Calendar.getInstance();

            String currentDate = dateFormat.format(calendar.getTime());
            Date convertDate = HelperMethods.parseDate(currentSession.getDate());
            holder.imgStart.setVisibility(View.VISIBLE);
           /* if(convertDate != null)
            {
                String givenDate = dateFormat.format(convertDate);
                // check if the session is today, if not hide the play button
                if(currentDate.equals(givenDate))
                    holder.imgStart.setVisibility(View.VISIBLE);
                else
                    holder.imgStart.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.imgStart.setVisibility(View.INVISIBLE);
        }*/


    }

    @Override
    public int getItemCount() {
        return sessionItems.size();
    }


}
