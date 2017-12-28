package com.alkalynx.alanfal.alanfal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;


public class CreateNoteActivity extends AppCompatActivity {

    //Linear Layout
    RelativeLayout createNoteLayout, createNoteOption, createNoteAuthorElement;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    //get UserID
    String DIR_USERID;
    String ownerName;

    //String bundle (getPostID)
    String bundleNoteID;
    String bundleTitle;

    //Boolean apakah ini pilihan EDIT / VIEW only
    boolean noteEditable = false;
    boolean noteViewOnly = false;

    EditText createNoteTitle, createNoteContent;
    FloatingActionButton editNoteButton;
    FancyButton tagPartner, tagNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //==== Toolbar
        getSupportActionBar().setElevation(0);
        //==== Toolbar

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        createNoteLayout = (RelativeLayout) findViewById(R.id.createNoteLayout);
        createNoteOption = (RelativeLayout) findViewById(R.id.createNoteOption);
        createNoteAuthorElement = (RelativeLayout) findViewById(R.id.createNoteAuthorElement);
        createNoteTitle = (EditText) findViewById(R.id.createNoteTitle);
        createNoteContent = (EditText) findViewById(R.id.createNoteContent);
        tagPartner = (FancyButton) findViewById(R.id.tagPartner);
        tagNote = (FancyButton) findViewById(R.id.tagNote);
        editNoteButton = (FloatingActionButton) findViewById(R.id.editNoteButton);

        //GET INTENT FROM ACTIVITY START
        Intent intentNoteItem = getIntent();
        Bundle bundle = intentNoteItem.getExtras();

        if (bundle != null) {
            noteViewOnly = (boolean) bundle.get("noteViewOnly");
            noteEditable = (boolean) bundle.get("noteEditable");
            bundleNoteID = (String) bundle.get("getNoteID");
            bundleTitle = (String) bundle.get("noteTitle");
            String bundleContent = (String) bundle.get("noteContent");
            createNoteTitle.setText(bundleTitle);
            createNoteContent.setText(bundleContent);
        }
        //GET INTENT FROM ACTIVITY END

        //isViewOnly
        isViewOnly(noteViewOnly);

        //get User Data
        DIR_USERID = mFirebaseUser.getUid();

        setUsername();

        //Onclick agar EditText Terselect di luar Layout
        createNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNoteLayout.setEnabled(true);
                createNoteContent.requestFocus();
                createNoteContent.setSelection(createNoteContent.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(createNoteContent, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        //Floating Button Action
        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteViewOnly = false;
                isViewOnly(noteViewOnly);
                supportInvalidateOptionsMenu();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    //Toolbar getIcon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (!noteViewOnly) {
            menuInflater.inflate(R.menu.create_note, menu);
        } else {
            menuInflater.inflate(R.menu.create_note_view, menu);
        }
        return true;
    }

    //Toolbar Action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addNoteBtn:
                if (!noteEditable) {
                    postUserNote();
                } else {
                    editUserNote();
                }
                return true;

            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(bundleTitle);
                builder.setMessage("Are you sure you want to delete this note?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Notes").child(DIR_USERID).child(bundleNoteID).removeValue();
                        Intent intent = new Intent(CreateNoteActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.show();

                return true;
        }
        return false;
    }


    private void postUserNote() {

        String noteTitle, noteContent;
        noteTitle = createNoteTitle.getText().toString();
        noteContent = createNoteContent.getText().toString();

        if (!noteTitle.isEmpty() && !noteContent.isEmpty() || noteTitle.isEmpty() && !noteContent.isEmpty()) {

            if (noteTitle.isEmpty()) {
                noteTitle = "(No Title)";
            }

            NoteModel model = new NoteModel(ownerName.toString(), noteTitle, noteContent, "0", "0");

            mDatabaseReference.child("Notes").child(DIR_USERID).push().setValue(model, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(CreateNoteActivity.this, "Save Failed, Database Error!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Intent intent = new Intent(CreateNoteActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {
            return;
        }

    }


    private void editUserNote() {

        String noteTitle, noteContent;
        noteTitle = createNoteTitle.getText().toString();
        noteContent = createNoteContent.getText().toString();

        if (!noteTitle.isEmpty() && !noteContent.isEmpty() || noteTitle.isEmpty() && !noteContent.isEmpty()) {

            if (noteTitle.isEmpty()) {
                noteTitle = "(No Title)";
            }

            // =====

            HashMap<String, Object> result = new HashMap<>();
            result.put("title", noteTitle);
            result.put("content", noteContent);
            mDatabaseReference.child("Notes").child(DIR_USERID).child(bundleNoteID).updateChildren(result);

            // =====

            Intent intent = new Intent(CreateNoteActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {
            return;
        }

    }


    private void isViewOnly(boolean noteViewOnly) {
        if (noteViewOnly) { // keadaan mode VIEW
            editNoteButton.show();
            createNoteOption.setVisibility(View.GONE);
            createNoteAuthorElement.setVisibility(View.VISIBLE);
            createNoteTitle.setSingleLine(false);
            createNoteLayout.setEnabled(false);
            activateEditText(false);

        } else { // keadaan mode EDIT
            editNoteButton.hide();
            createNoteOption.setVisibility(View.VISIBLE);
            createNoteAuthorElement.setVisibility(View.GONE);
            createNoteTitle.setSingleLine(true);
            createNoteTitle.setMaxLines(1);
            createNoteLayout.setEnabled(true);
            activateEditText(true);
        }
    }


    private void activateEditText(boolean modeEdit) { // agar edittext tidak bisa di klik pada waktu preview note
        createNoteTitle.setFocusable(modeEdit);
        createNoteTitle.setFocusableInTouchMode(modeEdit);
        createNoteTitle.setClickable(modeEdit);
        createNoteTitle.setLongClickable(modeEdit);
        createNoteContent.setFocusable(modeEdit);
        createNoteContent.setFocusableInTouchMode(modeEdit);
        createNoteContent.setClickable(modeEdit);
        createNoteContent.setLongClickable(modeEdit);
    }


    //TODO GET USERNAME FROM FIREBASE
    public void setUsername() {
        mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void updateUI(FirebaseUser user) {
        if (user != null) {
            setUsername();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!noteEditable) {
            postUserNote();
        } else {
            editUserNote();
        }
    }


}
