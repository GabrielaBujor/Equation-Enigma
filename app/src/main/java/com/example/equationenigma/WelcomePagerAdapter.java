package com.example.equationenigma;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class WelcomePagerAdapter extends FragmentStateAdapter {

    private final List<Pair<Integer, String>> pagesData;

    // Initialize the adapter with the fragmentActivity and the list of pages
    public WelcomePagerAdapter(FragmentActivity fa, List<Pair<Integer, String>> pagesData) {
        super(fa);
        this.pagesData = pagesData;
    }

    // Create a fragment for a position
    @Override
    public Fragment createFragment(int position) {
        Pair<Integer, String> pageData = pagesData.get(position);
        // Create a new WelcomePageFragment with the image resource ID and text description
        return new WelcomePageFragment(pageData.first, pageData.second);
    }

    // Return the total number of items in adapter
    @Override
    public int getItemCount() {
        return pagesData.size();
    }
}
