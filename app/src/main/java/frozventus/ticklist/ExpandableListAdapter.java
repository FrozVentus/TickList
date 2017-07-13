package frozventus.ticklist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _titleList;
    private HashMap<String, ArrayList<String>> _taskDetails;

    public ExpandableListAdapter(Context context, ArrayList<String> titleList,
                                 HashMap<String, ArrayList<String>> taskDetails){
        _context = context;
        _titleList = titleList;
        _taskDetails = taskDetails;
    }

    @Override
    public int getGroupCount() {
        return _titleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _taskDetails.get(_titleList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _titleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _taskDetails.get(_titleList.get(groupPosition)).get(childPosition);
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.taskTitle);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setText(title);


        ImageButton delete = (ImageButton)convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteQuery = new AlertDialog.Builder(_context)
                        .setTitle("Delete Task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeGroup(groupPosition);
                            }
                        })
                        .setNegativeButton("No", null);
                AlertDialog dialog = deleteQuery.create();
                dialog.show();
            }
        });
        delete.setFocusable(false);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String details = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView detailView = (TextView) convertView
                .findViewById(R.id.taskDetails);
        detailView.setText(details);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void removeGroup(int groupPosition) {
        _taskDetails.remove(getGroup(groupPosition));
        _titleList.remove(getGroup(groupPosition));
        notifyDataSetInvalidated();
    }

}
