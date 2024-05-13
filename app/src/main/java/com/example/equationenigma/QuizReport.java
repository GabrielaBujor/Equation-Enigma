package com.example.equationenigma;

import java.util.List;
import java.util.Map;

public class QuizReport {
    private String reportName;
    private int mistakes;
    private String timeTaken;
    private Map<String, Boolean> detailedResults; // Function and its correctness

    public QuizReport(String reportName, int mistakes, String timeTaken, Map<String, Boolean> detailedResults) {
        this.reportName = reportName;
        this.mistakes = mistakes;
        this.timeTaken = timeTaken;
        this.detailedResults = detailedResults;
    }

    // Getters
    public String getReportName() {
        return reportName;
    }

    public int getMistakes() {
        return mistakes;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public Map<String, Boolean> getDetailedResults() {
        return detailedResults;
    }
}


