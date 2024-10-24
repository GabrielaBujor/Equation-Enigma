package com.example.equationenigma.Exponential;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equationenigma.HomeFragment;
import com.example.equationenigma.MainActivity;
import com.example.equationenigma.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ESolvedEx1 extends Fragment {

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
    private ProgressBar progressBar;

    public ESolvedEx1() {
        // Required empty public constructor
    }

    public static ESolvedEx1 createInstance() {
        ESolvedEx1 fragment = new ESolvedEx1();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e_solved_ex1, container, false);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

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
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

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
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e("PicassoError", "Error loading image", e);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.error);  // Fallback image on failure
                Log.e("FirebaseStorage", "Error getting image", exception);
            }
        });
    }
    private void fetchExerciseData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Exercises").document("ESolvedEx1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set the text from Firestore to the TextViews
                        textViewTitle.setText(documentSnapshot.getString("title"));
                        textViewFunction.setText(documentSnapshot.getString("function"));
                        textViewParity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("parity")));
                        textViewIntersection.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("intersection")));
                        textViewBorders.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("borders")));
                        textViewExtremePoints.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("extremePoints")));
                        textViewMonotonicity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("monotony")));
                        textViewBijectivity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("bijectivity")));
                        textViewConvexity.setText(Html.fromHtml("&#8226; " + documentSnapshot.getString("convexity")));


                        // Load the graph image
                        String graphPath = documentSnapshot.getString("graphURL");
                        if (graphPath != null && !graphPath.isEmpty()) {
                            fetchAndDisplayImage(imageViewGraph, graphPath);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.e("DataError", "No path for the image available");
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Log the error
                    Log.e("FirestoreError", "Error fetching document", e);

                    // Inform the user that an error occurred
                    Toast.makeText(getContext(), "Error loading exercise.", Toast.LENGTH_SHORT).show();
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