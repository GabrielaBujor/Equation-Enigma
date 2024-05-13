package com.example.equationenigma;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        // Load reports from Firestore
        loadReports();

        return rootView;
    }

    private Map<String, Boolean> convertMap(Map<String, Object> rawMap) {
        Map<String, Boolean> resultMap = new HashMap<>();
        if (rawMap != null) {
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Boolean) {  // Make sure the value is actually a Boolean
                    resultMap.put(entry.getKey(), (Boolean) value);
                } else {
                    // Handle the case where the value is not a Boolean
                    // Depending on your logic, you may throw an exception, log a warning, or set a default value
                    resultMap.put(entry.getKey(), false);  // Assuming default to false if type is incorrect
                }
            }
        }
        return resultMap;
    }


    private void loadReports() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("quizReports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reports.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String reportName = document.getString("reportName");
                            int mistakes = ((Long) document.get("mistakes")).intValue();
                            String timeTaken = document.getString("timeTaken");
                            Map<String, Object> rawResults = (Map<String, Object>) document.get("detailedResults");
                            Map<String, Boolean> detailedResults = convertMap(rawResults);  // Convert before passing to QuizReport

                            reports.add(new QuizReport(reportName, mistakes, timeTaken, detailedResults));
                        }
                        reportAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error getting reports.", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
