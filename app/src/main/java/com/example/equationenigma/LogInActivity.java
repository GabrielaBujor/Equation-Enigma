package com.example.equationenigma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.io.IOException;
import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button signUpButton;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the welcome screen has been shown before
        prefs = getSharedPreferences(Constants.SHARED_PREFS_FILE, MODE_PRIVATE);
        boolean welcomeShown = prefs.getBoolean("welcomeShown", false);

        if (!welcomeShown) {
            // If not shown, redirect to WelcomeActivity
            Intent intent = new Intent(LogInActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        logInButton = findViewById(R.id.log_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        rememberMeCheckBox = findViewById(R.id.checkbox_remember_me);

        boolean isRemembered = prefs.getBoolean("Remember Me", false);
        if(isRemembered) {
            String savedEmail = prefs.getString("Username", "");
            String savedPassword = prefs.getString("Password", "");
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(!validateEmail(email)) {
                    emailEditText.setError("Invalid email format");
                    emailEditText.requestFocus();
                    return;
                }

                if(password.isEmpty() || !validatePassword(password)) {
                    passwordEditText.setError("Invalid password");
                    passwordEditText.requestFocus();
                    return;
                }
                loginUser(email, password);
            }

            private void loginUser(String email, String password) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Log.d("Login", "signInWithEmail:success");

                                    SharedPreferences.Editor editor = prefs.edit();
                                    if (rememberMeCheckBox.isChecked()) {
                                        editor.putBoolean("RememberMe", true);
                                        editor.putString("Username", email);
                                        editor.putString("Password", password);
                                    } else {
                                        editor.putBoolean("RememberMe", false);
                                        editor.remove("Username");
                                        editor.remove("Password");
                                    }
                                    editor.apply();

                                    Toast.makeText(LogInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    startMainActivity();
                                    } else {
                                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(LogInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                            Toast.makeText(LogInActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                        } else if (task.getException() instanceof IOException) {
                                            Toast.makeText(LogInActivity.this, "Network connection error. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //Log.w("Login", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();

                                        }


                                }
                            }
                        });
            }

            private boolean validateEmail(String email) {
                return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }

            private boolean validatePassword(String password) {
                Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$");
                return pattern.matcher(password).matches();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}