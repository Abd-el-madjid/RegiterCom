package com.example.registrecom.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<Profile> profileList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    public ProfileAdapter(Context context, List<Profile> profileList, OnItemClickListener listener) {
        this.context = context;
        this.profileList = profileList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile currentProfile = profileList.get(position);

        if (currentProfile != null) {
            holder.iconImageView.setText(currentProfile.getLettername());
            holder.nameTextView.setText(currentProfile.getUsername());
            holder.discriptionTextView.setText(currentProfile.getEmail());
            holder.active.setBackgroundResource(currentProfile.getState() ? R.drawable.isactivebg : R.drawable.isdisactive);

            // Set the item ID as a tag to the itemView
            holder.itemView.setTag(currentProfile.getId());
        }
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iconImageView;
        TextView nameTextView;
        TextView discriptionTextView;
        ImageView active;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            discriptionTextView = itemView.findViewById(R.id.discriptionTextView);
            active = itemView.findViewById(R.id.active);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = (String) view.getTag(); // Retrieve the ID from the itemView tag
                    if (onItemClickListener != null && id != null) {
                        onItemClickListener.onItemClick(id);
                    }
                }
            });
        }
    }

    public void updateData(List<Profile> newList) {
        profileList.clear();
        profileList.addAll(newList);
        notifyDataSetChanged();
    }
}
