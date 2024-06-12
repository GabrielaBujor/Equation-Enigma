package com.example.equationenigma.Logarithmic;

import android.os.Bundle;

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
import com.example.equationenigma.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class LEx2 extends Fragment {

    private TextView textViewTitle, textViewFunction, textViewParity, textViewBorders, textViewExtremePoints, textViewMonotonicityHint, textViewBijectivity, textViewConvexity;
    private EditText editTextF1, editTextIntersection, editTextMonotonicity, editTextF5, editTextF25;
    private Button buttonVerify;
    private ImageView imageViewGraph;
    private ProgressBar progressBar;
    private ImageButton hintButton;
    private Button HomeButton;


    private Map<String, String> correctAnswers = new HashMap<>();


    public static LEx2 createInstance() {
        return new LEx2();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_l_ex2, container, false);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewFunction = view.findViewById(R.id.textViewFunction);
        textViewParity = view.findViewById(R.id.textViewParity);
        textViewBorders = view.findViewById(R.id.textViewBorders);
        textViewExtremePoints = view.findViewById(R.id.textViewExtremePoints);
        textViewMonotonicityHint = view.findViewById(R.id.textViewMonotonicityHint);
        textViewBijectivity = view.findViewById(R.id.textViewBijectivity);
        textViewConvexity = view.findViewById(R.id.textViewConvexity);
        editTextF1 = view.findViewById(R.id.editTextF1);
        editTextF5 = view.findViewById(R.id.editTextF5);
        editTextF25 = view.findViewById(R.id.editTextF25);
        editTextIntersection = view.findViewById(R.id.editTextIntersection);
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

        fetchExerciseData();

        buttonVerify.setOnClickListener(v -> verifyAnswers());

        HomeButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment()) // 'R.id.fragment_container' is the container ID from the main activity's layout.
                    .addToBackStack(null) // Optional, if you want to add the transaction to the back stack.
                    .commit();
        });
        return view;
    }

    private void fetchExerciseData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Exercises").document("LEx2")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Store the text data in TextViews
                        textViewTitle.setText(documentSnapshot.getString("title"));
                        textViewFunction.setText(documentSnapshot.getString("function"));
                        textViewParity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("parity")));
                        textViewBorders.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("borders")));
                        textViewExtremePoints.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("extremePoints")));
                        textViewBijectivity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("bijectivity")));
                        textViewConvexity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("convexity")));
                        textViewMonotonicityHint.setText(documentSnapshot.getString("monotonicityHint"));

                        // Store the graph URL for later use
                        correctAnswers.put("graphURL", documentSnapshot.getString("graphURL"));

                        // Store other correct answers
                        correctAnswers.put("f(1)", documentSnapshot.getString("f(1)"));
                        correctAnswers.put("f(5)", documentSnapshot.getString("f(5)"));
                        correctAnswers.put("f(25)", documentSnapshot.getString("f(25)"));
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

    private void verifyAnswers() {
        boolean allCorrect = true;
        progressBar.setVisibility(View.VISIBLE);

        // Check each answer and set errors accordingly
        allCorrect &= checkAnswer(editTextIntersection, correctAnswers.get("intersection"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextF1, correctAnswers.get("f(1)"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextF5, correctAnswers.get("f(5)"), "Incorrect answer");
        allCorrect &= checkAnswer(editTextF25, correctAnswers.get("f(25)"), "Incorrect answer");
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


    private boolean checkAnswer(EditText editText, String correctAnswer, String errorMessage) {
        if (!editText.getText().toString().equalsIgnoreCase(correctAnswer)) {
            editText.setError(errorMessage);
            return false;
        }
        return true;
    }


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
}