package com.alkalynx.alanfal.alanfal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mehdi.sakout.fancybuttons.FancyButton;

public class BookmarkActivity extends AppCompatActivity {

    //FIREBASE
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    //MISC
    RecyclerView recyclerView;
    EditText bookmarkInput;
    LinearLayoutManager linearLayoutManager;
    FancyButton addBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = (RecyclerView) findViewById(R.id.tagRecycler);
        addBookmark = (FancyButton) findViewById(R.id.addBookmark);
        bookmarkInput = (EditText) findViewById(R.id.bookmarkInput);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);


        //FIREBASE RECYLER
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookmarkModel, BookmarkViewHolder>(BookmarkModel.class, R.layout.item_bookmark, BookmarkViewHolder.class, databaseReference.child("Bookmark").child(firebaseAuth.getCurrentUser().getUid().toString())) {
            @Override
            protected void populateViewHolder(final BookmarkViewHolder viewHolder, BookmarkModel model, final int position) {

                viewHolder.tagTitle.setText(model.getTagName());

            }
        };

        //Adapter Set untuk recyclerview
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        addBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagInput = bookmarkInput.getText().toString();
                if (!tagInput.matches("")) {


                    databaseReference.child("Bookmark").child(firebaseAuth.getCurrentUser().getUid().toString()).push().child("tagName").setValue(bookmarkInput.getText().toString());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            bookmarkInput.setText(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }
        });


    }


    //FIREBASE VIEWHOLDER
    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {

        private TextView tagTitle;

        public BookmarkViewHolder(View itemView) {
            super(itemView);
            tagTitle = (TextView) itemView.findViewById(R.id.tagName);
        }

    }


}
