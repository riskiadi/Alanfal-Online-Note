package com.alkalynx.alanfal.alanfal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUserActivity extends AppCompatActivity {

    //Firebase
    DatabaseReference mDatabaseReference;
    DatabaseReference dirUsers;

    private EditText inputName, inputEmail, inputPassword;
    private Button registerButton;
    private FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;
    Alert alert;

    String name;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        getSupportActionBar().setElevation(0);

        inputName = (EditText) findViewById(R.id.inputName);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        registerButton = (Button) findViewById(R.id.registerButton);

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = inputName.getText().toString();
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                createAccount(email, password);
            }
        });


    }


    private void createAccount(String email, String password) {

        if (!validateForm()) {
            return;
        }

        dialog(true);

        //PROSES PENDAFTARAN FIREBASE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Jika SUKSES REGISTRASI
                    dialog(false);
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUserDatabase();
                    updateUI(user);
                } else {
                    // Jika GAGAL REGISTRASI
                    dialog(false);
                    alert = new Alert(false, "Registration Failed", "You must enter a valid email and password!", CreateUserActivity.this);
                    updateUI(null);
                }

                dialog(false);

            }
        });

    }


    private void updateUserDatabase(){ //Set database setelah user terdaftar
        dirUsers = mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid());
        dirUsers.child("Name").setValue(name);
        dirUsers.child("Email").setValue(email);
        dirUsers.child("Notes").setValue("0");
        dirUsers.child("Notif").setValue(0);
        dirUsers.child("Partner").setValue("0");
        dirUsers.child("Premium").setValue("False");
        dirUsers.child("Verify").setValue("False");
    }

    //Proses kirim verifikasi
    private void sendEmailVerification() {

        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Jika sudah terkirim maka?
                            userVerifyActivity();
                        } else {
                            Toast.makeText(CreateUserActivity.this, "gagal terkirim", Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]

    }

    //Proses pindah Activity
    public void userVerifyActivity(){
        Intent intent = new Intent(this, VerificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // call this to finish the current activity
    }


    // PROSES CEK INPUTAN
    private boolean validateForm() {
        boolean valid = true;

        //CEK KONDISI SEMUA INPUTAN HARUS DI ISI!
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email) | TextUtils.isEmpty(password)) {
            alert = new Alert(false, "Error", "You must enter a value for all required fields!", this);
            valid = false;
        }
        return valid;
    }


    //UPDATE KONDISI USER
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            dialog(false);
            sendEmailVerification();
        } else {
            dialog(false);
            Toast.makeText(this, "Failed to Regsiter!", Toast.LENGTH_SHORT).show();
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


    public void loginIntent(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // call this to finish the current activity
    }
}
