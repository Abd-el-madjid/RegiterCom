package com.example.registrecom.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.example.registrecom.Admin.Fragment.HomeFragment;

import com.example.registrecom.Admin.Fragment.MessageFragment;
import com.example.registrecom.Admin.Fragment.UsersFragment;
import com.example.registrecom.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        bottomNavigationView = findViewById(R.id.botom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                handleFragmentNavigation(menuItem.getItemId());
                return true;
            }
        });

        // Set the default fragment when the activity is created
        selectorFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the menu item click based on the item ID
        return handleFragmentNavigation(item.getItemId());
    }

    private boolean handleFragmentNavigation(int itemId) {
        // Handle the navigation for each menu item
        if (itemId == R.id.nav_home) {
            selectorFragment = new HomeFragment();
        } /*else if (itemId == R.id.nav_ressources) {
            selectorFragment = new RessourcesFragment();
        }*/ else if (itemId == R.id.nav_chat) {
            selectorFragment = new MessageFragment();
        } /*else if (itemId == R.id.nav_supprot) {
            selectorFragment = new SupportFragment();
        }*/ else if (itemId == R.id.nav_account) {
            selectorFragment = new UsersFragment();
        }

        // Replace the fragment if needed
        if (selectorFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
            return true;
        }

        return false;
    }
}
