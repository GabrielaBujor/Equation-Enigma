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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveChanges() {
        String name = editTextName.getText().toString().trim();
        if(name.isEmpty()) {
            showToastMessage("Name cannot be empty");
            return;
        }

        if(imageUri != null) {
            uploadImageToFirebase(name, imageUri);
        } else {
            updateFirebaseAuthProfile(name, null);
            updateUserInRealtimeDatabase(FirebaseAuth.getInstance().getCurrentUser().getUid(), name, null);
        }




        // Prepare result intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra("USER_NAME", name);
        resultIntent.putExtra("PROFILE_IMAGE_URI", imageUri != null ? imageUri.toString() : null);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void uploadImageToFirebase(String name, Uri imageUri) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Please wait while we upload your profile picture");
            progressDialog.show();
            StorageReference fileRef = storageRef.child("profile_pics/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            progressDialog.dismiss();
                            String imageUrl = uri.toString();
                            // Handle the URL (e.g., store in user's profile, update UI)
                            updateFirebaseAuthProfile(name, imageUrl);
                            updateUserInRealtimeDatabase(FirebaseAuth.getInstance().getCurrentUser().getUid(), name, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                        showToastMessage("Upload failed: " + e.getMessage());
                    });
    }

    private void updateFirebaseAuthProfile(String name, @Nullable String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name);

            if (imageUrl != null) {
                builder.setPhotoUri(Uri.parse(imageUrl));
            }

            UserProfileChangeRequest profileUpdates = builder.build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("UserProfileUpdate", "User profile updated.");
                            // Prepare and set the result for the activity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("USER_NAME", name);
                            resultIntent.putExtra("PROFILE_IMAGE_URI", imageUrl);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            // Handle the error
                            Log.e("UserProfileUpdate", "Error updating profile", task.getException());
                            showToastMessage("Profile update failed: " + task.getException().getMessage());
                        }
                    });
        }
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