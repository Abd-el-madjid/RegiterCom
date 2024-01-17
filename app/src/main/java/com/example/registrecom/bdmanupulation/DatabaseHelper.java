package com.example.registrecom.bdmanupulation;


import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.registrecom.Models.Canaux;
import com.example.registrecom.Models.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.registrecom.Models.Personne;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper {
    private  DatabaseReference canauxRef;
    private DatabaseReference personneRef;
    private DatabaseReference demandesRef;
    private DatabaseReference documentRef;
    private DatabaseReference messagesRef;
    private  DatabaseReference adminRef ;
    private  DatabaseReference notificationRef;
    public DatabaseHelper() {
        // Get a reference to the "personne" table
        this.personneRef = FirebaseDatabase.getInstance().getReference("Personne");
        this.demandesRef = FirebaseDatabase.getInstance().getReference("Demandes");
        this.documentRef = FirebaseDatabase.getInstance().getReference("Documents");
        this.canauxRef = FirebaseDatabase.getInstance().getReference("Canaux");
        this.messagesRef = FirebaseDatabase.getInstance().getReference("Messages");
        this.adminRef = FirebaseDatabase.getInstance().getReference("Admin");
        this.notificationRef = FirebaseDatabase.getInstance().getReference("Notification");

    }

    public void deleteNotificationsByUserId(long userId, OnCompleteListener<Void> onCompleteListener) {
        // Get the reference to the Notifications table
        DatabaseReference notificationsTableRef = notificationRef;

        // Create a query to find notifications with the specified idPersonne
        Query query = notificationsTableRef.orderByChild("idPersonne").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if there are notifications for the specified userId
                if (dataSnapshot.exists()) {
                    // Iterate through the notifications and remove them
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        notificationSnapshot.getRef().removeValue();
                    }

                    // Notify the onCompleteListener about the successful deletion
                    onCompleteListener.onComplete(null);
                } else {
                    // Handle the case where no notifications are found for the specified userId
                    Log.d(TAG, "No notifications found for User ID " + userId);

                    // Notify the onCompleteListener about the success (no notifications found)
                    onCompleteListener.onComplete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if needed
                Log.w(TAG, "Error querying notifications for User ID " + userId, databaseError.toException());

                // Notify the onCompleteListener about the failure
                onCompleteListener.onComplete(null);
            }
        });
    }
    public void deleteDemandeById(String demandeId, OnCompleteListener<Void> onCompleteListener) {
        // Get the reference to the specific Demande using its ID
        DatabaseReference demandeToDeleteRef = demandesRef.child(demandeId);

        // Remove the Demande from the database
        demandeToDeleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Handle success, if needed
                Log.d(TAG, "Demande with ID " + demandeId + " deleted successfully");

                // Now, delete all associated documents

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, if needed
                Log.w(TAG, "Error deleting Demande with ID " + demandeId, e);

                // If deleting Demande fails, also notify the onCompleteListener
                onCompleteListener.onComplete(null);
            }
        });
    }

    public void deleteDocumentsByDemandeId(String demandeId, OnCompleteListener<Void> onCompleteListener) {
        // Get the reference to the Documents table
        DatabaseReference documentsTableRef = documentRef;

        // Create a query to find documents with the specified idDemande
        Query query = documentsTableRef.orderByChild("idDemande").equalTo(demandeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if there are documents for the specified demandeId
                if (dataSnapshot.exists()) {
                    // Iterate through the documents and remove them
                    for (DataSnapshot documentSnapshot : dataSnapshot.getChildren()) {
                        documentSnapshot.getRef().removeValue();
                    }

                    // Notify the onCompleteListener about the successful deletion
                    onCompleteListener.onComplete(null);
                } else {
                    // Handle the case where no documents are found for the specified demandeId
                    Log.d(TAG, "No documents found for Demande ID " + demandeId);

                    // Notify the onCompleteListener about the success (no documents found)
                    onCompleteListener.onComplete(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if needed
                Log.w(TAG, "Error querying documents for Demande ID " + demandeId, databaseError.toException());

                // Notify the onCompleteListener about the failure
                onCompleteListener.onComplete(null);
            }
        });
    }

    public void addMessages(Messages messages, OnCompleteListener<Void> onCompleteListener) {
        // Generate a new ID using push() and set the ID for Canaux
        String messageId = messagesRef.push().getKey();
        messages.setIdMessage(messageId);

        // Set the Canaux with the generated ID
        messagesRef.child(messageId).setValue(messages)
                .addOnCompleteListener(onCompleteListener);
    }

    public void addCanaux(Canaux canaux, OnCompleteListener<Void> onCompleteListener) {
        // Generate a new ID using push() and set the ID for Canaux
        String canauxId = canauxRef.push().getKey();
        canaux.setCanauxId(canauxId);

        // Set the Canaux with the generated ID
        canauxRef.child(canauxId).setValue(canaux)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Pass the generated ID to the onCompleteListener
                        onCompleteListener.onComplete(task);
                    } else {
                        onCompleteListener.onComplete(task);
                    }
                });
    }
    public void addPersonne(Personne personne, String Id, OnCompleteListener<Void> onCompleteListener) {
        // Set the data using the provided Id
        personneRef.child(Id).setValue(personne)
                .addOnCompleteListener(onCompleteListener);
    }

    public void deletePersonneById(String personneId) {
        // Get the reference to the specific Personne using its ID
        DatabaseReference personneToDeleteRef = personneRef.child(personneId);

        // Remove the Personne from the database
        personneToDeleteRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Handle success, if needed
                Log.d(TAG, "Personne with ID " + personneId + " deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, if needed
                Log.w(TAG, "Error deleting Personne with ID " + personneId, e);
            }
        });
    }


    public void updatePersonne(String personneId, Personne updatedPersonne) {
        // Get the reference to the specific Personne using its ID
        DatabaseReference personneToUpdateRef = personneRef.child(personneId);

        // Convert the updatedPersonne to a Map for updating specific fields
        Map<String, Object> updatedValues = new HashMap<>();

        if (updatedPersonne.getNom() != null) {
            updatedValues.put("nom", updatedPersonne.getNom());
        }

        if (updatedPersonne.getPrenom() != null) {
            updatedValues.put("prenom", updatedPersonne.getPrenom());
        }

        // Add other fields as needed

        // Update the Personne in the database
        personneToUpdateRef.updateChildren(updatedValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Handle success, if needed
                Log.d(TAG, "Personne with ID " + personneId + " updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, if needed
                Log.w(TAG, "Error updating Personne with ID " + personneId, e);
            }
        });
    }

    public void updateLastLogin(String personneId) {
        // Get the reference to the specific Personne using its ID
        DatabaseReference personneToUpdateRef = personneRef.child(personneId);

        // Generate the current date and time as a Date object
        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        Date currentDateTime = null;

        try {
            currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the parsing exception according to your requirements
        }

        // Update the lastLogin field in the database
        if (currentDateTime != null) {
            personneToUpdateRef.child("lastLogin").setValue(currentDateTime)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Handle success, if needed
                            Log.d(TAG, "Last login for Personne with ID " + personneId + " updated successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure, if needed
                            Log.w(TAG, "Error updating last login for Personne with ID " + personneId, e);
                        }
                    });
        }
    }


    public void updateLastLoginAdmin(String Id) {
        // Get the reference to the specific Personne using its ID
        DatabaseReference adminToUpdateRef = adminRef.child(Id);

        // Generate the current date and time as a Date object
        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        Date currentDateTime = null;

        try {
            currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the parsing exception according to your requirements
        }

        // Update the lastLogin field in the database
        if (currentDateTime != null) {
            adminToUpdateRef.child("lastLogin").setValue(currentDateTime)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Handle success, if needed
                            Log.d(TAG, "Last login for admin with ID " + Id + " updated successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure, if needed
                            Log.w(TAG, "Error updating last login for admin with ID " + Id, e);
                        }
                    });
        }
    }

    public void updateIsactive(String personneId) {
        // Get the reference to the specific Personne using its ID
        DatabaseReference personneToUpdateRef = personneRef.child(personneId);

        // Use a ValueEventListener to read the current value of isactive
        personneToUpdateRef.child("isactive").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the current value of isactive
                Boolean currentIsactive = dataSnapshot.getValue(Boolean.class);

                // Toggle the value of isactive (true to false and vice versa)
                boolean updatedIsactive = currentIsactive != null && !currentIsactive;

                // Update the value of isactive
                personneToUpdateRef.child("isactive").setValue(updatedIsactive)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Handle success, if needed
                                Log.d("MyTag", "isactive for Personne with ID " + personneId + " updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure, if needed
                                Log.w("MyTag", "Error updating isactive for Personne with ID " + personneId, e);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if needed
                Log.w("MyTag", "Error reading isactive for Personne with ID " + personneId, databaseError.toException());
            }
        });
    }


}

