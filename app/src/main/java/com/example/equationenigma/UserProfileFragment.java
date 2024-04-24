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

    private TextView textFullName, textEmail;
    private ImageView profilePicture;
    private Button buttonEditProfile, buttonLogout;
    //private Button buttonDeleteAccount;

    private ValueEventListener userDataListener;
    private ProgressBar progressBar;

    private static final int EDIT_PROFILE_REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        profilePicture = view.findViewById(R.id.profile_picture);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        textFullName = view.findViewById(R.id.text_full_name);
        textEmail = view.findViewById(R.id.text_email);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonLogout = view.findViewById(R.id.button_logout);
//        buttonDeleteAccount = view.findViewById(R.id.button_delete_account);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        setUpButtonListeners();

        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    textFullName.setText(fullName != null ? fullName : "Name not available");
                    textEmail.setText(email != null ? email : "Email not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UserProfileFragment", "Database error: " + databaseError.toException());
            }
        };

        return view;
    }


    private void setUpButtonListeners() {
        buttonEditProfile.setOnClickListener(v -> editProfile());

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

//        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteAccount();
//            }
//        });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!isNetworkConnected()) {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "Not authenticated", Toast.LENGTH_SHORT).show();
            // Handle unauthenticated user scenario
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        setUpUserDataListener();
        databaseReference.addValueEventListener(userDataListener);
    }

    private void setUpUserDataListener() {
        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.exists()) {
                    // Extracting user information from the snapshot
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    // Updating UI with the fetched data
                    textFullName.setText(fullName != null ? fullName : "N/A");
                    textEmail.setText(email != null ? email : "N/A");

                    // If you store the URL of the profile picture in Firebase, fetch and set it as well
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.baseline_person_24); // default or placeholder image
                    }
                } else {
                    Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UserProfileFragment", "Database error: " + databaseError.toException());
            }
        };
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void editProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
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



    private void updateUIAfterProfileUpdate(String updatedName, String updatedImageUrl) {
        // Assuming you have a TextView in your fragment for the user's name
        TextView userNameTextView = getView().findViewById(R.id.text_full_name);
        userNameTextView.setText(updatedName);
        ImageView profileImageView = getView().findViewById(R.id.profile_picture);

        // Update the ImageView with the new image
        if (updatedImageUrl != null && !updatedImageUrl.isEmpty()) {
            // Using Picasso to load the image. You can use Glide or another library similarly.
            Picasso.get().load(updatedImageUrl).into(profileImageView);
        } else {
            // Set a default image or placeholder
            profileImageView.setImageResource(R.drawable.baseline_person_24);
        }
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

//    private void deleteAccount() {
//        new AlertDialog.Builder(getContext())
//                .setTitle("Delete Account")
//                .setMessage("Are you sure you want to permanently delete your account?")
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        if (user != null) {
//                            user.delete()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
//                                                // Redirect to Login or Welcome Activity
//                                                Intent intent = new Intent(getActivity(), LogInActivity.class);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                startActivity(intent);
//                                            } else {
//                                                Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//
//    }

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