package com.example.registrecom.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.registrecom.Activites.ResourceActivity;
import com.example.registrecom.Activites.SupportActivity;
import com.example.registrecom.MainActivity;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.Models.Personne;
import com.example.registrecom.R;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfileFragment extends Fragment {
    private static final String PREF_SELECTED_LANGUAGE = "selected_language";
    DatabaseReference notificationRef;


    private FirebaseAuth auth;
    private DatabaseReference personneRef;
    private FirebaseUser currentUser;

    private RelativeLayout personnelLayout,passwordLayout,logoutlayout,languageLayout,ResourceLayout,SupportLayout,modeLayout;
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText,c_newpassowrd,newpassword,oldpassword;
    private Button validchangeinfo;
    private long userId;
    private ImageButton eyeOldPassword, eyeNewPassword,eyeValidNewPassword;
    private boolean isOldPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isValidNewPasswordVisible = false;
 private String nom,prenom,email,phonenum,password_db,password_dbdchekc;
    private  Context context;
private   DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        databaseHelper=  new DatabaseHelper();
         notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");


        String selectedLanguage = getSelectedLanguage();

        // Apply the selected language to the app


        personnelLayout = view.findViewById(R.id.personnelLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        languageLayout = view.findViewById(R.id.languageLayout);
        logoutlayout = view.findViewById(R.id.logoutLayout);
        SupportLayout = view.findViewById(R.id.SupportLayout);
        ResourceLayout = view.findViewById(R.id.ResourceLayout);
        modeLayout = view.findViewById(R.id.modeLayout);
        // Initialize EditTexts



        auth = FirebaseAuth.getInstance();
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
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

                             nom = personneSnapshot.child("nom").getValue(String.class);
                            prenom = personneSnapshot.child("prenom").getValue(String.class);
                            email = personneSnapshot.child("email").getValue(String.class);
                            Long phoneNumberLong = personneSnapshot.child("numTelephone").getValue(Long.class);
                            phonenum = (phoneNumberLong != null) ? String.valueOf(phoneNumberLong) : "";


                            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(prenom)) {
                                Toast.makeText(getContext(), "User information is incomplete", Toast.LENGTH_SHORT).show();
                            } else {
                               String letteruser = combineInitials(nom,prenom);
                                TextView usernameHint = view.findViewById(R.id.Username_hint);
                                TextView usernameletter = view.findViewById(R.id.username_letter);
                                usernameHint.setText(nom + " " + prenom);
                                usernameletter.setText(letteruser);
                                Log.d("MyTag", "user info  " +phonenum  +" "+ email+" "+ nom  +" "+prenom);

                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                        Log.d("MyTag", "User Information Not Found for Email: " + currentUserEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                    Log.e("MyTag", "Error fetching user information", databaseError.toException());
                }
            });
        }
        else {
            Log.d("MyTag", "Current User not found " );

        }

        ResourceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent resource = new Intent(getActivity(), ResourceActivity.class);
            startActivity(resource);

            }
        });


        SupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent support = new Intent(getActivity(), SupportActivity.class);
                startActivity(support);

            }
        });

        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialogpassword();
            }
        });


        personnelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialog();
            }
        });

        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialoglangue();
            }
        });
        logoutlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialoglogout();
            }
        });
        modeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDialogmode();
            }
        });

        return view;
    }
    private void showDialogmode() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottommodechange);

        // Find views in the dialog layout
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        RadioButton radioButtonDark = dialog.findViewById(R.id.radioButton1);
        RadioButton radioButtonLight = dialog.findViewById(R.id.radioButton2);
        AppCompatButton validationButton = dialog.findViewById(R.id.validationbuttonlanguage);

        // Set the initial checked state based on the current mode
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            radioButtonDark.setChecked(true);
        } else {
            radioButtonLight.setChecked(true);
        }

        // Set click listener for the validation button
        validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(ProfileFragment.this.getContext());
                pd.setMessage("Please wait...");
                pd.show();
                // Check which radio button is selected
                int selectedMode = radioButtonDark.isChecked()
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO;

                // Set the selected mode
                AppCompatDelegate.setDefaultNightMode(selectedMode);

                // Save the selected mode to SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isDarkModeOn", selectedMode == AppCompatDelegate.MODE_NIGHT_YES);
                editor.apply();
                pd.dismiss();
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDialoglogout() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centrelogout);


        Button logout = dialog.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // Logout the user


                        currentUser = auth.getCurrentUser();

                        personneRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                                        Personne personne = personneSnapshot.getValue(Personne.class);



                                        userId = Long.parseLong(personneSnapshot.getKey());
                                        databaseHelper.updateIsactive(personneSnapshot.getKey());
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_logout);
                                        String content = context.getString(R.string.notification_logout);
                                        long personId = userId;
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);

                                        FirebaseAuth.getInstance().signOut();
                                        // You can redirect the user to the login screen or perform any other action after logout
                                        // For example, you can start a new activity or fragment
                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                        getActivity().finish(); // Finish the current activity if needed
                                        dialog.dismiss();
                                        dialog2.dismiss();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                                    Log.d("MyTag", "User Information Not Found for Email: " + currentUser.getEmail());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                                Log.e("MyTag", "Error fetching user information", databaseError.toException());
                            }
                        });

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        dialog.dismiss();
                        dialog2.dismiss();
                    }
                });

                AlertDialog dialog2= builder.create();
                dialog2.show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog
    }
    private void showDialoglangue() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomlanguagechange);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        Button validationbuttonlanguage = dialog.findViewById(R.id.validationbuttonlanguage);

        // Set default language as English
        String selectedLanguage = getSelectedLanguage();
        if (selectedLanguage.equals("en")) {
            radioGroup.check(R.id.radioButton1);
        } else if (selectedLanguage.equals("fr")) {
            radioGroup.check(R.id.radioButton2);
        }

        validationbuttonlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ProfileFragment.this.getContext());
                pd.setMessage("Please wait...");
                pd.show();

                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                // Handle the selected language based on the radio button
                if (selectedRadioButtonId == R.id.radioButton1) {
                    saveSelectedLanguage("en");
                } else if (selectedRadioButtonId == R.id.radioButton2) {
                    saveSelectedLanguage("fr");
                }

                // Restart the main activity to apply the language changes
                getActivity().finish();
                startActivity(new Intent(getActivity(), getActivity().getClass()));
                pd.dismiss();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog


    }


    private void saveSelectedLanguage(String language) {
        SharedPreferences preferences = requireContext().getSharedPreferences("YourAppNamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SELECTED_LANGUAGE, language);
        editor.apply();

        // Restart the main activity to apply the language changes
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }



    private String getSelectedLanguage() {
        SharedPreferences preferences = requireContext().getSharedPreferences("YourAppNamePreferences", MODE_PRIVATE);
        return preferences.getString(PREF_SELECTED_LANGUAGE, "en"); // Default language is English
    }

    private void showDialogpassword() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottompasswordchange);

        eyeOldPassword = dialog.findViewById(R.id.eye_oldPassword);
        eyeNewPassword = dialog.findViewById(R.id.eye_newPassword);
        eyeValidNewPassword = dialog.findViewById(R.id.eye_valid_newPassword);



        oldpassword = dialog.findViewById(R.id.old_password);
        newpassword = dialog.findViewById(R.id.new_password);
        c_newpassowrd = dialog.findViewById(R.id.validation_new_password);


        // Set click listeners for eye icons
        eyeOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(oldpassword, isOldPasswordVisible,eyeOldPassword);
                isOldPasswordVisible = !isOldPasswordVisible;
            }
        });

        eyeNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(newpassword, isNewPasswordVisible,eyeNewPassword);
                isNewPasswordVisible = !isNewPasswordVisible;
            }
        });
        eyeValidNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(c_newpassowrd, isValidNewPasswordVisible,eyeValidNewPassword);
                isValidNewPasswordVisible = !isValidNewPasswordVisible;
            }
        });

        Button validp = dialog.findViewById(R.id.validationPasswordbutton);
        validp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String password = oldpassword.getText().toString();
                String new_password = newpassword.getText().toString();
                String c_new_password = c_newpassowrd.getText().toString();
                  // Check if any of the fields are empty
                if (password.isEmpty()) {
                    oldpassword.setError("old  password is required");
                }

                if (new_password.isEmpty()) {
                    newpassword.setError("new password is required");
                }

                if (c_new_password.isEmpty()) {
                    c_newpassowrd.setError("valid your new passowrd first ");
                }

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

                        password_db = personneSnapshot.child("motPasse").getValue(String.class);
                        Log.d("MyTag", "User password_db" + password_db);

                    }
                } else {
                    Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                    Log.d("MyTag", "User Information Not Found for Email: " + currentUserEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching user information", databaseError.toException());
            }
        });
    }
    else {
        Log.d("MyTag", "Current User not found " );

    }



                if (new_password.equals(password_db)){
                    newpassword.setError("old password make new one");

                }
                if (!c_new_password.equals(new_password)){

                    c_newpassowrd.setError("Passwords do not match");

                }
                if (!password.equals(password_db)){
                    oldpassword.setError("wrong password");

                }


// update passzord
 // Authenticate user with old password before changing it
                final ProgressDialog pd = new ProgressDialog(ProfileFragment.this.getContext());
                pd.setMessage("Please wait...");
                pd.show();
                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Change password in Firebase Authentication
                                    currentUser.updatePassword(new_password)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        // Change password in Firebase Realtime Database
                                                        personneRef.child(String.valueOf(userId))
                                                                .child("password")
                                                                .setValue(c_new_password);
                                                        pd.dismiss();

                                                        String notificationId = notificationRef.push().getKey();
                                                        String title = context.getString(R.string.notification_title_update_password);
                                                        String content = context.getString(R.string.notification_update_password);
                                                        long personId = userId;
                                                        Date currentDate = new Date();
                                                        // Create a Notification object
                                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                                        notificationRef.child(notificationId).setValue(newNotification);

                                                        showMessage("Password changed successfully");
                                                        dialog.dismiss();
                                                    } else {
                                                        pd.dismiss();
                                                        showMessage("Failed to change password: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                } else {
                                    pd.dismiss();
                                    showMessage("Authentication failed: " + task.getException().getMessage());
                                }
                            }
                        });




                Log.d("MyTag", "password " + password + " " + new_password + " " + c_new_password );
                // Dismiss the dialog
              //  dialog.dismiss();


            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog
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


    private void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        firstNameEditText = dialog.findViewById(R.id.firstname);
        lastNameEditText = dialog.findViewById(R.id.lastname);
        emailEditText = dialog.findViewById(R.id.email);
        phoneNumberEditText = dialog.findViewById(R.id.phonenumber);


        phoneNumberEditText.setText(phonenum);
        emailEditText.setText(email);
        lastNameEditText.setText(nom);
        firstNameEditText.setText(prenom);

        Button valid = dialog.findViewById(R.id.validationbutton);
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String editemail = emailEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();

                // Check if any of the fields are empty
                if (firstName.isEmpty()) {
                    firstNameEditText.setError("First name is required");
                }

                if (lastName.isEmpty()) {
                    lastNameEditText.setError("Last name is required");
                }

                if (editemail.isEmpty()) {
                    emailEditText.setError("Email is required");
                }

                if (phoneNumber.isEmpty()) {
                    phoneNumberEditText.setError("Phone number is required");
                }

                final ProgressDialog pd = new ProgressDialog(ProfileFragment.this.getContext());
                pd.setMessage("Please wait...");
                pd.show();
                // Update the fields in the specific Personne node
                 //updateEmailIfChanged(email);
                 if (!lastName.equals(nom)){
                     personneRef.child(String.valueOf(userId)).child("nom").setValue(lastName);
                     pd.dismiss();
                     String notificationId = notificationRef.push().getKey();
                     String title = context.getString(R.string.notification_title_update_lastname);
                     String content = context.getString(R.string.notification_update_lastname);
                     long personId = userId;
                     Date currentDate = new Date();
                     // Create a Notification object
                     Notification newNotification = new Notification(title, content, personId, currentDate);
                     notificationRef.child(notificationId).setValue(newNotification);

                 }
                if (!firstName.equals(prenom)){
                    personneRef.child(String.valueOf(userId)).child("prenom").setValue(firstName);
                    pd.dismiss();
                    String notificationId = notificationRef.push().getKey();
                    String title = context.getString(R.string.notification_title_update_firstname);
                    String content = context.getString(R.string.notification_update_firstname);
                    long personId = userId;
                    Date currentDate = new Date();
                    // Create a Notification object
                    Notification newNotification = new Notification(title, content, personId, currentDate);
                    notificationRef.child(notificationId).setValue(newNotification);

                }
                if (!phoneNumber.equals(phonenum)){
                    // Assuming numTelephone is a Long field
                    personneRef.child(String.valueOf(userId)).child("numTelephone").setValue(Long.parseLong(phoneNumber));
                    pd.dismiss();
                    String notificationId = notificationRef.push().getKey();
                    String title = context.getString(R.string.notification_title_change_phone);
                    String content = context.getString(R.string.notification_change_phone);
                    long personId = userId;
                    Date currentDate = new Date();
                    // Create a Notification object
                    Notification newNotification = new Notification(title, content, personId, currentDate);
                    notificationRef.child(notificationId).setValue(newNotification);
                }
                if(!editemail.equals(null)){
                    personneRef.orderByChild("email").equalTo(editemail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Username already exists
                            Log.d("MyTag", "email is not unique. Please choose a different email." );

                            if (!editemail.equals(currentUser.getEmail()) ){
                                emailEditText.setError("Email is not unique");
                            }else{
                                pd.dismiss();
                                dialog.dismiss();
                            }

                        } else {

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

                                                password_db = personneSnapshot.child("motPasse").getValue(String.class);
                                                Log.d("MyTag", "User password_db" + password_db);

                                                if(!password_db.isEmpty()){
                                                    if (!editemail.equals(email) ){
                                                    Log.d("MyTag", "emain is changed"+editemail  +""+email);
                                                    Log.d("MyTag", "password"+password_db);
                                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                    mAuth.createUserWithEmailAndPassword(editemail, password_db)
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Sign up success
                                                                        Log.d("MyTag", "User registration successful");

                                                                        FirebaseUser newUser = mAuth.getCurrentUser();

                                                                        // Update email in Firebase Realtime Database
                                                                        personneRef.child(String.valueOf(userId))
                                                                                .child("email")
                                                                                .setValue(editemail);
                                                                        pd.dismiss();


                                                                        String notificationId = notificationRef.push().getKey();
                                                                        String title = context.getString(R.string.notification_title_change_email);
                                                                        String content = context.getString(R.string.notification_change_email);
                                                                        long personId = userId;
                                                                        Date currentDate = new Date();
                                                                        // Create a Notification object
                                                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                                                        notificationRef.child(notificationId).setValue(newNotification);
                                                                        // Send email verification
                                                                        sendVerificationEmail(newUser);

                                                                        // Dismiss the dialog
                                                                        dialog.dismiss();
                                                                    } else {
                                                                        // If sign up fails, display a message to the user.
                                                                        Log.w("MyTag", "User registration failed", task.getException());
                                                                        // You can handle the error here, e.g., show an error message to the user
                                                                    }
                                                                }
                                                            });

                                                }
                                                    else{
                                                        pd.dismiss();
                                                        // Reload the necessary UI elements with the updated data
                                                        // For example, update the TextView displaying user info
                                                        TextView usernameHint = getView().findViewById(R.id.Username_hint);
                                                        usernameHint.setText(firstName+ " " + lastNameEditText.getText().toString());


                                                        phoneNumberEditText.setText(phoneNumber);
                                                        emailEditText.setText(email);
                                                        lastNameEditText.setText(lastName);
                                                        firstNameEditText.setText(firstName);

                                                        Log.d("MyTag", "user info geted: " + firstName + " " + lastName + " " + email + " " + phoneNumber);
                                                        // Dismiss the dialog

                                                        dialog.dismiss();
                                                        // Reload the page or update the UI
                                                        refreshFragment();

                                                    }
                                                }


                                            }
                                        } else {
                                            Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                                            Log.d("MyTag", "User Information Not Found for Email: " + currentUserEmail);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                                        Log.e("MyTag", "Error fetching user information", databaseError.toException());
                                    }
                                });
                            }
                            else {
                                Log.d("MyTag", "Current User not found " );

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("MyTag", "Error checking username uniqueness: " + databaseError.getMessage() );

                    }
                });
                }




// Add this method to update the UI

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog
    }

    private void refreshFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }


    // Function to send email verification
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MyTag", "Verification email sent to " + user.getEmail());
                            // You can display a message to the user informing them to check their email for verification
                        } else {
                            Log.e("MyTag", "Error sending verification email", task.getException());
                            // You can handle the error here, e.g., show an error message to the user
                        }
                    }
                });
    }
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private String combineInitials(String nom, String prenom) {
        // Check if nom and prenom are not null or empty
        if (nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty()) {
            // Extract the first letter from nom and prenom
            char nomInitial = nom.charAt(0);
            char prenomInitial = prenom.charAt(0);

            // Combine the initials into a single string
            return String.valueOf(nomInitial) + String.valueOf(prenomInitial);
        } else {
            // Handle the case where nom or prenom is null or empty
            return "";
        }
    }

}
