package com.example.equationenigma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.equationenigma.Exercises.Chapter;
import com.example.equationenigma.Exercises.Exercise;
import com.example.equationenigma.Exercises.SolvedExercise;
import com.example.equationenigma.Power.PSolvedEx1;
import com.example.equationenigma.Power.PSolvedEx2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter{
    private Context context;
    private List<Chapter> chapterList; // List of chapters (groups)
    private HashMap<Chapter, List<Exercise>> exerciseListMap; // Mapping of each chapter to its list of exercises (children)
    private Map<String, FragmentSupplier> fragmentCreatorMap;

    public CustomExpandableListAdapter(Context context, List<Chapter> chapterList,
                                       HashMap<Chapter, List<Exercise>> exerciseListMap) {
        this.context = context;
        this.chapterList = chapterList;
        this.exerciseListMap = exerciseListMap;

        fragmentCreatorMap = new HashMap<>();
        fragmentCreatorMap.put("Solved ex 1", () -> new PSolvedEx1());
        fragmentCreatorMap.put("Solved ex 2", () -> new PSolvedEx2());
// Add other exercises...
    }

    @Override
    public int getGroupCount() {
        return chapterList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return exerciseListMap.get(chapterList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return chapterList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return exerciseListMap.get(chapterList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Chapter chapter = (Chapter) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_item, parent, false);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.tvGroup);
        listTitleTextView.setText(chapter.getTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Exercise exercise = (Exercise) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(exercise.getTitle());
        // Handle child item clicks
        textView.setOnClickListener(v -> {
            FragmentSupplier fragmentSupplier = fragmentCreatorMap.get(exercise.getTitle());
            if (fragmentSupplier != null) {
                navigateToFragment(fragmentSupplier.get());
            } else {
                // Handle the case where the exercise does not have a corresponding fragment
            }
        });

//        convertView.setOnClickListener(v -> {
//            String exerciseTitle = (String) getChild(groupPosition, childPosition);
//            Class<? extends Fragment> fragmentClass = fragmentMap.get(exerciseTitle);
//
//            if (fragmentClass != null) {
//                try {
//                    Fragment fragmentInstance = fragmentClass.newInstance();
//                    navigateToFragment(fragmentInstance);
//                } catch (InstantiationException | IllegalAccessException e) {
//                    e.printStackTrace(); // Handle the exception as appropriate
//                }
//            }
//        });

        return convertView;


        }

        private void navigateToFragment(Fragment fragment) {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment) // Use the frame layout ID from MainActivity
                    .commit();
        }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Allows the children to be selectable
    }
}
