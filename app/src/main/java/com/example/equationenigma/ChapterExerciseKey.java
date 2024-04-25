package com.example.equationenigma;

import java.util.Objects;

public class ChapterExerciseKey {
    private String chapter;
    private String exercise;

    public ChapterExerciseKey(String chapter, String exercise) {
        this.chapter = chapter;
        this.exercise = exercise;
    }

    // Implement equals() and hashCode() to ensure that keys work correctly in a HashMap
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChapterExerciseKey that = (ChapterExerciseKey) o;
        return chapter.equals(that.chapter) && exercise.equals(that.exercise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapter, exercise);
    }
}
