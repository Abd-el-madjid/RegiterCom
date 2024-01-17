package com.example.registrecom.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.registrecom.MainActivity;
import com.example.registrecom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgetPassword extends AppCompatActivity {
private EditText EmailReset;
private ImageView RemoveclickedForget ;
    private FirebaseAuth auth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        auth = FirebaseAuth.getInstance();
        EmailReset = findViewById(R.id.EmailReset);


        RemoveclickedForget=findViewById(R.id.RemoveclickedForget);
        RemoveclickedForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRemoveclickedForget();
            }
        });


    }
    public void viewResetClicked(View view) {
        String email = EmailReset.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showMessage("Please enter your email address");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Personne");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email exists, send password reset email
                    sendPasswordResetEmail(email);
                } else {
                    // Email does not exist
                    showMessage("Email does not exist. Please sign up.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // An error occurred while checking email existence
                showMessage("Error checking email existence: " + databaseError.getMessage());
            }
        });
    }
    private void sendPasswordResetEmail(String email) {
        // Send password reset email
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            showMessage("Password reset email sent successfully.");
                            viewRemoveclickedForget();
                        } else {
                            // Password reset email sending failed
                            showMessage("Password reset email failed to send.");
                        }
                    }
                });
    }

    public void viewRemoveclickedForget() {
        Intent main = new Intent(forgetPassword.this, MainActivity.class);
        startActivity(main);
        finish();
    }


    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}