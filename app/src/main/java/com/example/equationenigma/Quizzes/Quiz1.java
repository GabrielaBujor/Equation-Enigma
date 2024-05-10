package com.example.equationenigma.Quizzes;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.equationenigma.HomeFragment;
import com.example.equationenigma.QuizFragment;
import com.example.equationenigma.R;

public class Quiz1 extends Fragment {
    private TextView timerTextView;
    private Button backButton;
    private int secondsElapsed = 0;
    private Handler timerHandler = new Handler();
    private boolean timerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                String timeString = String.format("%02d:%02d", minutes, seconds);
                timerTextView.setText(timeString);
                secondsElapsed++;
                timerHandler.postDelayed(this, 1000); // post again after 1 second
            }
        }
    };
    public Quiz1() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz1, container, false);
        timerTextView = rootView.findViewById(R.id.timerTextView);
        backButton = rootView.findViewById(R.id.backToMainButton);

        startTimer();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the QuizFragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new QuizFragment())
                            .commit();
                }
            }
        });

        return rootView;
    }

    private void startTimer() {
        timerRunning = true;
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void onBackToMainClicked() {
        stopTimer();
        // Assuming you're within an activity that manages fragments
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!timerRunning) {
            startTimer();
        }
    }


}