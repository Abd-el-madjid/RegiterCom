package com.example.registrecom.Admin.classes;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;
import com.example.registrecom.classes.MessagesItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapterAdmin extends RecyclerView.Adapter<MessagesAdapterAdmin.ViewHolder> {
    private DatabaseReference personneRef, demandesRef,documentsRef,adminRef,notificationRef;
    private List<MessagesItem> messagesList = new ArrayList<>();
    private String currentUserId; // Added field for current user ID
    private Context context;

    // Constructor to set the current user ID
    public MessagesAdapterAdmin(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    public void setMessagesList(List<MessagesItem> messagesList) {
        this.messagesList = messagesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessagesItem message = messagesList.get(position);
        holder.senderTextView.setText(message.getsendeur());
        holder.timeTextView.setText(message.getTime());
        holder.contentTextView.setText(message.getcontenu());

        // Test user ID with idEmeteur
        String idEmeteur = message.getsendeur();
        Log.d("MyTag", "onBindViewHolder: "+idEmeteur +" ------ "+currentUserId);
        if (currentUserId.equals(idEmeteur) ) {

            adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");
            personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
            personneRef.orderByChild("id").equalTo(Long.parseLong(currentUserId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            String userId = personneSnapshot.getKey();
                            Log.d("MyTag", "User ID found: " + userId);

                            String  nom = personneSnapshot.child("nomUtilisateur").getValue(String.class);
                            // String   prenom = personneSnapshot.child("prenom").getValue(String.class);
                            holder.senderTextView.setText(nom);
                            holder.senderTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        }
                    } else {
                        Log.d("MyTag", "User Information Not Found for Email: " + idEmeteur);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MyTag", "Error fetching user information", databaseError.toException());
                }
            });

        }
        else {
            holder.senderTextView.setText("You");


        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;
        TextView timeTextView;
        TextView contentTextView;

        ViewHolder(View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.textSender);
            timeTextView = itemView.findViewById(R.id.textTimestamp);
            contentTextView = itemView.findViewById(R.id.textMessage);
        }
    }
}
