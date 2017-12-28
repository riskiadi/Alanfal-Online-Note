package com.alkalynx.alanfal.alanfal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //Firebase
    DatabaseReference mDatabaseReference;
    DatabaseReference dirUsers;

    private EditText inputemail, inputpassword;
    private Button signButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog mProgressDialog;
    Alert alert;
    CheckBox rememberMeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);

        inputemail = (EditText) findViewById(R.id.inputEmail);
        inputpassword = (EditText) findViewById(R.id.inputPassword);
        signButton = (Button) findViewById(R.id.signButton);
        rememberMeCheck = (CheckBox) findViewById(R.id.rememberMe);

        mAuth = FirebaseAuth.getInstance();

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(true);
                startSignIn();
            }
        });

        //Firebase Database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Get SharedPref
        getPrefRememberMe();

    }


    // LIFE CYCLE
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void startSignIn() {

        dialog(true);
        final String email = inputemail.getText().toString();
        String password = inputpassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            dialog(false);
            alert = new Alert(false, "Error", "Field can not be empty!", LoginActivity.this);
        } else {

            //Pref Remember Me
            rememberPref();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        //JIKA LOGIN BERHASIL
                        FirebaseUser user = mAuth.getCurrentUser();

                        //All Preference Set Here!
                        setPreferenceDisplayName();

                        // SetPref Untuk Email
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor prefEdt;
                        prefEdt = sharedPrefs.edit();
                        prefEdt.putString("prefEmail", email);
                        prefEdt.commit();

                        updateUI(user);
                    } else {
                        // JIKA LOGIN GAGAL
                        updateUI(null);
                        alert = new Alert(false, "Login Failed", "Incorrect email or password combination.", LoginActivity.this);
                    }

                }
            });

        }


    }


    public void createIntent(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // call this to finish the current activity
    }


    private void updateUI(FirebaseUser user) {

        dialog(false);
        if (user != null) {

            user = FirebaseAuth.getInstance().getCurrentUser();
            Boolean verify = user.isEmailVerified();

            if (verify) {

                dirUsers = mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid());
                dirUsers.child("Verify").setValue("True");

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }

    }


    public void dialog(boolean show) {
        if (show) {
            if (mProgressDialog == null) {
                mProgressDialog = new android.app.ProgressDialog(this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Loading...");
            }
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
                mProgressDialog.dismiss();
            }
        }
    }


    //TODO GET USERNAME FROM FIREBASE
    public void setPreferenceDisplayName() {

        // SetPref Untuk DisplayName
        mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //SHARED PREFERENCE
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor prefEdt;
                prefEdt = sharedPrefs.edit();
                prefEdt.putString("prefUser", dataSnapshot.getValue().toString());
                prefEdt.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void rememberPref(){
        if (rememberMeCheck.isChecked()) {
            //ShardPreference
            SharedPreferences rememberMePref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = rememberMePref.edit();
            editor.putBoolean("rememberMe", true);
            editor.putString("rememberEmail", inputemail.getText().toString());
            editor.putString("rememberPassword", inputpassword.getText().toString());
            editor.commit();
        } else {
            //ShardPreference
            SharedPreferences rememberMePref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = rememberMePref.edit();
            editor.putBoolean("rememberMe", false);
            editor.putString("rememberEmail", "");
            editor.putString("rememberPassword", "");
            editor.commit();
        }
    }


    public void getPrefRememberMe(){

        SharedPreferences rememberMePref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        boolean rememberCheck = rememberMePref.getBoolean("rememberMe", false);
        rememberMeCheck.setChecked(rememberMePref.getBoolean("rememberMe", false));

        if(rememberCheck){
            inputemail.setText(rememberMePref.getString("rememberEmail", null).toString());
            inputpassword.setText(rememberMePref.getString("rememberPassword", null).toString());
        }else{
            SharedPreferences.Editor editor = rememberMePref.edit();
            editor.putString("rememberEmail", "");
            editor.putString("rememberPassword", "");
            editor.commit();
            inputemail.setText("");
            inputpassword.setText("");
        }


    }


}
