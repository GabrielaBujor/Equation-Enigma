package com.example.equationenigma;

public class SolvedExercise extends Exercise{
    private String solution;

    // Constructor, getters, and setters
    public SolvedExercise(String title, String hint, String solution) {
        super(title, true, hint); // SolvedExercise is always isSolved = true
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
