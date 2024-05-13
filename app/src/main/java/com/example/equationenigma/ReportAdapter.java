package com.example.equationenigma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<QuizReport> reports;

    public ReportAdapter(List<QuizReport> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        QuizReport report = reports.get(position);
        holder.reportNameTextView.setText(report.getReportName());
        holder.mistakesTextView.setText("Mistakes: " + report.getMistakes());
        holder.timeTakenTextView.setText("Time: " + report.getTimeTaken());

        StringBuilder detailsBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : report.getDetailedResults().entrySet()) {
            detailsBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue() ? "Correct" : "Incorrect")
                    .append("\n");
        }
        holder.detailsTextView.setText(detailsBuilder.toString());
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportNameTextView;
        TextView mistakesTextView;
        TextView timeTakenTextView;
        TextView detailsTextView; // TextView to show detailed results

        ReportViewHolder(View itemView) {
            super(itemView);
            reportNameTextView = itemView.findViewById(R.id.reportNameTextView);
            mistakesTextView = itemView.findViewById(R.id.mistakesTextView);
            timeTakenTextView = itemView.findViewById(R.id.timeTakenTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView); // Make sure this ID exists in item_report.xml
        }
    }
}

