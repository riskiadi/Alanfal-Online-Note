package com.alkalynx.alanfal.alanfal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SaputraPC on 17-Dec-17.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    String displayName, userEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_screen);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        preferemceVal();

        //Logout Button
        Preference logoutButton = (Preference) findPreference("prefLogout");
        logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                return true;
            }
        });

    }

    //Read shared preference
    private void preferemceVal() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        displayName = sharedPrefs.getString("prefUser", null);
        userEmail = sharedPrefs.getString("prefEmail", null);

        Preference prefDisplayName = (Preference) findPreference("prefUser");
        Preference prefUserEmail = (Preference) findPreference("prefEmail");
        prefDisplayName.setSummary(displayName);
        prefUserEmail.setSummary(userEmail);
    }

    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        preferemceVal();
        changeDisplayName();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    public void changeDisplayName(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid().toString()).child("Name");
        databaseReference.setValue(displayName);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(getActivity(), IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

}
