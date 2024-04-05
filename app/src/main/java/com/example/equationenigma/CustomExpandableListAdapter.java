package com.example.equationenigma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter{
    private Context context;
    private List<Chapter> chapterList; // List of chapters (groups)
    private HashMap<Chapter, List<Exercise>> exerciseListMap; // Mapping of each chapter to its list of exercises (children)

    public CustomExpandableListAdapter(Context context, List<Chapter> chapterList,
                                       HashMap<Chapter, List<Exercise>> exerciseListMap) {
        this.context = context;
        this.chapterList = chapterList;
        this.exerciseListMap = exerciseListMap;
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
            convertView = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(android.R.id.text1);
        listTitleTextView.setText(chapter.getTitle());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Exercise exercise = (Exercise) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(android.R.id.text1);
        expandedListTextView.setText(exercise.getTitle());
        // If it's a solved exercise, you might want to set a different background or text color
        if (exercise instanceof SolvedExercise) {
            // Set a different style for solved exercises
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Allows the children to be selectable
    }
}
