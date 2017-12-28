package com.alkalynx.alanfal.alanfal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddPartnerActivity extends AppCompatActivity {

    //FIREBASE
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    Query searchQuery;
    FirebaseUser currentUser;

    //Profile Box Component
    TextView searchUsername, searchEmail;

    CardView partnerDiv;
    LinearLayout userNotFound;
    EditText searchPartner;
    FancyButton searchAddPartner, deletePartner;
    ProgressBar addPartnerProgress;

    String inputEmail;

    String getPartnerKey;

    //User get name and ID
    String userID;
    String userName;

    //Notify Total
    int tmpNotif;

    //GetDateTime
    long userDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);

        partnerDiv = (CardView) findViewById(R.id.partnerDiv);
        userNotFound = (LinearLayout) findViewById(R.id.userNotFound);
        searchPartner = (EditText) findViewById(R.id.searchPartner);
        searchAddPartner = (FancyButton) findViewById(R.id.searchAddPartner);
        deletePartner = (FancyButton) findViewById(R.id.deletePartner);
        addPartnerProgress = (ProgressBar) findViewById(R.id.addPartnerProgress);


        //Profile Box Component
        searchUsername = (TextView) findViewById(R.id.searchUsername);
        searchEmail = (TextView) findViewById(R.id.searchEmail);

        // ===== FIREBASE START=====

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Ambil data di firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // ===== FIREBASE END=====

        //User get ID
        userID = mAuth.getCurrentUser().getUid();

        //Mendapatkan Nama Berdasarkan UserID
        DatabaseReference getUsername = FirebaseDatabase.getInstance().getReference();
        getUsername.child("Users").child(userID).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Add Button
        searchAddPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GetDateTime == 1
                userDate = new Date().getTime(); //cek waktu


                //Send Notification
                NotificationModel model = new NotificationModel(userID, "added you as partner", userName, userDate);
                databaseReference.child("Notification").child(getPartnerKey).push().setValue(model, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {

                        }
                    }
                });


                //Add Total Notification == 2
                DatabaseReference notifData = FirebaseDatabase.getInstance().getReference().child("Users").child(getPartnerKey).child("Notif");

                notifData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tmpNotif = dataSnapshot.getValue(Integer.class);
                            Intent intent = new Intent(AddPartnerActivity.this, IntroActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                notifData.setValue(tmpNotif + 1);

            }
        });


        //DELETE PARTER
        deletePartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddPartnerActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    //TODO MENCARI USER DARI FIREBASE
    public void getUserData() {

        searchQuery = databaseReference.child("Users").orderByChild("Email").equalTo(inputEmail);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addPartnerProgress.setVisibility(View.GONE);

                if (dataSnapshot.getValue() != null) {

                    //Jika ketemu jalankan animasi
                    playAnimation();

                    for (DataSnapshot getUserKey : dataSnapshot.getChildren()) { // mencari key yang mengandung meail inputan
                        getPartnerKey = getUserKey.getKey().toString();

                        databaseReference.child("Users").child(getPartnerKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                searchUsername.setText(dataSnapshot.child("Name").getValue().toString());
                                searchEmail.setText(dataSnapshot.child("Email").getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        checkRelationship();

                    }

                } else {
                    partnerDiv.setVisibility(View.GONE);
                    userNotFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                partnerDiv.setVisibility(View.GONE);
                userNotFound.setVisibility(View.GONE);
            }
        });

    }


    public void xsearchButton(View view) {
        inputEmail = searchPartner.getText().toString();
        addPartnerProgress.setVisibility(View.VISIBLE);
        getUserData();
    }


    public void checkRelationship() {

        Query queryRelation = databaseReference.child("Relationship").child(userID).orderByChild("partnerUID").equalTo(getPartnerKey);
        queryRelation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    searchAddPartner.setVisibility(View.GONE);
                    deletePartner.setVisibility(View.VISIBLE);
                    return;
                } else {
                    searchAddPartner.setVisibility(View.VISIBLE);
                    deletePartner.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void playAnimation() {
        //ANIMATION
        Animation slideUp = AnimationUtils.loadAnimation(AddPartnerActivity.this, R.anim.anim_slide);
        userNotFound.setVisibility(View.GONE);
        partnerDiv.setVisibility(View.VISIBLE);
        partnerDiv.startAnimation(slideUp);
    }

}
