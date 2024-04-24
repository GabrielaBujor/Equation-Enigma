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
import com.example.equationenigma.Power.PSolvedEx1;
import com.example.equationenigma.Power.PSolvedEx2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    ExpandableListView expandableListView;
    ChaptersExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private HashMap<String, Class<? extends Fragment>> fragmentMap;


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
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = (String) listAdapter.getChild(groupPosition, childPosition);
                // Use the createInstance() method instead of newInstance()
                Fragment fragment;
                switch (selectedItem) {
                    case "Solved ex 1":
                        fragment = PSolvedEx1.createInstance();
                        break;
                    case "Solved ex 2":
                        fragment = PSolvedEx2.createInstance();
                        break;
                    // Add cases for other fragments
                    default:
                        fragment = null; // or handle it with a default case
                        break;
                }

                // Make sure you don't attempt to navigate with a null fragment
                if (fragment != null) {
                    navigateToFragment(fragment);
                } else {
                    // Handle the null fragment case (show error message or log)
                }
                return true;
            }
        });

        return rootView;
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void prepareFragmentMap() {
        fragmentMap = new HashMap<>();
        fragmentMap.put("Solved ex 1", PSolvedEx1.class);
        fragmentMap.put("Solved ex 2", PSolvedEx2.class);
        // add mappings for other exercise fragments
    }

    /*
     * Preparing the list data
     */
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
        powerExercises.add("Solved ex 1");
        powerExercises.add("Solved ex 2");
        powerExercises.add("Exercise 1");
        powerExercises.add("Exercise 2");
        powerExercises.add("Exercise 3");

        List<String> rootsExercises = new ArrayList<>();
        rootsExercises.add("Solved ex 1");
        rootsExercises.add("Solved ex 2");
        rootsExercises.add("Exercise 1");
        rootsExercises.add("Exercise 2");
        rootsExercises.add("Exercise 3");

        List<String> logarithmicExercises = new ArrayList<>();
        logarithmicExercises.add("Solved ex 1");
        logarithmicExercises.add("Solved ex 2");
        logarithmicExercises.add("Exercise 1");
        logarithmicExercises.add("Exercise 2");
        logarithmicExercises.add("Exercise 3");

        List<String> exponentialExercises = new ArrayList<>();
        exponentialExercises.add("Solved ex 1");
        exponentialExercises.add("Solved ex 2");
        exponentialExercises.add("Exercise 1");
        exponentialExercises.add("Exercise 2");
        exponentialExercises.add("Exercise 3");

        listDataChild.put("Power", powerExercises); // Header, Child data
        listDataChild.put("Roots", rootsExercises);
        listDataChild.put("Logarithmic", logarithmicExercises);
        listDataChild.put("Exponential", exponentialExercises);





    }



}