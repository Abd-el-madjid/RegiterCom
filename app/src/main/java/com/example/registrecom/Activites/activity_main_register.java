package com.example.registrecom.Activites;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.registrecom.Fragments.DemandAddFragment;
import com.example.registrecom.MainActivity;
import com.example.registrecom.Models.Admin;
import com.example.registrecom.MyFirebaseMessagingService;
import com.example.registrecom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.registrecom.Models.Personne;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class activity_main_register extends AppCompatActivity {
    private MyFirebaseMessagingService myfirebaseMessagingService;
    private boolean isPasswordVisible = false;
    private boolean isCPasswordVisible = false;

    private ImageButton eyePassword ,eyeCPassword;
    private Date dateOfBirth;
    private EditText dateEditText;
    private Spinner genderSpinner;
    private LinearLayout l1, l2;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private EditText firstNameEditText, lastNameEditText, nationalNumberEditText,
            dateOfBirthEditText, placeOfBirthEditText, numTelephoneEditText, nationalityEditText, usernameEditText,
            emailEditText, passwordEditText, confirmPasswordEditText;
    private Button buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);
        myfirebaseMessagingService = new MyFirebaseMessagingService();

// date
        dateEditText = findViewById(R.id.dateEditText);



// gendre
        genderSpinner = findViewById(R.id.spinner_gender);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);
        // Set the default selection to "Gender"
        int defaultPosition = adapter.getPosition("Gender");
        genderSpinner.setSelection(0);


// toggle L1 L2
        l1 = findViewById(R.id.L1);
        l2 = findViewById(R.id.L2);

        // Initially, show l1 and hide l2
        showL1();

// user registeration

        eyePassword = findViewById(R.id.eye_Password);
        eyeCPassword = findViewById(R.id.eye_CPassword);
        buttonSignUp = findViewById(R.id.B_Register);

        // Initialize all the views using findViewById
        firstNameEditText = findViewById(R.id.firstname);
        lastNameEditText = findViewById(R.id.lastname);
        nationalNumberEditText = findViewById(R.id.nationalnumber);
        dateOfBirthEditText = findViewById(R.id.dateEditText);
        placeOfBirthEditText = findViewById(R.id.placeofbirth);

        numTelephoneEditText = findViewById(R.id.numTelephone);
        nationalityEditText = findViewById(R.id.nationalite);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.c_password);

        eyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(passwordEditText, isPasswordVisible,eyePassword);
                isPasswordVisible = !isPasswordVisible;
            }
        });
        eyeCPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(confirmPasswordEditText, isCPasswordVisible,eyeCPassword);
                isCPasswordVisible = !isCPasswordVisible;
            }
        });
        auth = FirebaseAuth.getInstance();

        String dateOfBirthString = "04/12/2002";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        dateOfBirth = null;

       try {
            dateOfBirth = sdf.parse(dateOfBirthString);
        } catch (ParseException e) {
            e.printStackTrace();
            showMessage("Erreur: Invalid date format. Please enter the date in the format dd/MM/yyyy");
        }
        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        Date dateTime = null;

        try {
            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            showMessage("Error creating user");
        }
        Admin admin = new Admin(

                /* nom */ "abd el madjid",
                /* prenom */ "kahoul",
                /* dateNaissance */ dateOfBirth,
                /* email */ "abdelmadjid.kahoul@univ-constantine2.dz",
                /* nomUtilisateur */ "mad_kAdmin",
                /* motPasse */"12345678",
                /* lastLogin */ null,
                /* dateCreation */dateTime
        );

            //    addAdmin(admin);


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFieldsNotEmpty2()) {
                    pd = new ProgressDialog(activity_main_register.this);
                    pd.setMessage("Please wait...");
                    pd.show();

                    long phoneNumber = Long.parseLong(numTelephoneEditText.getText().toString().trim());
                    String username = usernameEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                    String firstName = firstNameEditText.getText().toString().trim();
                    String lastName = lastNameEditText.getText().toString().trim();
                    long nationalNumber = Long.parseLong(nationalNumberEditText.getText().toString().trim());
                    String dateOfBirthString = dateOfBirthEditText.getText().toString().trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                     dateOfBirth = null;

                    try {
                        dateOfBirth = sdf.parse(dateOfBirthString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        showMessage("Erreur: Invalid date format. Please enter the date in the format dd/MM/yyyy");
                    }

                    String placeOfBirth = placeOfBirthEditText.getText().toString().trim();

                    String selectedGender = genderSpinner.getSelectedItem().toString();
                    boolean isGender = "Male".equals(selectedGender);

                    String nationality = nationalityEditText.getText().toString().trim();





                    if (checkPhoneNumber(numTelephoneEditText.getText().toString().trim())) {
                        if (isValidEmail(email)) {
                            checkEmail(email, new activity_main_register.EmailCheckCallback() {
                                @Override
                                public void onEmailCheckComplete(boolean isUniqueEmail) {
                                    if (isUniqueEmail) {
                                        if (password.length() >= 6) {
                                            if (password.equals(confirmPassword)) {
                                                checkUsername(username, new activity_main_register.UsenameCheckCallback() {
                                                    @Override
                                                    public void onUsernameCheckComplete(boolean isUniqueUsename) {
                                                        if (isUniqueUsename) {

                                                            String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
                                                            Date dateTime = null;

                                                            try {
                                                                dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                                showMessage("Error creating user");
                                                            }

                                                            Personne personne = new Personne(
                                                                    /* id */ nationalNumber,
                                                                    /* nom */ lastName,
                                                                    /* prenom */ firstName,
                                                                    /* dateNaissance */ dateOfBirth,
                                                                    /* lieuNaissance */ placeOfBirth,
                                                                    /* sex */ isGender,
                                                                    /* email */ email,
                                                                    /* nationalite */ nationality,
                                                                    /* numTelephone */ phoneNumber,
                                                                    /* nomUtilisateur */ username,
                                                                    /* motPasse */ password,
                                                                    /* lastLogin */ null,
                                                                    /* dateCreation */ dateTime
                                                            );
                                                            register(personne, String.valueOf(nationalNumber), email, password);


                                                        } else {
                                                            pd.dismiss();
                                                            usernameEditText.setError("Erreur: Username is not unique. Please choose a different username.");

                                                        }
                                                    }
                                                });

                                            }else{
                                                pd.dismiss();
                                                confirmPasswordEditText.setError("Password not matche");
                                            }

                                        }else {
                                            pd.dismiss();
                                            passwordEditText.setError("Password too short!");

                                        }

                                    } else {
                                        pd.dismiss();
                                        emailEditText.setError("Erreur: Email is not unique. Please choose a different Email.");

                                    }
                                }


                            });
                        }else {
                            pd.dismiss();
                            emailEditText.setError("Please enter a valid email address");
                        }

                    }else {
                        pd.dismiss();
                        numTelephoneEditText.setError("Invalid phone number. Please enter a 10-digit number starting with 0.");

                    }


                } else {
                    showMessage(getEmptyFieldsMessage2());
                }

            }
        });






    }
    private void addAdmin(Admin admin) {
        auth.createUserWithEmailAndPassword(admin.getEmail(), admin.getMotPasse()).addOnCompleteListener(activity_main_register.this, task -> {

            if (task.isSuccessful()){

        reference = FirebaseDatabase.getInstance().getReference().child("Admin");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Generate a unique ID for the new admin
                admin.setId(user.getUid());
        // Set the admin data under the generated adminId
        reference.child(user.getUid()).setValue(admin).addOnCompleteListener(innerTask -> {
            if (innerTask.isSuccessful()) {

                sendVerificationEmail();
                showMessage("User created. Please check your email for verification.");

                Intent intent = new Intent(activity_main_register.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                showMessage("Failed to write user data to the database: " + innerTask.getException().getMessage());
            }
        });
            }else {
                showMessage("You can't register with this email and password: " + task.getException().getMessage());
            }});
    }

    private void register(Personne personne, String id, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity_main_register.this, task -> {
            pd.dismiss();
            if (task.isSuccessful()) {
                FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                // Get new FCM registration token
                                String token = task.getResult();
                                personne.setToken(token);
                                Log.d("MyTag", "MYAPP   "+ token);

                FirebaseUser firebaseUser = auth.getCurrentUser();
                String userid = id;

                                reference = FirebaseDatabase.getInstance().getReference().child("Personne").child(userid);

                                reference.setValue(personne).addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {


                                        sendVerificationEmail();
                                        showMessage("User created. Please check your email for verification.");

                                        Intent intent = new Intent(activity_main_register.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        showMessage("Failed to write user data to the database: " + innerTask.getException().getMessage());
                                    }
                                });


                                }else {
                                Log.w("MyTag", "Fetching FCM registration token failed", task.getException());
                                return;

                            }


                            }
                        });



            } else {
                showMessage("You can't register with this email and password: " + task.getException().getMessage());
            }
        });
    }
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showMessage("Verification email sent to " + user.getEmail());
                            } else {
                                showMessage("Failed to send verification email: " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }


    private boolean isValidEmail(String email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValid) {
            Log.d("MyTag", "Please enter a valid email address");
        }
        return isValid;
    }
    private boolean checkPhoneNumber( String phoneNumber) {
        boolean t = false;
        if (phoneNumber.length() == 10 && phoneNumber.startsWith("0")) {

            t=true;
        } else {
            // Invalid phone number
            Log.d("MyTag", "Invalid phone number. Please enter a 10-digit number starting with 0.");

        }
        return t ;
    }

    public interface UsenameCheckCallback {
        void onUsernameCheckComplete(boolean isUnique);
    }
    private void checkUsername(String username, activity_main_register.UsenameCheckCallback callback) {
        DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");

        personneRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUnique = !dataSnapshot.exists();
                callback.onUsernameCheckComplete(isUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MyTag", "checking username uniqueness: " + databaseError.getMessage());

                callback.onUsernameCheckComplete(false); // Assume not unique in case of error
            }
        });
    }

    public interface EmailCheckCallback {
        void onEmailCheckComplete(boolean isUnique);
    }
    private void checkEmail(String email, activity_main_register.EmailCheckCallback callback) {
        DatabaseReference personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");

        personneRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUnique = !dataSnapshot.exists();
                callback.onEmailCheckComplete(isUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MyTag", "Error checking Email uniqueness: " + databaseError.getMessage());

                callback.onEmailCheckComplete(false); // Assume not unique in case of error
            }
        });
    }


    private boolean checkPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {

            return true;
        } else {
            // Passwords do not match
            showMessage("Passwords do not match. Please enter the same password in both fields.");
            return false;
        }
    }



    // Method to show l1 and hide l2
    private void showL1() {
        l1.setVisibility(View.VISIBLE);
        l2.setVisibility(View.INVISIBLE);
    }

    // Method to show l2 and hide l1
    private void showL2() {
        l1.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.VISIBLE);
    }

    // Method to handle the "Next" button click
    public void viewNextClicked(View view) {
        if (checkFieldsNotEmpty()) {
            showL2();
        } else {
            showMessage(getEmptyFieldsMessage());
        }
    }


    // Method to handle the "Back" button click
    public void viewBackClicked(View view) {
        showL1();
    }
    public void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
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
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the exception as needed
        }
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
    private boolean isGenderSelected() {
        // Get the selected item position in the Spinner
        int selectedPosition = genderSpinner.getSelectedItemPosition();

        // Check if the selected position is not the default (0)
        return selectedPosition != 0;
    }
    private boolean checkFieldsNotEmpty() {
        return !isEditTextEmpty(firstNameEditText) &&
                !isEditTextEmpty(lastNameEditText) &&
                !isEditTextEmpty(nationalNumberEditText) &&
                !isEditTextEmpty(dateOfBirthEditText) &&
                !isEditTextEmpty(placeOfBirthEditText) &&
                isGenderSelected();
    }


    private boolean checkFieldsNotEmpty2() {
        return !isEditTextEmpty(numTelephoneEditText) &&
                !isEditTextEmpty(nationalityEditText) &&
                !isEditTextEmpty(usernameEditText) &&
                !isEditTextEmpty(emailEditText) &&
                !isEditTextEmpty(passwordEditText) &&
                !isEditTextEmpty(confirmPasswordEditText) ;

    }
    private String getEmptyFieldsMessage() {
        StringBuilder emptyFieldsMessage = new StringBuilder("Empty fields:");

        int emptyCount = 0;

        if (isEditTextEmpty(firstNameEditText)) {
            firstNameEditText.setError("First Name required");
            emptyCount++;
        }

        if (isEditTextEmpty(lastNameEditText)) {
            lastNameEditText.setError("Last Name required");

            emptyCount++;
        }

        if (isEditTextEmpty(nationalNumberEditText)) {
            nationalNumberEditText.setError("National Numbe required");
            emptyCount++;
        }

        if (isEditTextEmpty(dateOfBirthEditText)) {
            dateOfBirthEditText.setError("Date Of Birth  required");

            emptyCount++;
        }

        if (isEditTextEmpty(placeOfBirthEditText)) {
            placeOfBirthEditText.setError("Place Of Birth required");

            emptyCount++;
        }

        if (!isGenderSelected()) {
            emptyFieldsMessage.append("\n- Gender");
            emptyCount++;
        }

        // Show the message only if there are 2 or fewer empty fields
        if (emptyCount <= 2) {
            return "Empty fields, please fill in the required information.";
        } else {
            return " ";
        }
    }

    private String getEmptyFieldsMessage2() {
        StringBuilder emptyFieldsMessage = new StringBuilder("Empty fields");

        int emptyCount = 0;


        if (isEditTextEmpty(numTelephoneEditText)) {
            numTelephoneEditText.setError("Phone number required");
            emptyCount++;
        }

        if (isEditTextEmpty(nationalityEditText)) {
            nationalityEditText.setError("nationality required");
            emptyCount++;
        }

        if (isEditTextEmpty(usernameEditText)) {
            usernameEditText.setError("UserName required");
            emptyCount++;
        }

        if (isEditTextEmpty(emailEditText)) {
            emailEditText.setError("Email required");
            emptyCount++;
        }

        if (isEditTextEmpty(passwordEditText)) {
            passwordEditText.setError("Password required");
            emptyCount++;
        }

        if (isEditTextEmpty(confirmPasswordEditText)) {
            confirmPasswordEditText.setError("Validat Password required");
            emptyCount++;
        }


        // Show the message only if there are 2 or fewer empty fields
        if (emptyCount > 2) {
            return "Empty fields, please fill in the required information.";

        } else {
            return " ";
        }
    }




    public void viewLoginClicked(View view){
        Intent register = new Intent(activity_main_register.this, MainActivity.class);
        startActivity(register);
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
