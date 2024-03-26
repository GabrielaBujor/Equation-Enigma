package com.example.equationenigma;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {

    private TextView textFullName, textEmail;
    private ImageView profilePicture;
    private Button buttonEditProfile, buttonLogout, buttonDeleteAccount;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        textFullName = view.findViewById(R.id.text_full_name);
        textEmail = view.findViewById(R.id.text_email);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonLogout = view.findViewById(R.id.button_logout);
        buttonDeleteAccount = view.findViewById(R.id.button_delete_account);

        setUpButtonListeners();

        loadUserData();

        return view;
    }

    private void setUpButtonListeners() {
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    private void loadUserData() {

    }

    private void editProfile() {

    }

    private void logout() {
//        Intent intent = new Intent(UserProfileFragment.this, LogInActivity.class);
        //startActivity(intent);
    }

    private void deleteAccount() {

    }
}