package frozventus.ticklist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _titleList;
    private List<String> _dateList;
    private HashMap<Integer, ArrayList<String>> _taskDetails; // _id of task is used as Key
    private List<Integer> _orderList; // hold _id of the task in order
    private List<String> _lastCompleted;
    private int currYear, currMonth, currDay, currHour, currMinute;
    private DBHandler _db;

    public ExpandableListAdapter(Context context, List<String> titleList,
                                 List<String> dateList,
                                 HashMap<Integer, ArrayList<String>> taskDetails,
                                 List<Integer> orderList, List<String> lastCompleted,
                                 DBHandler db){
        _context = context;
        _titleList = titleList;
        _dateList = dateList;
        _taskDetails = taskDetails;
        _orderList = orderList;
        _lastCompleted = lastCompleted;
        _db = db;
    }

    @Override
    public int getGroupCount() {
        return _titleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _taskDetails.get(getGroupIndex(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        String topString = _titleList.get(groupPosition);
        String isDaily = _taskDetails.get(getGroupIndex(groupPosition)).get(2); // get daily
        if(isDaily.equals("Daily task")) { // check if is daily task
            topString = topString.concat(" (Daily Task)");
        }
        String groupString = topString +
                System.getProperty("line.separator") +
                _dateList.get(groupPosition);
        return groupString;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _taskDetails.get(getGroupIndex(groupPosition)).get(childPosition);
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
        // set text in group
        TextView titleView = (TextView) convertView.findViewById(R.id.taskTitle);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setText(title); // set title

        // set checkbox button
        ImageButton checkbox = (ImageButton)convertView.findViewById(R.id.checkbox); // delete button
        final Calendar c = Calendar.getInstance(); // obtains current date
        currYear = c.get(Calendar.YEAR);
        currMonth = c.get(Calendar.MONTH);
        currDay = c.get(Calendar.DAY_OF_MONTH);
        GregorianCalendar dueDate = new GregorianCalendar(currYear, currMonth, currDay) {
            @Override
            public String toString() {
                return new SimpleDateFormat("d MMM yyyy").format(this.getTime());
            }
        };
        String dateString = dueDate.toString(); // get current date
        String lastCompleted = _lastCompleted.get(groupPosition); // get last completed
        if(lastCompleted.equals("null")) { // incomplete task
            setCheckboxEmpty(checkbox, groupPosition, dateString);
        }
        else { // completed task
            String isDaily = _taskDetails.get(getGroupIndex(groupPosition)).get(2); // get daily
            if(isDaily.equals("Daily task")) { // check if is daily task
                if(lastCompleted.equals(dateString)) {// completed on that day
                    setCheckboxFull(checkbox, groupPosition, dateString);
                }
                else { // not completed that day
                    setCheckboxEmpty(checkbox, groupPosition, dateString);
                }
            }
            else {  // one time off task & completed
                setCheckboxFull(checkbox, groupPosition, dateString);
            }
        }


        // set edit button
        Button editButton = (Button)convertView.findViewById(R.id.editGroup); // edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTitle(groupPosition);
            }
        });
        editButton.setFocusable(false); // allow the text to be clickable

        // set delete button
        ImageButton delete = (ImageButton)convertView.findViewById(R.id.delete); // delete button
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // delete confirmation
                AlertDialog.Builder deleteQuery = new AlertDialog.Builder(_context)
                        .setTitle(R.string.title_deleteTask)
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
        delete.setFocusable(false); // allow the text to be clickable

        return convertView;
    }

    private void setCheckboxEmpty(final ImageButton checkbox, final int groupPosition, final String dateString) {
        checkbox.setImageResource(R.drawable.ic_checkbox_empty);
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // checkbox
                checkbox.setImageResource(R.drawable.ic_checkbox_full);
                setCheckboxFull(checkbox, groupPosition, dateString);
                _db.updateTask(getGroupIndex(groupPosition), 3, dateString);
            }
        });
        checkbox.setFocusable(false); // allow the text to be clickable
    }

    private void setCheckboxFull(final ImageButton checkbox, final int groupPosition, final String dateString) {
        checkbox.setImageResource(R.drawable.ic_checkbox_full);
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // checkbox
                checkbox.setImageResource(R.drawable.ic_checkbox_empty);
                setCheckboxEmpty(checkbox, groupPosition, dateString);
                _db.updateTask(getGroupIndex(groupPosition), 3, "null");
            }
        });
        checkbox.setFocusable(false); // allow the text to be clickable
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
        detailView.setText(details); // set detail

        Button editButton = (Button)convertView.findViewById(R.id.editChild); // edit button
        editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(childPosition) { // determine which entry to edit
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
        editButton.setFocusable(false); // allow the text to be clickable
        return convertView;
    }

    private int getGroupIndex(int groupPostion) { //used to obtain index of task saved in database
        return _orderList.get(groupPostion);
    }

    private boolean editTitle(final int groupPosition) {
        final EditText textInput = new EditText(_context);
        final AlertDialog addQuery = new AlertDialog.Builder(_context)
                .setTitle(R.string.title_editTask)
                .setMessage(R.string.prompt_editTask)
                .setView(textInput)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(textInput.getText());
                        // update datebase
                        _db.updateTitle(getGroupIndex(groupPosition), task);
                        // update adapter
                        notifyDataSetInvalidated();
                    }})
                .setNegativeButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
    }

    private boolean editDetails(final int groupPosition, final int childPosition) {
        // edit detail of task
        final EditText textInput = new EditText(_context); // text input
        final AlertDialog addQuery = new AlertDialog.Builder(_context) // text input popup
                .setTitle(R.string.title_editDetails)
                .setMessage(R.string.prompt_editDetails)
                .setView(textInput)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(textInput.getText());
                        // update database
                        _db.updateTask(getGroupIndex(groupPosition), childPosition, input);
                        // update adapter
                        notifyDataSetInvalidated();
                    }})
                .setNegativeButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
    }

    private boolean editDate(final int groupPosition, final int childPosition) {
        final Calendar c = Calendar.getInstance(); // obtains current date
        currYear = c.get(Calendar.YEAR);
        currMonth = c.get(Calendar.MONTH);
        currDay = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dateDialog = // date picker popup
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
                                timeInput(groupPosition, childPosition, dateString);
                            }
                        }, currYear, currMonth, currDay);
        dateDialog.setTitle(R.string.title_editDate);
        dateDialog.show();
        return true;
    }

    private Boolean timeInput(final int groupPosition, final int childPosition, final String dateString) {
        final Calendar c = Calendar.getInstance();
        currHour = c.get(Calendar.HOUR_OF_DAY);
        currMinute = c.get(Calendar.MINUTE);
        final TimePickerDialog timeDialog =
                new TimePickerDialog(_context, android.app.AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                String timeDateString = dateString.concat("  " + timeFormat(hour) +
                                        ":" + timeFormat(minute));
                                // update database
                                _db.updateTask(getGroupIndex(groupPosition), childPosition, timeDateString);
                                // update adapter
                                notifyDataSetInvalidated();
                            }
                            public String timeFormat(int input) {
                                return (input<10)?("0" + input):("" + input);
                            }
                        }, currHour, currMinute, true);
        timeDialog.setTitle(R.string.title_editTime);
        timeDialog.show();
        return true;
    }

    private boolean editDaily(final int groupPosition, final int childPosition) {
        final AlertDialog addQuery = new AlertDialog.Builder(_context)
                .setTitle(R.string.title_editDaily)
                .setMessage(R.string.prompt_editDaily)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // update database
                        _db.updateTask(getGroupIndex(groupPosition), childPosition, "Daily Task");
                        // update adapter
                        notifyDataSetInvalidated();
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // update database
                        _db.updateTask(getGroupIndex(groupPosition), childPosition, "One time task");
                        // update adapter
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
        // update database
        _db.deleteTask(getGroupIndex(groupPosition));
        // update adapter
        notifyDataSetInvalidated();
    }

}
