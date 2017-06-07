package frozventus.ticklist;

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
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

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
//Test Data
        mView = (ListView) findViewById(R.id.item_list);
        activityList = new ArrayList<>();
        activityList.add("First");
        activityList.add("Second");
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, activityList);
        mView.setAdapter(listAdapter);
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
            AlertDialog popout = new AlertDialog.Builder(this)
                    .setTitle("Add Task")
                    .setMessage("Enter details of task")
                    .setView(textInput)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(textInput.getText());
                            activityList.add(task);
                            mView.setAdapter(listAdapter);
                        }})
                    .setNegativeButton("Cancel", null)
                    .create();
            popout.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
