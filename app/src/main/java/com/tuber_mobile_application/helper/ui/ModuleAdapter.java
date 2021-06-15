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

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>
{
    private ArrayList<MyModuleItem> myModuleItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int pos);
        void onActionClick(int pos);
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public static class  ModuleViewHolder extends RecyclerView.ViewHolder
    {
        //public ImageView imageView;
        public TextView txtModuleName;
        public TextView txtModuleCode;
        public ImageView imgActionView;

        public ModuleViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            //imageView = itemView.findViewById(R.id.img_card_module_icon);
            txtModuleCode = itemView.findViewById(R.id.txt_card_module_code);
            txtModuleName = itemView.findViewById(R.id.txt_card_module_name);
            imgActionView = itemView.findViewById(R.id.img_card_action);

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

            imgActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onActionClick(pos);
                        }
                    }
                }
            });
        }
    }

    public ModuleAdapter(ArrayList<MyModuleItem> myModuleItems){
        this.myModuleItems = myModuleItems;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_module_item,parent,false);
        ModuleViewHolder moduleViewHolder = new ModuleViewHolder(v,onItemClickListener);
        return moduleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position)
    {
        MyModuleItem currentItem = myModuleItems.get(position);
        //holder.imageView.setImageResource(currentItem.getImgId());
        holder.txtModuleName.setText(currentItem.getModuleName());
        holder.txtModuleCode.setText(currentItem.getModuleCode());
        holder.imgActionView.setImageResource(currentItem.getImgActionId());

    }

    @Override
    public int getItemCount() {
        return myModuleItems.size();
    }
}
