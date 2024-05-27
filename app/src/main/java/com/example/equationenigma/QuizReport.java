package com.example.equationenigma;

import java.util.List;
import java.util.Map;

public class QuizReport {
    private String userId;
    private String userName;
    private String reportName;
    private int mistakes;
    private String timeTaken;
    private Map<String, Boolean> detailedResults;

    // Constructor

    // No-argument constructor required for Firebase deserialization
    public QuizReport() {
        //Default constructor required for calls to DataSnapshot.getValue(QuizReport.class)
    }

    public QuizReport(String userId, String userName, String reportName, int mistakes, String timeTaken, Map<String, Boolean> detailedResults) {
        this.userId = userId;
        this.userName = userName;
        this.reportName = reportName;
        this.mistakes = mistakes;
        this.timeTaken = timeTaken;
        this.detailedResults = detailedResults;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

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

    // Setters if needed
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setDetailedResults(Map<String, Boolean> detailedResults) {
        this.detailedResults = detailedResults;
    }
}



