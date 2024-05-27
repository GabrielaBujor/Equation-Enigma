package com.example.equationenigma;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends Fragment {
    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<QuizReport> reports = new ArrayList<>();
    private EditText searchInput;

    private static final String TAG = "ReportFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize the RecyclerView and set its adapter
        reportsRecyclerView = rootView.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reportAdapter = new ReportAdapter(reports);
        reportsRecyclerView.setAdapter(reportAdapter);

        searchInput = rootView.findViewById(R.id.searchInput);
        Button searchButton = rootView.findViewById(R.id.searchButton);

        // Determine the user type and load data accordingly
        determineUserTypeAndLoadReports();

        // Setup search button click listener for teachers
        searchButton.setOnClickListener(v -> {
            String searchText = searchInput.getText().toString().trim();
            if (!TextUtils.isEmpty(searchText)) {
                searchStudentsReports(searchText);
            } else {
                Toast.makeText(getContext(), "Please enter a student name to search.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void determineUserTypeAndLoadReports() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("userType").getValue(String.class);
                if ("Teacher".equals(userType)) {
                    // Allow teachers to search
                    searchInput.setEnabled(true);
                } else {
                    // Load only the logged-in student's reports
                    loadReportsForUser(auth.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReportsForUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Loading reports for user ID: " + userId);
        db.collection("quizReports")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reports.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuizReport report = document.toObject(QuizReport.class);
                            reports.add(report);
                        }
                        reportAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Number of reports fetched: " + reports.size());  // Log the count here

                        if (reports.isEmpty()) {
                            Toast.makeText(getContext(), "No reports found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error getting reports: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error getting reports: " + task.getException().getMessage());
                    }
                });
    }




    private void searchStudentsReports(String studentName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Search based on a 'name' field in reports - adjust if using a different field
        db.collection("quizReports")
                .whereEqualTo("studentName", studentName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reports.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuizReport report = document.toObject(QuizReport.class);
                            reports.add(report);
                        }
                        reportAdapter.notifyDataSetChanged();
                        if (reports.isEmpty()) {
                            Toast.makeText(getContext(), "No reports found for that student.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error searching reports: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
