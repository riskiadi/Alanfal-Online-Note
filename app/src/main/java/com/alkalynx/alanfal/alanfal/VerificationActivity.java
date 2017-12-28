package com.alkalynx.alanfal.alanfal;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    EditText emailPlain;
    Button signButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        getSupportActionBar().setElevation(0);

        emailPlain = (EditText) findViewById(R.id.emailPlain);
        signButton = (Button) findViewById(R.id.signButton);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String email = user.getEmail();
        emailPlain.setText(email);

    }

    public void resendBtn(View view) {
        sendEmailVerification();
        signButton.setTextColor(Color.LTGRAY);
        signButton.setEnabled(false);
        countDown();
    }


    public void backToLoginBtn(View view) {

        Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }



    private void sendEmailVerification() {
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification();

    }



    public void countDown() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                signButton.setText("Resend verification (" + millisUntilFinished / 1000 + ")");
            }

            public void onFinish() {
                signButton.setText("Resend verification");
                signButton.setTextColor(Color.WHITE);
                signButton.setEnabled(true);
            }
        }.start();
    }



}
