package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private Button buttonNext;
    private Button buttonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Initialize ViewPager2 and buttons
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonSkip = (Button) findViewById(R.id.button_skip);

        // Data for the welcome pages
        List<Pair<Integer, String>> pagesData = Arrays.asList(
                new Pair<>(R.drawable.graphs, "You will learn how to draw graphs"),
                new Pair<>(R.drawable.plotting, "Using Geogebra, you will learn how to plot the graph of different functions"),
                new Pair<>(R.drawable.quiz, "You will have some short quizzes"),
                new Pair<>(R.drawable.quote, "Keep it up and don't give up! You can do this")
        );

        // Set up the adapter
        WelcomePagerAdapter adapter = new WelcomePagerAdapter(this, pagesData);
        viewPager2.setAdapter(adapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager2.getCurrentItem();
                int total = pagesData.size();

                // Navigate to the next page or mark welcome screen as shown and navigate to the login page
                if(current < total - 1) {
                    viewPager2.setCurrentItem(current + 1, true);
                } else {
                    markWelcomeScreenShown();
                    navigateToLogin();
                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markWelcomeScreenShown();
                navigateToLogin();
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == pagesData.size() - 1) {
                    buttonNext.setText("Get started");
                } else {
                    buttonNext.setText("Next");
                }
            }
        });
    }

    // Mark the welcome screen as shown
    private void markWelcomeScreenShown() {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("welcomeShown", true);
        editor.apply();
    }

    // Navigate to the login screen
    private void navigateToLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }
}