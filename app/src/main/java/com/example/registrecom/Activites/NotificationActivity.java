package com.example.registrecom.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Models.Notification;
import com.example.registrecom.R;
import com.example.registrecom.classes.notif;
import com.example.registrecom.classes.NotifAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton  back_btn;
    private Button  clearAllButton;

    private RecyclerView recyclerViewNotifications;
    private NotifAdapter notifAdapter;
    private List<notif> notifList;
    private DatabaseReference notificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        findViewById(R.id.no_notification_panel).setVisibility(View.VISIBLE);

        String  data = getIntent().getStringExtra("data");

         clearAllButton = findViewById(R.id.clrearall);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String currentUserEmail = currentUser.getEmail();

                    DatabaseReference personnesRef = FirebaseDatabase.getInstance().getReference().child("Personne");
                    personnesRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                long userId = snapshot.child("id").getValue(Long.class);
                                clearNotifications(userId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MyTag", "Database error: " + databaseError.getMessage());
                        }
                    });
                }
            }
        });
        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        recyclerViewNotifications = findViewById(R.id.notification_recycler_view);
        notifList = new ArrayList<>();
        notifAdapter = new NotifAdapter(notifList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewNotifications.setLayoutManager(layoutManager);
        recyclerViewNotifications.setAdapter(notifAdapter);

        // Get current user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            DatabaseReference personnesRef = FirebaseDatabase.getInstance().getReference().child("Personne");
            personnesRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        long userId = snapshot.child("id").getValue(Long.class);
                        // Now that you have the user ID, fetch notifications for the user
                        notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notification");





                        notificationsRef.orderByChild("idPersonne").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    notifList.clear();
                                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                                        // Retrieve notification details from Firebase
                                        String id = notificationSnapshot.getKey(); // Assuming the notification ID is the key
                                        String title = notificationSnapshot.child("title").getValue(String.class);
                                        String contenu = notificationSnapshot.child("contenu").getValue(String.class);
                                        Date dateCreation = notificationSnapshot.child("dateCreation").getValue(Date.class);

                                        // Create a notif object and add it to the list
                                        notif notification = new notif(id, title, contenu, dateCreation);
                                        notifList.add(notification);

                                    }
                                    // Notify the adapter about the data change
                                    notifAdapter.notifyDataSetChanged();

                                    // Check if the notification list is empty
                                    if (notifList.isEmpty()) {
                                        // If the list is empty, make the no_notification_panel visible
                                        findViewById(R.id.no_notification_panel).setVisibility(View.VISIBLE);
                                        Log.d("MyTag", "Notification list is empty");
                                    } else {
                                        // If the list is not empty, make the no_notification_panel invisible
                                        findViewById(R.id.no_notification_panel).setVisibility(View.GONE);
                                        Log.d("MyTag", "Notification list is not empty");
                                    }
                                } else {
                                    Log.d("MyTag", "no notification for user " + userId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("MyTag", "Database error: " + databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MyTag", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void clearNotifications(long userId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        notificationsRef.orderByChild("idPersonne").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        // Delete each notification
                        notificationSnapshot.getRef().removeValue();
                    }
                    // Clear the notifList and notify the adapter
                    notifList.clear();
                    notifAdapter.notifyDataSetChanged();
                    // Check if the notification list is empty
                    if (notifList.isEmpty()) {
                        // If the list is empty, make the no_notification_panel visible
                        findViewById(R.id.no_notification_panel).setVisibility(View.VISIBLE);
                        Log.d("MyTag", "Notification list is empty");
                    } else {
                        // If the list is not empty, make the no_notification_panel invisible
                        findViewById(R.id.no_notification_panel).setVisibility(View.GONE);
                        Log.d("MyTag", "Notification list is not empty");
                    }
                    fetchNotifications(userId);

                    Log.d("MyTag", "All notifications cleared for user ID: " + userId);
                } else {
                    Log.d("MyTag", "No notifications found for user ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyTag", "Database error: " + databaseError.getMessage());
            }
        });
    }



    private void fetchNotifications(long userId) {
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notification");





        notificationsRef.orderByChild("idPersonne").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notifList.clear();
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve notification details from Firebase
                        String id = notificationSnapshot.getKey(); // Assuming the notification ID is the key
                        String title = notificationSnapshot.child("title").getValue(String.class);
                        String contenu = notificationSnapshot.child("contenu").getValue(String.class);
                        Date dateCreation = notificationSnapshot.child("dateCreation").getValue(Date.class);

                        // Create a notif object and add it to the list
                        notif notification = new notif(id, title, contenu, dateCreation);
                        notifList.add(notification);

                    }
                    // Notify the adapter about the data change
                    notifAdapter.notifyDataSetChanged();

                    // Check if the notification list is empty
                    if (notifList.isEmpty()) {
                        // If the list is empty, make the no_notification_panel visible
                        findViewById(R.id.no_notification_panel).setVisibility(View.VISIBLE);
                        Log.d("MyTag", "Notification list is empty");
                    } else {
                        // If the list is not empty, make the no_notification_panel invisible
                        findViewById(R.id.no_notification_panel).setVisibility(View.GONE);
                        Log.d("MyTag", "Notification list is not empty");
                    }
                } else {
                    Log.d("MyTag", "no notification for user " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyTag", "Database error: " + databaseError.getMessage());
            }
        });


    }
}
