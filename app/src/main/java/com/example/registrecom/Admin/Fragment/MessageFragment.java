package com.example.registrecom.Admin.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;


import android.widget.Spinner;
import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Admin.Activity.CanuxAdminActivity;
import com.example.registrecom.Fragments.HomeFragment;
import com.example.registrecom.MainActivity;
import com.example.registrecom.Models.Documents;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.Models.Personne;
import com.example.registrecom.R;
import com.example.registrecom.Models.Demandes;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.example.registrecom.classes.CustomListDAdapter;
import com.example.registrecom.classes.ListItem;
import com.example.registrecom.classes.PdfList;
import com.example.registrecom.classes.PdfListAdapter;
import com.example.registrecom.classes.Profile;
import com.example.registrecom.classes.ProfileAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageFragment extends Fragment implements ProfileAdapter.OnItemClickListener {
    private float startY;
    private float initialY;
    private boolean isDragging = false;
    private float screenHeight; // Declare this variable
    private float dialogHeight; // Declare this variable


    private RecyclerView recyclerView,pdfRecyclerView;
    private Map<String, String> documentUrls = new HashMap<>();
    private FirebaseAuth auth;
    private DatabaseReference personneRef, demandesRef,documentsRef,adminRef,notificationRef;
    private FirebaseUser currentUser;
    private DatabaseHelper databaseHelper ;

    private String nom, prenom;
    private RelativeLayout noDemandsPanel;
    private Button submitdemandeButton   , button,companyfile,supprimer,accept,refuse;
    private TextView  companyname , companyaddress , companyphone , companyemail , companyactivite,demandedate,choosetxt ;
    private ImageView etatimage,notification;
    private   String etat ;
    private Spinner spinner_filtre;
    private ProfileAdapter personneAdapter;
    private  Context context;
    private EditText  firstNameEditText , lastNameEditText ,  natinalityEditText , emailEditText ,   phoneNumberEditText ,
            dateEditText ,password;
    private ImageButton eyeOldPassword;
    private boolean isOldPasswordVisible = false;
    private  DatabaseHelper databasehelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        context = getContext();

        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");


        databasehelper = new DatabaseHelper();



        spinner_filtre = view.findViewById(R.id.spinner_filtre);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.filter_options2,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        // Apply the adapter to the spinner
        spinner_filtre.setAdapter(adapter);
        // Set the default selection to "Gender"
        spinner_filtre.setSelection(0);

        spinner_filtre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                // Get the current search query from the SearchView

                retrieveUsersFromFirebaseWithFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Set the default selection to "All" when nothing is selected
                spinner_filtre.setSelection(0);


                // Call the method to retrieve all users from Firebase
                retrieveUsersFromFirebaseWithFilter(0);
            }
        });
        databaseHelper =  new DatabaseHelper();


        recyclerView = view.findViewById(R.id.recycler_view_users);
        noDemandsPanel = view.findViewById(R.id.no_user_panel);



        noDemandsPanel.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        documentsRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        demandesRef = FirebaseDatabase.getInstance().getReference().child("Demandes");
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");

        currentUser = auth.getCurrentUser();
        String id =  currentUser.getUid();
        Log.d("MyTag", "user id  auth: "+id);





        return view;
    }



    private void retrieveUsersFromFirebaseWithFilter(final int selectedPosition) {
        personneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Profile> profile = new ArrayList<>();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                        Personne currentPersonne = personneSnapshot.getValue(Personne.class);

                        if (currentPersonne != null) {
                            boolean shouldAdd = false;


                            if (selectedPosition == 0) {
                                // Filter based on selected position
                                shouldAdd = true;  // All
                            } else if (selectedPosition == 1) {
                                shouldAdd = currentPersonne.isIsactive();  // Active
                            } else if (selectedPosition == 2) {
                                shouldAdd = !currentPersonne.isIsactive();  // Inactive
                            }

                            // Add the user to the list only if it matches the search query and other filters
                            if (shouldAdd ) {
                                String id = String.valueOf(currentPersonne.getId());
                                String username = currentPersonne.getNomUtilisateur();
                                String email = currentPersonne.getEmail();
                                String letter = combineInitials(currentPersonne.getNom(), currentPersonne.getPrenom());
                                boolean state = currentPersonne.isIsactive();

                                profile.add(new Profile(id, letter, username, email, state));
                            }
                        }
                    }

                    ProfileAdapter adapter = new ProfileAdapter(getContext(), profile, MessageFragment.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    noDemandsPanel.setVisibility(View.VISIBLE);
                    Log.d("MyTag", "No users found  ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyTag", "Error fetching data", databaseError.toException());
            }
        });
    }




    public void onItemClick(String id) {
        Log.d("MyTag", "Clicked on item with ID: " + id);
        Toast.makeText(getContext(), "clique personne " + id, Toast.LENGTH_SHORT).show();

        personneRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Personne personne = dataSnapshot.getValue(Personne.class);
                    if (personne != null) {
                        Log.d("MyTag", "Personne object retrieved: " + personne.toString());
                       Intent canauxUser = new Intent(getActivity(), CanuxAdminActivity.class);
                       canauxUser.putExtra("Personne",personne);
                       startActivity(canauxUser);
                    } else {
                        Log.d("MyTag", "Personne is null for ID: " + id);
                    }
                } else {
                    Log.d("MyTag", "No data found for user UID: " + id);
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching data", databaseError.toException());
            }
        });
    }

    private void openDialog(Personne personne) {
        Log.d("MyTag", "openDialog: "+ personne);



        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottominfousers);

        password = dialog.findViewById(R.id.old_password);
        eyeOldPassword = dialog.findViewById(R.id.eye_oldPassword);
        // Set click listeners for eye icons
        eyeOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(password, isOldPasswordVisible,eyeOldPassword);
                isOldPasswordVisible = !isOldPasswordVisible;
            }
        });

        choosetxt = dialog.findViewById(R.id.choosetxt);
        choosetxt.setText("Profile  "+personne.getNomUtilisateur());


        firstNameEditText = dialog.findViewById(R.id.firstname);
        lastNameEditText = dialog.findViewById(R.id.lastname);
        natinalityEditText = dialog.findViewById(R.id.nationality);
        emailEditText = dialog.findViewById(R.id.email);
        phoneNumberEditText = dialog.findViewById(R.id.phonenumber);
        dateEditText = dialog.findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        phoneNumberEditText.setText(String.valueOf(personne.getNumTelephone()));
        emailEditText.setText(personne.getEmail());
        lastNameEditText.setText(personne.getNom());
        firstNameEditText.setText(personne.getPrenom());
        natinalityEditText.setText(personne.getNationalite());

        // Assuming personne.getDateNaissance() returns a Date object
        Date dateOfBirth = personne.getDateNaissance();

        // Format the Date object into a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(dateOfBirth);

        // Set the formatted date to the EditText
        dateEditText.setText(formattedDate);





        Button saveButton = dialog.findViewById(R.id.validationbutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] k = {0};
                // Check if any of the required fields are empty
                if (TextUtils.isEmpty(firstNameEditText.getText())) {
                    firstNameEditText.setError("First name is required");
                    return;
                }

                if (TextUtils.isEmpty(lastNameEditText.getText())) {
                    lastNameEditText.setError("Last name is required");
                    return;
                }

                if (TextUtils.isEmpty(natinalityEditText.getText())) {
                    natinalityEditText.setError("Nationality is required");
                    return;
                }

                if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumberEditText.getText())) {
                    phoneNumberEditText.setError("Phone number is required");
                    return;
                }


                // If all required fields are filled, proceed with saving data
                // Implement your method to save data here
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Get the current user's ID
                    String userId = currentUser.getUid();

                    // You might have your own database structure
                    // Let's assume you have a "Admin" table where you store admin details
                    DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin").child(userId);

                    adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String adminPassword = dataSnapshot.child("motPasse").getValue(String.class);
                                String enteredPassword = password.getText().toString(); // Assuming 'password' is your EditText

                                if (enteredPassword.equals(adminPassword)) {
                                    // Assuming you have access to the EditText values
                                    String newFirstName = firstNameEditText.getText().toString();
                                    String newLastName = lastNameEditText.getText().toString();
                                    String newNationality = natinalityEditText.getText().toString();
                                    String newEmail = emailEditText.getText().toString();
                                    String newPhoneNumber = phoneNumberEditText.getText().toString();
                                    String newDateOfBirth = dateEditText.getText().toString();

                                    if (!newLastName.equals(personne.getNom())) {
                                        personneRef.child(String.valueOf(personne.getId())).child("nom").setValue(newLastName);
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_update_lastname);
                                        String content = context.getString(R.string.notification_update_lastname);
                                        long personId = personne.getId();
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                        showMessage("last name updated ");
                                        k[0] =1;
                                    }
                                    if (!newFirstName.equals(personne.getPrenom())) {
                                        personneRef.child(String.valueOf(personne.getId())).child("prenom").setValue(newFirstName);
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_update_firstname);
                                        String content = context.getString(R.string.notification_update_firstname);
                                        long personId = personne.getId();
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                        k[0] =1;
                                        showMessage("first name updated ");

                                    }
                                    if (!newNationality.equals(personne.getNationalite())) {
                                        personneRef.child(String.valueOf(personne.getId())).child("nationalite").setValue(newNationality);
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_nationality_update);
                                        String content = context.getString(R.string.notification_nationality_update);
                                        long personId = personne.getId();
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                        k[0] =1;
                                        showMessage("nationalite updated ");

                                    }
                                    if (!newPhoneNumber.equals(String.valueOf(personne.getNumTelephone()))) {

                                        // Assuming numTelephone is a Long field
                                        personneRef.child(String.valueOf(personne.getId())).child("numTelephone").setValue(Long.parseLong(newPhoneNumber));
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_change_phone);
                                        String content = context.getString(R.string.notification_change_phone);
                                        long personId = personne.getId();
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                        k[0] =1;
                                        showMessage("phone number updated ");

                                    }

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    Date newDate = null;
                                    try {
                                        newDate = dateFormat.parse(newDateOfBirth);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (newDate != null && !newDate.equals(personne.getDateNaissance())) {

                                        // Assuming numTelephone is a Long field
                                        personneRef.child(String.valueOf(personne.getId())).child("dateNaissance").setValue(newDate);
                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_birthday_update);
                                        String content = context.getString(R.string.notification_birthday_update);
                                        long personId = personne.getId();
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                        k[0] =1;
                                        showMessage("date of birthday updated ");

                                    }

                                    if (!newEmail.equals(personne.getEmail())) {

                                        personneRef.orderByChild("email").equalTo(newEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // Username already exists
                                                    Log.d("MyTag", "email is not unique. Please choose a different email.");
                                                    emailEditText.setError("Email is not unique");
                                                    showMessage("email not unique");
                                                    k[0] =0;
                                                } else {

                                                    personneRef.child(String.valueOf(personne.getId())).child("email").setValue(newEmail);
                                                    String notificationId = notificationRef.push().getKey();
                                                    String title = context.getString(R.string.notification_title_change_email);
                                                    String content = context.getString(R.string.notification_change_email);
                                                    long personId = personne.getId();
                                                    Date currentDate = new Date();
                                                    // Create a Notification object
                                                    Notification newNotification = new Notification(title, content, personId, currentDate);
                                                    notificationRef.child(notificationId).setValue(newNotification);
                                                    k[0] =1;
                                                    showMessage("email updated ");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d("MyTag", "Error checking username uniqueness: " + databaseError.getMessage());

                                            }
                                        });



                                    }
                                    if ( k[0] >0){
                                        dialog.dismiss();}


                                } else {
                                    showMessage("password Incorrect ");

                                    password.setError("Incorrect password");
                                }
                            } else {
                                showMessage("admin data not found ");

                                Log.e("MyTag", "Admin data not found");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MyTag", "Error fetching admin data", databaseError.toException());

                        }
                    });
                }

                // Dismiss the dialog after saving data

            }
        });

        Button deletButton =dialog.findViewById(R.id.supprisionbutton);
        deletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    // Get the current user's ID
                    String userId = currentUser.getUid();

                    // You might have your own database structure
                    // Let's assume you have a "Admin" table where you store admin details
                    DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admin").child(userId);

                    adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String adminPassword = dataSnapshot.child("motPasse").getValue(String.class);
                                String enteredPassword = password.getText().toString(); // Assuming 'password' is your EditText

                                if (enteredPassword.equals(adminPassword)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setTitle("Delete User");
                                    builder.setMessage("Are you sure you want to delet this user?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog2, int which) {
                                            // Logout the user

                                            databasehelper.deletePersonneById(String.valueOf(personne.getId()));
                                            dialog.dismiss();
                                            showMessage("User has been deleted  "+personne.getNomUtilisateur());

                                            Log.e("MyTag", "User has been deleted "+personne.getId());

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


                                } else {
                                    showMessage("password Incorrect ");

                                    password.setError("Incorrect password");
                                }
                            } else {
                                showMessage("admin data not found ");

                                Log.e("MyTag", "Admin data not found");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MyTag", "Error fetching admin data", databaseError.toException());

                        }
                    });}
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void togglePasswordVisibility(EditText editText, boolean isVisible, ImageButton k) {
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
    public void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the EditText with the chosen date
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    // Method to convert a date string to a Date object
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
