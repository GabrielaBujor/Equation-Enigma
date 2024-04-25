package com.example.equationenigma.Roots;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equationenigma.HomeFragment;
import com.example.equationenigma.Power.PSolvedEx1;
import com.example.equationenigma.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class RSolvedEx2 extends Fragment {

    private TextView textViewTitle;
    private TextView textViewFunction;
    private TextView textViewParity;
    private TextView textViewIntersection;
    private TextView textViewBorders;
    private TextView textViewExtremePoints;
    private TextView textViewMonotonicity;
    private TextView textViewBijectivity;
    private TextView textViewConvexity;

    private ImageView imageViewGraph;
    private Button HomeButton;

    public RSolvedEx2() {
        // Required empty public constructor
    }

    public static RSolvedEx2 createInstance() {
        RSolvedEx2 fragment = new RSolvedEx2();
        // Do any initial setup or pass arguments here if needed
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_solved_ex2, container, false);

        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewFunction = view.findViewById(R.id.textViewFunction);
        textViewParity = view.findViewById(R.id.textViewParity);
        textViewIntersection = view.findViewById(R.id.textViewIntersection);
        textViewBorders = view.findViewById(R.id.textViewBorders);
        textViewExtremePoints = view.findViewById(R.id.textViewExtremePoints);
        textViewMonotonicity = view.findViewById(R.id.textViewMonotonicity);
        textViewBijectivity = view.findViewById(R.id.textViewBijectivity);
        textViewConvexity = view.findViewById(R.id.textViewConvexity);

        imageViewGraph = view.findViewById(R.id.imageViewGraph);
        HomeButton = view.findViewById(R.id.homeButton);


        // Fetch the document from Firestore
        fetchExerciseData();

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
        db.collection("Exercises").document("RSolvedEx2")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set the text from Firestore to the TextViews
                        textViewTitle.setText(documentSnapshot.getString("title"));
                        textViewFunction.setText(documentSnapshot.getString("function"));
                        textViewParity.setText(documentSnapshot.getString("parity"));
                        textViewIntersection.setText(documentSnapshot.getString("intersection"));
                        textViewBorders.setText(documentSnapshot.getString("borders"));
                        textViewExtremePoints.setText(documentSnapshot.getString("extremePoints"));
                        textViewMonotonicity.setText(documentSnapshot.getString("monotony"));
                        textViewBijectivity.setText(documentSnapshot.getString("bijectivity"));
                        textViewConvexity.setText(documentSnapshot.getString("convexity"));


                        // Load the graph image
                        String graphUrl = documentSnapshot.getString("graphURL");
                        if (graphUrl != null && !graphUrl.isEmpty()) {
                            Picasso.get().load(graphUrl).into(imageViewGraph);
                        } else {
                            // Document does not exist
                            // Handle this case
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Log the error
                    Log.e("FirestoreError", "Error fetching document", e);

                    // Inform the user that an error occurred
                    Toast.makeText(getContext(), "Error loading exercise.", Toast.LENGTH_SHORT).show();

                    // You can also update the UI to reflect the error
                    // For example, hiding the loading indicator or showing an error message directly in the UI
                });
    }
}