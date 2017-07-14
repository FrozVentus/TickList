package frozventus.ticklist;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> titleList;
    HashMap<String, ArrayList<String>> detailList;
    ExpandableListAdapter expListAdapter;
    ExpandableListView mView;

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
/*
                            if(!added) {
                                dialog.dismiss();
                                notAdded();
                            }
*/
//                            storeArrayMem(activityList, getApplicationContext());
                            updateView();
                        }})
                    .setNegativeButton("Cancel", null)
                    .create();
            addQuery.show();
            updateView();
            return true;
        }
/*
        if (id == R.id.action_clearAll) {
            AlertDialog clearQuery = new AlertDialog.Builder(this)
                    .setTitle("Clear All")
                    .setMessage("Are you sure you want to clear all tasks?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activityList.clear();
                            storeArrayMem(activityList, getApplicationContext());
                            updateView();
                        }})
                    .setNegativeButton("No", null)
                    .create();
            clearQuery.show();
            return true;
        }
*/
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

    private void getTestData() {

        //add titles
        titleList.add("task 1");
        titleList.add("task 2");
        titleList.add("task 3");

        fillDetails("task 1");
        fillDetails("task 2");
        fillDetails("task 3");
    }

    private boolean addTask(String taskTitle) {
        if(titleList.contains(taskTitle)) {
            Context context = getApplicationContext();
            CharSequence text = "An error has occurred, make sure task is not repeated";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }
        titleList.add(taskTitle);
        fillDetails(taskTitle);
        Context context = getApplicationContext();
        CharSequence text = "Task Added";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return true;
    }

    private void fillDetails(String title) {
        ArrayList<String> details = new ArrayList<String>();
        details.add("details here");
        details.add("is daily? here");
        details.add("due date here");
        details.add("");
        details.add("click here to delete task");

        detailList.put(title, details);
    }

    private void notAdded() {
        AlertDialog.Builder notAddedMsg =
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Error")
                        .setMessage("An error has occurred, make sure task is not repeated")
                        .setNegativeButton("Close", null);
        AlertDialog notAdded = notAddedMsg.create();
        notAdded.show();
    }
}
