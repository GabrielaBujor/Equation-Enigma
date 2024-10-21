package com.example.equationenigma;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ChaptersExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context; // Context for accessing resources and layout inflater
    private List<String> listDataHeader; // List of group headers
    private HashMap<String, List<String>> listDataChild; // Child data in format of header title, child title

    // Constructor
    public ChaptersExpandableListAdapter(Context context, List<String> listDataHeader,
                                         HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size(); // Returns the number of headers
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size(); // Returns the number of children
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition); // Returns the group at the specified position
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
        // Returns the child at the specified position within a group
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition; // Returns the group position as its ID
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition; // Returns the child position within its group as its ID
    }

    @Override
    public boolean hasStableIds() {
        return false; // Indicates that IDs are not stable
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item, parent, false);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tvGroup);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(android.R.id.text1);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Indicates that the child items are selectable
    }
}
