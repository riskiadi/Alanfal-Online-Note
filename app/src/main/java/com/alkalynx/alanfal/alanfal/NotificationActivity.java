package com.alkalynx.alanfal.alanfal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActivity extends AppCompatActivity {

    //FIREBASE
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    DatabaseReference databaseReference;

    //Misc
    LinearLayoutManager linearLayoutManager;
    RecyclerView notificationRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationRecycler = (RecyclerView) findViewById(R.id.notificationRecycler);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        notificationRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        //Initial AFTER READ NOTIFICATION
        afterReadNotification();

        //FIREBASE ADAPTER
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NotificationModel, NotificationViewHolder>(NotificationModel.class, R.layout.item_notification, NotificationViewHolder.class, databaseReference.child("Notification").child(mFirebaseUser.getUid().toString())) {
            @Override
            protected void populateViewHolder(final NotificationViewHolder viewHolder, NotificationModel model, final int position) {

                final String getuserID = model.getNotifUserID().toString();
                final String getuserName = model.getNotifUserName().toString();

                viewHolder.notifTitle.setText(model.getNotifUserName() + " " + model.getNotifUserTitle());
                viewHolder.notifUserDate.setReferenceTime(model.getNotifUserDate());

                viewHolder.itemNotifLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(NotificationActivity.this, AcceptPartner.class);
                        intent.putExtra("userID", getuserID);
                        intent.putExtra("userName", getuserName);
                        startActivity(intent);
                    }
                });

            }
        };

        //Adapter Set untuk recyclerview
        notificationRecycler.setLayoutManager(linearLayoutManager);
        notificationRecycler.setAdapter(firebaseRecyclerAdapter);

    }

    //FIREBASE VIEWHOLDER
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout itemNotifLayout;
        private TextView notifTitle;
        private RelativeTimeTextView notifUserDate;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            itemNotifLayout = (LinearLayout) itemView.findViewById(R.id.itemNotifLayout);
            notifTitle = (TextView) itemView.findViewById(R.id.notifTitle);
            notifUserDate = (RelativeTimeTextView) itemView.findViewById(R.id.notifDate);

        }

    }

    //Init after read notification
    public void afterReadNotification() {

        DatabaseReference notifData = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid().toString()).child("Notif");
        notifData.setValue(0);

    }


}
