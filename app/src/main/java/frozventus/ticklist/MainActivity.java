package frozventus.ticklist;

import android.app.DatePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;
import java.lang.Object;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    ArrayList<String> titleList;
    HashMap<String, ArrayList<String>> detailList;
    ExpandableListAdapter expListAdapter;
    ExpandableListView mView;
    int currYear, currMonth, currDay;

    //memory saver.
    public static final String USERDATA = "MyVariables";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mView = (ExpandableListView) findViewById(R.id.item_list);
        //   activityList = getArrayMem(getApplicationContext());
        titleList = new ArrayList<String>();
        detailList = new HashMap<String, ArrayList<String>>();
        expListAdapter = new frozventus.ticklist.ExpandableListAdapter(this,
                titleList, detailList);
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
                    .setTitle("Add Task")
                    .setMessage("Enter title of task")
                    .setView(textInput)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(textInput.getText());

                            boolean added = addTask(task);

                            if(!added) {
                                dialog.dismiss();
                                notAdded();
                            }

//                            storeArrayMem(activityList, getApplicationContext());
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
                    .setTitle("Clear All")
                    .setMessage("Are you sure you want to clear all tasks?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog confirmQuery = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Clear All")
                                    .setMessage("This process cannot be reverted, proceed to clear all?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            detailList.clear();
                                            titleList.clear();
//                            storeArrayMem(activityList, getApplicationContext());
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

    public void updateView() {

        mView.setAdapter(expListAdapter);

    }
/*
    public static void storeArrayMem(ArrayList<String> inArrayList, Context context){
        Set<String> addWrite = new HashSet<String>(inArrayList);
        SharedPreferences WordSearchPutPrefs = context.getSharedPreferences("dbArrayValues",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = WordSearchPutPrefs.edit();
        prefEditor.putStringSet("myArray", addWrite);
        prefEditor.commit();
    }
    public static ArrayList getArrayMem(Context infoCon)
    {
        SharedPreferences WordSearchGetPrefs = infoCon.getSharedPreferences("dbArrayValues",
                Activity.MODE_PRIVATE);
        Set<String> tempSet = new HashSet<String>();
        tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);
        return new ArrayList<String>(tempSet);
    }
*/

    private void getTestData() {// not used anymore

        //add titles
        titleList.add("task 1");
        titleList.add("task 2");
        titleList.add("task 3");

        fill("task 1");
        fill("task 2");
        fill("task 3");
    }

    private boolean addTask(String taskTitle) {
        if(titleList.contains(taskTitle)) {
            return false;
        }
        fill(taskTitle);
        titleList.add(taskTitle);
        Context context = getApplicationContext();
        CharSequence text = "Task Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return true;
    }

    private void removeTask(String title) {
        detailList.remove(title);
        titleList.remove(title);
    }

    private void fill(String title) {
        ArrayList<String> details = new ArrayList<String>(3);
        details.add("");
        details.add("");
        details.add("");

        detailsInput(details, title);

        //details.add("details here");
        //details.add(1,"due date here");
        //details.add(2,"is daily? here");

        detailList.put(title, details);
    }

    private Boolean detailsInput(final ArrayList<String> details, final String title) {
        final EditText textInput = new EditText(this);
        final AlertDialog addQuery = new AlertDialog.Builder(this)
                .setTitle("Details")
                .setMessage("Enter details of task")
                .setView(textInput)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(textInput.getText());
                        details.remove(0);
                        details.add(0, input);
                        dateInput(details, title);
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTask(title);
                        updateView();
                    }})
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
                        details.remove(1);
                        details.add(1, dateString);
                        dailyInput(details, title);
                    }
                }, currYear, currMonth, currDay);
        dateDialog.setTitle("Due Date");
        dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            removeTask(title);
                            updateView();
                        }
                    }
                });
        dateDialog.show();
        return true;
    }

    private boolean dailyInput(final ArrayList<String> details, final String title) {
        final AlertDialog addQuery = new AlertDialog.Builder(this)
                .setTitle("Daily")
                .setMessage("Is this task to be repeated daily?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        details.remove(2);
                        details.add(2, "Daily task");
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        details.remove(2);
                        details.add(2, "One time task");
                    }})
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTask(title);
                        updateView();
                    }})
                .create();
        addQuery.show();
        return true;
    }

    private void notAdded() {
        AlertDialog.Builder notAddedMsg =
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Task Already Exists")
                        .setMessage("A task with the same title already exists, please edit that task or choose a different title")
                        .setNeutralButton("Close", null);
        AlertDialog notAdded = notAddedMsg.create();
        notAdded.show();
    }


    //save hashMap
    private static void saveMap(String key, Map<String,String> inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).commit();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }

    //load map
    private static Map<String,String> loadMap(String key){
        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = getApplicationContext().getInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }


}
