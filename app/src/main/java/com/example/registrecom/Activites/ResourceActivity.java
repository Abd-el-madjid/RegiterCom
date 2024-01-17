package com.example.registrecom.Activites;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.registrecom.R;

public class ResourceActivity extends AppCompatActivity {
    private RelativeLayout linksLayout ;
    private ImageView iconImageView1, iconImageView2, iconImageView3,back_btn;
    private View firstAccountItem, secondAccountItem,thirdAccountItem;
    private TextView nameTextView1, nameTextView2,nameTextView3, descriptionTextView1, descriptionTextView2, descriptionTextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        // Find the views in the first instance of list_item_account.xml
        firstAccountItem = findViewById(R.id.firstAccountItem);
        iconImageView1 = firstAccountItem.findViewById(R.id.iconImageView);
        nameTextView1 = firstAccountItem.findViewById(R.id.nameTextView);
        descriptionTextView1 = firstAccountItem.findViewById(R.id.discriptionTextView);

        // Set the icon and text in the first instance
        iconImageView1.setImageResource(R.drawable.outline_quiz_24);
        nameTextView1.setText(R.string.faq);
        descriptionTextView1.setText(R.string.faq_description);

        // Set onClickListener for the first account item
        firstAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(ResourceActivity.this, FaqActivity.class);
                startActivity(chatIntent);
            }
        });

        // Find the views in the second instance of list_item_account.xml
        secondAccountItem = findViewById(R.id.secondAccountItem);
        iconImageView2 = secondAccountItem.findViewById(R.id.iconImageView);
        nameTextView2 = secondAccountItem.findViewById(R.id.nameTextView);
        descriptionTextView2 = secondAccountItem.findViewById(R.id.discriptionTextView);

        // Set the icon and text in the second instance
        iconImageView2.setImageResource(R.drawable.baseline_adjust_24);
        nameTextView2.setText(R.string.assistance);
        descriptionTextView2.setText(R.string.assistance_description);

        // Set onClickListener for the second account item
        secondAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(ResourceActivity.this, GuidelineActivity.class);
                startActivity(chatIntent);

            }
        });
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Find the views in the second instance of list_item_account.xml
        thirdAccountItem = findViewById(R.id.thirdAccountItem);
        iconImageView3 = thirdAccountItem.findViewById(R.id.iconImageView);
        nameTextView3 = thirdAccountItem.findViewById(R.id.nameTextView);
        descriptionTextView3 = thirdAccountItem.findViewById(R.id.discriptionTextView);

        // Set the icon and text in the second instance
        iconImageView3.setImageResource(R.drawable.baseline_link_24);
        nameTextView3.setText(R.string.links);
        descriptionTextView3.setText(R.string.links_description);

        // Set onClickListener for the second account item
        thirdAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();

            }


        });


    }
    private void showdialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomlink);

        // Find RelativeLayouts in the dialog layout
        RelativeLayout button1Layout = dialog.findViewById(R.id.Button1Layout);
        RelativeLayout button2Layout = dialog.findViewById(R.id.Button2Layout);
        RelativeLayout button3Layout = dialog.findViewById(R.id.Button3Layout);

        // Set OnClickListener for Button 1 Layout
        button1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.cnrc.org.dz/");
                dialog.dismiss(); // Close the dialog after opening the link
            }
        });

        // Set OnClickListener for Button 2 Layout
        button2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.commerce.gov.dz/fr/faq");
                dialog.dismiss(); // Close the dialog after opening the link
            }
        });

        // Set OnClickListener for Button 3 Layout
        button3Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.commerce.gov.dz/fr/3-natures-dinscription-au-registre-du-commerce");
                dialog.dismiss(); // Close the dialog after handling the click
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void openLink(String url) {
        Uri webpage = Uri.parse(url);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage );
        startActivity(webIntent);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        startActivity(intent);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}