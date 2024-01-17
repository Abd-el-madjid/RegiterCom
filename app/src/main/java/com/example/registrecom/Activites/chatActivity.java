package com.example.registrecom.Activites;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Models.Messages;
import com.example.registrecom.R;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.example.registrecom.classes.MessagesAdapter;
import com.example.registrecom.classes.MessagesItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class chatActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private DatabaseReference personneRef, demandesRef, canauxRef, messagesRef;
    private FirebaseUser currentUser;
    private long userId, currentUserId;
    private TextView k, discutionname;
    private EditText chat_message_input;
    private ImageButton message_send_btn;
    private String outgoing_message, canauxId, canauxName;
    private RecyclerView recyclerView;
    private ImageButton back_btn;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseHelper = new DatabaseHelper();

        auth = FirebaseAuth.getInstance();
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        canauxRef = FirebaseDatabase.getInstance().getReference().child("Canaux");
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        currentUser = auth.getCurrentUser();



        // Initialize RecyclerView
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();

            personneRef.orderByChild("email").equalTo(currentUserEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                                    userId = Long.parseLong(personneSnapshot.getKey());

                                    k = findViewById(R.id.k);
                                    Intent intent = getIntent();
                                    if (intent != null) {
                                        canauxId = intent.getStringExtra("canauxId");
                                        canauxName = intent.getStringExtra("canauxName");
                                        discutionname = findViewById(R.id.discutionname);
                                        discutionname.setText(canauxName);

                                        if (canauxId != null) {
                                            Log.d("MyTag", "Channel ID: " + canauxId);

                                            // Retrieve messages after userId is obtained
                                            retrieveMessages(canauxId, userId);
                                            Log.d("MyTag", "kholst User ID : " + userId);
                                        }
                                    }

                                    message_send_btn = findViewById(R.id.message_send_btn);
                                    chat_message_input = findViewById(R.id.chat_message_input);
                                    message_send_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            outgoing_message = chat_message_input.getText().toString();

                                            if (!outgoing_message.isEmpty()) {
                                                chat_message_input.setText(" ");
                                                currentUserId = userId;

                                                // Create a Messages object with the necessary information
                                                Messages message = new Messages(
                                                        String.valueOf(userId),  // senderID
                                                        canauxId,
                                                        outgoing_message // content
                                                        // timestamp
                                                );

                                                // Use the addMessages method from DatabaseHelper to add the message to Firebase
                                                databaseHelper.addMessages(message, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "message added successfully with ID emeteur: " + String.valueOf(message.getIdEmeteur()));
                                                            String canauxId = message.getIdCanaux();
                                                            String currentTime = getCurrentTime();
                                                            // Update the lastDateMessage of the specific Canaux with the latest message time
                                                            canauxRef.child(canauxId).child("lastDateMessage").setValue(currentTime);
                                                            // Message added successfully
                                                            // Scroll to the last item in the RecyclerView
                                                            int lastItemPosition = messagesAdapter.getItemCount() - 1;
                                                            recyclerView.scrollToPosition(lastItemPosition);
                                                        } else {
                                                            // Error adding message
                                                            showMessage("Error sending message. Please try again.");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    back_btn = findViewById(R.id.back_btn);
                                    back_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent canaux = new Intent(chatActivity.this, canauxActivity.class);
                                            startActivity(canaux);
                                            finish();
                                        }
                                    });




                                }
                            } else {
                                showMessage("User information not found");
                                Log.d("MyTag", "User Information Not Found for Email: " + currentUserEmail);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showMessage("Error fetching user information");
                            Log.e("MyTag", "Error fetching user information", databaseError.toException());
                        }
                    });
        } else {
            showMessage("User not found");
        }


    }
private void sendMessage(){

}
    private void retrieveMessages(String idCanaux, long currentUserId) {
        messagesRef.orderByChild("idCanaux").equalTo(idCanaux).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MessagesItem> messagesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Messages message = snapshot.getValue(Messages.class);
                    if (message != null) {
                        // Create MessagesItem from Messages
                        MessagesItem messagesItem = new MessagesItem(
                                String.valueOf(message.getIdEmeteur()),
                                message.getTimestamp(),
                                message.getContenu()
                        );
                        messagesList.add(messagesItem);
                    }
                }
                messagesAdapter = new MessagesAdapter(chatActivity.this, String.valueOf(userId));
                messagesAdapter.setMessagesList(messagesList);
                recyclerView.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void retrieveUserId() {
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            Log.d("MyTag", "Current User Email: " + currentUserEmail);

            personneRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            userId = Long.parseLong(personneSnapshot.getKey());
                            Log.d("MyTag", "User ID found: " + userId);
                        }
                    } else {
                        showMessage("User information not found");
                        Log.d("MyTag", "User Information Not Found for Email: " + currentUserEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showMessage("Error fetching user information");
                    Log.e("MyTag", "Error fetching user information", databaseError.toException());
                }
            });
        } else {
            showMessage("User not found");
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return dateFormat.format(new Date());
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
