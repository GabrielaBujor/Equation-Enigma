package com.example.equationenigma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {

    private TextView textFullName, textEmail;
    private ImageView profilePicture;
    private Button buttonEditProfile, buttonLogout, buttonDeleteAccount;

    private ValueEventListener userDataListener;



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

        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    textFullName.setText(fullName != null ? fullName : "");
                    textEmail.setText(email != null ? email : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the error or show an error message to the user
                Log.e("UserProfileFragment", "Database error: " + databaseError.toException());
                // You can also use a Toast to notify the user
                Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            databaseReference.addValueEventListener(userDataListener);
        }
    }


    private void editProfile() {

    }

    private void logout() {
        clearUserSession();

        Intent intent = new Intent(getActivity(), LogInActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if(getActivity() != null) {
            getActivity().finish();
        }
    }

    private void clearUserSession() {
        if(getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

    }

    private void deleteAccount() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userDataListener != null && getActivity() != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userID = currentUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                databaseReference.removeEventListener(userDataListener);
            }
        }
    }

}