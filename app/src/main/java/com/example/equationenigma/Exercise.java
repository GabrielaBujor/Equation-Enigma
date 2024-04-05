package com.example.equationenigma;

public class Exercise {
    private String title;
    private boolean isSolved;
    private String hint;

    // Constructor, getters, and setters
    public Exercise(String title, boolean isSolved, String hint) {
        this.title = title;
        this.isSolved = isSolved;
        this.hint = hint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
