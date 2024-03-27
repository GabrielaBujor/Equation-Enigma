package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;


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

import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private ImageView imageViewProfile;
    private Uri imageUri;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        editTextName = findViewById(R.id.editTextName);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonChooseImage = findViewById(R.id.buttonChooseImage);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonChooseImage.setOnClickListener(view -> openImageChooser());
        buttonSave.setOnClickListener(view -> saveChanges());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveChanges() {
        String name = editTextName.getText().toString().trim();
        // TODO: Validate name and handle imageUri (upload to Firebase and get URL)


        uploadImageToFirebase(imageUri);

        // Prepare result intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_NAME", name);
        resultIntent.putExtra("PROFILE_IMAGE_URI", imageUri != null ? imageUri.toString() : null);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child("profile_pics/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Handle the URL (e.g., store in user's profile, update UI)
                            updateFirebaseAuthProfile(editTextName.getText().toString().trim(), imageUrl);
                            updateUserInRealtimeDatabase(FirebaseAuth.getInstance().getCurrentUser().getUid(), editTextName.getText().toString().trim(), imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateFirebaseAuthProfile(String name, String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(imageUrl)) // imageUrl should be a String URL
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("UserProfileUpdate", "User profile updated.");
                            // Optionally, you can update your app's UI or database here.
                            updateUIAfterProfileUpdate(name, imageUrl);
                        } else {
                            // Handle the error
                            Log.e("UserProfileUpdate", "Error updating profile", task.getException());
                            showToastMessage("Profile update failed: " + task.getException().getMessage());
                        }
                    });
        }
    }

    private void updateUIAfterProfileUpdate(String name, String imageUrl) {
        // Update UI elements with the new name and image URL
        // For example:
        // textViewUserName.setText(name);
        // if(imageUrl != null && !imageUrl.isEmpty()) {
        //     Picasso.get().load(imageUrl).into(imageViewUserProfile);
        // }
    }


    private void showToastMessage(String message) {
        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void updateUserInRealtimeDatabase(String userId, String name, String imageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("profileImageUrl", imageUrl);

        databaseReference.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Log.d("DatabaseUpdate", "User data updated successfully."))
                .addOnFailureListener(e -> Log.e("DatabaseUpdate", "Failed to update user data.", e));
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }
}