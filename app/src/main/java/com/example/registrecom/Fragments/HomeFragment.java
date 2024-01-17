package com.example.registrecom.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.Activites.NotificationActivity;
import com.example.registrecom.Models.Documents;
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
    private float startY;
    private float initialY;
    private boolean isDragging = false;
    private float screenHeight; // Declare this variable
    private float dialogHeight; // Declare this variable


    private RecyclerView recyclerView,pdfRecyclerView;
    private Map<String, String> documentUrls = new HashMap<>();
    private FirebaseAuth auth;
    private DatabaseReference personneRef, demandesRef,documentsRef,notificationRef;
    private FirebaseUser currentUser;
    private DatabaseHelper databaseHelper ;

    private String nom, prenom;
    private RelativeLayout noDemandsPanel;
    private Button submitdemandeButton   , button,companyfile,supprimer;
    private TextView  companyname , companyaddress , companyphone , companyemail , companyactivite,demandedate , notificationNumber;
private ImageView etatimage;
private LinearLayout notification ;
    private   String etat ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        databaseHelper =  new DatabaseHelper();

        notificationNumber =view.findViewById(R.id.notificationNumber);


        recyclerView = view.findViewById(R.id.recyclerView);
        noDemandsPanel = view.findViewById(R.id.no_demande_panel);

        submitdemandeButton = view.findViewById(R.id.submitdemandeButton);
        submitdemandeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the fragment you want to navigate to
                DemandAddFragment addDemandFragment = new DemandAddFragment();

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Begin a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.fragment_container, addDemandFragment);  // Replace R.id.fragmentContainer with the ID of your fragment container in the layout

                // Add the transaction to the back stack (optional)
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });


        noDemandsPanel.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        documentsRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        demandesRef = FirebaseDatabase.getInstance().getReference().child("Demandes");
        personneRef = FirebaseDatabase.getInstance().getReference().child("Personne");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");

        currentUser = auth.getCurrentUser();
        String id =  currentUser.getUid();
        Log.d("MyTag", "user id  auth: "+id);

        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            Log.d("MyTag", "Current User Email: " + currentUserEmail);

            personneRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot personneSnapshot : dataSnapshot.getChildren()) {
                            long userId = Long.parseLong(personneSnapshot.getKey());
                            Log.d("MyTag", "User ID found: " + userId);


                            notificationRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long notificationCount = 0;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        long idPersonne = snapshot.child("idPersonne").getValue(Long.class); // Note the use of Long instead of long

                                        if (idPersonne != 0 && idPersonne == userId) {
                                            notificationCount++;
                                        }
                                    }

                                    if (notificationCount > 0) {
                                        notificationNumber.setText(String.valueOf(notificationCount));
                                        notificationNumber.setVisibility(View.VISIBLE);
                                    } else {
                                        notificationNumber.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("MyTag", "onCancelled: ", databaseError.toException());
                                }
                            });

                            nom = personneSnapshot.child("nom").getValue(String.class);
                            prenom = personneSnapshot.child("prenom").getValue(String.class);

                            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(prenom)) {
                                Toast.makeText(getContext(), "User information is incomplete", Toast.LENGTH_SHORT).show();
                            } else {
                                TextView usernameHint = view.findViewById(R.id.Username_hint);
                                usernameHint.setText(prenom + " " + nom);
                                setupDatabaseListener(userId);

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
        notification = view.findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser != null) {
                    String currentUserEmail = currentUser.getEmail();
                    Log.d("MyTag", "Current User Email: " + currentUserEmail);
                    Intent notification = new Intent(getActivity(), NotificationActivity.class);
                    startActivity(notification);

                }



            }
        });

        return view;
    }

    private void setupDatabaseListener(long userId) {
        demandesRef.orderByChild("id_demandeur").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<ListItem> itemList = new ArrayList<>();

                    for (DataSnapshot demandeSnapshot : dataSnapshot.getChildren()) {
                        if (demandeSnapshot.exists()) {
                            Demandes demande = demandeSnapshot.getValue(Demandes.class);

                            if (demande != null) {
                                String id_demande = demande.getID_demande();
                                String name = demande.getNomEnterprise();
                                Date k = demande.getDateSoumission();
                                String dateSolution = formatDate(k);
                                 etat = demande.getEtat().getLibelle();

                                int etatImageResource;

                                if ("En attente".equals(etat)) {
                                    etatImageResource = R.drawable.processing;
                                } else if ("accepte".equals(etat)) {
                                    etatImageResource = R.drawable.approved;
                                } else if ("refuse".equals(etat)) {
                                    etatImageResource = R.drawable.rejected;
                                } else {
                                    etatImageResource = R.drawable.empty_one; // Set a default image
                                }

                                itemList.add(new ListItem(id_demande, name, dateSolution, etatImageResource));

                            }
                        }
                    }

                    CustomListDAdapter adapter = new CustomListDAdapter(getContext(), itemList, HomeFragment.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                    // Call your method here when data changes

                    } else {
                    // No demands exist, hide RecyclerView and show the noDemandsPanel
                    recyclerView.setVisibility(View.GONE);
                    noDemandsPanel.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                    Log.d("MyTag", "No demandes found for user UID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("MyTag", "Error fetching data", databaseError.toException());
            }
        });
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
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.bottomdemande_info_home, null);
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
                                supprimer.setText("delet demande");
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
                                    supprimer.setVisibility(View.GONE);
                                } else if ("refuse".equals(etat)) {
                                    etatimage.setImageResource(R.drawable.rejected);
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

}
