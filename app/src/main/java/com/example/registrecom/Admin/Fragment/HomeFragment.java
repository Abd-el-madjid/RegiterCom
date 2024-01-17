package com.example.registrecom.Admin.Fragment;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.MainActivity;
import com.example.registrecom.Models.Documents;
import com.example.registrecom.Models.Notification;
import com.example.registrecom.R;
import com.example.registrecom.Models.Demandes;
import com.example.registrecom.bdmanupulation.DatabaseHelper;
import com.example.registrecom.classes.CustomListDAdapter;
import com.example.registrecom.classes.ListItem;
import com.example.registrecom.classes.PdfList;
import com.example.registrecom.classes.PdfListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements CustomListDAdapter.OnItemClickListener {
    long iduser;
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
    private TextView  companyname , companyaddress , companyphone , companyemail , companyactivite,demandedate ;
    private ImageView etatimage,notification;
    private   String etat ;
    private Spinner spinner_filtre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);


         notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");






        spinner_filtre = view.findViewById(R.id.spinner_filtre);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner_filtre.setAdapter(adapter);
        // Set the default selection to "Gender"
        spinner_filtre.setSelection(0);

        // Set the OnItemSelectedListener
        spinner_filtre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                // Call the method to retrieve demands from Firebase with filtering
                setupDatabaseListener(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Set the default selection to "All" when nothing is selected
                spinner_filtre.setSelection(0);

                // Call the method to retrieve all demands from Firebase
                setupDatabaseListener(0);
            }
        });
        databaseHelper =  new DatabaseHelper();


        recyclerView = view.findViewById(R.id.recyclerView);
        noDemandsPanel = view.findViewById(R.id.no_demande_panel);



        noDemandsPanel.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        documentsRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        demandesRef = FirebaseDatabase.getInstance().getReference().child("Demandes");
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        adminRef = FirebaseDatabase.getInstance().getReference().child("Admin");

        currentUser = auth.getCurrentUser();
        String id =  currentUser.getUid();
        Log.d("MyTag", "user id  auth: "+id);

        if (currentUser != null) {
            String UserId = currentUser.getUid();
            Log.d("MyTag", "Current User Email: " + UserId);

            adminRef.orderByChild("id").equalTo(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            String userId = personneSnapshot.getKey();
                            Log.d("MyTag", "User ID found: " + userId);

                            nom = personneSnapshot.child("nom").getValue(String.class);
                            prenom = personneSnapshot.child("prenom").getValue(String.class);

                            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(prenom)) {
                                Toast.makeText(getContext(), "User information is incomplete", Toast.LENGTH_SHORT).show();
                            } else {
                                TextView usernameHint = view.findViewById(R.id.Username_hint);
                                usernameHint.setText(prenom + " " + nom);

                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "User information not found", Toast.LENGTH_SHORT).show();
                        Log.d("MyTag", "User Information Not Found for Email: " + UserId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error fetching user information", Toast.LENGTH_SHORT).show();
                    Log.e("MyTag", "Error fetching user information", databaseError.toException());
                }
            });
        }
        notification = view.findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm sign out");
                builder.setMessage("Are you sure you want to sign out");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                     auth.signOut();
                        dialog2.dismiss();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        getActivity().finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // User clicked "No," do nothing
                        dialog2.dismiss();

                    }
                });

                AlertDialog dialog2 = builder.create();
                dialog2.show();
            }


        });


        return view;
    }

    private void setupDatabaseListener(final int selectedPosition) {
        demandesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<ListItem> itemList = new ArrayList<>();

                    for (DataSnapshot demandeSnapshot : dataSnapshot.getChildren()) {
                        if (demandeSnapshot.exists()) {
                            Demandes demande = demandeSnapshot.getValue(Demandes.class);
                             iduser = demande.getID_demandeur();
                            if (demande != null) {

                                String id_demande = demande.getID_demande();
                                String name = demande.getNomEnterprise();
                                Date k = demande.getDateSoumission();
                                String dateSolution = formatDate(k);
                                etat = demande.getEtat().getLibelle();

                                // Check if all demands should be included or filter by the selected state
                                if (selectedPosition == 0 || selectedPosition == getPositionForEtat(etat)) {
                                    int etatImageResource = getEtatImageResource(etat);
                                    itemList.add(new ListItem(id_demande, name, dateSolution, etatImageResource));
                                }
                            }
                        }
                    }

                    CustomListDAdapter adapter = new CustomListDAdapter(getContext(), itemList, HomeFragment.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                } else {
                    // No demands exist, hide RecyclerView and show the noDemandsPanel
                    recyclerView.setVisibility(View.GONE);
                    noDemandsPanel.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                    Log.d("MyTag", "No demandes found for user UID: " + iduser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching data", databaseError.toException());
            }
        });
    }

    private int getPositionForEtat(String etat) {
        // Map each etat to its corresponding position
        if ("En attente".equals(etat)) {
            return 1;
        } else if ("accepte".equals(etat)) {
            return 2;
        } else if ("refuse".equals(etat)) {
            return 3;
        } else {
            return 0; // Default position for other states
        }
    }

    private int getEtatImageResource(String etat) {
        // Map each etat to its corresponding image resource
        if ("En attente".equals(etat)) {
            return R.drawable.processing;
        } else if ("accepte".equals(etat)) {
            return R.drawable.approved;
        } else if ("refuse".equals(etat)) {
            return R.drawable.rejected;
        } else {
            return R.drawable.empty_one; // Set a default image
        }
    }


    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public void onItemClick(String id_demande) {
        // Call your method to open the dialog with the id_demande

        Toast.makeText(getContext(), "clique demande "+id_demande, Toast.LENGTH_SHORT).show();
        demandesRef.orderByChild("id_demande").equalTo(id_demande).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot demandeSnapshot : dataSnapshot.getChildren()) {
                        if (demandeSnapshot.exists()) {
                            Demandes demande = demandeSnapshot.getValue(Demandes.class);

                            if (demande != null) {
                                Log.d("MyTag", "demade geted and show dialog begins");

                                openDialog(demande);

                            }
                        }
                    }



                } else {

                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                    Log.d("MyTag", "No data found for demande UID: " + id_demande);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching data", databaseError.toException());
            }
        });

    }

    private void openDialog(Demandes demande) {



        Log.d("MyTag", " begins dialogshow");

        // Replace Dialog declaration with a custom view
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.bottomdemande_info_home_admin, null);
        final Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(dialogView);
        Log.d("MyTag", " associated dialog with xml");
        // Calculate the height of the status bar and action bar
        int actionBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            actionBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // Set the dialog's position under the app bar by 40dp
        int offset = actionBarHeight + dpToPx(40); // 40dp
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.y = offset;
            window.setAttributes(layoutParams);
        }


        int maxDialogHeight = dpToPx(300); // 300dp
        ViewGroup.LayoutParams contentParams = dialogView.getLayoutParams();
        if (contentParams.height > maxDialogHeight) {
            contentParams.height = maxDialogHeight;
            dialogView.setLayoutParams(contentParams);
        }


        String id_demande = demande.getID_demande();
        String name = demande.getNomEnterprise();
        Date dateSoumission = demande.getDateSoumission();
        String dateSolution = formatDate(dateSoumission);
        etat = demande.getEtat().getLibelle();
        String adresseEnterprise = demande.getAdresseEnterprise();
        String numeroTelephoneEnterprise = demande.getNumeroTelephoneEnterprise();
        String emailEnterprise = demande .getEmailEnterprise();
        String typeActivite =  demande.getTypeActivite();

        show_pdfs( id_demande);
        Log.d("MyTag", " get demande info");


        supprimer = dialog.findViewById(R.id.validationbutton);
        accept = dialog.findViewById(R.id.acceptebutton);
                refuse = dialog.findViewById(R.id.refusebutton);
        supprimer.setText("delet demande");
        accept.setText("validate demande");
        refuse.setText("refuse demande");
        etatimage  = dialog.findViewById(R.id.etatimage);
        pdfRecyclerView = dialog.findViewById(R.id.pdfRecyclerView);
        companyname = dialog.findViewById(R.id.companyname);
        demandedate = dialog.findViewById(R.id.demandedate);
        companyaddress = dialog.findViewById(R.id.companyaddress);
        companyphone = dialog.findViewById(R.id.companyphone);
        companyemail = dialog.findViewById(R.id.companyemail);
        companyactivite = dialog.findViewById(R.id.companyactivite);
        Log.d("MyTag", " get xml element by id");

        if ("En attente".equals(etat)) {
            etatimage.setImageResource(R.drawable.processing);
        } else if ("accepte".equals(etat)) {
            etatimage.setImageResource(R.drawable.approved);
            refuse.setVisibility(View.GONE);
             accept.setVisibility(View.GONE);
        } else if ("refuse".equals(etat)) {
            etatimage.setImageResource(R.drawable.rejected);
            refuse.setVisibility(View.GONE);
        } else {
            etatimage.setImageResource(R.drawable.empty_one);
        }
        Log.d("MyTag", " show etat image associated image ");
        demandedate.setText(dateSolution) ;
        companyname.setText(name);
        companyaddress.setText( adresseEnterprise);
        companyphone.setText(numeroTelephoneEnterprise);
        companyemail.setText(emailEnterprise);
        companyactivite.setText( typeActivite);
        Log.d("MyTag", " set xml with the demande info");


        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm decline");
                builder.setMessage("Are you sure you want to refuse this request?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // Assuming you have a DatabaseReference for your demands
                        DatabaseReference demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes").child(id_demande);

                        // Set the etat field to "accept"
                        demandeRef.child("etat").setValue("Refuse").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    // Etat set to "accept" successfully
                                    Log.d("MyTag", "Request Refused");

                                    // Add notification to Firebase Notification table
                                    String notificationId = notificationRef.push().getKey();
                                    String title = "Your request has been updated";
                                    String content = "Your request for registre " + name + " has been Refused for more information please contact us";
                                    long personId = demande.getID_demandeur();
                                    Date currentDate = new Date();
                                    // Create a Notification object
                                    Notification newNotification = new Notification(title, content, personId, currentDate);
                                    notificationRef.child(notificationId).setValue(newNotification);

                                    // Send FCM notification to the user
                                } else {
                                    // Setting etat to "accept" failed
                                    Toast.makeText(requireContext(), "Error accepting request", Toast.LENGTH_SHORT).show();
                                    Log.d("MyApp", "Error accepting request");
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // User clicked "No," do nothing
                        dialog2.dismiss();
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog2 = builder.create();
                dialog2.show();
            }


        });


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm Acceptance");
                builder.setMessage("Are you sure you want to accept this request?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // Assuming you have a DatabaseReference for your demands
                        DatabaseReference demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes").child(id_demande);

                        // Set the etat field to "accept"
                        demandeRef.child("etat").setValue("Accepte").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    dialog2.dismiss();
                                    // Etat set to "accept" successfully
                                    Toast.makeText(requireContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                    Log.d("MyTag", "Request accepted");

                                    // Add notification to Firebase Notification table
                                    String notificationId = notificationRef.push().getKey();
                                    String title = "Your request has been updated";
                                    String content = "Your request for registre " + name + " has been accepted";
                                    long personId = demande.getID_demandeur();
                                    Date currentDate = new Date();
                                    // Create a Notification object
                                    Notification newNotification = new Notification(title, content, personId, currentDate);
                                    notificationRef.child(notificationId).setValue(newNotification);

                                    // Send FCM notification to the user
                                } else {
                                    // Setting etat to "accept" failed
                                    Toast.makeText(requireContext(), "Error accepting request", Toast.LENGTH_SHORT).show();
                                    Log.d("MyApp", "Error accepting request");
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        // User clicked "No," do nothing
                        dialog.dismiss();
                        dialog2.dismiss();
                    }
                });

                AlertDialog dialog2 = builder.create();
                dialog2.show();
            }


        });

// Method to send FCM notification



        supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyTag", "clique delet");
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Delet Request");
                builder.setMessage("Are you sure you want to Delet ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        databaseHelper.deleteDemandeById(id_demande, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    databaseHelper.deleteDocumentsByDemandeId(id_demande, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                dialog.dismiss();
                                                // Deletion successful
                                                Toast.makeText(requireContext(), "Request deleted successfully", Toast.LENGTH_SHORT).show();
                                                Log.d("MyTag", "Request deleted successfully");


                                            }
                                            else {
                                                // Deletion failed
                                                Toast.makeText(requireContext(), "Error deleting request", Toast.LENGTH_SHORT).show();
                                                Log.d("MyApp", "Error deleting request");

                                            }
                                        }
                                    }); }
                                else {
                                    // Deletion failed
                                    Toast.makeText(requireContext(), "Error deleting request", Toast.LENGTH_SHORT).show();
                                    Log.d("MyApp", "Error deleting request");

                                }
                            }
                        });





                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
    public void show_pdfs(String id_demande) {
        Log.d("MyTag", "show_pdfs method called");

        documentsRef.orderByChild("idDemande").equalTo(id_demande).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<PdfList> pdfList = new ArrayList<>();

                    for (DataSnapshot documentSnapshot : dataSnapshot.getChildren()) {
                        if (documentSnapshot.exists()) {
                            Documents document = documentSnapshot.getValue(Documents.class);

                            if (document != null && document.getCheminFichier() != null) {
                                pdfList.add(new PdfList(document.getDocumentName(), document.getCheminFichier()));
                            }
                        }
                    }

                    // Use the PdfListAdapter to display the list of documents
                    PdfListAdapter pdfListAdapter = new PdfListAdapter(requireContext(), pdfList, new PdfListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String link) {

                        }
                    });

                    // Set the adapter for the pdfRecyclerView
                    pdfRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    pdfRecyclerView.setAdapter(pdfListAdapter);
                } else {
                    Log.e("MyTag", "No documents found for the specified request");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error retrieving documents: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error retrieving documents: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(R.drawable.logo) // Change this to your notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        android.app.Notification notification = builder.build();
        notificationManager.notify(0, notification);
    }


}
