package com.example.equationenigma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.equationenigma.Quizzes.Quiz1;
import com.example.equationenigma.Quizzes.Quiz2;
import com.example.equationenigma.Quizzes.Quiz3;
import com.example.equationenigma.Quizzes.Quiz4;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {
    ListView listView;
    QuizListAdapter adapter;
    List<String> quizzes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Initialize the ListView
        listView = rootView.findViewById(R.id.quiz_listView);

        // Prepare the list data for quizzes
        prepareQuizData();

        // Initialize the adapter with the quizzes list
        adapter = new QuizListAdapter(getActivity(), quizzes);

        // Setting the adapter to the ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigateToQuizDetails(position);
            }
        });

        return rootView;
    }

    private void navigateToQuizDetails(int position) {
        String quizName = quizzes.get(position);
        // Navigate based on the quiz selected
        switch (quizName) {
            case "Quiz 1":
                navigateToFragment(new Quiz1());
                break;
            case "Quiz 2":
                navigateToFragment(new Quiz2());
                break;
            case "Quiz 3":
                navigateToFragment(new Quiz3());
                break;
            case "Quiz 4":
                navigateToFragment(new Quiz4());
                break;
            default:
                Toast.makeText(getActivity(), "Quiz not found", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void prepareQuizData() {
        quizzes = new ArrayList<>();
        quizzes.add("Quiz 1");
        quizzes.add("Quiz 2");
        quizzes.add("Quiz 3");
        quizzes.add("Quiz 4");
    }
}
