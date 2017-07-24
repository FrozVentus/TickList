package frozventus.ticklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

import java.util.*;


public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "tasksInfo";
    // Tasks table name
    private static final String TASK_TABLE_NAME = "tasks";
    // Tasks Table Columns names
    private static final String TASK_COLUMN_ID = "_id";
    private static final String TASK_COLUMN_TITLE = "title";
    private static final String TASK_COLUMN_DETAILS = "details";
    private static final String TASK_COLUMN_DATE = "date";
    private static final String TASK_COLUMN_DAILY ="isDaily";
    private static final String TASK_COLUMN_LASTCOMPLETED = "lastCompleted";

    private List<String> _titleList;
    private List<String> _dateList;
    private HashMap<Integer, ArrayList<String>> _detailList; // _id of task is used as Key
    private List<Integer> _titleOrder; // hold _id of the task in order
    private List<String> _lastCompleted;
    private Context _context;


    public DBHandler(Context context, List<String> titleList, List<String> dateList,
                     HashMap<Integer, ArrayList<String>> detailList, List<Integer> titleOrder,
                     List<String> lastCompleted) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _titleList = titleList;
        _dateList = dateList;
        _detailList = detailList;
        _titleOrder = titleOrder;
        _lastCompleted = lastCompleted;
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TASK_TABLE_NAME + "(" +
                TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_COLUMN_TITLE + " TEXT, " +
                TASK_COLUMN_DETAILS + " TEXT, " +
                TASK_COLUMN_DATE + " TEXT, " +
                TASK_COLUMN_DAILY + " TEXT, " +
                TASK_COLUMN_LASTCOMPLETED + " TEXT" +
        ")";
        //debug
        // Toast.makeText(_context, CREATE_CONTACTS_TABLE, Toast.LENGTH_LONG).show();
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    // Adding new Task
    public void addTask(String title, ArrayList<String> details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_TITLE, title); // TaskName
        values.put(TASK_COLUMN_DETAILS, details.get(0)); // Details
        values.put(TASK_COLUMN_DATE, details.get(1)); // Date
        values.put(TASK_COLUMN_DAILY, details.get(2)); // Daily
        values.put(TASK_COLUMN_LASTCOMPLETED, "null"); // Last Completed

        // Inserting Row
        long newRowID = db.insert(TASK_TABLE_NAME, null, values);
        db.close(); // Closing database connection
        // debug
        // Toast.makeText(_context, "The new Row Id is " + newRowID, Toast.LENGTH_LONG).show();
        getAllTasks();
    }

    // Getting All Tasks
    // Also update what is shown on screen
    public boolean getAllTasks() {
        List<String> newTitleList = new LinkedList<String>();
        List<String> newDateList = new LinkedList<String>();
        HashMap<Integer, ArrayList<String>> newDetailList =
                new HashMap<Integer, ArrayList<String>>();
        List<Integer> newOrder = new LinkedList<Integer>();
        List<String> newLastCompleted = new LinkedList<String>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TASK_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // get order
                Integer id = cursor.getInt(0);
                newOrder.add(id);
                // get title
                newTitleList.add(cursor.getString(1));

                ArrayList<String> currDetails = new ArrayList<String>();
                // get details
                currDetails.add(cursor.getString(2));
                // get date
                String date = "Due :  " + cursor.getString(3);
                currDetails.add(date);
                newDateList.add(date);
                // get daily
                currDetails.add(cursor.getString(4));
                // put into hashmap
                newDetailList.put(id, currDetails);

                // get last completed
                newLastCompleted.add(cursor.getString(5));

            } while (cursor.moveToNext());
        }
        // update _titleOrder
        _titleOrder.clear();
        _titleOrder.addAll(newOrder);
        // update _titleList
        _titleList.clear();
        _titleList.addAll(newTitleList);
        // update _dateList
        _dateList.clear();
        _dateList.addAll(newDateList);
        // update _detailList
        _detailList.clear();
        _detailList.putAll(newDetailList);
        // update _lastCompleted
        _lastCompleted.clear();
        _lastCompleted.addAll(newLastCompleted);

        cursor.close();
        // return contact list
        return true;
    }
    // Getting tasks Count
    public int getTasksCount() {
        String countQuery = "SELECT * FROM " + TASK_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating title
    public int updateTitle(int id, String newString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TASK_COLUMN_TITLE, newString);

        db.update(TASK_TABLE_NAME, values,TASK_COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        getAllTasks();
        return id;
    }

    // Updating a task
    public int updateTask(int id, int position, String newString) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        switch(position) { //which detail to edit
            case 0:
                values.put(TASK_COLUMN_DETAILS, newString);
                break;
            case 1:
                values.put(TASK_COLUMN_DATE, newString);
                break;
            case 2:
                values.put(TASK_COLUMN_DAILY, newString);
                break;
            case 3:
                values.put(TASK_COLUMN_LASTCOMPLETED, newString);
        }
        // updating row
        db.update(TASK_TABLE_NAME, values,TASK_COLUMN_ID + " = ?",
        new String[]{String.valueOf(id)});
        getAllTasks();
        return id;
    }

    // Deleting a Task
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASK_TABLE_NAME, TASK_COLUMN_ID + " = ?",
        new String[] { String.valueOf(id) });
        getAllTasks();
        //db.close();
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        // drop current table
        db.execSQL("DROP TABLE " + TASK_TABLE_NAME);
        // create new table
        this.onCreate(db);
        getAllTasks();
    }
}
