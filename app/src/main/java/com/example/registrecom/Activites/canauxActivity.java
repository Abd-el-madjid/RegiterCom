package com.example.registrecom.Activites;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import com.example.registrecom.classes.CustomListAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.example.registrecom.Models.Canaux;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class canauxActivity extends AppCompatActivity  implements CustomListAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private DatabaseReference personneRef, demandesRef,canauxRef,messagesRef;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private DatabaseHelper databaseHelper ;

    private RelativeLayout noCanauxPanel;
    private CustomListAdapter adapter;
    private Button writeMessageButton;
    private ImageButton AddMessageButton ,back_btn;
    long userId , currentUserId ;
    int k =0;
    private TextView textChannellastsendeur;
    private  Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canaux);

         context = getApplicationContext();

        auth = FirebaseAuth.getInstance();
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        canauxRef = FirebaseDatabase.getInstance().getReference().child("Canaux");
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        databaseHelper = new DatabaseHelper();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.channel_recycler_view);
        noCanauxPanel = findViewById(R.id.no_canaux_panel);


        textChannellastsendeur = recyclerView.findViewById(R.id.textChannellastsendeur);

        recyclerView.setVisibility(View.GONE);
        noCanauxPanel.setVisibility(View.GONE);

        AddMessageButton = findViewById(R.id.AddMessageButton);
        writeMessageButton = findViewById(R.id.writeMessageButton);






        back_btn = findViewById(R.id.back_btn);

        AddMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_canaux();
            }
        });
        writeMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_canaux();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You may need to modify this part based on your actual implementation
                // For example, if you are using fragments, you might want to replace the current fragment.

                // Assuming you are using fragments and have access to FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Pop the current fragment from the back stack
                fragmentManager.popBackStack();

                // Finish the current activity
                finish();
            }
        });
        adapter = new CustomListAdapter(new ArrayList<>(), this);


        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        currentUser = auth.getCurrentUser();


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
                            checkIfCanauxExist(userId);
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
        }else { showMessage("User  not found");
        }


        // Add a ChildEventListener to listen for new messages and changes
     /*   messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Handle a new message being added
                String canauxId = dataSnapshot.child("idCanaux").getValue(String.class);

                if (canauxId != null) {
                    // Update the lastDateMessage of the specific Canaux with the latest message time
                    updateLastDateMessageForCanaux(canauxId, getCurrentTime());
                } else {
                    Log.e("canauxActivity", "canauxId is null for the added message");
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Handle a change in a message
                String canauxId = dataSnapshot.child("idCanaux").getValue(String.class);

                // Update the lastDateMessage of the specific Canaux with the latest message time
                updateLastDateMessageForCanaux(canauxId, getCurrentTime());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle a message being removed (if needed)
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Handle a change in the order of messages (if needed)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });*/
    }
    @Override
    public void onItemClick(String canauxId,String canauxName) {
        // Handle the click event here, for example, start a new activity
        Log.d("CanauxActivity", "Item clicked with ID: " + canauxId);

        Intent intent = new Intent(this, chatActivity.class);
        intent.putExtra("canauxId", canauxId);
        intent.putExtra("canauxName", canauxName);
        startActivity(intent);
        finish();
    }

    private void checkIfCanauxExist(long userIdk) {
        DatabaseReference canauxRef = databaseRef.child("Canaux");
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            Log.d("MyTag", "Current User Email: " + currentUserEmail);

            personneRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            userId = Long.parseLong(personneSnapshot.getKey());
                            canauxRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot canauxSnapshot) {
                                    List<Canaux> canauxList = new ArrayList<>();

                                    for (DataSnapshot channelSnapshot : canauxSnapshot.getChildren()) {
                                        Canaux channelItem = channelSnapshot.getValue(Canaux.class);

                                        if (channelItem != null) {
                                            canauxList.add(channelItem);
                                        }
                                    }

                                    if (!canauxList.isEmpty()) {
                                        // Sort the canauxList based on the lastDateMessage in descending order
                                        Collections.sort(canauxList, new Comparator<Canaux>() {
                                            @Override
                                            public int compare(Canaux o1, Canaux o2) {
                                                // Assuming lastDateMessage is a String representing the date
                                                return o2.getLastDateMessage().compareTo(o1.getLastDateMessage());
                                            }
                                        });

                                        recyclerView.setVisibility(View.VISIBLE);
                                        noCanauxPanel.setVisibility(View.GONE);
                                        adapter.updateData(canauxList);
                                    } else {
                                        recyclerView.setVisibility(View.GONE);
                                        noCanauxPanel.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showMessage("Error fetching Canaux information");
                                    Log.e("MyTag", "Error fetching Canaux information", databaseError.toException());
                                }
                            });
                        }} else {
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
            }else { showMessage("User  not found");
            }
    }




    private void Add_canaux() {

        // Retrieve the current number of channels
        canauxRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long channelCount = dataSnapshot.getChildrenCount();

                // Create a new channel with a name based on the current channel count
                String newCanauxName = String.valueOf(context.getString(R.string.canaux_name) + (channelCount + 1));
                String currentTime = getCurrentTime();

                currentUser = auth.getCurrentUser();


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
// Create a new Canaux object
                                    Canaux newCanaux = new Canaux(newCanauxName, currentTime,userId);

                                    // Use the addCanaux method to add the new channel to the database
                                    databaseHelper.addCanaux(newCanaux, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                String canauxId = newCanaux.getCanauxId();
                                                String canauxName = newCanaux.getName();

                                                onItemClick(canauxId,canauxName);


                                                // Canaux added successfully
                                                Log.d(TAG, "Canaux added successfully with ID: " + canauxId);
                                                Log.d(TAG, "Canaux added successfully with name: " + canauxName);


                                            } else {
                                                // Error adding Canaux
                                                Log.w(TAG, "Error adding Canaux", task.getException());
                                            }
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
                }else { showMessage("User  not found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e(TAG, "Error retrieving channel count: " + databaseError.getMessage());
            }
        });
    }

    // Helper method to get the current time as a string
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return dateFormat.format(new Date());
    }


    private int compareTimestamps(String timestamp1, String timestamp2) {
        // Replace this with your own logic for comparing timestamps
        return timestamp1.compareTo(timestamp2);
    }
    private long get_userId() {
        currentUser = auth.getCurrentUser();


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
        }else { showMessage("User  not found");
        }
        return  userId ;
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}