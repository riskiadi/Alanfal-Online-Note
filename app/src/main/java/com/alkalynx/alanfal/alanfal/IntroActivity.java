package com.alkalynx.alanfal.alanfal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends AppCompatActivity {

    //TODO VERSI 2

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] SlideImages = {R.drawable.ic_slide1,R.drawable.ic_slide2,R.drawable.ic_slide3,R.drawable.ic_slide4,R.drawable.ic_slide5};
    private ArrayList<Integer> SlideArray = new ArrayList<Integer>();

    //TODO END VERSI 2 ==============

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //TODO VERSI 2

        init();

        //TODO END VERSI 2 ==============

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    Boolean verify = user.isEmailVerified();

                    if (verify) {
                        Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        };



    }


    // LIFE CYCLE
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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


    public void daftarBtn(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }


    public void masukBtn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    //TODO VERSI 2

    private void init() {
        for(int i=0;i<SlideImages.length;i++)
            SlideArray.add(SlideImages[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new IntroAdapter(IntroActivity.this,SlideArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == SlideImages.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 4000);
    }

    //TODO END VERSI 2 ==============


}
