package com.example.registrecom.Admin.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Admin.classes.MessagesAdapterAdmin;
import com.example.registrecom.Models.Canaux;
import com.example.registrecom.Models.Messages;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.Models.Personne;
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

public class chatAdminActivity extends AppCompatActivity {
    private Context context;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth auth;
    private DatabaseReference personneRef, demandesRef, canauxRef, messagesRef,adminRef,notificationRef;
    private FirebaseUser currentUser;
    private long userId, currentUserIdAdmin;
    private TextView k, discutionname;
    private EditText chat_message_input;
    private ImageButton message_send_btn;
    private String outgoing_message, canauxId, canauxName;
    private RecyclerView recyclerView;
    private ImageButton back_btn;
    private MessagesAdapterAdmin messagesAdapterAdmin;
   private Canaux channelItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_admin);

        databaseHelper = new DatabaseHelper();

        auth = FirebaseAuth.getInstance();
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        canauxRef = FirebaseDatabase.getInstance().getReference().child("Canaux");
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        notificationRef  = FirebaseDatabase.getInstance().getReference().child("Notification");
        currentUser = auth.getCurrentUser();



        // Initialize RecyclerView
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();


                                    k = findViewById(R.id.k);
                                    Intent intent = getIntent();
                                    if (intent != null) {
                                        canauxId = intent.getStringExtra("canauxId");
                                        canauxName = intent.getStringExtra("canauxName");
                                        discutionname = findViewById(R.id.discutionname);
                                        discutionname.setText(canauxName);

                                        if (canauxId != null) {
                                            canauxRef.orderByChild("canauxId").equalTo(canauxId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot canauxSnapshot) {
                                                    for (DataSnapshot channelSnapshot : canauxSnapshot.getChildren()) {
                                                        channelItem = channelSnapshot.getValue(Canaux.class);
                                                    }

                                                    if (channelItem != null) {
                                                        // Now channelItem is not null, you can access its properties safely
                                                        String channelId = channelItem.getCanauxId();
                                                        String chaneluser = String.valueOf(channelItem.getUserId());

                                                        Log.d("MyTag", "Channel ID: " + canauxId);
                                                        // Retrieve messages after userId is obtained
                                                        if (!chaneluser.isEmpty() && !channelId.isEmpty()){
                                                            retrieveMessages(channelId, chaneluser);
                                                            Log.d("MyTag", "kholst User ID : " + currentUserId);

                                                        }
                                                        // Continue with your logic here...
                                                    } else {
                                                        // Handle the case where channelItem is null
                                                        showMessage("Channel information is null");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    showMessage("Error fetching Canaux information");
                                                    Log.e("MyTag", "Error fetching Canaux information", databaseError.toException());
                                                }
                                            });


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


                                                // Create a Messages object with the necessary information
                                                Messages message = new Messages(
                                                        currentUserId,  // senderID
                                                        canauxId,
                                                        outgoing_message, // content
                                                        String.valueOf(channelItem.getUserId())// timestamp
                                                );

                                                // Use the addMessages method from DatabaseHelper to add the message to Firebase
                                                databaseHelper.addMessages(message, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            String notificationId = notificationRef.push().getKey();
                                                            String title = getString(R.string.notification_title_new_message_received);
                                                            String content = getString(R.string.notification_new_message_received);

                                                            long personId = channelItem.getUserId();
                                                            Date currentDate = new Date();
                                                            // Create a Notification object
                                                            Notification newNotification = new Notification(title, content, personId, currentDate);
                                                            notificationRef.child(notificationId).setValue(newNotification);


                                                            Log.d(TAG, "message added successfully with ID emeteur: " + String.valueOf(message.getIdEmeteur()));
                                                            String canauxId = message.getIdCanaux();
                                                            String currentTime = getCurrentTime();
                                                            // Update the lastDateMessage of the specific Canaux with the latest message time
                                                            canauxRef.child(canauxId).child("lastDateMessage").setValue(currentTime);

                                                            int lastItemPosition = messagesAdapterAdmin.getItemCount() - 1;
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

                    // Assuming currentUserId is a String, convert it to long before passing
                    personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
                    personneRef.orderByChild("id").equalTo(channelItem.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                                    Personne personne = personneSnapshot.getValue(Personne.class);
                                    if (personne != null) {
                                        Log.d("MyTag", "Personne object retrieved: " + personne.toString());
                                        Intent canaux = new Intent(chatAdminActivity.this, CanuxAdminActivity.class);
                                        canaux.putExtra("Personne",personne);
                                        canaux.putExtra("IdUser", channelItem.getUserId());


                                        startActivity(canaux);
                                        finish();

                                    }
               }
                            } else {
                                Log.d("MyTag", "User Information Not Found for Email: " );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MyTag", "Error fetching user information", databaseError.toException());
                        }
                    });


                }
            });







        } else {
            showMessage("User not found");
        }


    }
    private void sendMessage(){

    }
    private void retrieveMessages(String idCanaux, String currentUserId) {
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
                messagesAdapterAdmin = new MessagesAdapterAdmin(chatAdminActivity.this, currentUserId);
                messagesAdapterAdmin.setMessagesList(messagesList);
                recyclerView.setAdapter(messagesAdapterAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return dateFormat.format(new Date());
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
