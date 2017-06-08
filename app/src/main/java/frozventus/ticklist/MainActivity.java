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
import java.util.HashSet;
import java.util.Set;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> activityList;
    ArrayAdapter listAdapter;
    ListView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mView = (ListView) findViewById(R.id.item_list);
        activityList = getArrayMem(getApplicationContext());
        //ss
        //test data
        //activityList.add("First");
        //activityList.add("Second");
        //activityList.add("Third");
        //end of test data
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, activityList);
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
            AlertDialog addQuery = new AlertDialog.Builder(this)
                    .setTitle("Add Task")
                    .setMessage("Enter details of task")
                    .setView(textInput)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(textInput.getText());
                            activityList.add(task);
                            storeArrayMem(activityList, getApplicationContext());
                            updateView();
                        }})
                    .setNegativeButton("Cancel", null)
                    .create();
            addQuery.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateView() {

        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                deletePopup(v);
            }
        });

        mView.setAdapter(listAdapter);
    }

    public void deletePopup(View v) {
        final View mView = v;
        AlertDialog.Builder deleteQuery = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Task")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView selectedText = (TextView) mView;
                        String task = String.valueOf(selectedText.getText());
                        activityList.remove(task);
                        storeArrayMem(activityList, getApplicationContext());
                        listAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog dialog = deleteQuery.create();
        dialog.show();
    }

    public static void storeArrayMem(ArrayList<String> inArrayList, Context context){
        Set<String> addWrite = new HashSet<String>(inArrayList);
        SharedPreferences WordSearchPutPrefs = context.getSharedPreferences("dbArrayValues", Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = WordSearchPutPrefs.edit();
        prefEditor.putStringSet("myArray", addWrite);
        prefEditor.commit();
    }
    public static ArrayList getArrayMem(Context infoCon)
    {
        SharedPreferences WordSearchGetPrefs = infoCon.getSharedPreferences("dbArrayValues",Activity.MODE_PRIVATE);
        Set<String> tempSet = new HashSet<String>();
        tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);
        return new ArrayList<String>(tempSet);
    }

}
