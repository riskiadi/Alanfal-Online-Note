package com.alkalynx.alanfal.alanfal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SaputraPC on 09-Dec-17.
 */

public class FirebaseOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase Offline handler, agar database tersimpan di lokal
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}
