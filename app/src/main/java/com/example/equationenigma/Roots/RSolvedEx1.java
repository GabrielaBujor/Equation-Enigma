package com.example.equationenigma.Roots;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class RSolvedEx1 extends Fragment {

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

    public RSolvedEx1() {
        // Required empty public constructor
    }

    public static RSolvedEx1 createInstance() {
        RSolvedEx1 fragment = new RSolvedEx1();
        // Do any initial setup or pass arguments here if needed
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_solved_ex1, container, false);

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
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchAndDisplayImage(ImageView imageView, String path) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Check if the path is a full 'gs://' URL or just a filename
        if (!path.startsWith("gs://")) {
            // Assume the file is stored at the root of the Firebase Storage bucket
            path = "gs://equation-enigma.appspot.com/" + path;
        }
        Log.d("FirebaseStorage", "Attempting to load image from path: " + path);
        StorageReference storageRef = storage.getReferenceFromUrl(path);


        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString())
                        .placeholder(R.drawable.loading)  // Placeholder image
                        .error(R.drawable.error)        // Error image
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.error);  // Fallback image on failure
                Log.e("FirebaseStorage", "Error getting image", exception);
            }
        });
    }

    private void fetchExerciseData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Exercises").document("RSolvedEx1")
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
                        String graphPath = documentSnapshot.getString("graphURL");
                        if (graphPath != null && !graphPath.isEmpty()) {
                            fetchAndDisplayImage(imageViewGraph, graphPath);
                        } else {
                            Log.e("DataError", "No path for the image available");
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