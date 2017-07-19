package frozventus.ticklist;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _titleList;
    private HashMap<String, ArrayList<String>> _taskDetails;
    private int currYear, currMonth, currDay;

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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String details = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView detailView = (TextView) convertView
                .findViewById(R.id.taskDetails);
        Button editButton = (Button)convertView.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(childPosition) {
                            case 0:
                                editDetails(groupPosition, childPosition);
                                break;
                            case 1:
                                editDate(groupPosition, childPosition);
                                break;
                            case 2:
                                editDaily(groupPosition, childPosition);
                                break;
                        }
                    }
                });
        editButton.setFocusable(false);
        detailView.setText(details);
        return convertView;
    }

    private boolean editDetails(final int groupPosition, final int childPosition) {
        final EditText textInput = new EditText(_context);
        final AlertDialog addQuery = new AlertDialog.Builder(_context)
                .setTitle("Edit Details")
                .setMessage("Enter new details of task")
                .setView(textInput)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(textInput.getText());
                        _taskDetails.get(_titleList.get(groupPosition)).remove(childPosition);
                        _taskDetails.get(_titleList.get(groupPosition)).add(childPosition, input);
                        notifyDataSetInvalidated();
                    }})
                .setNegativeButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
    }

    private boolean editDate(final int groupPosition, final int childPosition) {
        final Calendar c = Calendar.getInstance();
        currYear = c.get(Calendar.YEAR);
        currMonth = c.get(Calendar.MONTH);
        currDay = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dateDialog =
                new DatePickerDialog(_context, android.app.AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                GregorianCalendar dueDate = new GregorianCalendar(year, month, dayOfMonth) {
                                    @Override
                                    public String toString() {
                                        return new SimpleDateFormat("d MMM yyyy").format(this.getTime());
                                    }
                                };
                                String dateString = dueDate.toString();
                                _taskDetails.get(_titleList.get(groupPosition)).remove(childPosition);
                                _taskDetails.get(_titleList.get(groupPosition)).add(childPosition, dateString);
                                notifyDataSetInvalidated();
                            }
                        }, currYear, currMonth, currDay);
        dateDialog.setTitle("Edit Due Date");
        dateDialog.show();
        return true;
    }

    private boolean editDaily(final int groupPosition, final int childPosition) {
        final AlertDialog addQuery = new AlertDialog.Builder(_context)
                .setTitle("Edit Daily")
                .setMessage("Is this task to be repeated daily?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _taskDetails.get(_titleList.get(groupPosition)).remove(childPosition);
                        _taskDetails.get(_titleList.get(groupPosition)).add(childPosition, "Daily task");
                        notifyDataSetInvalidated();
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _taskDetails.get(_titleList.get(groupPosition)).remove(childPosition);
                        _taskDetails.get(_titleList.get(groupPosition)).add(childPosition, "One time task");
                        notifyDataSetInvalidated();
                    }})
                .setNeutralButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
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
