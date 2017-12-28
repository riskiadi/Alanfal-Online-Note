package com.alkalynx.alanfal.alanfal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    TextView nameBanner, emailBanner, notesBanner, partnerBanner;
    String name, email;
    long notes, partner;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nameBanner = (TextView) view.findViewById(R.id.profile_name);
        emailBanner = (TextView) view.findViewById(R.id.profile_email);
        notesBanner = (TextView) view.findViewById(R.id.profile_notes);
        partnerBanner = (TextView) view.findViewById(R.id.profile_partner);

        //get User Data from Home
        HomeActivity homeActivity = (HomeActivity) getActivity();
        name = homeActivity.getName();
        email = homeActivity.getEmail();
        notes = homeActivity.getNotes();
        partner = homeActivity.getPartner();

        //setTextProfile
        nameBanner.setText(name);
        emailBanner.setText(email);
        notesBanner.setText(Long.toString(notes));
        partnerBanner.setText(Long.toString(partner));


        return view;
    }


}
