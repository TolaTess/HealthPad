package com.tolaotesanya.healthpad.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.fragment.posts.PostsFragment;
import com.tolaotesanya.healthpad.fragment.chat.ChatFragment;
import com.tolaotesanya.healthpad.fragment.requests.RequestFragment;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private PostsFragment homeFragment;
    private FirebasePresenter presenter;
    private Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new FirebaseDatabaseLayer(mContext);
        homeFragment = new PostsFragment();
        setFragment(homeFragment);
        attachDrawerNav();
        onlineCheck();

    }

    private void onlineCheck() {
        if (presenter.getMcurrent_user_id() != null) {
            presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                    .child("online").setValue("true");
        }
    }

    private void userDisplay() {

        final Menu menu = navigationView.getMenu();
        DatabaseReference userCheck = presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                .child("user_type");
        userCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("user_type")) {
                    String user_type = dataSnapshot.getValue().toString();
                    Log.d(TAG, "onDataChange: user_type" + user_type);
                    if (user_type.equals("doctor")) {
                        menu.findItem(R.id.nav_request).setVisible(true);
                        menu.findItem(R.id.nav_doctor).setVisible(false);
                    } else {
                        menu.findItem(R.id.nav_request).setVisible(false);
                        menu.findItem(R.id.nav_doctor).setVisible(true);
                    }
                } else {
                    menu.findItem(R.id.nav_request).setVisible(false);
                    menu.findItem(R.id.nav_doctor).setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void attachDrawerNav() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        Toolbar mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setTitle("Health Pad");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View navView = navigationView.getHeaderView(0);
        final CircleImageView header_image = navView.findViewById(R.id.header_image);
        final TextView userNameView = navView.findViewById(R.id.header_name);

        final String user_id = presenter.getHelper().getMcurrent_user().getUid();
        Log.d(TAG, "attachDrawerNav: " + user_id);
        final Menu menu = navigationView.getMenu();
        presenter.getmUserDatabase().child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("user_type")) {
                    String userType = dataSnapshot.child("user_type").getValue().toString();
                    if (userType.equals("doctor")) {
                        menu.findItem(R.id.nav_request).setVisible(true);
                        menu.findItem(R.id.nav_doctor).setVisible(false);
                    } else {
                        menu.findItem(R.id.nav_request).setVisible(false);
                        menu.findItem(R.id.nav_doctor).setVisible(true);
                    }
                } else {
                    menu.findItem(R.id.nav_request).setVisible(false);
                    menu.findItem(R.id.nav_doctor).setVisible(true);
                }
                if (dataSnapshot.hasChild("thumb_image")) {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    userNameView.setText(username);
                    Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(header_image);
                } else {
                    Toast.makeText(MainActivity.this, "Please load an image", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                setFragment(homeFragment);
                break;
            case R.id.nav_doctor:
                presenter.getIntentPresenter().presentIntent(ClassName.AllDoctors, null, null);
                break;
            case R.id.nav_account:
                presenter.getIntentPresenter().presentIntent(ClassName.Account, null, null);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                        .child("online").setValue(ServerValue.TIMESTAMP);
                sendToStart();
                break;
            case R.id.nav_posts:
                presenter.getIntentPresenter().presentIntent(ClassName.Posts, null, null);
                break;
            case R.id.nav_chat:
                ChatFragment chatFragment = new ChatFragment();
                setFragment(chatFragment);
                break;
            case R.id.nav_request:
                RequestFragment requestFragment = new RequestFragment();
                setFragment(requestFragment);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToStart() {
        presenter.getIntentPresenter().presentIntent(ClassName.Auth, null, null);
        finish();
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_cont, fragment);
        fragmentTransaction.commit();
    }


}
