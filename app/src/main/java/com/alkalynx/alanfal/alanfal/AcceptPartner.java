package com.alkalynx.alanfal.alanfal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import mehdi.sakout.fancybuttons.FancyButton;

public class AcceptPartner extends AppCompatActivity {

    DatabaseReference databaseReference;

    //Bundle Intent
    String bundleUserID;
    String bundleUserName;

    //User ViewPoint
    String userID;

    //Partner  ViewPoint
    String partnerEmail;

    TextView partner_name, partner_email, partner_notes, partner_friend;
    FancyButton acceptButton, deletePartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_partner);

        partner_name = (TextView) findViewById(R.id.profile_name);
        partner_email = (TextView) findViewById(R.id.profile_email);
        partner_notes = (TextView) findViewById(R.id.profile_notes);
        partner_friend = (TextView) findViewById(R.id.profile_partner);
        acceptButton = (FancyButton) findViewById(R.id.accept_as_partner);
        deletePartner = (FancyButton) findViewById(R.id.deletePartner);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //GET INTENT FROM ACTIVITY START
        Intent intentNoteItem = getIntent();
        Bundle bundle = intentNoteItem.getExtras();

        if (bundle != null) {
            bundleUserID = (String) bundle.get("userID");
            bundleUserName = (String) bundle.get("userName");

            getSupportActionBar().setTitle(bundleUserName);
            partner_name.setText(bundleUserName);

            //get Profile Data
            FirebaseDatabase.getInstance().getReference().child("Users").child(bundleUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    partnerEmail = dataSnapshot.child("Email").getValue().toString();
                    partner_email.setText(partnerEmail);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            finish();
        }


        deletePartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRelationship();
            }
        });


        //CHECK Relationship
        checkRelationship();
        partnerNotes();
        partnerFriend();
    }

    public void relation() {
        //Me viewpoint
        databaseReference.child("Relationship").child(userID).push().child("partnerUID").setValue(bundleUserID);
        //Partner viewpoint
        databaseReference.child("Relationship").child(bundleUserID).push().child("partnerUID").setValue(userID);

        Intent intent = new Intent(AcceptPartner.this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void deleteRelationship() {

        Query queryDeleteMe = databaseReference.child("Relationship").child(userID).orderByChild("partnerUID").equalTo(bundleUserID);
        queryDeleteMe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot getUserKey : dataSnapshot.getChildren()) {
                        String getPartnerKey = getUserKey.getKey().toString();
                        databaseReference.child("Relationship").child(userID).child(getPartnerKey).removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query queryDeletePartner = databaseReference.child("Relationship").child(bundleUserID).orderByChild("partnerUID").equalTo(userID);
        queryDeletePartner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot getUserKey : dataSnapshot.getChildren()) {
                        String getPartnerKey = getUserKey.getKey().toString();
                        databaseReference.child("Relationship").child(bundleUserID).child(getPartnerKey).removeValue();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(AcceptPartner.this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void animateAcceptButton() {
        acceptButton.animate().alpha(0.0f).setDuration(700).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                acceptButton.setVisibility(View.GONE);
            }
        });
    }

    public void checkRelationship() {

        Query queryRelation = databaseReference.child("Relationship").child(userID).orderByChild("partnerUID").equalTo(bundleUserID);
        queryRelation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    acceptButton.setVisibility(View.GONE);
                    deletePartner.setVisibility(View.VISIBLE);
                } else {
                    acceptButton.setVisibility(View.VISIBLE);
                    deletePartner.setVisibility(View.GONE);
                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptButton.setClickable(false);
                            animateAcceptButton();
                            relation();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void partnerNotes() {
        databaseReference.child("Notes").child(bundleUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long getNoteData = dataSnapshot.getChildrenCount();
                partner_notes.setText(Long.toString(getNoteData));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void partnerFriend() {
        databaseReference.child("Relationship").child(bundleUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long getFriendData = dataSnapshot.getChildrenCount();
                partner_friend.setText(Long.toString(getFriendData));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
