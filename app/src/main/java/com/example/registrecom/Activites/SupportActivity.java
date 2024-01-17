package com.example.registrecom.Activites;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.registrecom.R;

public class SupportActivity extends AppCompatActivity {
    private ImageView iconImageView1, iconImageView2 ,   back_btn;
    private View firstAccountItem, secondAccountItem;
    private TextView nameTextView1, nameTextView2, descriptionTextView1, descriptionTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        // Find the views in the first instance of list_item_account.xml
        firstAccountItem = findViewById(R.id.firstAccountItem);
        iconImageView1 = firstAccountItem.findViewById(R.id.iconImageView);
        nameTextView1 = firstAccountItem.findViewById(R.id.nameTextView);
        descriptionTextView1 = firstAccountItem.findViewById(R.id.discriptionTextView);

        // Set the icon and text in the first instance
        iconImageView1.setImageResource(R.drawable.outline_chat_bubble_outline_24);
        nameTextView1.setText(R.string.chat);
        descriptionTextView1.setText(R.string.chat_description);

        // Set onClickListener for the first account item
        firstAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(SupportActivity.this, canauxActivity.class);
                startActivity(chatIntent);
            }
        });

        // Find the views in the second instance of list_item_account.xml
        secondAccountItem = findViewById(R.id.secondAccountItem);
        iconImageView2 = secondAccountItem.findViewById(R.id.iconImageView);
        nameTextView2 = secondAccountItem.findViewById(R.id.nameTextView);
        descriptionTextView2 = secondAccountItem.findViewById(R.id.discriptionTextView);

        // Set the icon and text in the second instance
        iconImageView2.setImageResource(R.drawable.baseline_local_phone_24);
        nameTextView2.setText(R.string.helpline);
        descriptionTextView2.setText(R.string.helpline_description);

        // Set onClickListener for the second account item
        secondAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for the second account item (Helpline)
                // Example: Open phone dialer with the helpline number

                String phoneNumber = "04845775883";
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));

                // Check if there is an activity that can handle the intent
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    // There is an activity that can handle the intent, so start the dialer
                    startActivity(dialIntent);
                } else {
                    // Handle the case where no activity can handle the intent (e.g., no phone app installed)
                    // You might want to display an error message or use a different approach.
                    showMessage("No phone app available");
                }

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
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}