package com.example.registrecom.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;  // Add this import statement
import android.widget.Button;
import android.widget.ImageButton;

import com.example.registrecom.R;
import com.example.registrecom.classes.Faq;
import com.example.registrecom.classes.FaqAdapter;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private  RecyclerView recyclerView;
    private ImageButton back_btn;
    private List<Faq> faq_list;
    private Button users,general,buisnes_regitre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        recyclerView = findViewById(R.id.faq_list);

        users= findViewById(R.id.users);
        general= findViewById(R.id.general);
        buisnes_regitre= findViewById(R.id.buisnes_registre);

        ArrayList<Faq> userFaqList = new ArrayList<>();
        ArrayList<Faq> commerceFaqList = new ArrayList<>();
        ArrayList<Faq> generalFaqList = new ArrayList<>();
        // Populating User FAQs
        userFaqList.add(new Faq("How do I create a user account?", "To create a user account, download the RegCom app, and follow the on-screen instructions. Provide your name, email, and phone number to set up your account."));
        userFaqList.add(new Faq("What information is needed for user registration?", "User registration requires basic information such as your name, email, and phone number for account creation."));
        userFaqList.add(new Faq("How can I recover my password?", "If you forget your password, you can use the 'Forgot Password' feature on the login screen to reset it."));
        userFaqList.add(new Faq("Is my personal information secure?", "Yes, we take the security of your personal information seriously. We use encryption and secure authentication methods to protect your data."));
        userFaqList.add(new Faq("Can I change my registered email address?", "Yes, you can update your email address in the account settings. Go to the 'Settings' section in the app to make changes."));
        userFaqList.add(new Faq("What should I do if I encounter issues during registration?", "If you encounter any issues during registration, contact our support team through the app's messaging feature for assistance."));
        userFaqList.add(new Faq("Can I use the same account on multiple devices?", "Yes, you can use your account on multiple devices. Simply log in with your credentials on each device."));
        userFaqList.add(new Faq("How do I delete my account?", "To delete your account, go to the 'Account Settings' section and choose the 'Delete Account' option. Follow the prompts to confirm."));

        // Populating Commerce FAQs
        commerceFaqList.add(new Faq("What is 'Registre de commerce'?", "'Registre de commerce' refers to the official register of businesses. In the context of RegCom, it is a part of the application process where businesses can submit and track their registration requests online."));
        commerceFaqList.add(new Faq("How long does the business registration process take?", "The processing time varies, but you can track the status of your business registration in real-time using the 'Track Request' feature."));
        commerceFaqList.add(new Faq("What documents are required for business registration?", "The required documents may include identification documents, certificates, contracts, and other relevant materials. Check the registration form for specifics."));
        commerceFaqList.add(new Faq("Can I register multiple businesses with the same account?", "Yes, you can register multiple businesses using the same account. Each business will have a separate application and tracking process."));
        commerceFaqList.add(new Faq("Is there a fee for business registration?", "Yes, there may be fees associated with business registration. The app will provide details on the applicable fees during the registration process."));
        commerceFaqList.add(new Faq("What is the role of administrators in the business registration process?", "Administrators oversee and manage the entire business registration process. They review documents, communicate with users, and make decisions on applications."));
        commerceFaqList.add(new Faq("Can I edit my business information after registration?", "Limited edits may be possible after registration. Contact the support team through the app's messaging feature for assistance with specific changes."));
        commerceFaqList.add(new Faq("How do I renew my business registration?", "Renewal processes vary. The app will provide notifications and guidance when it's time to renew your business registration."));

        // Populating General FAQs
        generalFaqList.add(new Faq("What is RegCom?", "RegCom is a mobile application designed to simplify and enhance the business registry process. It allows users to submit and track their business registry requests online, providing a user-friendly experience."));
        generalFaqList.add(new Faq("How do I get started with RegCom?", "Simply download the RegCom app, create a personal account by providing your name, email, and phone number, and start submitting your business registry requests."));
        generalFaqList.add(new Faq("What information is needed in the registration form?", "The registration form collects personal details, business information, and other necessary data required for the business registry process."));
        generalFaqList.add(new Faq("Can I track the status of my application in real-time?", "Yes, you can. RegCom provides real-time tracking, allowing you to monitor the progress of your application from submission to approval. You'll receive notifications for important updates."));
        generalFaqList.add(new Faq("How do I upload required documents?", "Easily upload necessary documents directly from the app. This may include identification documents, certificates, contracts, or any other documents needed for the business registry process."));
        generalFaqList.add(new Faq("Is my data and uploaded documents secure?", "Yes, your data and documents are secure. RegCom employs encryption and secure authentication methods to ensure the confidentiality and integrity of your information."));
        generalFaqList.add(new Faq("Can I communicate with authorities through the app?", "Absolutely. RegCom features a built-in messaging system, allowing you to communicate directly with authorities. Ask questions, seek clarification, and receive responses within the app."));
        generalFaqList.add(new Faq("What kind of notifications will I receive?", "You'll receive notifications for important updates, such as the status of your application, requests for additional information, and approval notices."));


        setRecyclerView(generalFaqList);
        general.setBackgroundResource(R.drawable.button_pressed);
        general.setTextColor(getResources().getColor(R.color.darkgrey));
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.setTextColor(getResources().getColor(R.color.whiteTextColorF));
                general.setTextColor(getResources().getColor(R.color.darkgreyF));

                buisnes_regitre.setTextColor(getResources().getColor(R.color.darkgreyF));

                general.setBackgroundResource(R.drawable.button_normal);
                users.setBackgroundResource(R.drawable.button_pressed);
                buisnes_regitre.setBackgroundResource(R.drawable.button_normal);
                setRecyclerView(userFaqList);
            }
        });
        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.setTextColor(getResources().getColor(R.color.darkgreyF));
                general.setTextColor(getResources().getColor(R.color.whiteTextColorF));

                buisnes_regitre.setTextColor(getResources().getColor(R.color.darkgreyF));

                general.setBackgroundResource(R.drawable.button_pressed);
                users.setBackgroundResource(R.drawable.button_normal);
                buisnes_regitre.setBackgroundResource(R.drawable.button_normal);
                setRecyclerView(generalFaqList);
            }
        });
        buisnes_regitre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.setTextColor(getResources().getColor(R.color.darkgreyF));
                general.setTextColor(getResources().getColor(R.color.darkgreyF));

                buisnes_regitre.setTextColor(getResources().getColor(R.color.whiteTextColorF));

                setRecyclerView(commerceFaqList);
                general.setBackgroundResource(R.drawable.button_normal);
                users.setBackgroundResource(R.drawable.button_normal);
                buisnes_regitre.setBackgroundResource(R.drawable.button_pressed);
            }
        });

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setRecyclerView( ArrayList<Faq> list) {
        FaqAdapter faqAdapter = new FaqAdapter(list);
        recyclerView.setAdapter(faqAdapter);
        recyclerView.setHasFixedSize(true);
    }


}
