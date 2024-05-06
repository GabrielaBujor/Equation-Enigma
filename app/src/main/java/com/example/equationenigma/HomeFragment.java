package com.example.equationenigma;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.equationenigma.Exercises.Chapter;
import com.example.equationenigma.Exercises.Exercise;
import com.example.equationenigma.Exponential.EEx1;
import com.example.equationenigma.Exponential.EEx2;
import com.example.equationenigma.Exponential.EEx3;
import com.example.equationenigma.Exponential.ESolvedEx1;
import com.example.equationenigma.Exponential.ESolvedEx2;
import com.example.equationenigma.Logarithmic.LEx1;
import com.example.equationenigma.Logarithmic.LEx2;
import com.example.equationenigma.Logarithmic.LEx3;
import com.example.equationenigma.Logarithmic.LSolvedEx1;
import com.example.equationenigma.Logarithmic.LSolvedEx2;
import com.example.equationenigma.Power.PEx1;
import com.example.equationenigma.Power.PEx2;
import com.example.equationenigma.Power.PEx3;
import com.example.equationenigma.Power.PSolvedEx1;
import com.example.equationenigma.Power.PSolvedEx2;
import com.example.equationenigma.Roots.REx1;
import com.example.equationenigma.Roots.REx2;
import com.example.equationenigma.Roots.REx3;
import com.example.equationenigma.Roots.RSolvedEx1;
import com.example.equationenigma.Roots.RSolvedEx2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    ExpandableListView expandableListView;
    ChaptersExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private HashMap<ChapterExerciseKey, Class<? extends Fragment>> fragmentMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // get the listview
        expandableListView = rootView.findViewById(R.id.fragment_container);

        // preparing list data
        prepareListData();
        prepareFragmentMap();

        listAdapter = new ChaptersExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(listAdapter);

        // Listview Group click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String chapter = (String) listAdapter.getGroup(groupPosition);
                String exercise = (String) listAdapter.getChild(groupPosition, childPosition);
                ChapterExerciseKey key = new ChapterExerciseKey(chapter, exercise);
                Class<? extends Fragment> fragmentClass = fragmentMap.get(key);

                if (fragmentClass != null) {
                    try {
                        Fragment fragmentInstance = (Fragment) fragmentClass.getMethod("createInstance").invoke(null);
                        navigateToFragment(fragmentInstance);
                    } catch (Exception e) { // Handle all the reflection related exceptions
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });


        return rootView;
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void prepareFragmentMap() {
        fragmentMap = new HashMap<>();
        // Use chapter and exercise names to create  unique keys
        fragmentMap.put(new ChapterExerciseKey("Power", "Solved exercise 1"), PSolvedEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Power", "Solved exercise 2"), PSolvedEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Power", "Exercise 1"), PEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Power", "Exercise 2"), PEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Power", "Exercise 3"), PEx3.class);
        fragmentMap.put(new ChapterExerciseKey("Roots", "Solved exercise 1"), RSolvedEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Roots", "Solved exercise 2"), RSolvedEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Roots", "Exercise 1"), REx1.class);
        fragmentMap.put(new ChapterExerciseKey("Roots", "Exercise 2"), REx2.class);
        fragmentMap.put(new ChapterExerciseKey("Roots", "Exercise 3"), REx3.class);
        fragmentMap.put(new ChapterExerciseKey("Exponential", "Solved exercise 1"), ESolvedEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Exponential", "Solved exercise 2"), ESolvedEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Exponential", "Exercise 1"), EEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Exponential", "Exercise 2"), EEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Exponential", "Exercise 3"), EEx3.class);
        fragmentMap.put(new ChapterExerciseKey("Logarithmic", "Solved exercise 1"), LSolvedEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Logarithmic", "Solved exercise 2"), LSolvedEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Logarithmic", "Exercise 1"), LEx1.class);
        fragmentMap.put(new ChapterExerciseKey("Logarithmic", "Exercise 2"), LEx2.class);
        fragmentMap.put(new ChapterExerciseKey("Logarithmic", "Exercise 3"), LEx3.class);
    }



    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Power");
        listDataHeader.add("Roots");
        listDataHeader.add("Exponential");
        listDataHeader.add("Logarithmic");


        // Adding child data for the chapters
        List<String> powerExercises = new ArrayList<>();
        powerExercises.add("Solved exercise 1");
        powerExercises.add("Solved exercise 2");
        powerExercises.add("Exercise 1");
        powerExercises.add("Exercise 2");
        powerExercises.add("Exercise 3");

        List<String> rootsExercises = new ArrayList<>();
        rootsExercises.add("Solved exercise 1");
        rootsExercises.add("Solved exercise 2");
        rootsExercises.add("Exercise 1");
        rootsExercises.add("Exercise 2");
        rootsExercises.add("Exercise 3");

        List<String> logarithmicExercises = new ArrayList<>();
        logarithmicExercises.add("Solved exercise 1");
        logarithmicExercises.add("Solved exercise 2");
        logarithmicExercises.add("Exercise 1");
        logarithmicExercises.add("Exercise 2");
        logarithmicExercises.add("Exercise 3");

        List<String> exponentialExercises = new ArrayList<>();
        exponentialExercises.add("Solved exercise 1");
        exponentialExercises.add("Solved exercise 2");
        exponentialExercises.add("Exercise 1");
        exponentialExercises.add("Exercise 2");
        exponentialExercises.add("Exercise 3");

        listDataChild.put("Power", powerExercises); // Header, Child data
        listDataChild.put("Roots", rootsExercises);
        listDataChild.put("Logarithmic", logarithmicExercises);
        listDataChild.put("Exponential", exponentialExercises);





    }



}