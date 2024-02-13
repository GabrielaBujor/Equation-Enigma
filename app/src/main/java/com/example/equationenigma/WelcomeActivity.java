package com.example.equationenigma;

import androidx.appcompat.app.AppCompatActivity;
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

        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonSkip = (Button) findViewById(R.id.button_skip);

        List<Pair<Integer, String>> pagesData = Arrays.asList(
                new Pair<>(R.drawable.graphs, "You will learn how to draw graphs"),
                new Pair<>(R.drawable.plotting, "Using Geogebra, you will learn how to plot the graph of different functions"),
                new Pair<>(R.drawable.quiz, "You will have some short quizzes"),
                new Pair<>(R.drawable.quote, "Keep it up and don't give up! You can do this")
        );

        WelcomePagerAdapter adapter = new WelcomePagerAdapter(this, pagesData);
        viewPager2.setAdapter(adapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager2.getCurrentItem();
                int total = pagesData.size();

                if(current < total - 1) {
                    viewPager2.setCurrentItem(current + 1, true);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LogIn.class);
                    startActivity(intent);

                    finish();
                }
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("EquationEnigmaPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("welcomeShown", true);
                editor.apply();

                Intent intent = new Intent(WelcomeActivity.this, LogIn.class);
                startActivity(intent);

                finish();
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
}