package com.example.registrecom;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.registrecom.Activites.NotificationActivity;
import com.example.registrecom.Activites.activity_main_register;
import com.example.registrecom.Activites.dashboard;
import com.example.registrecom.Activites.forgetPassword;
import com.example.registrecom.Admin.AdminActivity;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.Models.Personne;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
private EditText editTextEmail ,editTextPassword;
    private Button buttonLogin;
private FirebaseAuth auth ;
private DatabaseHelper databaseHelper;
private ImageButton    eyePassword;
private RelativeLayout startbglogo;
private ScrollView main ;
    DatabaseReference notificationRef;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
       main = findViewById(R.id.main);
        startbglogo =  findViewById(R.id.startbglogo);
        auth = FirebaseAuth.getInstance();


         notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        if (auth.getCurrentUser() != null) {

            if (auth.getCurrentUser().isEmailVerified()) {
                // User is authenticated and email is verified
                findViewById(R.id.startbglogo).setVisibility(View.VISIBLE);
                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admin");
                Query queryAdmin = adminRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
                queryAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User found in "Admin" table

                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            findViewById(R.id.startbglogo).setVisibility(View.GONE);
                            finish();
                            Log.d("MyTag", "Admin login successful");

                        } else {
                            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Personne");
                            Query queryAdmin = adminRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
                            queryAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // User found in "Admin" table

                                        Intent intent = new Intent(MainActivity.this, dashboard.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        findViewById(R.id.startbglogo).setVisibility(View.GONE);
                                        finish();
                                        Log.d("MyTag", "user login successful");

                                    } else {
                                        Log.d("MyTag", "User not found in database");


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                    Log.d("MyTag", "onCancelled: "+databaseError);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.d("MyTag", "onCancelled: "+databaseError);
                    }
                });

            } else {
                // Email is not verified, show a message or handle it accordingly
                Toast.makeText(MainActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
            }
        }else{findViewById(R.id.startbglogo).setVisibility(View.GONE);}


        databaseHelper = new DatabaseHelper();

        eyePassword = findViewById(R.id.eye_Password);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.B_Login);


        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(editTextPassword, isPasswordVisible,eyePassword);
                isPasswordVisible = !isPasswordVisible;
            }
        });



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    editTextEmail.setError("Credential$ requeded");
                    editTextPassword.setError("Credential$ requeded");

                } else {
                    login(email,password);
                }
            }
        });

    }

    private void isAdmin(String email) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admin");
        Query queryAdmin = adminRef.orderByChild("email").equalTo(email);
        queryAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User found in "Admin" table

                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Log.d("MyTag", "Admin login successful");

                } else {
                    // User not found in "Admin" table

                    Log.d("MyTag", "User not found in database");
                    showMessage("User not found in database");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.d("MyTag", "onCancelled: "+databaseError);
            }
        });
    }





    private void login(String email, String password) {

        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Please wait...");
        pd.show();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Please enter a valid email address");
            pd.dismiss();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Check if the user's email is verified
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        // Email is verified, proceed with login
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference("Personne");
                                Query query = personneRef.orderByChild("email").equalTo(email);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Loop through the results (although there should be only one)
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                // Retrieve the personne ID from the dataSnapshot
                                                Personne personne = snapshot.getValue(Personne.class);
                                                personne.setIsactive(true);

                                                String userId =snapshot.getKey();
                                                databaseHelper.updateLastLogin(userId);
                                                databaseHelper.updateIsactive(userId);
                                                Context context = getApplicationContext();
                                                String notificationId = notificationRef.push().getKey();
                                                // Retrieve the string values using the resource IDs
                                                String title = context.getString(R.string.new_login);
                                                String content = context.getString(R.string.notification_new_login);
                                                long personId = Long.parseLong(userId);
                                                Date currentDate = new Date();
                                                // Create a Notification object
                                                Notification newNotification = new Notification(title, content, personId, currentDate);
                                                notificationRef.child(notificationId).setValue(newNotification);

                                                pd.dismiss();
                                                Intent intent = new Intent(MainActivity.this, dashboard.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {

                                           isAdmin(email);
                                            pd.dismiss();
                                            }
                                    }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle database error
                                        pd.dismiss();
                                        showMessage("Database error");
                                        }
                                });



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                pd.dismiss();
                            }
                        });
                    } else {
                        // Email is not verified, show a message to the user
                        pd.dismiss();
                        showMessage("Please verify your email before logging in.");
                        // You may also want to provide a way for the user to resend the verification email.
                    }
                } else {
                    pd.dismiss();
                    showMessage("User not login: " + task.getException().getMessage());
                }
            }
        });
    }


    public void viewForgotPAssword(View view){
        Intent forgetPassword = new Intent(MainActivity.this, forgetPassword.class);
        startActivity(forgetPassword);
        finish();
    }
    public void viewRegisterClicked(View view){
        Intent login = new Intent(MainActivity.this, activity_main_register.class);
        startActivity(login);
        finish();
    }

   
    private void togglePasswordVisibility(EditText editText, boolean isVisible,ImageButton k) {
        if (editText == null) {
            // Log an error or handle it appropriately
            return;
        }

        if (isVisible) {
            // Hide password
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            k.setImageResource(R.drawable.baseline_key_24); // Change to your closed eye icon
        } else {
            // Show password
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            k.setImageResource(R.drawable.outline_key_off_24); // Change to your open eye icon
        }

        // Move cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
