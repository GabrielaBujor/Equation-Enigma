package com.example.equationenigma;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.equationenigma.R;

public class WelcomePageFragment extends Fragment {

    private int imageResourceId;
    private String textDescription;

    public WelcomePageFragment(int imageResourceId, String textDescription) {
        this.imageResourceId = imageResourceId;
        this.textDescription = textDescription;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_page, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);

        imageView.setImageResource(imageResourceId);
        textViewDescription.setText(textDescription);

        return view;
    }
}
