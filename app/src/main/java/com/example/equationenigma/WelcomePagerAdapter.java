package com.example.equationenigma;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class WelcomePagerAdapter extends FragmentStateAdapter {

    private final List<Pair<Integer, String>> pagesData;

    public WelcomePagerAdapter(FragmentActivity fa, List<Pair<Integer, String>> pagesData) {
        super(fa);
        this.pagesData = pagesData;
    }

    @Override
    public Fragment createFragment(int position) {
        Pair<Integer, String> pageData = pagesData.get(position);
        return new WelcomePageFragment(pageData.first, pageData.second);
    }

    @Override
    public int getItemCount() {
        return pagesData.size();
    }
}
