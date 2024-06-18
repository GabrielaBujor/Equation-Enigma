package com.example.equationenigma.Quizzes;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.equationenigma.MainActivity;
import com.example.equationenigma.QuizReport;
import com.example.equationenigma.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Quiz1 extends Fragment {
    private TextView timerTextView;
    private Button backButton;
    private final boolean[] matchStatus = new boolean[4]; // Track if each function is matched
    private final ImageView[] graphViews = new ImageView[4];
    private final TextView[] functionViews = new TextView[4];
    private final int[] matchedWith = new int[4]; // Store the index of the graph each function is matched with
    private int selectedFunctionIndex = -1; // -1 indicates no function is selected
    private boolean[] isFunctionMatched;

    private int secondsElapsed = 0;
    private Handler timerHandler = new Handler();
    private boolean timerRunning = false;
    private static final String TAG = "Quiz 1";


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                updateTimer();
                timerHandler.postDelayed(this, 1000); // Schedule the update again after 1 second
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

        // Initialize views
        initializeViews(rootView);

        // Set listeners for functions and graphs
        setupFunctionListeners();
        setupGraphListeners();

        // Initialize matchedWith array with -1 indicating no matches
        java.util.Arrays.fill(matchedWith, -1);
        isFunctionMatched = new boolean[functionViews.length];
        Arrays.fill(isFunctionMatched, false);  // Initially, no functions are matched


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
        graphViews[0] = rootView.findViewById(R.id.graph3);
        graphViews[1] = rootView.findViewById(R.id.graph4);
        graphViews[2] = rootView.findViewById(R.id.graph1);
        graphViews[3] = rootView.findViewById(R.id.graph2);
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
            if(matchStatus[i] == true) {
                functionViews[i].setBackgroundColor(Color.LTGRAY);
            } else {
                functionViews[i].setBackgroundColor(i == index ? Color.LTGRAY : Color.TRANSPARENT);
            }
        }
        selectedFunctionIndex = index;
    }

    private void onGraphSelected(int index) {
        if (selectedFunctionIndex == -1) {
            // No function selected
            return;
        }

        matchedWith[selectedFunctionIndex] = index; // Record which graph was selected for which function
        boolean isMatchCorrect = checkMatch(selectedFunctionIndex, index);
        matchStatus[selectedFunctionIndex] = isMatchCorrect;

        // Update UI based on whether the match was correct or not
        updateUIAfterMatch(index, isMatchCorrect);

        // Reset selection and check for completion
        selectedFunctionIndex = -1;
        resetFunctionSelections();

        if (allFunctionsMatched()) {
            showBackButtonAndStopTimer();
        } else {
            // Debugging output
            System.out.println("Not all functions matched yet.");
        }
    }

    private void updateUIAfterMatch(int graphIndex, boolean isMatchCorrect) {
        graphViews[graphIndex].setColorFilter(isMatchCorrect ? Color.GREEN : Color.RED, PorterDuff.Mode.MULTIPLY);
        // Always set the function background to gray if matched, regardless of correctness
        functionViews[selectedFunctionIndex].setBackgroundColor(Color.LTGRAY);
        functionViews[selectedFunctionIndex].setClickable(false);
        graphViews[graphIndex].setClickable(false);

        // Mark the function as matched
        isFunctionMatched[selectedFunctionIndex] = true;
    }


    private void resetFunctionSelections() {
        for (int i = 0; i < functionViews.length; i++) {
            // Only reset the background color if the function has not been matched
            if (!isFunctionMatched[i]) {
                functionViews[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }


    private boolean allFunctionsMatched() {
        for (int status : matchedWith) {
            if (status == -1) {
                return false; // Means at least one function hasn't been matched yet
            }
        }
        return true; // All functions have been matched
    }

    private void showBackButtonAndStopTimer() {
        System.out.println("All functions matched. Showing back button and stopping timer.");
        backButton.setVisibility(View.VISIBLE);
        stopTimer();

        // Generate report
        generateAndSaveReport();
    }

    private void generateAndSaveReport() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "User is not logged in");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserName = user.getDisplayName() != null ? user.getDisplayName() : "Unknown User";

//        String currentUserName = user.getDisplayName();
//        if (currentUserName == null || currentUserName.isEmpty()) {
//            Log.d(TAG, "User name is not set or empty");
//            currentUserName = "Unknown User";
//        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String reportName = sdf.format(new Date());
        int mistakes = calculateMistakes();
        String timeTaken = timerTextView.getText().toString();
        Map<String, Object> detailedResults = new HashMap<>();
        for (int i = 0; i < functionViews.length; i++) {
            detailedResults.put(functionViews[i].getText().toString(), matchStatus[i]);
        }

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("userName", currentUserName);
        reportData.put("userId", user.getUid());
        reportData.put("reportName", reportName);
        reportData.put("mistakes", mistakes);
        reportData.put("timeTaken", timeTaken);
        reportData.put("detailedResults", detailedResults);

        Log.d(TAG, "Attempting to save report for user: " + currentUserName);
        Log.d(TAG, "Preparing to save report with data: " + reportData.toString());

        saveReportToFirebase(reportData);
    }

    private boolean checkMatch(int functionIndex, int graphIndex) {
        // Correct match if indices are the same
        return functionIndex == graphIndex;
    }

    private boolean allMatchesCorrect() {
        for (int i = 0; i < matchStatus.length; i++) {
            if (matchedWith[i] != -1) {
                matchStatus[i] = checkMatch(i, matchedWith[i]);
            } else {
                return false;
            }
        }
        return true;
    }


    private void saveReportToFirebase(Map<String, Object> reportData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("quizReports")
                .add(reportData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    if (isAdded()) {  // Check if the fragment is currently added to its activity
                        Toast.makeText(getContext(), "Report saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {  // Check if the fragment is currently added to its activity
                        Toast.makeText(getContext(), "Error saving report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error saving report", e);
                    }
                });
    }




    private int calculateMistakes() {
        int mistakes = 0;
        for (boolean wasCorrect : matchStatus) {
            if (!wasCorrect) {
                mistakes++;
            }
        }
        return mistakes;
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
        ((MainActivity) getActivity()).hideBottomNavigation();
        if (!timerRunning) {
            startTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).showBottomNavigation();
    }
}
