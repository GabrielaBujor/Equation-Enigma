package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Spinner spinnerUserType;
    private Button buttonSignUp;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference dbRT;
    private static final String TAG = "SignUp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dbRT = FirebaseDatabase.getInstance().getReference("Users");

        setupUI();
        setupSignUpButton();
    }

    private void setupUI() {
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);
    }

    private void setupSignUpButton() {
        buttonSignUp.setOnClickListener(v -> {
            Log.d(TAG, "Sign Up button clicked");

            String fullName = editTextFullName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();
            String userType = spinnerUserType.getSelectedItem().toString();

            if (!validateInputs(fullName, email, password, confirmPassword))
                return;

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser == null) {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String userId = firebaseUser.getUid();
                            User newUser = new User(fullName, email, userType);

                            // Proceed with writing user data to Firestore and Realtime Database
                            writeUserDataToDatabases(newUser, userId);
                        } else {
                            Log.e(TAG, "Signup failed", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void writeUserDataToDatabases(User user, String userId) {
        // Firestore
        db.collection("Users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User Firestore record created successfully for userID: " + userId);
                    // Realtime Database
                    dbRT.child(userId).setValue(user)
                            .addOnSuccessListener(aVoidRT -> {
                                Log.d("RealtimeDB", "User Realtime Database record created successfully for userID: " + userId);
                                navigateToLogin();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RealtimeDB", "Error writing document to Realtime Database for userID: " + userId, e);
                                Toast.makeText(SignUpActivity.this, "Failed to write user data to Realtime Database.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error writing document for userID: " + userId, e);
                    Toast.makeText(SignUpActivity.this, "Failed to write user data to Firestore.", Toast.LENGTH_SHORT).show();
                });
    }


    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        if(fullName.isEmpty()) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return false;
        }

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }

        if(!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if(!isValidEmail(email)) {
            editTextEmail.setError("Invalid Email");
            editTextEmail.requestFocus();
            return false;
        }

        if(!isValidPassword(password)) {
            editTextPassword.setError("Password must be at least 8 characters, include a capital letter and a number");
            editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$");
        return pattern.matcher(password).matches();
    }

    private void handleSignUpError(Task<AuthResult> task) {
        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to sign up: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }


    public static class User {
        public String fullName;
        public String email;
        public String userType;

        public User(String fullName, String email, String userType) {
            this.fullName = fullName;
            this.email = email;
            this.userType = userType;
        }
    }
}