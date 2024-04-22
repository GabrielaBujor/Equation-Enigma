package com.example.equationenigma.Power;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.equationenigma.R;


public class PEx1 extends Fragment {

    private TextView textViewTitle;
    private TextView textViewFunction;
    private TextView textViewParity;
    private TextView textViewIntersection;
    private TextView textViewBorders;
    private TextView textViewExtremePoints;
    private TextView textViewMonotonicity;
    private TextView textViewBijectivity;
    private TextView textViewConvexity;

    private ImageView imageViewGraph;
    private Button HomeButton;

    public PEx1() {
        // Required empty public constructor
    }

    public static PEx1 createInstance() {
        PEx1 fragment = new PEx1();
        // Do any initial setup or pass arguments here if needed
        return fragment;
    }

}