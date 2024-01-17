package com.example.registrecom.Fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.registrecom.classes.NetworkUtils.isNetworkAvailable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.registrecom.MainActivity;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.Models.Personne;
import com.example.registrecom.R;
import com.example.registrecom.Models.Demandes;
import com.example.registrecom.Models.Documents;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DemandAddFragment extends Fragment {
    private LinearLayout l1, l2,l3;
    private Spinner businessActivitySpinner;
    DatabaseReference notificationRef;
    private  DatabaseReference personneRef ;

    private TextView  companyname , companyaddress , companyphone , companyemail , companyactivite ,firstname,lastname,nationality,date_birthday,nationalnumber;
    EditText companyNameEditText, addressEditText, phoneNumberEditText, emailEditText;
    CheckBox declarationCheckBox,declarationCheckBox1;
    Button next, back,back1,next1, Submit_demande, btnUpload , btnOwnershipContractUpload,btnArticlesOfAssociationUpload,btnAnnouncementDocumentUpload,
            btnBirthCertificateUpload,btnCriminalRecordExtractUpload,btnStampDutyReceiptUpload,
            btnRegistrationFeesReceiptUpload,btnAuthorizationUpload;
    StorageReference Storage;
    DatabaseReference demande;
    DatabaseReference Document;
    private Map<String, String> documentUrls = new HashMap<>();
    private String currentFileName ,currentFileNamecheck;
    boolean a, ze, r, e, t, y, u, iuy, d;
    private  Context context;


    private ImageButton selectedFileNameCheck ,selectedFileNameCheckOwnershipContract,selectedFileNameCheckArticlesOfAssociation
           ,selectedFileNameCheckAnnouncementDocument,selectedFileNameCheckBirthCertificate ,selectedFileNameCheckCriminalRecordExtract
           ,selectedFileNameCheckStampDutyReceipt,selectedFileNameCheckRegistrationFeesReceipt ,selectedFileNameCheckAuthorization ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demand_add, container, false);
        personneRef = FirebaseDatabase.getInstance().getReference("Personne");
         context = getContext();
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        companyNameEditText = view.findViewById(R.id.companyName);
        addressEditText = view.findViewById(R.id.address);
        phoneNumberEditText = view.findViewById(R.id.phoneNumber);
        emailEditText = view.findViewById(R.id.email);
        businessActivitySpinner = view.findViewById(R.id.businessActivitySpinner);
        declarationCheckBox = view.findViewById(R.id.declarationCheckBox);
        declarationCheckBox1 = view.findViewById(R.id.declarationCheckBox1);



        firstname= view.findViewById(R.id.firstname);
        lastname= view.findViewById(R.id.lastname);
        nationality= view.findViewById(R.id.nationality);
        date_birthday= view.findViewById(R.id.date_birthday);
        nationalnumber = view.findViewById(R.id.nationalnumber);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            Log.d("MyTag", "Current User Email h: " + currentUserEmail);

            personneRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            // Assuming you have a Personne class to map the data
                            Personne currentPersonne = personneSnapshot.getValue(Personne.class);

                            if (currentPersonne != null) {

                                // Retrieve information from the currentPersonne object
                                String userFirstname = currentPersonne.getPrenom();
                                String userLastname = currentPersonne.getNom();
                                String userNationality = currentPersonne.getNationalite();

                                // Format date of birth
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String userDateBirthday = currentPersonne.getDateNaissance() != null ? dateFormat.format(currentPersonne.getDateNaissance()) : "";
                                String usernationalnumber = String.valueOf(currentPersonne.getId());
                                // You might not have a national number in the Personne class, so adjust accordingly
                                // String userNationalNumber = currentPersonne.getNationalNumber();

                                // Set the retrieved information to the TextViews
                                firstname.setText(userFirstname);
                                lastname.setText(userLastname);
                                nationality.setText(userNationality);
                                date_birthday.setText(userDateBirthday);
                                nationalnumber.setText(usernationalnumber);
                                // nationalnumber.setText(userNationalNumber);

                                // Assuming there is a TextView for the username hint
                                   } else {
                                // Handle the case where the Personne object is null
                                Toast.makeText(getContext(), "User information is unavailable", Toast.LENGTH_SHORT).show();
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
// toggle L1 L2
        l1 = view.findViewById(R.id.L1);
        l2 = view.findViewById(R.id.L2);
        l3 = view.findViewById(R.id.L3);
        // Initially, show l1 and hide l2
        showL3();

















// activite
        businessActivitySpinner = view.findViewById(R.id.businessActivitySpinner);
        if (businessActivitySpinner != null) {


            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.business_activity_options,
                    android.R.layout.simple_spinner_item
            );

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            businessActivitySpinner.setAdapter(adapter);

            // Set the default selection to "Business Activity"
            int defaultPosition = adapter.getPosition("Business Activity");
            businessActivitySpinner.setSelection(0);
        }
// slide betweend demande etape
        next = view.findViewById(R.id.NextDemande);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Your code to handle the click event goes here
                viewNextClickedDemand(view);
            }
        });

        back = view.findViewById(R.id.backDemande);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Your code to handle the click event goes here
                viewBackClickedDemand(view);
            }
        });

        next1 = view.findViewById(R.id.NextDemande1);

        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Your code to handle the click event goes here
                viewNextClickedDemand1(view);
            }
        });
        back1 = view.findViewById(R.id.backDemande1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Your code to handle the click event goes here
                viewBackClickedDemand1(view);
            }
        });
//  upload file

      // btn declaration
        btnUpload = view.findViewById(R.id.btnUpload);
         btnOwnershipContractUpload = view.findViewById(R.id.btnOwnershipContractUpload);
         btnArticlesOfAssociationUpload = view.findViewById(R.id.btnArticlesOfAssociationUpload);
         btnAnnouncementDocumentUpload = view.findViewById(R.id.btnAnnouncementDocumentUpload);
         btnBirthCertificateUpload = view.findViewById(R.id.btnBirthCertificateUpload);
         btnCriminalRecordExtractUpload = view.findViewById(R.id.btnCriminalRecordExtractUpload);
         btnStampDutyReceiptUpload = view.findViewById(R.id.btnStampDutyReceiptUpload);
         btnRegistrationFeesReceiptUpload = view.findViewById(R.id.btnRegistrationFeesReceiptUpload);
         btnAuthorizationUpload = view.findViewById(R.id.btnAuthorizationUpload);
        // check declaratuon
        selectedFileNameCheck= view.findViewById(R.id.selectedFileNameCheck);

        selectedFileNameCheckOwnershipContract = view.findViewById(R.id.OwnershipContractCheck);
        selectedFileNameCheckArticlesOfAssociation = view.findViewById(R.id.ArticlesOfAssociationCheck);
        selectedFileNameCheckAnnouncementDocument = view.findViewById(R.id.AnnouncementDocumentCheck);
        selectedFileNameCheckBirthCertificate = view.findViewById(R.id.BirthCertificateCheck);
        selectedFileNameCheckCriminalRecordExtract = view.findViewById(R.id.CriminalRecordExtractCheck);
        selectedFileNameCheckStampDutyReceipt = view.findViewById(R.id.StampDutyReceiptCheck);
        selectedFileNameCheckRegistrationFeesReceipt = view.findViewById(R.id.RegistrationFeesReceiptCheck);
        selectedFileNameCheckAuthorization = view.findViewById(R.id.AuthorizationCheck);

// check test

        // Populate the hashmap with names and empty URLs
        documentUrls.put("Authorization or License", "");
        documentUrls.put("Registration Fees Payment Receipt", "");
        documentUrls.put("Stamp Duty Payment Receipt", "");
        documentUrls.put("Criminal Record", "");
        documentUrls.put("Birth Certificate (Number 12)", "");
        documentUrls.put("Announcement Document", "");
        documentUrls.put("Articles of Association", "");
        documentUrls.put("Ownership contract", "");
        documentUrls.put("Location request", "");

        System.out.println("hashmap first: "+documentUrls);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
         Storage = FirebaseStorage.getInstance().getReference();
         demande = database.getReference("Demandes");
         Document = database.getReference("Documents");


        a = ze = r = e = t = y = u = iuy = d = false;
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 a = true;
                // Dynamically get the file name based on your app logic
                String fileName = "Location request"; // Replace this with your logic to determine the file name

                // Your code to handle the click event goes here
                SelectFile(fileName, currentFileNamecheck);

            }
        });

        btnOwnershipContractUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ze = true; // Initialize as true
                String fileName = "Ownership contract";
                SelectFile(fileName, currentFileNamecheck);

           }
        });
    

        btnArticlesOfAssociationUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 r = true; // Initialize as true
                String fileName = "Articles of Association";
                SelectFile(fileName, currentFileNamecheck);

           }
        });

        btnAnnouncementDocumentUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 e = true; // Initialize as true
                String fileName = "Announcement Document";
                SelectFile(fileName, currentFileNamecheck);

              }
        });

        btnBirthCertificateUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 t = true; // Initialize as true
                String fileName = "Birth Certificate (Number 12)";
                SelectFile(fileName, currentFileNamecheck);

           }
        });

        btnCriminalRecordExtractUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 y = true; // Initialize as true
                String fileName = "Criminal Record";
                SelectFile(fileName, currentFileNamecheck);
          }
        });

        btnStampDutyReceiptUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 u = true; // Initialize as true
                String fileName = "Stamp Duty Payment Receipt";
                SelectFile(fileName, currentFileNamecheck);
              }
        });

        btnRegistrationFeesReceiptUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 iuy = true; // Initialize as true
                String fileName = "Registration Fees Payment Receipt";
                SelectFile(fileName, currentFileNamecheck);
          }
        });

        btnAuthorizationUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 d = true; // Initialize as true
                String fileName = "Authorization or License";
                SelectFile(fileName, currentFileNamecheck);
            }
        });

// Add more buttons if needed...


/*
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "Location request"; // Replace this with your logic to determine the file name

                selectFile(fileName, new FileSelectionCallback() {
                    @Override
                    public void onFileSelected(boolean success) {
                        if (success) {
                            selectedFileNameCheck.setVisibility(View.VISIBLE);
                        } else {
                            selectedFileNameCheck.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
*/

// submit demande
        Submit_demande = view.findViewById(R.id.b_SubmitDemande);


        Submit_demande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialoglogout();

        }
        });

        return view;
    }






    private void showDialoglogout() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomdemande_info);




               companyname = dialog.findViewById(R.id.companyname);
        companyaddress = dialog.findViewById(R.id.companyaddress);
        companyphone = dialog.findViewById(R.id.companyphone);
        companyemail = dialog.findViewById(R.id.companyemail);
        companyactivite = dialog.findViewById(R.id.companyactivite);



           companyname.setText(companyNameEditText.getText().toString());
        companyaddress.setText( addressEditText.getText().toString());
        companyphone.setText(phoneNumberEditText.getText().toString());
        companyemail.setText(emailEditText.getText().toString());
        companyactivite.setText( businessActivitySpinner.getSelectedItem().toString());





        Button validationbutton = dialog.findViewById(R.id.validationbutton);

        validationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Submit Request");
                builder.setMessage("Are you sure you want to Submit ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        if (areAllLinksProvided()){
                            dialog2.dismiss();
                            dialog.dismiss();
                            Submit_demande();

                        }else{
                            dialog.dismiss();
                            dialog2.dismiss();
                        }


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        dialog2.dismiss();
                        dialog.dismiss();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Add any other necessary setup for your dialog
    }




















    private void Submit_demande() {
        ProgressDialog pd = new ProgressDialog(requireContext());
        pd.setMessage("Please wait ...");
        pd.show();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the current user's email
            String userEmail = currentUser.getEmail();

            // Query the database to find the corresponding personne entry based on email

            Query query = personneRef.orderByChild("email").equalTo(userEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Loop through the results (although there should be only one)
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the personne ID from the dataSnapshot
                            Integer userId = Integer.valueOf(snapshot.getKey());

                            // Now, userId holds the personne ID from the real-time database

                            DatabaseReference demandeRef = FirebaseDatabase.getInstance().getReference("Demandes");
                            String demandeId = demandeRef.push().getKey();

                            // Get current date and time
                            String dateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
                            Date dateTime = null;

                            try {
                                dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateTimeString);
                            } catch (ParseException e) {
                                e.printStackTrace(); // Handle the parsing exception according to your requirements
                            }

                            // Now, the 'dateTime' variable holds the Date object with the current date and time

                            String companyName = companyNameEditText.getText().toString();
                            String address = addressEditText.getText().toString();
                            String phoneNumber = phoneNumberEditText.getText().toString();
                            String email = emailEditText.getText().toString();
                            String businessActivity = businessActivitySpinner.getSelectedItem().toString();

                            // Assuming you have a Demandes class with appropriate constructors
                            Demandes demande = new Demandes(demandeId, userId, dateTime,
                                    companyName, address, phoneNumber, email, businessActivity);

                            // Push the new demand to the database
                            demandeRef.child(demandeId).setValue(demande).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();

                                    if (isNetworkAvailable(requireContext())) {
                                        // Firebase operations
                                        uploadFiles(demandeId);




                                        String notificationId = notificationRef.push().getKey();
                                        String title = context.getString(R.string.notification_title_submit_request);
                                        String content =context.getString(R.string.notification_submit_request);
                                        long personId = userId;
                                        Date currentDate = new Date();
                                        // Create a Notification object
                                        Notification newNotification = new Notification(title, content, personId, currentDate);
                                        notificationRef.child(notificationId).setValue(newNotification);
                                    } else {
                                        Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                    }



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss(); // Dismiss the ProgressDialog on failure
                                    Toast.makeText(requireContext(), "Failed to submit demand", Toast.LENGTH_SHORT).show();
                                    // Add any additional failure handling code here
                                }
                            });
                        }
                    } else {
                        pd.dismiss();
                        Toast.makeText(requireContext(), "User not found in database", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    pd.dismiss();
                    Toast.makeText(requireContext(), "Database error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // ... (previous code)

    private void uploadFiles(String demandeId) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Uploading files...");
        progressDialog.show();

        for (Map.Entry<String, String> entry : documentUrls.entrySet()) {
            String documentName = entry.getKey();
            Uri fileUri = Uri.parse(entry.getValue());

            Log.d("MyTag", "Uploading file: " + fileUri.toString());

            // Use a unique file name based on the current time as part of the storage reference
            String uniqueFileName = System.currentTimeMillis() + "_" + documentName + "_" + demandeId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Upload").child(uniqueFileName);

            storageReference.putFile(fileUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Create a new Documents object
                                String documentId = FirebaseDatabase.getInstance().getReference().push().getKey();
                                Documents document = new Documents(
                                        documentId,

                                        demandeId,
                                        uniqueFileName,
                                        uri.toString(), // Use the download URL as the file path
                                        "PDF", // You can set the document type as needed
                                        new Date() // Use the current date as the download date
                                );

                                // Save the Documents object to the database
                                FirebaseDatabase.getInstance().getReference("Documents").child(documentId).setValue(document)
                                        .addOnSuccessListener(aVoid -> {
                                            progressDialog.dismiss();


                                            // Inside DemandeFragment, where you want to restart the fragment
                                            FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() if you are in a Fragment
                                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                                            // Replace the current fragment with a new instance of DemandeFragment
                                            DemandAddFragment newFragment = new DemandAddFragment();
                                            transaction.replace(R.id.fragment_container, newFragment);

                                            // Add the transaction to the back stack (optional)
                                            transaction.addToBackStack(null);

                                            // Commit the transaction
                                            transaction.commit();

                                            // Remove the old fragment from the fragment manager
                                            Fragment oldFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                                            if (oldFragment != null) {
                                                fragmentManager.beginTransaction().remove(oldFragment).commit();
                                            }

                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            showMessage("Failed to save document details: " + e.getMessage());
                                        });
                            });
                        } else {
                            progressDialog.dismiss();
                            showMessage("File upload failed: " + task.getException().getMessage());
                            Log.e("MyTag", "File upload failed", task.getException());
                        }
                    })
                    .addOnProgressListener(snapshot -> {
                        long totalByteCount = snapshot.getTotalByteCount();
                        long bytesTransferred = snapshot.getBytesTransferred();
                        Log.d("MyTag", "Upload progress: " + bytesTransferred + " / " + totalByteCount);
                        // Calculate and use the progress as needed
                    });
        }
    }

// ... (remaining code)

    private void SelectFile(String fileName, String checkname) {
        currentFileName = fileName;
        currentFileNamecheck = checkname;

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PDF FILE ...."), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String fileUrl = getFileUrlFromUri(fileUri);

            if (containsFileName(currentFileName)) {
                documentUrls.put(currentFileName, fileUrl);
                handleVisibility();
            } else {
                documentUrls.put(currentFileName, fileUrl);
                handleVisibility();
            }

            showMessage("File selected: " + currentFileName);
        }
    }

    private String getFileUrlFromUri(Uri uri) {
        String result;

        try {
            // Use DocumentFile for handling Uris that point to documents
            if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
                DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), uri);
                result = documentFile.getUri().toString();
                Log.d("MyTag", "File URL from DocumentFile: " + result);
            } else {
                // For other Uris, fall back to the previous method
                Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    result = cursor.getString(idx);
                    Log.d("MyTag", "File URL from Cursor: " + result);
                } else {
                    result = uri.getPath();
                    Log.d("MyTag", "File URL using Uri path: " + result);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            result = uri.getPath();
            Log.e("MyTag", "Exception while getting file URL from Uri: " + e.getMessage());
        }

        return result;
    }

    private void handleVisibility() {
        if (a) {
            selectedFileNameCheck.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheck");
            a = false; // Reset the boolean variable
        } else if (ze) {
            selectedFileNameCheckOwnershipContract.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckOwnershipContract");
            ze = false; // Reset the boolean variable
        } else if (r) {
            selectedFileNameCheckArticlesOfAssociation.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckArticlesOfAssociation");
            r = false; // Reset the boolean variable
        } else if (e) {
            selectedFileNameCheckAnnouncementDocument.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckAnnouncementDocument");
            e = false; // Reset the boolean variable
        } else if (t) {
            selectedFileNameCheckBirthCertificate.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckBirthCertificate");
            t = false; // Reset the boolean variable
        } else if (y) {
            selectedFileNameCheckCriminalRecordExtract.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckCriminalRecordExtract");
            y = false; // Reset the boolean variable
        } else if (u) {
            selectedFileNameCheckStampDutyReceipt.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckStampDutyReceipt");
            u = false; // Reset the boolean variable
        } else if (iuy) {
            selectedFileNameCheckRegistrationFeesReceipt.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckRegistrationFeesReceipt");
            iuy = false; // Reset the boolean variable
        } else if (d) {
            selectedFileNameCheckAuthorization.setVisibility(View.VISIBLE);
            System.out.println("Visibility set for selectedFileNameCheckAuthorization");
            d = false; // Reset the boolean variable
        } else {
            System.out.println("Visibility set for default case");
            // Set visibility for default case
        }
    }

    /* private interface FileSelectionCallback {
        void onFileSelected(boolean success);
    }
    private FileSelectionCallback fileSelectionCallback;
    private boolean selectFile(String fileName, FileSelectionCallback callback) {
        currentFileName = fileName;
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PDF FILE ...."), 1);

        // Use the callback to indicate success or failure
        // The actual result will be communicated via onFileSelected callback
        if (callback != null) {
            callback.onFileSelected(false); // Initial assumption, adjust based on your logic
        }
        return false; // Initial assumption, adjust based on your logic
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Your code for handling the result goes here
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the file URL
            Uri fileUri = data.getData();
            String fileUrl = fileUri.toString();

            // Check if the HashMap contains the file name and has a URL
            if (containsFileName(currentFileName)) {
                // Update the URL if it exists
                documentUrls.put(currentFileName, fileUrl);
                System.out.println("hashmap update: "+documentUrls);
                if (fileSelectionCallback != null) {
                    fileSelectionCallback.onFileSelected(true);
                }
            } else {
                // Add the new URL directly
                documentUrls.put(currentFileName, fileUrl);
                System.out.println("hashmap add: " +documentUrls);
                if (fileSelectionCallback != null) {
                    fileSelectionCallback.onFileSelected(true);
                }
            }

            // Show a message or update UI to indicate that the file has been selected
            showMessage("File selected: " + currentFileName);
        }else {
            // Notify the callback about the failure
            if (fileSelectionCallback != null) {
                fileSelectionCallback.onFileSelected(false);
            }
        }
    }
*/
    // Method to check if a file name exists in the HashMap and has a URL
    private boolean containsFileName(String fileName) {
        return documentUrls.containsKey(fileName) && documentUrls.get(fileName) != null;
    }
    private boolean areAllLinksProvided() {
        int emptyLinkCount = 0;

        for (Map.Entry<String, String> entry : documentUrls.entrySet()) {
            String fileName = entry.getKey();
            String fileLink = entry.getValue();

            if (fileLink == null || fileLink.isEmpty()) {
                // Link is empty for this file name
                emptyLinkCount++;
                showMessage("Link is empty for: " + fileName);
            }
        }

        if (emptyLinkCount > 2) {
            showMessage("Field empty: More than 2 links are empty.");
            return false;
        }

        return true;
    }

  /*  private void Upload_Files(Uri data) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        // StorageReference to the "Upload" folder with a unique file name based on the current time
        StorageReference reference = Storage.child("Upload/" + System.currentTimeMillis() + ".pdf");

        // Put the file into storage
        reference.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully

                    // Get the download URL of the uploaded file
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Create a new Documents object
                        String documentId = FirebaseDatabase.getInstance().getReference().push().getKey(); // Generating a new document ID
                        String demandeId = "your_demande_id"; // Replace with the actual demand ID

                        Documents document = new Documents(
                                documentId,
                                demandeId,
                                uri.toString(), // Use the download URL as the file path
                                "PDF", // You can set the document type as needed
                                new Date() // Use the current date as the download date
                        );

                        // Save the Documents object to the database
                        FirebaseDatabase.getInstance().getReference("Documents").child(documentId).setValue(document)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    showMessage("File uploaded successfully");
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    showMessage("Failed to save document details: " + e.getMessage());
                                });
                    });
                })
                .addOnProgressListener(snapshot -> {
                    // This method can be used to track the progress of the upload
                    long totalByteCount = snapshot.getTotalByteCount();
                    long bytesTransferred = snapshot.getBytesTransferred();
                    // Calculate and use the progress as needed
                })
                .addOnFailureListener(e -> {
                    // File upload failed
                    progressDialog.dismiss();
                    showMessage("File upload failed: " + e.getMessage());
                });
    }
*/


    private boolean isValidEmail(String email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValid) {
            showMessage("Please enter a valid email address");
        }
        return isValid;
    }
    private boolean checkPhoneNumber( String phoneNumber) {
        boolean t = false;
        if (phoneNumber.length() == 10 ) {

            t=true;
        } else {
            // Invalid phone number
            showMessage("Invalid phone number. Please enter a 10-digit number starting with 0.");
        }
        return t ;
    }

    public interface EmailCheckCallback {
        void onEmailCheckComplete(boolean isUnique);
    }

    private void checkEmail(String email, EmailCheckCallback callback) {
        DatabaseReference demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes");

        demandeRef.orderByChild("emailEnterprise").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUnique = !dataSnapshot.exists();
                callback.onEmailCheckComplete(isUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Error checking Email uniqueness: " + databaseError.getMessage());
                callback.onEmailCheckComplete(false); // Assume not unique in case of error
            }
        });
    }
    public interface NameCheckCallback {
        void onNameCheckComplete(boolean isUnique);
    }

    private void checkEnterpriseName(String name, NameCheckCallback callback) {
        DatabaseReference demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes");

        demandeRef.orderByChild("nomEnterprise").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUnique = !dataSnapshot.exists();
                callback.onNameCheckComplete(isUnique);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Error checking Name company uniqueness: " + databaseError.getMessage());
                callback.onNameCheckComplete(false); // Assume not unique in case of error
            }
        });
    }
    private void viewBackClickedDemand(View view) {
        showL1();
    }

    private void viewBackClickedDemand1(View view) {
        showL3();
    }

    private void showL1() {
        l1.setVisibility(View.VISIBLE);
        l2.setVisibility(View.INVISIBLE);
        l3.setVisibility(View.INVISIBLE);
    }

    // Method to show l2 and hide l1
    private void showL2() {
        l1.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.VISIBLE);
        l3.setVisibility(View.INVISIBLE);
    }
    private void showL3() {
        l1.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.INVISIBLE);
        l3.setVisibility(View.VISIBLE);
    }
    public void viewNextClickedDemand(View view) {
        // Check if all fields are not empty
        if (checkFieldsNotEmpty()) {
            String companyName = companyNameEditText.getText().toString();

            // Check if the enterprise name is unique
            checkEnterpriseName(companyName, new NameCheckCallback() {
                @Override
                public void onNameCheckComplete(boolean isUnique) {
                    if (isUnique) {
                // Check if the email is in a valid format
                if (isValidEmail(emailEditText.getText().toString())) {
                    String email = emailEditText.getText().toString();
                    // Check if the email is unique
                    checkEmail(email, new EmailCheckCallback() {
                        @Override
                        public void onEmailCheckComplete(boolean isUniqueEmail) {
                            if (isUniqueEmail) {
                                // All checks passed, proceed to the next step (showL2())
                                showL2();
                            } else {
                                showMessage("Erreur: Email is not unique. Please choose a different Email.");
                                showL1();
                            }
                        }
                    });
                } else {
                    // Display error message for invalid email format
                    showL1();
                }

                    } else {
                        showMessage("Erreur: Name is not unique. Please choose a different Name.");
                        showL1();
                    }
                }
            });
        } else {
            showL1();
            // Display error message for empty fields
            showMessage(getEmptyFieldsMessage());
        }
    }

    public void viewNextClickedDemand1(View view) {
        // Check if all fields are not empty
if ( declarationCheckBox1.isChecked()){
    showL1();
}else {
    showMessage("check the declaration first");
}

    }

    private boolean checkFieldsNotEmpty() {
        return !isEditTextEmpty(companyNameEditText) &&
                !isEditTextEmpty(addressEditText) &&
                !isEditTextEmpty(phoneNumberEditText) &&
                !isEditTextEmpty(emailEditText) &&
                isBusinessActivitySelected() &&
                declarationCheckBox.isChecked();
    }
    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
    private boolean isBusinessActivitySelected() {
        // Get the selected item position in the Spinner
        int selectedPosition = businessActivitySpinner.getSelectedItemPosition();

        // Check if the selected position is not the default (0)
        return selectedPosition != 0;
    }
    private String getEmptyFieldsMessage() {
        StringBuilder emptyFieldsMessage = new StringBuilder("Empty fields:");

        int emptyCount = 0;

        if (isEditTextEmpty(companyNameEditText)) {
            companyNameEditText.setError("Company Name is required");


        }

        if (isEditTextEmpty(addressEditText)) {
            addressEditText.setError("Company Name is required");

        }

        if (isEditTextEmpty(phoneNumberEditText)) {
            phoneNumberEditText.setError("Company Name is required");

        }

        if (isEditTextEmpty(emailEditText)) {
            emailEditText.setError("Company Name is required");

        }

        if (!isBusinessActivitySelected()) {
            emptyFieldsMessage.append("\n- Business Activity");
            emptyCount++;
        }

        if (!declarationCheckBox.isChecked()) {
            emptyFieldsMessage.append("\n- Declaration");
            emptyCount++;
        }


            return "Empty fields, please fill in the required information.";

    }
    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean upload_file(String file_name){

showMessage("d");
return true ;
    }


}