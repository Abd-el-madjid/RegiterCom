package com.example.registrecom.classes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.VersionVH> {

    private List<notif> notifList;
    private DatabaseReference databaseReference;

    public NotifAdapter(List<notif> notifList) {
        this.notifList = notifList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification");
    }

    @NonNull
    @Override
    public NotifAdapter.VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new VersionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifAdapter.VersionVH holder, int position) {
        notif notif = notifList.get(position);
        holder.title.setText(notif.getTitle());
        holder.date.setText(notif.getFormattedDateString());
        holder.contenu.setText(notif.getContenu());
        boolean isExpendable = notif.isExpendable();
        holder.expandableLayout.setVisibility(isExpendable ? View.VISIBLE : View.GONE);

        // Check if the notification has been read (status is false) and handle the click event
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notif.setExpendable(!notif.isExpendable());
                notifyDataSetChanged();
                // Update the status field in Firebase
                updateNotificationStatus(notif.getId(), notif.isExpendable());
            }
        });
    }


    private void updateNotificationStatus(String notificationId, boolean status) {
        DatabaseReference notificationRef = databaseReference.child(notificationId).child("status");

        // Read the current status
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean currentStatus = dataSnapshot.getValue(Boolean.class);

                    // Check if the current status is false before updating
                    if (!currentStatus) {
                        // Update the status to true
                        DatabaseReference notificationRef = databaseReference.child(notificationId).child("status");

                        notificationRef.setValue(status);
                        Log.d("MyTag", "Notification status updated to true for ID: " + notificationId);
                    } else {
                        Log.d("MyTag", "Notification status is already true for ID: " + notificationId);
                    }
                } else {
                    Log.d("MyTag", "Notification ID not found: " + notificationId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyTag", "Database error: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return notifList.size();
    }

    public class VersionVH extends RecyclerView.ViewHolder {

        TextView title, date, contenu;
        RelativeLayout expandableLayout;
        ImageView arrow;

        public VersionVH(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            contenu = itemView.findViewById(R.id.contenu);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            arrow = itemView.findViewById(R.id.arrow);

            CardView cardView = (CardView) itemView;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notif notif = notifList.get(getAdapterPosition());
                    notif.setExpendable(!notif.isExpendable());

                    // Update the status field in Firebase
                    updateNotificationStatus(notif.getId(), notif.isExpendable());
                }
            });
        }
    }
    public void updateData(List<notif> newList) {
        notifList.clear();
        notifList.addAll(newList);
        notifyDataSetChanged();
    }
}
