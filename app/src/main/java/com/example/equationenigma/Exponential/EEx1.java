package com.example.equationenigma.Exponential;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equationenigma.HomeFragment;
import com.example.equationenigma.MainActivity;
import com.example.equationenigma.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class EEx1 extends Fragment {
    // Variables
    private TextView textViewTitle, textViewFunction, textViewInverseF, textViewExtremePoints, textViewMonotonicityHint, textViewBijectivity, textViewConvexity;
    private EditText editTextEven, editTextOdd, editTextF0, editTextIntersection, editTextF1, editTextFMinus1, editTextBound, editTextMonotonicity;
    private Button buttonVerify;
    private ImageView imageViewGraph;
    private ProgressBar progressBar;
    private ImageButton hintButton;
    private Button HomeButton;


    private Map<String, String> correctAnswers = new HashMap<>();


    public static EEx1 createInstance() {
        return new EEx1();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up the toolbar
        View view = inflater.inflate(R.layout.fragment_e_ex1, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        // Initialize view components
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewFunction = view.findViewById(R.id.textViewFunction);
        textViewInverseF = view.findViewById(R.id.textViewInverseF);
        textViewExtremePoints = view.findViewById(R.id.textViewExtremePoints);
        textViewMonotonicityHint = view.findViewById(R.id.textViewMonotonicityHint);
        textViewBijectivity = view.findViewById(R.id.textViewBijectivity);
        textViewConvexity = view.findViewById(R.id.textViewConvexity);
        editTextEven = view.findViewById(R.id.editTextEven);
        editTextOdd = view.findViewById(R.id.editTextOdd);
        editTextF0 = view.findViewById(R.id.editTextF0);
        editTextIntersection = view.findViewById(R.id.editTextIntersection);
        editTextF1 = view.findViewById(R.id.editTextF1);
        editTextFMinus1 = view.findViewById(R.id.editTextFMinus1);
        editTextBound = view.findViewById(R.id.editTextBound);
        editTextMonotonicity = view.findViewById(R.id.editTextMonotonicity);
        buttonVerify = view.findViewById(R.id.buttonVerify);
        imageViewGraph = view.findViewById(R.id.imageViewGraph);
        progressBar = view.findViewById(R.id.progressBar);
        hintButton = view.findViewById(R.id.hintButton);
        HomeButton = view.findViewById(R.id.homeButton);

        progressBar.setVisibility(View.VISIBLE);

        hintButton.setOnClickListener(v -> {
            // Toggle the visibility of the hint text view
            if (textViewMonotonicityHint.getVisibility() == View.GONE) {
                textViewMonotonicityHint.setVisibility(View.VISIBLE);
            } else {
                textViewMonotonicityHint.setVisibility(View.GONE);
            }
        });

        // Fetch exercise data from Firestore
        fetchExerciseData();

        buttonVerify.setOnClickListener(v -> verifyAnswers());

        HomeButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment()) //  container ID from the main activity's layout.
                    .addToBackStack(null) // add the transaction to the back stack.
                    .commit();
        });

        return view;
    }

    private void fetchExerciseData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Exercises").document("EEx1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Store the text data in TextViews
                        textViewTitle.setText(documentSnapshot.getString("title"));
                        textViewFunction.setText(documentSnapshot.getString("function"));
                        textViewInverseF.setText(documentSnapshot.getString("f(-x)"));
                        textViewExtremePoints.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("extremePoints")));
                        textViewBijectivity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("bijectivity")));
                        textViewConvexity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("convexity")));
                        textViewMonotonicityHint.setText(documentSnapshot.getString("monotonicityHint"));

                        // Store the graph URL
                        correctAnswers.put("graphURL", documentSnapshot.getString("graphURL"));

                        // Store correct answers
                        correctAnswers.put("even", documentSnapshot.getString("parity"));
                        correctAnswers.put("odd", documentSnapshot.getString("parity"));
                        correctAnswers.put("f(0)", documentSnapshot.getString("f(0)"));
                        correctAnswers.put("f(1)", documentSnapshot.getString("f(1)"));
                        correctAnswers.put("f(-1)", documentSnapshot.getString("f(-1)"));
                        correctAnswers.put("borders", documentSnapshot.getString("borders"));
                        correctAnswers.put("intersection", documentSnapshot.getString("intersection"));
                        correctAnswers.put("monotonicity", documentSnapshot.getString("monotonicity"));

                        progressBar.setVisibility(View.GONE);  // Ensure to hide progress bar after fetching data
                    } else {
                        Toast.makeText(getContext(), "Document does not exist!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading exercise data", Toast.LENGTH_SHORT).show();
                });
    }

    // Verify user answers against the correct answers stored
    private void verifyAnswers() {
        boolean allCorrect = true;
        progressBar.setVisibility(View.VISIBLE);

        // Check each answer and set errors accordingly
        allCorrect &= checkAnswer(editTextEven, correctAnswers.get("even"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextOdd, correctAnswers.get("odd"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextF0, correctAnswers.get("f(0)"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextIntersection, correctAnswers.get("intersection"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextF1, correctAnswers.get("f(1)"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextFMinus1, correctAnswers.get("f(-1)"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextBound, correctAnswers.get("borders"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextMonotonicity, correctAnswers.get("monotonicity"), "Incorrect answer");

        if (allCorrect) {
            // Only load and display the graph if all answers are correct
            String graphURL = correctAnswers.get("graphURL");
            if (graphURL != null && !graphURL.isEmpty()) {
                fetchAndDisplayImage(imageViewGraph, graphURL);
            } else {
                Toast.makeText(getContext(), "No graph available.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        } else {
            imageViewGraph.setVisibility(View.GONE); // Hide the graph if any answers are incorrect
            progressBar.setVisibility(View.GONE);
        }
    }


    // Check answer against correct answer
    private boolean checkAnswer(EditText editText, String correctAnswer, String errorMessage) {
        if (!editText.getText().toString().equalsIgnoreCase(correctAnswer)) {
            editText.setError(errorMessage);
            return false;
        }
        return true;
    }


    // Fetch and display image from Firebase Storage
    private void fetchAndDisplayImage(ImageView imageView, String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (!path.startsWith("gs://")) {
            path = "gs://equation-enigma.appspot.com/" + path;
        }
        StorageReference storageRef = storage.getReferenceFromUrl(path);

        progressBar.setVisibility(View.VISIBLE);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri.toString())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            Log.e("PicassoError", "Error loading image", e);
                        }
                    });
        }).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.error);
            Log.e("FirebaseStorage", "Error getting image", exception);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).hideBottomNavigation();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).showBottomNavigation();
    }

}