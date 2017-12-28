package com.alkalynx.alanfal.alanfal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Firebase
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    //NavHeader
    TextView toolbar_title, nameHeader, emailHeader;
    View headerView;

    //Userdata
    String userName;
    String userEmail;
    long userNotes;
    long userPartner;

    //Toolbar Option Menu
    int navOpen = 1;

    //Notifikasi Counter
    TextView notif_badge;
    int notifCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Nav Banner
        headerView = navigationView.getHeaderView(0);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        nameHeader = (TextView) headerView.findViewById(R.id.navNameTxt);
        emailHeader = (TextView) headerView.findViewById(R.id.navEmailTxt);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Firebase Get User Data
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //GET NOTIFICATION FROM FIREBASE
        mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Notif").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String getNotif = dataSnapshot.getValue().toString();
                    notifCount = Integer.parseInt(getNotif);
                    setupBadge();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //GET NOTIFICATION FROM FIREBASE

        // ==== BOTOM NAVBAR ====
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_home, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_partner, "Partner"))
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_me, "Me"))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {

                if (position == 0) {
                    navOpen = 1;
                    invalidateOptionsMenu();
                } else if (position == 1) {
                    navOpen = 2;
                    invalidateOptionsMenu();
                } else {
                    navOpen = 3;
                    invalidateOptionsMenu();
                }

            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
        // ==== BOTOM NAVBAR ====

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.home_toolbar, menu);

        if (navOpen == 1) {

            menu.findItem(R.id.action_Notify).setVisible(true);
            menu.findItem(R.id.action_addNote).setVisible(true);
            menu.findItem(R.id.action_AddPartner).setVisible(false);

            //Notification Badge Div
            final MenuItem menuItem = menu.findItem(R.id.action_Notify);
            View actionView = MenuItemCompat.getActionView(menuItem);
            notif_badge = (TextView) actionView.findViewById(R.id.notif_badge);
            setupBadge();
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOptionsItemSelected(menuItem);
                    Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                    startActivity(intent);
                }
            });
            //Notification Badge Div

            toolbar_title.setText("Notes");
            HomeFragment fragment1 = new HomeFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frameLayout, fragment1, "FragmentName");
            fragmentTransaction1.commit();

        } else if (navOpen == 2) {

            menu.findItem(R.id.action_Notify).setVisible(false);
            menu.findItem(R.id.action_addNote).setVisible(false);
            menu.findItem(R.id.action_AddPartner).setVisible(true);

            toolbar_title.setText("Partner");
            PartnerFragment fragment2 = new PartnerFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.frameLayout, fragment2, "FragmentName");
            fragmentTransaction2.commit();

        } else if (navOpen == 3) {

            menu.findItem(R.id.action_Notify).setVisible(false);
            menu.findItem(R.id.action_addNote).setVisible(false);
            menu.findItem(R.id.action_AddPartner).setVisible(true);

            toolbar_title.setText("Profile");
            ProfileFragment fragment3 = new ProfileFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction3.replace(R.id.frameLayout, fragment3, "FragmentName");
            fragmentTransaction3.commit();

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentSetting = new Intent(this, SettingsActivity.class);
                startActivity(intentSetting);
                return true;
            case R.id.action_addNote:
                Intent intentAddBookmark = new Intent(this, BookmarkActivity.class);
                startActivity(intentAddBookmark);
                return true;
            case R.id.action_AddPartner:
                Intent intentAddPartner = new Intent(this, AddPartnerActivity.class);
                startActivity(intentAddPartner);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_note) {
            Intent intent = new Intent(this, CreateNoteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage_bookmark) {
            Intent intentAddPartner = new Intent(this, BookmarkActivity.class);
            startActivity(intentAddPartner);
        } else if (id == R.id.nav_invites) {
            Intent intentAddPartner = new Intent(this, AddPartnerActivity.class);
            startActivity(intentAddPartner);
        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            setUsername();
            setNotes();
            setPartner();
            userEmail = user.getEmail();
            emailHeader.setText(user.getEmail());
        } else {
            Intent intent = new Intent(HomeActivity.this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    //TODO GET USERNAME FROM FIREBASE
    public void setUsername() {

        mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();
                nameHeader.setText(userName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //TODO GET NOTES FROM FIREBASE
    public void setNotes() {
        mDatabaseReference.child("Notes").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNotes = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //TODO GET PARTNER FROM FIREBASE
    public void setPartner() {
        mDatabaseReference.child("Relationship").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPartner = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Notification Badge Text =====
    public void setupBadge() {

        if (notif_badge != null) {
            if (notifCount == 0) {
                if (notif_badge.getVisibility() != View.GONE) {
                    notif_badge.setVisibility(View.GONE);
                }
            } else {
                notif_badge.setText(String.valueOf(Math.min(notifCount, 99)));
                if (notif_badge.getVisibility() != View.VISIBLE) {
                    notif_badge.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    //Notification Badge Text =====


    //User Data Center {For Profile}
    public String getName() {
        return userName;
    }

    public String getEmail() {
        return userEmail;
    }

    public long getNotes() {
        return userNotes;
    }

    public long getPartner() {
        return userPartner;
    }

}
