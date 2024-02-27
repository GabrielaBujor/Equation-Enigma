package com.example.equationenigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button signUpButton;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        logInButton = findViewById(R.id.log_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me);

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean("Remember Me", false);
        if(isRemembered) {
            startMainActivity();
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                if(password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                loginUser(email, password);

            }

            private void loginUser(String email, String password) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Log.d("Login", "signInWithEmail:success");

                                    if(rememberMeCheckBox.isChecked()) {
                                        prefs.edit()
                                                .putBoolean("RememberMe", true)
                                                .putString("Username", email)
                                                .putString("Password", password)
                                                .apply();
                                    } else {
                                        prefs.edit().putBoolean("RememberMe", false).apply();
                                    }
                                    startMainActivity();
                                    } else {
                                    Log.w("Login", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LogIn.this, "Login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateCredentials(String email, String password) {
        return true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}