package com.alkalynx.alanfal.alanfal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PartnerFragment extends Fragment {

    //FIREBASE
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter mFirebaseAdapter;

    //MISC
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    public PartnerFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partner, container, false);

        //MISC
        recyclerView = (RecyclerView) view.findViewById(R.id.partnerRecycle);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        //FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String getUID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        //FIREBASE ADAPTER
        mFirebaseAdapter = new FirebaseRecyclerAdapter<PartnerModel, PartnerViewHolder>(PartnerModel.class, R.layout.item_partner, PartnerViewHolder.class, databaseReference.child("Relationship").child(getUID)) {
            @Override
            protected void populateViewHolder(final PartnerViewHolder viewHolder, PartnerModel model, final int position) { //mendapatkan data

                databaseReference.child("Users").child(model.getPartnerUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.partnerName.setText(dataSnapshot.child("Name").getValue().toString());
                        viewHolder.partnerEmail.setText(dataSnapshot.child("Email").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };


        //Adapter Set untuk recyclerview
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);

        return view;
    }


    public static class PartnerViewHolder extends RecyclerView.ViewHolder {
        private TextView partnerName;
        private TextView partnerEmail;

        public PartnerViewHolder(View itemView) {
            super(itemView);
            partnerName = (TextView) itemView.findViewById(R.id.partnerName);
            partnerEmail = (TextView) itemView.findViewById(R.id.partnerEmail);
        }

    }

}
