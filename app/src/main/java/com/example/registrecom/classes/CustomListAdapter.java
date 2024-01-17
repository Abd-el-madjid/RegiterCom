package com.example.registrecom.classes;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Models.Canaux;
import com.example.registrecom.R;

import java.util.List;

public  class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    private List<Canaux> canauxList;
    private OnItemClickListener onItemClickListener;

    public CustomListAdapter(List<Canaux> canauxList, OnItemClickListener onItemClickListener) {
        this.canauxList = canauxList;
        this.onItemClickListener = onItemClickListener;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.canaux_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Canaux currentCanaux = canauxList.get(position);

        if (currentCanaux != null) {
            holder.nameTextView.setText(currentCanaux.getName());
            holder.lastDateTextView.setText(currentCanaux.getLastDateMessage());

            // Set click listener on the item view using lambda expression
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(currentCanaux.getCanauxId(),currentCanaux.getName()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String canauxId,String canauxName);
    }

    @Override
    public int getItemCount() {
        return canauxList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView lastDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textChannelName);
            lastDateTextView = itemView.findViewById(R.id.textChannelTime);
        }
    }

    public void updateData(List<Canaux> newList) {
        canauxList.clear();
        canauxList.addAll(newList);
        notifyDataSetChanged();
    }

}