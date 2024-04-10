package com.example.equationenigma.Exercises;

import java.util.List;

public class Chapter {
    private String title;
    private List<Exercise> exercises;

    // Constructor, getters, and setters
    public Chapter(String title, List<Exercise> exercises) {
        this.title = title;
        this.exercises = exercises;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
