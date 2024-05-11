package com.example.equationenigma.Quizzes;

import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class Quiz3 extends Fragment {

    private TextView timerTextView;
    private Button backButton;
    private final boolean[] matchStatus = new boolean[4]; // Track if each function is matched
    private final ImageView[] graphViews = new ImageView[4];
    private final TextView[] functionViews = new TextView[4];
    private int selectedFunctionIndex = -1; // -1 indicates no function is selected

    private int secondsElapsed = 0;
    private Handler timerHandler = new Handler();
    private boolean timerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                updateTimer();
                timerHandler.postDelayed(this, 1000); // Schedule the update again after 1 second
            }
        }
    };

    public Quiz3() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quiz3, container, false);
        // Initialize views
        initializeViews(rootView);

        // Set listeners for functions and graphs
        setupFunctionListeners();
        setupGraphListeners();

        // Start the timer
        startTimer();

        backButton.setOnClickListener(v -> goBackToQuizFragment());

        return rootView;
    }

    private void initializeViews(View rootView) {
        // Timer and button
        timerTextView = rootView.findViewById(R.id.timerTextView);
        backButton = rootView.findViewById(R.id.backToMainButton);

        // Functions
        functionViews[0] = rootView.findViewById(R.id.function1);
        functionViews[1] = rootView.findViewById(R.id.function2);
        functionViews[2] = rootView.findViewById(R.id.function3);
        functionViews[3] = rootView.findViewById(R.id.function4);

        // Graphs
        graphViews[0] = rootView.findViewById(R.id.graph4);
        graphViews[1] = rootView.findViewById(R.id.graph1);
        graphViews[2] = rootView.findViewById(R.id.graph2);
        graphViews[3] = rootView.findViewById(R.id.graph3);
    }

    private void setupFunctionListeners() {
        for (int i = 0; i < functionViews.length; i++) {
            final int index = i;
            functionViews[i].setOnClickListener(v -> onFunctionSelected(index));
        }
    }

    private void setupGraphListeners() {
        for (int i = 0; i < graphViews.length; i++) {
            final int index = i;
            graphViews[i].setOnClickListener(v -> onGraphSelected(index));
        }
    }

    private void onFunctionSelected(int index) {
        // Highlight the selected function
        for (int i = 0; i < functionViews.length; i++) {
            functionViews[i].setBackgroundColor(i == index ? Color.LTGRAY : Color.TRANSPARENT);
        }
        selectedFunctionIndex = index;
    }

    private void onGraphSelected(int index) {
        if (selectedFunctionIndex == -1) {
            // No function selected
            return;
        }

        // Check if the function and graph match
        if (checkMatch(selectedFunctionIndex, index)) {
            // Correct match
            matchStatus[index] = true;
            graphViews[index].setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY); // Indicate match visually
            functionViews[selectedFunctionIndex].setBackgroundColor(Color.LTGRAY); // Set function background to gray
            functionViews[selectedFunctionIndex].setClickable(false); // Prevent re-selection
            graphViews[index].setClickable(false); // Prevent re-selection

            // Reset selection
            selectedFunctionIndex = -1;
            for (TextView functionView : functionViews) {
                functionView.setBackgroundColor(Color.TRANSPARENT);
            }

            // Check if all are matched
            if (allMatchesCorrect()) {
                showBackButtonAndStopTimer();
            }
        } else {
            // Incorrect match
            Toast.makeText(getContext(), "Incorrect match, try again!", Toast.LENGTH_SHORT).show();
            functionViews[selectedFunctionIndex].setBackgroundColor(Color.TRANSPARENT);
            selectedFunctionIndex = -1;
        }
    }


    private boolean checkMatch(int functionIndex, int graphIndex) {
        // Correct match if indices are the same
        return functionIndex == graphIndex;
    }

    private boolean allMatchesCorrect() {
        for (boolean matched : matchStatus) {
            if (!matched) return false;
        }
        return true;
    }

    private void showBackButtonAndStopTimer() {
        backButton.setVisibility(View.VISIBLE);
        stopTimer();
    }

    private void startTimer() {
        timerRunning = true;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updateTimer() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeString);
        secondsElapsed++;
    }

    private void goBackToQuizFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
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