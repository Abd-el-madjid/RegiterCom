package com.example.registrecom.classes;

// CustomListAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.registrecom.Models.Demandes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

// CustomListAdapter.java

public class CustomListDAdapter extends RecyclerView.Adapter<CustomListDAdapter.ViewHolder> {
    private Context context;
    private List<ListItem> itemList;
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(String id_demande);
    }


    public CustomListDAdapter(Context context, List<ListItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.demande_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem currentItem = itemList.get(position);

        if (currentItem != null) {
            holder.nameTextView.setText(currentItem.getName());
            holder.timeTextView.setText(currentItem.getTime());
            holder.etatImageView.setImageResource(currentItem.getEtatImageResource());

            // Set the item ID_demande as a tag to the itemView
            holder.itemView.setTag(currentItem.getIdDemande());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView timeTextView;
        ImageView etatImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.TimeTextView);
            etatImageView = itemView.findViewById(R.id.etatImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id_demande = (String) view.getTag(); // Retrieve the ID_demande from the itemView tag
                    if (onItemClickListener != null && id_demande != null) {
                        onItemClickListener.onItemClick(id_demande);
                    }
                }
            });
        }
    }

    public void updateData(List<ListItem> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }
}
