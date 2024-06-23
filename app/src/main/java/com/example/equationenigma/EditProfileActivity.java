package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private ImageView imageViewProfile;
    private Uri imageUri;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        editTextName = findViewById(R.id.editTextName);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonChooseImage = findViewById(R.id.buttonChooseImage);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonChooseImage.setOnClickListener(view -> openImageChooser());
        buttonSave.setOnClickListener(view -> saveChanges());
    }

//    private void loadUserProfile() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(currentUser != null) {
//            if(currentUser.getDisplayName() != null) {
//                editTextName.setText(currentUser.getDisplayName());
//            }
//            if(currentUser.getPhotoUrl() != null) {
//                Picasso.get().load(currentUser.getPhotoUrl()).into(imageViewProfile);
//            } else {
//                imageViewProfile.setImageResource(R.drawable.baseline_person_24);
//            }
//        }
//    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveChanges() {
        String name = editTextName.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return; // Early exit if no user is logged in

        boolean nameChanged = !name.isEmpty() && !name.equals(user.getDisplayName());
        boolean imageChanged = imageUri != null;  // Check if a new image has been selected

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageChanged) {
            // If a new image is selected, upload it and handle updating name and image together
            uploadImageToFirebase(user.getUid(), user.getDisplayName(), imageUri, progressDialog);
        } else if (nameChanged) {
            // If only the name is changed, update it along with the current photo URL
            updateFirebaseAuthProfile(name, user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
            updateUserInRealtimeDatabase(user.getUid(), name, user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
            progressDialog.dismiss();
            finishActivityWithResult(name);
        } else {
            // No changes to save
            progressDialog.dismiss();
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageToFirebase(String userId, String name, Uri imageUri, ProgressDialog progressDialog) {
        StorageReference fileRef = storageRef.child("profile_pics/" + userId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Update Firebase Auth with the new name and image URL
                    updateFirebaseAuthProfile(name, imageUrl);
                    // Update user details in Realtime Database
                    updateUserInRealtimeDatabase(userId, name, imageUrl);
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finishActivityWithResult(name);
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInRealtimeDatabase(String userId, String name, String imageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        Map<String, Object> userUpdates = new HashMap<>();
        if (name != null) {
            userUpdates.put("fullName", name);  // Changed from "name" to "fullName"
        }
        if (imageUrl != null) {
            userUpdates.put("profileImageUrl", imageUrl);
        }

        if (!userUpdates.isEmpty()) {
            databaseReference.updateChildren(userUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("DatabaseUpdate", "User data updated successfully in Realtime Database.");
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                        finishActivityWithResult(name);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DatabaseUpdate", "Failed to update user data.", e);
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateFirebaseAuthProfile(String name, String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name);
            if (imageUrl != null) {
                builder.setPhotoUri(Uri.parse(imageUrl));
            }

            UserProfileChangeRequest profileUpdates = builder.build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Also update in Realtime Database
                    updateUserInRealtimeDatabase(user.getUid(), name, imageUrl);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finishActivityWithResult(name);
                } else {
                    Toast.makeText(this, "Profile update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void finishActivityWithResult(String name) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_NAME", name);
        resultIntent.putExtra("PROFILE_IMAGE_URI", imageUri != null ? imageUri.toString() : null);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }
}