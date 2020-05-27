package com.tolaotesanya.healthpad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.activities.business.AllDoctorsActivity;
import com.tolaotesanya.healthpad.fragment.HomeFragment;
import com.tolaotesanya.healthpad.activities.auth.AuthActivity;
import com.tolaotesanya.healthpad.fragment.requests.RequestFragment;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    HomeFragment homeFragment;
    RequestFragment requestFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        attachDrawerNav();
        requestFragment = new RequestFragment();
        homeFragment = new HomeFragment();
        setFragment(requestFragment);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Ensure user is logged in before proceeding to main activity
        final FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            sendToStart();
        }
    }

    public void attachDrawerNav() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        Toolbar mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Health Pad");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");
        switch (item.getItemId()) {
            case R.id.nav_home:
                //setFragment(homeFragment);
                setFragment(requestFragment);
                break;
            case R.id.nav_search:
                Intent allDoctorsIntent = new Intent(this, AllDoctorsActivity.class);
                startActivity(allDoctorsIntent);
                break;
            case R.id.nav_account:
                Intent accountIntent = new Intent(this, AccountActivity.class);
                startActivity(accountIntent);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            case R.id.nav_chat:
                //Fragment
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToStart() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void setFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_cont, fragment);
        fragmentTransaction.commit();
    }


}
