package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LogIn extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button signUpButton;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
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
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(username.isEmpty()) {
                    usernameEditText.setError("Username is required");
                    usernameEditText.requestFocus();
                    return;
                }

                if(password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                if(validateCredentials(username, password)) {
                    if(rememberMeCheckBox.isChecked()) {
                        prefs.edit()
                                .putBoolean("RememberMe", true)
                                .putString("Username", username)
                                .putString("Password", password)
                                .apply();
                    } else {
                        prefs.edit().putBoolean("RememberMe", false).apply();
                    }
                    startMainActivity();
                }

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

    private boolean validateCredentials(String username, String password) {
        return true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}