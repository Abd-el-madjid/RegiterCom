package com.example.registrecom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.registrecom.Activites.dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String FCM_TOKEN_KEY = "fcmToken";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            // Handle notification payload
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Show notification to the user
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("MyTag", "Refreshed token: " + token);

        // Save the token to SharedPreferences or send it to your server
        saveFCMTokenLocally(token);

    }

    private void saveFCMTokenLocally(String token) {
        // Save the token to SharedPreferences or any other local storage
        // You can also send the token to your server if needed
        // Example using SharedPreferences:
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        preferences.edit().putString("fcm_token", token).apply();

    }

    private void sendNotification(String title, String body) {
        String channelId = "CHANNEL_ID_NOTIFICATION";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        notificationBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference("Personne");
            Query queryPersonne = personneRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            queryPersonne.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User found in "Personne" table
                    Intent    intent = new Intent(getApplicationContext(), dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                intent,
                                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                        );
                        notificationBuilder.setContentIntent(pendingIntent);

                        Log.d("MyTag", "User login successful");
                    } else {
                        // User not found in database
                        Log.d("MyTag", "User not found in database");

                        // Sign out the current user
                        FirebaseAuth.getInstance().signOut();

                        // Redirect to the login activity
                        Intent   intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                intent,
                                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                        );
                        notificationBuilder.setContentIntent(pendingIntent);
                    }

                    // Notify only after setting the appropriate PendingIntent
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.notify(0, notificationBuilder.build());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.d("MyTag", "onCancelled: " + databaseError);
                }
            });
        } else {
            // User not authenticated or email not verified
            // Handle it accordingly, for now, let's show a message
            Log.d("MyTag", "Please verify your email address");
        }
    }




    // This method should be called during the initialization phase of your app
    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }
}