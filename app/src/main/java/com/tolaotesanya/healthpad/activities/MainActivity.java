package com.tolaotesanya.healthpad.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
        userDisplay();
        homeFragment = new PostsFragment();
        setFragment(homeFragment);
        onlineCheck();

    }

    private void onlineCheck() {
        if (presenter.getMcurrent_user_id() != null) {
            presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                    .child("online").setValue("true");
        }
    }

    private void userDisplay() {
        FirebaseUser mCurrentUser = presenter.getHelper().getMcurrent_user();
        if (mCurrentUser == null) {
            sendToStart();
        } else {
            attachDrawerNav();
            final Menu menu = navigationView.getMenu();
            DatabaseReference userCheck = presenter.getmUserDatabase().child(presenter.getMcurrent_user_id())
                    .child("user_type");
            userCheck.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        this.getSupportActionBar().setTitle("Health Pad");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        View navView = navigationView.getHeaderView(0);
        final CircleImageView header_image = navView.findViewById(R.id.header_image);
        String user_id = presenter.getHelper().getMcurrent_user().getUid();
        presenter.getmUserDatabase().child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(header_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
            case R.id.nav_search:
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
