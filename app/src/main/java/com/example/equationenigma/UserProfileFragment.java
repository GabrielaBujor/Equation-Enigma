package com.example.equationenigma;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {

    private TextView textFullName, textEmail, textUserType;
    private ImageView profilePicture;
    private Button buttonEditProfile, buttonLogout;
    private ProgressBar progressBar;

    private ValueEventListener userDataListener;

    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        // Initialize UI components
        textFullName = view.findViewById(R.id.text_full_name);
        textEmail = view.findViewById(R.id.text_email);
        textUserType = view.findViewById(R.id.text_user_type);
        profilePicture = view.findViewById(R.id.profile_picture);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonLogout = view.findViewById(R.id.button_logout);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        setUpButtonListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            String userId = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // Fetch user data
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    if (dataSnapshot.exists()) {
                        // Retrieve user data from database
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String userType = dataSnapshot.child("userType").getValue(String.class);

                        // Set user data
                        textFullName.setText(fullName != null ? fullName : "Name not available");
                        textEmail.setText(email != null ? email : "Email not available");
                        textUserType.setText(userType != null ? userType : "User type not available");

                        // Load profile picture using Picasso
                        String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(profilePicture);
                        } else {
                            profilePicture.setImageResource(R.drawable.baseline_person_24);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UserProfileFragment", "Database error: " + databaseError.toException());
                }
            });
        } else {
            Toast.makeText(getActivity(), "Not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpButtonListeners() {
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        buttonLogout.setOnClickListener(v -> logout());
    }

    // Handle logout
    private void logout() {
        clearUserSession();
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void clearUserSession() {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String updatedName = data.getStringExtra("USER_NAME");
            String updatedImageUrl = data.getStringExtra("PROFILE_IMAGE_URI");
            updateUIAfterProfileUpdate(updatedName, updatedImageUrl);
        }
    }

    // Update UI after profile update
    private void updateUIAfterProfileUpdate(String updatedName, String updatedImageUrl) {
        TextView userNameTextView = getView().findViewById(R.id.text_full_name);
        userNameTextView.setText(updatedName);

        ImageView profileImageView = getView().findViewById(R.id.profile_picture);
        if (updatedImageUrl != null && !updatedImageUrl.isEmpty()) {
            Picasso.get().load(updatedImageUrl).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.baseline_person_24);
        }
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
