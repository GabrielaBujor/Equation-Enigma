package com.example.equationenigma;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.equationenigma.Exercises.Chapter;
import com.example.equationenigma.Exercises.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    private ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<Chapter> chapterList;
    HashMap<Chapter, List<Exercise>> exerciseListMap;
    private CustomExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        expandableListView = view.findViewById(R.id.chaptersExpandableListView);

        // Initialize chapterList and exerciseListMap with data
        chapterList = new ArrayList<>();
        exerciseListMap = new HashMap<>();

        expandableListView = view.findViewById(R.id.chaptersExpandableListView);
        chapterList = new ArrayList<>();
        exerciseListMap = new HashMap<>();

        // Fetch chapters and exercises from Firebase
        fetchChapters();

        // Set up the adapter with the data
        adapter = new CustomExpandableListAdapter(getContext(), chapterList, exerciseListMap);
        expandableListView.setAdapter(adapter);

        // Load your chapters and exercises here or in another method

        return view;
    }

    private void fetchChapters() {
        // Firebase query to fetch chapter and exercises
        // Update chapterList and exerciseListMap accordingly
    }
}