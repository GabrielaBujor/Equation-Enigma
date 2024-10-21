package com.example.equationenigma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

// Adapter to display quiz reports
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<QuizReport> reports;

    // Initialize the adapter with a list of reports
    public ReportAdapter(List<QuizReport> reports) {
        this.reports = reports;
    }

    // Inflate the layout and create the ViewHolder
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    // Bind data to the ViewHolder for each item in the list
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        QuizReport report = reports.get(position);
        if (report != null) {
            holder.userNameTextView.setText(report.getUserName() != null ? report.getUserName() : "Unknown User");
            holder.reportNameTextView.setText(report.getReportName() != null ? report.getReportName() : "N/A");
            holder.mistakesTextView.setText("Mistakes: " + report.getMistakes());
            holder.timeTakenTextView.setText("Time: " + report.getTimeTaken());

            // Build a string for detailed results
            StringBuilder detailsBuilder = new StringBuilder();
            if (report.getDetailedResults() != null) {
                for (Map.Entry<String, Boolean> entry : report.getDetailedResults().entrySet()) {
                    detailsBuilder.append(entry.getKey())
                            .append(": ")
                            .append(entry.getValue() ? "Correct" : "Incorrect")
                            .append("\n");
                }
            }
            holder.detailsTextView.setText(detailsBuilder.toString());
        } else {
            // Default text if the report is null
            holder.userNameTextView.setText("User name not available");
            holder.reportNameTextView.setText("Report data not available");
            holder.mistakesTextView.setText("Mistakes: N/A");
            holder.timeTakenTextView.setText("Time: N/A");
            holder.detailsTextView.setText("Details not available");
        }
    }



    // Return the total number of items in list
    @Override
    public int getItemCount() {
        return reports.size();
    }

    // ViewHolder class to hold the views for each item
    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportNameTextView;
        TextView mistakesTextView;
        TextView timeTakenTextView;
        TextView detailsTextView; // TextView to show detailed results
        TextView userNameTextView;

        // Constructor to initiaize the views
        ReportViewHolder(View itemView) {
            super(itemView);
            reportNameTextView = itemView.findViewById(R.id.reportNameTextView);
            mistakesTextView = itemView.findViewById(R.id.mistakesTextView);
            timeTakenTextView = itemView.findViewById(R.id.timeTakenTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }
}

