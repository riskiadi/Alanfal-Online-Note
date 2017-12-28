package com.alkalynx.alanfal.alanfal;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseRecyclerAdapter mFirebaseAdapter;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    RelativeLayout noteEmptyIcon;
    ImageView iconImageBook, iconImagePencil;
    TextView iconLabelTitle, iconLabelDesc;

    //Coppy post content to clipboard
    ClipboardManager clipBoardNoteContent;
    ClipData clipNoteContent;

    String anyNotes;

    //Float Button
    FloatingActionButton fab;

    //Note Directory Setting
    static final String DIR_NOTE = "Notes"; //mendapatkan data dari database firebase dari pesan chat
    String DIR_USER;

    // Pop Menu DIALOG
    Dialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.noteProgress);
        noteEmptyIcon = (RelativeLayout) view.findViewById(R.id.noteEmptyIcon);
        recyclerView = (RecyclerView) view.findViewById(R.id.noteRecycle);
        iconImageBook = (ImageView)view.findViewById(R.id.iconImageBook);
        iconImagePencil = (ImageView)view.findViewById(R.id.iconImagePencil);
        iconLabelTitle = (TextView) view.findViewById(R.id.iconLabelTitle);
        iconLabelDesc = (TextView) view.findViewById(R.id.iconLabelDesc);

        clipBoardNoteContent = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        //Icon Animation Set
        Animation animationBook = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_note_book);
        Animation animationPencil = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_note_pencil);
        iconImageBook.startAnimation(animationBook);
        iconImagePencil.startAnimation(animationPencil);


        //Float Button
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference(); //<< Di perlukan untuk database dan deteksi dbase eventlistener

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        //Directory User
        DIR_USER = mFirebaseUser.getUid().toString();

        // Pop Menu DIALOG
        dialog = new Dialog(getActivity());

        if (mFirebaseUser == null) {
            startActivity(new Intent(view.getContext(), IntroActivity.class));
            getActivity().finish();
            return view;
        }

        //FIREBASE ADAPTER
        mFirebaseAdapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(NoteModel.class, R.layout.item_note, NoteViewHolder.class, databaseReference.child(DIR_NOTE).child(DIR_USER)) {
            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, NoteModel model, final int position) { //mendapatkan data

                final DatabaseReference getRefPosition = getRef(position);
                final String getPostKey = getRefPosition.getKey();
                final String getPostContent = model.getContent();
                final String getPostTitle = model.getTitle();

                // Pop Menu DIALOG

                viewHolder.notePop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.setContentView(R.layout.pop_notemenu);
                        CardView copyNote = (CardView) dialog.findViewById(R.id.popCopy);
                        CardView editNote = (CardView) dialog.findViewById(R.id.popEdit);
                        CardView deleteNote = (CardView) dialog.findViewById(R.id.popDelete);

                        copyNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                clipNoteContent = ClipData.newPlainText("noteContent", getPostContent);
                                clipBoardNoteContent.setPrimaryClip(clipNoteContent);
                                Toast.makeText(getContext(), "Note Copied", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        });

                        editNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                                intent.putExtra("noteViewOnly", false);
                                intent.putExtra("noteEditable", true);
                                intent.putExtra("getNoteID", getPostKey);
                                intent.putExtra("noteContent", getPostContent);
                                intent.putExtra("noteTitle", getPostTitle);
                                startActivity(intent);
                            }
                        });

                        deleteNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getPostTitle);
                                builder.setMessage("Are you sure you want to delete this note?");
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseDatabase.getInstance().getReference().child("Notes").child(DIR_USER).child(getPostKey).removeValue();
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();

                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                    }
                });

                // Pop Menu DIALOG


                // Cardview Click Event START
                viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                        intent.putExtra("noteViewOnly", true);
                        intent.putExtra("noteEditable", true);
                        intent.putExtra("getNoteID", getPostKey);
                        intent.putExtra("noteContent", getPostContent);
                        intent.putExtra("noteTitle", getPostTitle);
                        startActivity(intent);
                    }
                });
                // Cardview Click Event END


                if (model.getTitle() != null) {
                    anyNotes = model.getTitle();
                    viewHolder.name.setText(model.getName());
                    viewHolder.title.setText(model.getTitle());
                    viewHolder.content.setText(model.getContent());
                    viewHolder.like.setText(model.getLike());
                    viewHolder.comment.setText(model.getComment());
                }
            }
        };

        //Adapter Set untuk recyclerview
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);


        //TODO UNTUK NAMPILKAN LOADING
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                anyNotes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    //CEK apakah punya notes apa tidak
    public void anyNotes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (mFirebaseAdapter.getItemCount() == 0) {
                    noteEmptyIcon.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    noteEmptyIcon.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //TODO 6 buat view holder
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView name, title, content, like, comment;
        ImageButton notePop;
        private CardView cardview;

        public NoteViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.noteOwner);
            title = (TextView) itemView.findViewById(R.id.noteTitle);
            content = (TextView) itemView.findViewById(R.id.noteContent);
            like = (TextView) itemView.findViewById(R.id.noteLike);
            comment = (TextView) itemView.findViewById(R.id.noteComment);

            notePop = (ImageButton) itemView.findViewById(R.id.notePop);

            cardview = (CardView) itemView.findViewById(R.id.layoutbackground);

        }

    }

}
