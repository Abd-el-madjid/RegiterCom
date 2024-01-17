package com.example.registrecom.Activites;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.registrecom.R;
import com.google.android.material.tabs.TabLayout;

public class GuidelineActivity extends AppCompatActivity {
private ImageButton back_btn ;
    private LinearLayout step1Layout, step2Layout, step3Layout, step4Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guideline);

        // Find your LinearLayouts by their IDs
        step1Layout = findViewById(R.id.step1);
        step2Layout = findViewById(R.id.step2);
        step3Layout = findViewById(R.id.step3);
        step4Layout = findViewById(R.id.step4);

        // Find your TabLayout by its ID
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Set up click listeners for each TabItem
        setupTabClickListener(tabLayout.getTabAt(0), step1Layout);
        setupTabClickListener(tabLayout.getTabAt(1), step2Layout);
        setupTabClickListener(tabLayout.getTabAt(2), step3Layout);
        setupTabClickListener(tabLayout.getTabAt(3), step4Layout);

        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupTabClickListener(TabLayout.Tab tab, final LinearLayout layout) {
        tab.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(layout);
            }
        });
    }

    private void toggleVisibility(LinearLayout layout) {
        // Toggle visibility of the given layout
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }

        // Hide other layouts
        hideOtherLayouts(layout);
    }

    private void hideOtherLayouts(LinearLayout currentLayout) {
        if (currentLayout != step1Layout) {
            step1Layout.setVisibility(View.GONE);
        }
        if (currentLayout != step2Layout) {
            step2Layout.setVisibility(View.GONE);
        }
        if (currentLayout != step3Layout) {
            step3Layout.setVisibility(View.GONE);
        }
        if (currentLayout != step4Layout) {
            step4Layout.setVisibility(View.GONE);
        }
    }
}
