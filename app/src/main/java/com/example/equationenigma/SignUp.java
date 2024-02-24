package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Spinner spinnerUserType;
    private Button buttonSignUp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String fullName = editTextFullName.getText().toString().trim();
               String email = editTextEmail.getText().toString().trim();
               String password = editTextPassword.getText().toString().trim();
               String confirmPassword = editTextConfirmPassword.getText().toString().trim();
               String userType = spinnerUserType.getSelectedItem().toString();

               if(fullName.isEmpty()) {
                   editTextFullName.setError("Full name is required");
                   editTextFullName.requestFocus();
                   return;
               }

               if(email.isEmpty()) {
                   editTextEmail.setError("Email is required");
                   editTextEmail.requestFocus();
                   return;
               }

               if(password.isEmpty()) {
                   editTextPassword.setError("Password is required");
                   editTextPassword.requestFocus();
                   return;
               }

               if(!password.equals(confirmPassword)) {
                   editTextConfirmPassword.setError("Passwords do not match");
                   editTextConfirmPassword.requestFocus();
                   return;
               }

               mAuth.createUserWithEmailAndPassword(email, password)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()) {
                                   Toast.makeText(SignUp.this, "User has successfully signed up", Toast.LENGTH_LONG).show();
                                   String userId = task.getResult().getUser().getUid();

                                   User user = new User(fullName, email, userType);

                                   usersRef.child(userId).setValue(user)
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                                public void onSuccess(Void aVoid) {
                                                   if(task.isSuccessful()) {
                                                       Toast.makeText(SignUp.this, "User profile created", Toast.LENGTH_LONG).show();

                                                       Intent intent = new Intent(SignUp.this, LogIn.class);
                                                       startActivity(intent);
                                                       finish();
                                                   } else {
                                                       Toast.makeText(SignUp.this, "Failed to create user profile.", Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           })
                                            .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                Log.e("SignUpError", "Failed to create user profile", e);
                                            }
                                        });
                               } else {
                                   Toast.makeText(SignUp.this, "Failed to sign up: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                               }
                           }
                       });
           }
        });
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