package frozventus.ticklist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.lang.Object;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    List<String> _titleList;
    List<String> _dateList;
    HashMap<Integer, ArrayList<String>> _detailList; // _id of task is used as Key
    List<Integer> _orderList; // hold _id of the task in order
    DBHandler myDB;
    ExpandableListAdapter expListAdapter;
    ExpandableListView mView;
    int currYear, currMonth, currDay, currHour, currMinute;

    //memory saver.
    //public static final String USERDATA = "MyVariables";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mView = (ExpandableListView) findViewById(R.id.item_list);
        _titleList = new LinkedList<String>();
        _dateList = new LinkedList<String>();
        _detailList = new HashMap<Integer, ArrayList<String>>();
        _orderList = new LinkedList<Integer>();
        myDB = new DBHandler(this, _titleList, _dateList, _detailList, _orderList);
        expListAdapter = new frozventus.ticklist.ExpandableListAdapter(this,
                _titleList, _dateList, _detailList, _orderList, myDB);
        myDB.getAllTasks();
        updateView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addTask) {
            //add new task
            final EditText textInput = new EditText(this);
            final AlertDialog addQuery = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_addTask)
                    .setMessage(R.string.prompt_addTask)
                    .setView(textInput)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(textInput.getText());
                            addTask(task);
                            updateView();
                        }})
                    .setNegativeButton("Cancel", null)
                    .create();
            addQuery.show();
            updateView();
            return true;
        }

        if (id == R.id.action_clearAll) {
            AlertDialog clearQuery = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_clearAll)
                    .setMessage(R.string.prompt_clearAll)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog confirmQuery = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.title_clearAllConfirm)
                                    .setMessage(R.string.prompt_clearAllConfirm)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myDB.clearAll();
                                            updateView();
                                        }})
                                    .setNegativeButton("No", null)
                                    .create();
                            confirmQuery.show();
                        }})
                    .setNegativeButton("No", null)
                    .create();
            clearQuery.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        myDB.close();
        super.onDestroy();
    }

    public void updateView() { // update screen

        mView.setAdapter(expListAdapter);

    }

    private boolean addTask(String taskTitle) {

        fill(taskTitle);

        return true;
    }

    private void fill(String title) {
        ArrayList<String> details = new ArrayList<String>();
        // placeholder value
        details.add("");
        details.add("");
        details.add("");

        detailsInput(details, title);
        updateView();
    }

    private Boolean detailsInput(final ArrayList<String> details, final String title) {
        final EditText textInput = new EditText(this);
        final AlertDialog addQuery = new AlertDialog.Builder(this)
                .setTitle(R.string.title_addDetails)
                .setMessage(R.string.prompt_addDetails)
                .setView(textInput)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(textInput.getText());
                        details.remove(0);
                        details.add(0, input); // enter detail
                        dateInput(details, title); // call input of date
                    }})
                .setNegativeButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
    }

    private Boolean dateInput(final ArrayList<String> details, final String title) {
        final Calendar c = Calendar.getInstance();
        currYear = c.get(Calendar.YEAR);
        currMonth = c.get(Calendar.MONTH);
        currDay = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dateDialog =
                new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT,
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
                        timeInput(details, title, dateString); // call input of daily
                    }
                }, currYear, currMonth, currDay);
        dateDialog.setTitle(R.string.title_addDate);
        dateDialog.show();
        return true;
    }

    private Boolean timeInput(final ArrayList<String> details, final String title, final String dateString) {
        final Calendar c = Calendar.getInstance();
        currHour = c.get(Calendar.HOUR_OF_DAY);
        currMinute = c.get(Calendar.MINUTE);
        final TimePickerDialog timeDialog =
                new TimePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                String timeDateString = dateString.concat("  " + timeFormat(hour) +
                                        ":" + timeFormat(minute));
                                details.remove(1);
                                details.add(1, timeDateString); // enter date
                                dailyInput(details, title); // call input of daily
                            }
                            public String timeFormat(int input) {
                                return (input<10)?("0" + input):("" + input);
                            }
                        }, currHour, currMinute, true);
        timeDialog.setTitle(R.string.title_addTime);
        timeDialog.show();
        return true;
    }

    private boolean dailyInput(final ArrayList<String> details, final String title) {
        final AlertDialog addQuery = new AlertDialog.Builder(this)
                .setTitle(R.string.title_addDaily)
                .setMessage(R.string.prompt_addDaily)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        details.remove(2);
                        details.add(2, "Daily task"); // enter daily
                        myDB.addTask(title, details); // save in database
                        updateView();
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        details.remove(2);
                        details.add(2, "One time task"); // enter daily
                        myDB.addTask(title, details); // save in database
                        updateView();
                    }})
                .setNeutralButton("Cancel", null)
                .create();
        addQuery.show();
        return true;
    }

}
