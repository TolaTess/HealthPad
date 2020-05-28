package com.tolaotesanya.healthpad.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.activities.business.AllDoctorsActivity;
import com.tolaotesanya.healthpad.fragment.HomeFragment;
import com.tolaotesanya.healthpad.activities.auth.AuthActivity;
import com.tolaotesanya.healthpad.fragment.requests.RequestFragment;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.helper.DisplayScreen;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    //private String user_type;

    HomeFragment homeFragment;
    private FragmentTransaction fragmentTransaction;
    private FirebasePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        presenter = new FirebaseDatabaseLayer();
        attachDrawerNav();
        homeFragment = new HomeFragment();
        setFragment(homeFragment);
        userDisplay();

    }

    private void userDisplay() {
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            sendToStart();
        } else {
            final Menu menu = navigationView.getMenu();
            DatabaseReference userCheck = presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                    .child("user_type");
            userCheck.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
                    String user_type = dataSnapshot.getValue().toString();
                    if (user_type.equals("doctor")) {
                        menu.findItem(R.id.nav_request).setVisible(true);
                        menu.findItem(R.id.nav_search).setVisible(false);
                    } else {
                        menu.findItem(R.id.nav_request).setVisible(false);
                        menu.findItem(R.id.nav_search).setVisible(true);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        View navView = navigationView.getHeaderView(0);
        final CircleImageView header_image = navView.findViewById(R.id.header_image);
        presenter.getmUserDatabase().child(presenter.getMcurrent_user_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                Log.d(TAG, "onDataChange: image" + image);
                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(header_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                setFragment(homeFragment);

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
                break;
            case R.id.nav_chat:
                Intent postIntent = new Intent(this, PostsActivity.class);
                startActivity(postIntent);
                break;
            case R.id.nav_request:
                RequestFragment requestFragment = new RequestFragment();
                setFragment(requestFragment);
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
